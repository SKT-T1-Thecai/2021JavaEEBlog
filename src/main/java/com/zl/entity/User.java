package com.zl.entity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer uid;
    private boolean gender;
    private String userName;
    @JsonIgnore
    private String passWord;
    @JSONField(format="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date birthday;

    private String imgPath;
    private String selfIntro;
    private String email;
    @ManyToMany
    @JsonIgnore
    private Set<User> idols;
    @ManyToMany
    @JsonIgnore
    private Set<User> fans;
}

