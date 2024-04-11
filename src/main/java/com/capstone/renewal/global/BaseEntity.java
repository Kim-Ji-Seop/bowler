package com.capstone.renewal.global;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Enumerated(value = EnumType.STRING)
    protected Status status= Status.valueOf(Status.ACTIVE.toString());


    public enum Status {
        ACTIVE,
        DELETE
    }
}
