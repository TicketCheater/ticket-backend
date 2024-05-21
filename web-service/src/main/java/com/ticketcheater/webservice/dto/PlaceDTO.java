package com.ticketcheater.webservice.dto;

import com.ticketcheater.webservice.entity.Place;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PlaceDTO {

    private Long id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static PlaceDTO toDTO(Place place) {
        return new PlaceDTO(
                place.getId(),
                place.getName(),
                place.getCreatedAt(),
                place.getUpdatedAt(),
                place.getDeletedAt()
        );
    }

}
