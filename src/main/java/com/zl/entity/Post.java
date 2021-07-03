package com.zl.entity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Clob;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer pid;
    @ManyToOne
    @JsonIgnore
    private User user;
    @LastModifiedDate
    @Column(nullable = false)
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @Lob
    @Basic(fetch=FetchType.LAZY)
    private String content;
    private Integer commentNum;
    private Integer thumbUpNum;
    private Integer browseNum;
    private String summary;
    private String title;

}


