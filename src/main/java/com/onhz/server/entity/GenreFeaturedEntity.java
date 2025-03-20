package com.onhz.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "genre_featured_tb")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreFeaturedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "image_path")
    private String imagePath;
    @Column(name = "description", nullable = false)
    private String description;
}
