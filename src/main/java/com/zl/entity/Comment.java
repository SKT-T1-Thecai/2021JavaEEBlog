package com.zl.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer cid;
    @OneToOne
    private User user;
    @OneToOne
    private Post post;
    private String content;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date commentTime;
}