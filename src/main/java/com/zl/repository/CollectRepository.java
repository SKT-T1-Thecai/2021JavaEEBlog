package com.zl.repository;

import com.zl.entity.Collect;
import com.zl.entity.Post;
import com.zl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectRepository extends JpaRepository<Collect,Integer> {
    List<Collect> findCollectsByUser(User user);
    List<Collect> findCollectByUserAndPost(User user, Post post);
}