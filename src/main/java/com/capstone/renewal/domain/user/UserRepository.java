package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.response.DuplicationUidResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsUserEntityByUid(String uid);
    Optional<UserEntity> findByUid(String uid);
}
