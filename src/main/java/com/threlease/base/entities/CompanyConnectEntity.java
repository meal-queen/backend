package com.threlease.base.entities;

import com.threlease.base.enums.AffiliationUserRoles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CompanyConnectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", length = 36, columnDefinition = "varchar(36)", nullable = false)
    private String uuid;

    @ManyToOne(cascade = CascadeType.ALL)
    private AuthEntity author;

    @ManyToOne(cascade = CascadeType.ALL)
    private CompanyEntity company;

    @ColumnDefault("0")
    @Column(name = "point", columnDefinition = "int", nullable = false)
    private int point;

    @Enumerated(EnumType.STRING)
    private AffiliationUserRoles role;
}
