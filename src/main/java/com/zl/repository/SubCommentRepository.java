package com.zl.repository;

import com.zl.entity.Comment;
import com.zl.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubComment,Integer> {
    List<SubComment> findSubCommentsByComment(Comment comment);
}
