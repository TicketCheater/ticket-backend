package com.ticketcheater.webservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_place_id_deletedAt", columnList = "id, deleted_at"),
        @Index(name = "idx_place_name", columnList = "name", unique = true),
        @Index(name = "idx_place_name_deletedAt", columnList = "name, deleted_at")
})
@Entity
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public static Place of(String name) {
        Place place = new Place();
        place.setName(name);
        return place;
    }

}
