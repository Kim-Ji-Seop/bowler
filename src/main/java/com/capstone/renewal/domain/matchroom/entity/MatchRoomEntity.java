package com.capstone.renewal.domain.matchroom.entity;

import com.capstone.renewal.domain.user.entity.UserEntity;
import com.capstone.renewal.global.BaseEntity;
import com.capstone.renewal.global.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "match_room")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@DynamicInsert
public class MatchRoomEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long matchRoomIdx;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", length = 200)
    private String content;

    @Column(name = "game_time", nullable = false, length = 50)
    private LocalDateTime gameTime;

    @Column(name = "target_score", nullable = false, length = 50)
    private Integer targetScore;

    @Column(name = "network_type", nullable = false, length = 45)
    private String networkType;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "match_code")
    private String matchCode;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private UserEntity user;

    @OneToMany(mappedBy = "matchRoom")
    private List<HistoryEntity> historys;

}
