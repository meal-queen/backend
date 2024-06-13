package com.threlease.base.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", length = 36, columnDefinition = "varchar(36)", nullable = false)
    private String uuid;

    @Column(length = 24, nullable = false)
    private String name;

    @Column(unique = true, length = 16, nullable = false)
    private String bizNumber;

    private String address;

//    /restaurant/profile
    @Column(columnDefinition = "text", nullable = false)
    private String path;

    @ColumnDefault("0")
    @Column(columnDefinition = "int", nullable = false)
    private int point;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(referencedColumnName = "uuid")
    private AuthEntity author;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @CreatedDate
    private LocalDateTime createdAt;
}
