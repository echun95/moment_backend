package com.moment.entity;

import com.moment.enums.Gender;
import com.moment.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_COUPLE")
public class Couple extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COUPLE_ID")
    private Long coupleId;

    @Column(name = "MEETING_DATE")
    private LocalDate meetingDate;

    @Builder
    public Couple(Long coupleId, LocalDate meetingDate) {
        this.coupleId = coupleId;
        this.meetingDate = meetingDate;
    }
}
