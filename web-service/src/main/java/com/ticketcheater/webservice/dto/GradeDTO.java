package com.ticketcheater.webservice.dto;

import com.ticketcheater.webservice.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GradeDTO {

    private Long id;
    private Long placeId;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static GradeDTO toDTO(Grade grade) {
        return new GradeDTO(
                grade.getId(),
                grade.getPlace().getId(),
                grade.getName(),
                grade.getCreatedAt(),
                grade.getUpdatedAt(),
                grade.getDeletedAt()
        );
    }

}
