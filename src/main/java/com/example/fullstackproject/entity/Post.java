package com.example.fullstackproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.annotations.CascadeType.ALL;
import static org.hibernate.annotations.CascadeType.DELETE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String text;
    private int viewsCount;
    private String imageUrl;
    private int likes = 0;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name = "post_liked_user",
            joinColumns = @JoinColumn(name = "post_id")
    )
    private Set<String> likedUsers = new HashSet<>();

    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name = "post_tags",
            joinColumns = {@JoinColumn(name = "post_id")}
    )
    @Cascade(ALL)
    private List<String> tags;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    @Cascade(DELETE)
    private List<Comment> comments;

    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "created_user", referencedColumnName = "id"
    )
    private User createdUser;
}
