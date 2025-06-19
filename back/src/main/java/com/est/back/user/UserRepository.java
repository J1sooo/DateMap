package com.est.back.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByNickName(String nickName);
    boolean existsByEmail(String email);
    Optional<User> findByUsn(Long usn);
    Optional<User> findByNickNameAndEmail(String nickName, String email);
    Optional<User> findByUserIdAndEmail(String userId, String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickName(String nickName);
}