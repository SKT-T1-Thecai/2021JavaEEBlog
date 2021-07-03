package com.zl.repository;

import com.zl.entity.Post;
import com.zl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findPostsByUser(User user);
    List<Post> findPostsByBrowseNumGreaterThanEqual(Integer browseNum);
    List<Post> findPostsByTitleContainingOrContentContaining(String title, String content);
}
