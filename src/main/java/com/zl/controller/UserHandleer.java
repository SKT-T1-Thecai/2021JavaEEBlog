package com.zl.controller;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.xml.bind.v2.schemagen.XmlSchemaGenerator;
import com.zl.entity.*;
import com.zl.repository.*;
import com.zl.utils.GenerateRandomString;
import com.zl.utils.Json;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.zl.utils.UploadFile;

import javax.persistence.criteria.CriteriaBuilder;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserHandleer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CollectRepository collectRepository;
    @Autowired
    private FileRepository fileRepository;
    @GetMapping("/findAll")
    public List<User>findAll(){
        return userRepository.findAll();
    }
    SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
    @PostMapping("/test")
    public String test(@RequestHeader(name = "User-Agent")String token)
    {
        Map<String,Object> map = new HashMap<>();
        System.out.println(token);
        return  "xxx";
    }


    /**
     * 登录
     * @param name
     * @param password
     */
    @PostMapping("/login")
    public Map<String,Object> userLogin(String name, String password)
    {
        Map<String,Object> json = new HashMap<>();
        List<User> userList= userRepository.getUserByName(name);
        if(userList.isEmpty())
        {
            json.put("msg","No user named "+name);
            json.put("state",0);
        }
        else if (userList.size()>=2)
        {
            json.put("msg","Internal server error.");
            json.put("state",0);
        }
        else {
            User user = userList.get(0);
            if(password.equals(user.getPassWord()))
            {
                json.put("msg","Login Success");
                json.put("state",1);
                Token token;
                if (!tokenRepository.findByUser(user).isEmpty())
                    token = tokenRepository.findByUser(user).get(0);
                else token = new Token();
                String ranStr = GenerateRandomString.generateString();
                token.setUser(user);
                token.setTokenStr(ranStr);
                tokenRepository.save(token);
                json.put("token",ranStr);
                json.put("uid",user.getUid());

            }
            else json.put("msg","Wrong password");

        }
        return json;
    }

    /**
     * 注册
     * @param name
     * @param password
     * @param email
     * @param gender
     * @return
     * @throws ParseException
     */
    @PostMapping("/register")
    public Object register(String name,String password,String email,String gender) throws ParseException {
        System.out.println(name);
        System.out.println(password);
        System.out.println(email);
        Map<String,Object> map = new HashMap<>();
        if (!userRepository.getUserByName(name).isEmpty())
        {
            map.put("msg","username "+name+"already exists");
            map.put("state",0);
            return map;
        }
        User newUser = new User();
        newUser.setEmail(email);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2000-01-01");
        System.out.println(gender);
        newUser.setGender(Integer.parseInt(gender)!=0);

        newUser.setBirthday(date);
        newUser.setPassWord(password);
        newUser.setUserName(name);
        newUser.setImgPath(Integer.parseInt(gender)!=0?"default_m.jpg":"default_f.jpg");
        userRepository.save(newUser);
        map.put("msg","success");
        map.put("state",1);
        return map;
    }

    /**
     * 修改用户信息
     * @param newUserName
     * @param gender
     * @param headImage
     * @param birthday
     * @param selfIntro
     * @param token
     * @return
     */
    @PostMapping("changeUserInfo")
    public Map<String,Object> changeUserInfo(String newUserName, String gender, MultipartFile headImage, String
                                             birthday, String selfIntro,@RequestHeader(name = "token")String token) throws FileNotFoundException, ParseException {
        Map<String,Object> map = new HashMap<>();
        User user = tokenRepository.findTokensByTokenStr(token).get(0).getUser();
        if(newUserName!=user.getUserName()&&
                !userRepository.getUserByName(newUserName).isEmpty())
        {
            map.put("state",0);
            map.put("msg","Username "+ newUserName+" has existed.");
        }
        user.setUserName(newUserName);

        user.setGender(Integer.parseInt(gender)==1);
        user.setSelfIntro(selfIntro);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(birthday);
        user.setBirthday(date);

        /* 1 把文件存在/HeadImage/uid.type  压缩到100K
         * 2 数据库里更改路径
         */
        if(null!=headImage&&!headImage.isEmpty())
        {
            String fileName = user.getUid().toString()+GenerateRandomString.generateString(5)+UploadFile.getFileType(headImage);
            String targetPath = "HeadImage/"+ fileName;
            if(UploadFile.SaveFileToPath(headImage,targetPath,true))
            {
                user.setImgPath(fileName);

            }else{
                map.put("msg","save headImage failed");
                map.put("state",0);
                return map;
            }
        }
        map.put("msg","success");
        map.put("state",1);
        userRepository.save(user);
        return map;
    }

    /**
     * 修改密码
     * @param tokenStr
     * @param originPassword
     * @param newPassword
     * @return
     */
    @PostMapping("changePassword")
    public Map<String,Object> changePassword(@RequestHeader(name = "token")String tokenStr,String originPassword,String newPassword)
    {
        Map<String,Object> map = new HashMap<>();
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        if(!user.getPassWord().equals(originPassword))
        {
            map.put("msg","the origin password you input is wrong.");
            return map;
        }
        else {
            user.setPassWord(newPassword);
            Token token = tokenRepository.findByUser(user).get(0);
            String newTokenStr = GenerateRandomString.generateString();
            token.setTokenStr(newTokenStr);
            tokenRepository.save(token);
            userRepository.save(user);
            map.put("msg","change password successfully!");
            //map.put("token",newTokenStr);
        }
    return map;
    }

    /**
     * 获取用户信息
     * @param tokenStr
     * @param uid
     * @return
     */
    @PostMapping("/getUserInfo")
    public JSONObject getUserInfo(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        int id = Integer.parseInt(uid);
        Optional<User> user;
        if(id==-1)
            user = Optional.ofNullable(tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser());
        else user = Optional.of(userRepository.findById(id).get());

        JSONObject jo = Json.EntityToJson(user.get());
        jo.remove("passWord");
        jo.remove("idols");
        jo.remove("fans");
        jo.put("idolNum",user.get().getIdols().size());
        jo.put("fansNum",user.get().getFans().size());
        List<Post> posts = postRepository.findPostsByUser(user.get());
        int commentNum =  posts.stream().mapToInt(Post::getCommentNum).sum();
        int thumbUpNum = posts.stream().mapToInt(Post::getThumbUpNum).sum();
        jo.put("commentNum",commentNum);
        jo.put("thumbUpNum",thumbUpNum);
        int collectNum = collectRepository.findCollectsByUser(user.get()).size();
        jo.put("collectNum",collectNum);
        jo.put("postNum",postRepository.findPostsByUser(user.get()).size());
        jo.put("fileNum",fileRepository.findFilesByUploader(user.get()).size());
        return jo;
    }

    /**
     * 关注  / 取关
     * @param tokenStr
     * @param uid
     * @return
     */
    @PostMapping("follow")
    public JSONObject follow(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        JSONObject jo = new JSONObject();
        User fans = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
     User idol = userRepository.getOne(Integer.parseInt(uid));
     Set<User> idolList = fans.getIdols();
     Set<User> fansList = idol.getFans();
     if(!idolList.contains(idol))
     {
         idolList.add(idol);
         fans.setIdols(idolList);
         fansList.add(fans);
         idol.setFans(fansList);
         jo.put("state",1);
         jo.put("msg","subscribe successfully.");

     }
     else {
        idolList.remove(idol);
        fansList.remove(fans);
         jo.put("state",2);
         jo.put("msg","unsubscribe successfully.");
     }
     userRepository.save(idol);
     userRepository.save(fans);
     return jo;

    }
    /**
     * 获取关注列表
     */
    @PostMapping("getIdolList")
    public List<JSONObject> getIdolList(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        List<JSONObject> res = new ArrayList<>();
        int id = Integer.parseInt(uid);
     User user;
     if(id==-1)
         user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
     else  user = userRepository.getOne(id);
     // 用户名 头像 介绍  id
        Set<User> idols = user.getIdols();
        for(User u:idols)
        {
            JSONObject jo = new JSONObject();
            jo.put("uid",u.getUid());
            jo.put("name",u.getUserName());
            jo.put("imgpath",u.getImgPath());
            jo.put("intro",u.getSelfIntro());
            res.add(jo);
        }
        return res;

    }

    /**
     * 获取粉丝列表
     */
    @PostMapping("getFansList")
    public  List<JSONObject>  getFansList(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        List<JSONObject> res = new ArrayList<>();
        int id = Integer.parseInt(uid);
        User user;
        if(id==-1)
            user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        else  user = userRepository.getOne(id);
        // 用户名 头像 介绍  id
        Set<User> fans = user.getFans();
        for(User u:fans)
        {
            JSONObject jo = new JSONObject();
            jo.put("uid",u.getUid());
            jo.put("name",u.getUserName());
            jo.put("imgpath",u.getImgPath());
            jo.put("intro",u.getSelfIntro());
            res.add(jo);
        }
        return res;
    }
    /**
     * 获取用户上传文件列表
     */
    @PostMapping("getFilesByUser")
    public List<JSONObject> getFilesByUser(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        List<JSONObject> res = new ArrayList<>();
        int id = Integer.parseInt(uid);
        User user;
        if(id==-1)
            user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        else  user = userRepository.getOne(id);
        List<File> files = fileRepository.findFilesByUploader(user);
        // 文件名  介绍 大小 上传时间

        for(File file:files)
        {
            JSONObject jo = new JSONObject();
            jo.put("fid",file.getFid());
            jo.put("fileName",file.getFilePath());
            jo.put("fileIntro",file.getFileIntro());
            jo.put("fileSize",file.getFileSize());
            jo.put("uploadTime",sdf.format(file.getUploadTime()));

            jo.put("downloadNum",file.getDownLoadNum());
            res.add(jo);
        }
        return res;
    }

    @PostMapping("search")
    public List<JSONObject> search(@RequestHeader(name = "token")String tokenStr,String keyword)
    {
        User viewer = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        List<User> users = userRepository.findUsersByUserNameContaining(keyword);
        List<JSONObject> res = new ArrayList<>();
        for(User user:users)
        {
            JSONObject jo = Json.EntityToJson(user);
            jo.remove("uploader");
            jo.remove("idols");
            jo.remove("fans");
            jo.remove("password");
            jo.put("followed",viewer.getIdols().contains(user)?1:0);
            res.add(jo);
        }
        return res;
    }

    @PostMapping("hasFollowed")
    public JSONObject hasFollowed(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        JSONObject jo = new JSONObject();
        User  user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        User checked = userRepository.getOne(Integer.parseInt(uid));
        jo.put("followed",user.getIdols().contains(checked)?1:0);
        jo.put("msg","success");
        return jo;
    }






}

