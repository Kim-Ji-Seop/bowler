package com.capstone.renewal.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsUserEntityByUid(String uid);
    Optional<UserEntity> findByUid(String uid);
}
