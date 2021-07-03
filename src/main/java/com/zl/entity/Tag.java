package com.zl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class Tag {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer tid;
    @ManyToOne
    private Post post;
    private String tagStr;

}
