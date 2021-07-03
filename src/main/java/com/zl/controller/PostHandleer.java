package com.zl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zl.entity.*;
import com.zl.repository.*;
import com.zl.utils.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zl.utils.Json;


import javax.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostHandleer {
    SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
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
    private ThumbUpRepository thumbUpRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubCommentRepository subCommentRepository;
    @PostMapping("/jsonTest")
    public Map<String,Object> jsonTest(@RequestBody Map<String,Object> params)
    {
        List<String> s = (List<String>) params.get("zl");

        System.out.println(s.toString());
        return params;
    }


    /**
     *
     * @param tokenStr
     * @param params
     * @return
     */
    @PostMapping("/upload")
    public Map<String,Object> uploadPost(@RequestHeader(name = "token")String tokenStr,
                                         @RequestBody Map<String,Object> params)
    {
        Map<String,Object> map = new HashMap<>();
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        List<?> strList = (List<?>) params.get("tags");
        List<String> tags = new ArrayList<>();
        for(Object item:strList)
        {
            tags.add((String) item);
        }
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String summary = (String) params.get("summary");
        Post newPost = new Post();
        newPost.setContent(content);
        newPost.setTitle(title);
        newPost.setSummary(summary);
        newPost.setThumbUpNum(0);
        newPost.setCommentNum(0);
        newPost.setBrowseNum(0);
        newPost.setUser(user);
        postRepository.save(newPost);
        for(String tag:tags)
        {
            Tag newTag = new Tag();
            newTag.setPost(newPost);
            newTag.setTagStr(tag);
            tagRepository.save(newTag);
        }
        map.put("msg","success");
        map.put("state",1);
        return map;
    }

    /**
     *
     * @param tokenStr
     * @param postId
     * @return
     */
    @PostMapping("getPostContent")
    public JSONObject getPostContent(@RequestHeader(name = "token")String tokenStr,String postId)
    {
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        Optional<Post> selectedPost = postRepository.findById(Integer.parseInt(postId));
        JSONObject jo = new JSONObject();
        if(!selectedPost.isPresent())
        {
            jo.put("msg","post do not exist.");
            jo.put("state",0);
            return jo;
        }
        Post post = selectedPost.get();
        post.setBrowseNum(post.getBrowseNum()+1);
        postRepository.save(post);
        jo = Json.EntityToJson(post);
        jo.remove("user");
        JSONObject author = new JSONObject();
        author.put("uid",post.getUser().getUid());
        author.put("name",post.getUser().getUserName());
        jo.put("author",author);
        List<Tag> Tags = tagRepository.findTagByPost(post);
        List<String> tags =  Tags.stream().map(Tag::getTagStr).collect(Collectors.toList());
        jo.put("tags",tags);
        jo.put("msg","success");
        jo.put("state",0);
        jo.put("collected",collectRepository.findCollectByUserAndPost(user,post).size());
        jo.put("thumbuped",thumbUpRepository.findThumbUpsByPostAndUser(post,user).size());

        return jo;
    }

    /**
     *
     * @param tokenStr
     * @param keyword
     * @return
     */
    @PostMapping("/getPostsByKeyword")
    public List<JSONObject> getPostsByKeyword(@RequestHeader(name = "token")String tokenStr,String keyword)
    {
        List<Tag> Tags = tagRepository.findTagsByTagStrContaining(keyword);
        Set<Post> selectedPosts = Tags.stream().map(Tag::getPost).collect(Collectors.toSet());
        List<JSONObject> jsonObjects = new ArrayList<>();
        for(Post p:selectedPosts)
        {
            JSONObject article = Json.EntityToJson(p);
            article.remove("user");
            article.remove("content");

            JSONObject author = new JSONObject();
            author.put("name",p.getUser().getUserName());
            author.put("ImgSrc",p.getUser().getImgPath());
            author.put("uid",p.getUser().getUid());
            article.put("author",author);
            jsonObjects.add(article);
        }
        return jsonObjects;
    }

    /**
     * 收藏 / 取消收藏
     * @param tokenStr
     * @param pid
     * @return
     */
    @PostMapping("collect")
    public JSONObject collect(@RequestHeader(name = "token")String tokenStr,String pid)
    {
        JSONObject jo = new JSONObject();
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        Post post = postRepository.getOne(Integer.parseInt(pid));
        List<Post> postList = collectRepository
                .findCollectsByUser(user).stream().map(Collect::getPost).collect(Collectors.toList());
        if(postList.contains(post))
        {
            collectRepository.delete(collectRepository.findCollectByUserAndPost(user,post).get(0));
            jo.put("state",2);
            jo.put("msg","uncollect successfully");
        }
        else {
            Collect collect = new Collect();
            collect.setPost(post);
            collect.setUser(user);

            collectRepository.save(collect);
            jo.put("state",1);
            jo.put("msg","collect successfully");
        }
        return jo;
    }
    /**
     * 获取用户发帖
     */
    @PostMapping("getUserPosts")
    public List<JSONObject> getUserPosts(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        List<JSONObject> res = new ArrayList<>();
        int id = Integer.parseInt(uid);
        User user;
        if(id==-1)
            user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        else  user = userRepository.getOne(id);
        // 标题，浏览量，评论数，点赞数，id，简介，发帖时间
        List<Post> posts = postRepository.findPostsByUser(user);
        for(Post p :posts)
        {
            JSONObject jo = new JSONObject();
            jo.put("title",p.getTitle());
            jo.put("browseNum",p.getBrowseNum());
            jo.put("commentNum",p.getCommentNum());
            jo.put("thumbUpNum",p.getThumbUpNum());
            jo.put("pid",p.getPid());
            jo.put("summary",p.getSummary());
            jo.put("createTime",sdf.format(p.getCreateTime()));
            res.add(jo);
        }
        return res;
    }
    /**
     * 获取用户的收藏列表
     */
    @PostMapping("getUserCollects")
    public List<JSONObject> getUserCollects(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        List<JSONObject> res = new ArrayList<>();
        int id = Integer.parseInt(uid);
        User user;
        if(id==-1)
            user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        else  user = userRepository.getOne(id);
        // 标题，浏览量，评论数，点赞数，id，简介，发帖时间,用户名，用户id
        List<Post> posts = collectRepository.findCollectsByUser(user).stream().map(Collect::getPost)
                .collect(Collectors.toList());
        for(Post p :posts)
        {
            JSONObject jo = new JSONObject();
            jo.put("title",p.getTitle());
            jo.put("browseNum",p.getBrowseNum());
            jo.put("commentNum",p.getCommentNum());
            jo.put("thumbUpNum",p.getThumbUpNum());
            jo.put("pid",p.getPid());
            jo.put("summary",p.getSummary());
            jo.put("createTime",sdf.format(p.getCreateTime()));
            jo.put("authorName",p.getUser().getUserName());
            jo.put("authorId",p.getUser().getUid());
            res.add(jo);
        }
        return res;
    }

    /**
     * 点赞  / 取消赞
     */
    @PostMapping("thumbUp")
    public JSONObject thumbUp(@RequestHeader(name = "token")String tokenStr,String pid)
    {
        JSONObject jo = new JSONObject();
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        Post post = postRepository.getOne(Integer.parseInt(pid));
        List<ThumbUp> thumbUps = thumbUpRepository.findThumbUpsByPostAndUser(post,user);
        if(thumbUps.size()==0)
        {
            ThumbUp thumbUp = new ThumbUp();
            thumbUp.setPost(post);
            thumbUp.setUser(user);
            thumbUpRepository.save(thumbUp);
            post.setThumbUpNum(post.getThumbUpNum()+1);
            postRepository.save(post);
            jo.put("state",1);
            jo.put("msg","thumbup successfully");

        }
        else {
            ThumbUp thumbUp = thumbUps.get(0);
            thumbUpRepository.delete(thumbUp);
            post.setThumbUpNum(post.getThumbUpNum()-1);
            postRepository.save(post);
            jo.put("state",2);
            jo.put("msg","cancel thumbup successfully");
        }
        return jo;
    }

    /**
     * 评论帖子
     */
    @PostMapping("comment")
    public JSONObject comment(@RequestHeader(name = "token")String tokenStr,String pid,String content)
    {
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        Post post = postRepository.getOne(Integer.parseInt(pid));
        JSONObject jo = new JSONObject();
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        commentRepository.save(comment);
        post.setCommentNum(post.getCommentNum()+1);
        postRepository.save(post);
        jo.put("state",1);
        jo.put("msg","comment successfully");
        return jo;
    }

    /**
     * 子评论，评论原评论/子评论
     * @param tokenStr
     * @param commentId
     * @param subCommentId -1回复原评论   其他回复子评论
     * @param content
     * @return
     */
    @PostMapping("subComment")
    public JSONObject subComment(@RequestHeader(name = "token")String tokenStr,String commentId,String subCommentId
    ,String content)
    {
        JSONObject jo = new JSONObject();
        User user = tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        Comment comment = commentRepository.getOne(Integer.parseInt(commentId));
        int subId = Integer.parseInt(subCommentId);
        SubComment subComment = new SubComment();
        subComment.setUser(user);
        subComment.setContent(content);
        subComment.setComment(comment);
        subComment.setResponseTo(subId==-1?comment.getUser():subCommentRepository.getOne(subId).getUser());
        subCommentRepository.save(subComment);
        jo.put("state",1);
        jo.put("msg","comment successfully");
        return jo;
    }
    /**
     * 获得评论和子评论
     */
    @PostMapping("getComments")
    public List<JSONObject> getComments(@RequestHeader(name = "token")String tokenStr,String pid)
    {
        Post post = postRepository.getOne(Integer.parseInt(pid));
        List<JSONObject> res = new ArrayList<>();
        List<Comment> comments = commentRepository.findCommentsByPost(post);
        /**
         * [{
         *     name:
         *     uid:
         *     commentId:
         *     content:
         *     imgSrc:
         *     Time:
         *     subComments:[
         *         {
         *              name:
         *              uid:
         *              subId:
         *              content:
         *              responedTo: name
         *              time:
         *         }
         *         ....
         *     ]
         *
         *
         * }
         * .....]
         */
        for(Comment comment:comments)
        {
            JSONObject commentJson = new JSONObject();
            commentJson.put("name",comment.getUser().getUserName());
            commentJson.put("uid",comment.getUser().getUid());
            commentJson.put("commentId",comment.getCid());
            commentJson.put("content",comment.getContent());
            commentJson.put("imgsrc",comment.getUser().getImgPath());
            commentJson.put("commentTime",sdf.format(comment.getCommentTime()));
            List<JSONObject> subComments = new ArrayList<>();
            List<SubComment> subCommentList = subCommentRepository.findSubCommentsByComment(comment);
            for(SubComment subComment:subCommentList)
            {
                JSONObject subCommentJson = new JSONObject();
                subCommentJson.put("name",subComment.getUser().getUserName());
                subCommentJson.put("uid",subComment.getUser().getUid());
                subCommentJson.put("content",subComment.getContent());
                subCommentJson.put("subCommentTime",subComment.getCreateTime());
                subCommentJson.put("subCommentId",subComment.getScid());
                subCommentJson.put("RespondToName",subComment.getResponseTo().getUserName());
                subCommentJson.put("RespondToUid",subComment.getResponseTo().getUid());
                subComments.add(subCommentJson);
            }
            commentJson.put("subComments",subComments);
            res.add(commentJson);

        }
        return res;





    }

    /**
     * 获取热门文章
     */
    @PostMapping("getHotPosts")
    public List<JSONObject> getHotPosts(@RequestHeader(name = "token")String tokenStr,String uid)
    {
        List<JSONObject> res = new ArrayList<>();
        // 标题，浏览量，评论数，点赞数，id，简介，发帖时间，发帖人 ，发帖人头像，发帖人id
        List<Post> posts = postRepository.findPostsByBrowseNumGreaterThanEqual(10);
        for(Post p :posts)
        {
            JSONObject jo = new JSONObject();
            jo.put("title",p.getTitle());
            jo.put("browseNum",p.getBrowseNum());
            jo.put("commentNum",p.getCommentNum());
            jo.put("thumbUpNum",p.getThumbUpNum());
            jo.put("pid",p.getPid());
            jo.put("summary",p.getSummary());
            jo.put("createTime",sdf.format(p.getCreateTime()));
            jo.put("authorName",p.getUser().getUserName());
            jo.put("authorId",p.getUser().getUid());
            jo.put("authorImg",p.getUser().getImgPath());
            res.add(jo);
        }
        return res;
    }


    /**
     *
     * @param tokenStr
     * @param keyword
     * @return
     */
    @PostMapping("search")
    public List<JSONObject> search(@RequestHeader(name = "token")String tokenStr,String keyword)
    {
        User user =tokenRepository.findTokensByTokenStr(tokenStr).get(0).getUser();
        List<Post> posts = postRepository.findPostsByTitleContainingOrContentContaining(keyword,keyword);
        List<JSONObject> jsonObjects = new ArrayList<>();
        for(Post p:posts)
        {
            JSONObject article = Json.EntityToJson(p);
            article.remove("user");
            article.remove("content");

            JSONObject author = new JSONObject();
            author.put("name",p.getUser().getUserName());
            author.put("ImgSrc",p.getUser().getImgPath());
            author.put("uid",p.getUser().getUid());
            article.put("author",author);
            article.put("collected",collectRepository.findCollectByUserAndPost(user,p).size());
            article.put("thumbuped",thumbUpRepository.findThumbUpsByPostAndUser(p,user).size());

            jsonObjects.add(article);
        }
        return jsonObjects;
    }










}
