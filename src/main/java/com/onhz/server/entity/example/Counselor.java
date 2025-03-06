//package com.onhz.server.entity.example;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.DynamicUpdate;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Entity
//@DynamicUpdate
//@EntityListeners(AuditingEntityListener.class)
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Counselor {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(length = 50, nullable = false)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String introduction;
//
//    @Column(nullable = false)
//    private Boolean isUse;
//
//    @Column
//    private String profile;
//
//    @Column
//    @CreatedDate
//    private LocalDateTime createdAt;
//
//    @Column
//    private Boolean isDeleted = false;
//
//    @OneToMany(mappedBy = "counselor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    List<CounselorSubject> counselorSubjects = new ArrayList<>();
//
//    @Builder
//    public Counselor(Long id, String name, String introduction, boolean isUse, String profile, LocalDateTime createdAt,
//                     boolean isDeleted, List<CounselorSubject> counselorSubjects) {
//        if (id != null) {
//            this.id = id;
//        }
//        this.name = name;
//        this.introduction = introduction;
//        this.isUse = isUse;
//        this.profile = profile;
//        this.createdAt = createdAt;
//        this.isDeleted = isDeleted;
//
//        if (counselorSubjects != null) {
//            counselorSubjects.forEach(counselorSubject -> this.addCounselorSubject(counselorSubject));
//        }
//    }
//
//    public void addCounselorSubject(CounselorSubject counselorSubject) {
//        counselorSubject.setCounselor(this);
//        this.counselorSubjects.add(counselorSubject);
//    }
//
//    public void delete() {
//        this.isDeleted = true;
//    }
//
//    public void update(String name, String introduction, Boolean isUse, String profile) {
//        if (name != null) this.name = name;
//        if (introduction != null) this.introduction = introduction;
//        if (isUse != null) this.isUse = isUse;
//        if (profile != null) this.profile = profile;
//    }
//
//    public void updateProfile(String path) {
//        this.profile = path;
//    }
//}