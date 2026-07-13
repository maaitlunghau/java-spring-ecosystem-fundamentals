package com.maaitlunghau.__fullstack_user_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maaitlunghau.__fullstack_user_management.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
