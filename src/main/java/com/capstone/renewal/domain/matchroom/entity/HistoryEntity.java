package com.capstone.renewal.domain.matchroom.entity;

import com.capstone.renewal.domain.user.entity.UserEntity;
import com.capstone.renewal.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@DynamicInsert
public class HistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long historyIdx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "matchroom_idx")
    private MatchRoomEntity matchRoom;
}
