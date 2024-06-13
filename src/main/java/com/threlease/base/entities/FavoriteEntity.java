package com.threlease.base.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FavoriteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", length = 36, columnDefinition = "varchar(36)", nullable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(referencedColumnName = "uuid")
    private RestaurantEntity restaurant;

    @ManyToOne
    @JoinColumn(referencedColumnName = "uuid")
    private AuthEntity author;
}
