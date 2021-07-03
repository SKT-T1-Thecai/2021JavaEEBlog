package com.zl.entity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Time;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class Collect {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer cid;
    @OneToOne
    private User user;
    @OneToOne
    private Post post;
    @CreatedDate
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false, nullable = false)
    private Date createTime;
}


