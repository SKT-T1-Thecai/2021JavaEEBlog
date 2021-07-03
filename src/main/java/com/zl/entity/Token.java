package com.zl.entity;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@Proxy(lazy = false)
public class Token {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer tid;
    @OneToOne
    private User user;
    private String tokenStr;
}