package com.onhz.server.repository;

import com.onhz.server.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.social WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithSocial(@Param("email") String email);
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.social WHERE u.id = :id")
    Optional<UserEntity> findByIdWithSocial(@Param("id") Long id);
}
