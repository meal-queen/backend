package com.threlease.base.entities;

import com.threlease.base.enums.UserRoles;
import jakarta.persistence.*;
import lombok.*;
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
public class AuthEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false)
    private String uuid;

    @Column(length = 24, nullable = false, unique = true)
    private String username;

    @Column(columnDefinition = "varchar(5)", nullable = false)
    private String name;

    @Column(columnDefinition = "text", nullable = false)
    private String password;

    @Column(length = 32, nullable = false)
    private String salt;

    @CreatedDate
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private UserRoles role;
}
