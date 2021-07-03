package com.zl.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SubComment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer scid;
    @OneToOne
    private User user;
    @OneToOne
    private Comment comment;
    @OneToOne
    private User responseTo;
    @CreatedDate
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false, nullable = false)
    private Date createTime;
    private String content;
}