package com.ticketcheater.webservice.controller.response.grade;

import com.ticketcheater.webservice.dto.GradeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GradeCreateResponse {
    private Long gradeId;
    private String gradeName;

    public static GradeCreateResponse from(GradeDTO dto) {
        return new GradeCreateResponse(dto.getId(), dto.getName());
    }
}
