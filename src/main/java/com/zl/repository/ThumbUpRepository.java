package com.zl.repository;

import com.zl.entity.Post;
import com.zl.entity.ThumbUp;
import com.zl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThumbUpRepository extends JpaRepository<ThumbUp,Integer> {
    List<ThumbUp> findThumbUpsByPostAndUser(Post post, User user);
}
