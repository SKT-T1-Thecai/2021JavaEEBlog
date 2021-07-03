package com.zl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class FileDownLoadController {
//    @GetMapping("/HeadImage/{ImageName}")
    public String  Download( @PathVariable String ImageName,HttpServletResponse response) {
        //待下载文件名
        String fileName = ImageName;
        //设置为png格式的文件
        response.setHeader("content-type", ImageName.substring(ImageName.lastIndexOf('.')));
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        byte[] buff = new byte[1024];
        //创建缓冲输入流
        BufferedInputStream bis = null;
        OutputStream outputStream = null;

        try {
            outputStream = response.getOutputStream();
            File path = new File(ResourceUtils.getURL("src/main/resources/").getPath());
            String baseUrl =  path.getAbsolutePath()+"/HeadImage/";
            //这个路径为待下载文件的路径
            bis = new BufferedInputStream(new FileInputStream(new File(baseUrl + fileName )));
            int read = bis.read(buff);

            //通过while循环写入到指定了的文件夹中
            while (read != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                read = bis.read(buff);
            }
        } catch (IOException e ) {
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
        return "employee/EmployeeDownloadFile";
    }
}
