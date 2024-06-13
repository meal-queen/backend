package com.threlease.base.entities;

import com.threlease.base.enums.PayStatus;
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
public class PayLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(referencedColumnName = "uuid")
    private RestaurantEntity restaurant;

    @ManyToOne
    @JoinColumn(referencedColumnName = "uuid")
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(referencedColumnName = "uuid")
    private AuthEntity author;

    @Column(nullable = false)
    private long point;

    @Enumerated(EnumType.STRING)
    private PayStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}
