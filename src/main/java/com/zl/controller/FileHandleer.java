package com.zl.controller;

import com.alibaba.fastjson.JSONObject;
import com.zl.entity.File;
import com.zl.entity.User;
import com.zl.repository.*;
import com.zl.utils.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.zl.utils.Json;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileHandleer {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollectRepository collectRepository;
    @Autowired
    private FileRepository fileRepository;


    @PostMapping("upload")
    public JSONObject upload(@RequestHeader(name = "token") String tokenStr, MultipartFile file, String fileIntro, String
            filename) throws FileNotFoundException {
        JSONObject jo = new JSONObject();
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = df.format(new Date());
        String fileName = filename + '_' + user.getUserName() + '_' + date + UploadFile.getFileType(file);
        String filePath = "ShareFile/" + fileName;
        if (!UploadFile.SaveFileToPath(file, filePath, false)) {
            jo.put("msg", "upload failed");
            jo.put("state", 0);
            return jo;
        }
        File newFile = new File();
        newFile.setDownLoadNum(0);
        newFile.setFilePath(fileName);
        newFile.setUploader(user);
        newFile.setFileSize(file.getSize());
        newFile.setFileIntro(fileIntro);
        fileRepository.save(newFile);
        jo.put("msg", "upload success");
        jo.put("state", 1);
        return jo;
    }

    @PostMapping("download")
    public JSONObject download(@RequestHeader(name = "token") String tokenStr, String fid, HttpServletResponse response) throws UnsupportedEncodingException {
        File file = fileRepository.getOne(Integer.parseInt(fid));
        String filePath = file.getFilePath();
        JSONObject jo = new JSONObject();
        response.setHeader("content-type", filePath.substring(filePath.lastIndexOf('.')));
        response.setContentType("application/force-download");
        response.setHeader("Content-disposition", "attachment; filename=" + new String(filePath.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        byte[] buff = new byte[1024];
        //创建缓冲输入流
        BufferedInputStream bis = null;
        OutputStream outputStream = null;


        try {
            outputStream = response.getOutputStream();
            java.io.File path = new java.io.File(ResourceUtils.getURL("src/main/resources/").getPath());
            String baseUrl = path.getAbsolutePath() + "/ShareFile/";
            //这个路径为待下载文件的路径
            bis = new BufferedInputStream(new FileInputStream(new java.io.File(baseUrl + filePath)));
            int read = bis.read(buff);

            //通过while循环写入到指定了的文件夹中
            while (read != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                read = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        jo.put("msg", "download has started.");
        jo.put("state", 1);
        file.setDownLoadNum(file.getDownLoadNum() + 1);
        fileRepository.save(file);
        return jo;
    }

    @PostMapping("search")
    public List<JSONObject> search(@RequestHeader(name = "token") String tokenStr, String keyword) {
        List<File> files = fileRepository.findFilesByFilePathContaining(keyword);
        List<JSONObject> res = new ArrayList<>();
        for (File file : files) {
            JSONObject jo = Json.EntityToJson(file);
            jo.remove("uploader");
            jo.put("uploaderName", file.getUploader().getUserName());
            jo.put("uploaderId", file.getUploader().getUid());
            jo.put("uploaderImg", file.getUploader().getImgPath());
            res.add(jo);
        }
        return res;
    }

    @GetMapping("getdownload")
    public JSONObject getdownload( String fid, HttpServletResponse response) throws UnsupportedEncodingException {
        File file = fileRepository.getOne(Integer.parseInt(fid));
        String filePath = file.getFilePath();
        JSONObject jo = new JSONObject();
        response.setHeader("content-type", filePath.substring(filePath.lastIndexOf('.')));
        response.setContentType("application/force-download");
        response.setHeader("Content-disposition", "attachment; filename=" + new String(filePath.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        byte[] buff = new byte[1024];
        //创建缓冲输入流
        BufferedInputStream bis = null;
        OutputStream outputStream = null;


        try {
            outputStream = response.getOutputStream();
            java.io.File path = new java.io.File(ResourceUtils.getURL("src/main/resources/").getPath());
            String baseUrl = path.getAbsolutePath() + "/ShareFile/";
            //这个路径为待下载文件的路径
            bis = new BufferedInputStream(new FileInputStream(new java.io.File(baseUrl + filePath)));
            int read = bis.read(buff);

            //通过while循环写入到指定了的文件夹中
            while (read != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                read = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        jo.put("msg", "download has started.");
        jo.put("state", 1);
        file.setDownLoadNum(file.getDownLoadNum() + 1);
        fileRepository.save(file);
        return jo;
    }
}
