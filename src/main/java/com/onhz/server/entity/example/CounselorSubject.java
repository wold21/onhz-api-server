package com.onhz.server.entity.example;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "counselor_subject")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CounselorSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id")
    private Counselor counselor;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Builder
    public CounselorSubject(Long id, Long price, Counselor counselor, Subject subject) {
        if (id != null) {
            this.id = id;
        }
        this.price = price;
        this.counselor = counselor;
        this.subject = subject;
    }

    public void setCounselor (Counselor counselor) {
        this.counselor = counselor;
    }

    public void updatePrice(Long price) {
        this.price = price;
    }
}
