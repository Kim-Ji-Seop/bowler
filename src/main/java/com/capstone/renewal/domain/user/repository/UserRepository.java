package com.capstone.renewal.domain.user.repository;

import com.capstone.renewal.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsUserEntityByUid(String uid);
    Optional<UserEntity> findByUid(String uid);
}
