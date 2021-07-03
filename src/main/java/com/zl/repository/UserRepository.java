package com.zl.repository;



import com.zl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;


public interface UserRepository extends JpaRepository<User,Integer> {
@Query("from User where userName = ?1")
    List<User> getUserByName(String name);
List<User> findUsersByUserNameContaining(String userName);
}
