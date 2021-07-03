package com.zl.repository;



import com.zl.entity.Token;
import com.zl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TokenRepository extends JpaRepository<Token,Integer>{
    List<Token> findByUser(User user);
    List<Token> findTokensByTokenStr(String tokenStr);
}
