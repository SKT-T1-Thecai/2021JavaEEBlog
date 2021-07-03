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
public class File {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer fid;
    private String filePath;
    @ManyToOne
    private User uploader;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;
    private Integer downLoadNum;
    private String fileIntro;
    private Long fileSize;
}
