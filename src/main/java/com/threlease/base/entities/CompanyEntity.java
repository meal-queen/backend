package com.threlease.base.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, columnDefinition = "varchar(36)", nullable = false)
    private String uuid;

    @Column(unique = true, length = 16, nullable = false)
    private String bizNumber;

    @Column(length = 24, nullable = false)
    private String name;

    @Column(length = 24, nullable = false)
    private String team;

    //    /restaurant/profile
    @Column(columnDefinition = "text", nullable = false)
    private String path;

    private String address;

    @ColumnDefault("0")
    @Column(columnDefinition = "int", nullable = false)
    private int point;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Column(length = 8, nullable = false, unique = true)
    private String inviteCode;

    @CreatedDate
    private LocalDateTime createdAt;
}
