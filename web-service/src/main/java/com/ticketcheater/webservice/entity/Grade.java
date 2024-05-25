package com.ticketcheater.webservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = @Index(name = "idx_grade_place_name", columnList = "place_id, name", unique = true))
@Entity(name = "grade")
public class Grade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "name", nullable = false)
    private String name;

    public static Grade of(Place place, String name) {
        Grade grade = new Grade();
        grade.setPlace(place);
        grade.setName(name);
        return grade;
    }

}
