package com.zl.repository;

import com.zl.entity.Book;
import com.zl.entity.Post;
import com.zl.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Integer> {
    List<Tag> findTagByPost(Post post);
    List<Tag> findTagsByTagStrContaining(String tagStr);
}
