package com.zl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class ThumbUp {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer tid;
    @OneToOne
    private User user;
    @OneToOne
    private Post post;

}
