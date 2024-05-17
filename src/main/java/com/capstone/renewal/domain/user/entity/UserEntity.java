package com.capstone.renewal.domain.user.entity;

import com.capstone.renewal.domain.matchroom.entity.HistoryEntity;
import com.capstone.renewal.domain.matchroom.entity.MatchRoomEntity;
import com.capstone.renewal.global.BaseEntity;
import com.capstone.renewal.global.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@DynamicInsert
public class UserEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long userIdx;

    @Column(name = "uid", nullable = false, length = 100)
    private String uid;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 45)
    private Role role;

    @Column(name = "score_avg", nullable = false)
    @ColumnDefault("0")
    private Integer scoreAvg;

    @OneToMany(mappedBy = "user")
    private List<MatchRoomEntity> matchRooms;

    @OneToMany(mappedBy = "user")
    private List<HistoryEntity> historys;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getKey());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return this.uid;
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
