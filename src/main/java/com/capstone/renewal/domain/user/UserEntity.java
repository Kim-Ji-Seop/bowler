package com.capstone.renewal.domain.user;

import com.capstone.renewal.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "user")
@Builder
@AllArgsConstructor
@Getter
@DynamicInsert
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long userIdx;
    @Column(name = "uid", nullable = false)
    private String uid;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "score_avg", nullable = false)
    @ColumnDefault("0")
    private int scoreAvg;

    public UserEntity() {

    }
}
