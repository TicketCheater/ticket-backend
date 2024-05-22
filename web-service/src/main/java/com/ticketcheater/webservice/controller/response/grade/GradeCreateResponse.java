package com.ticketcheater.webservice.controller.response.grade;

import com.ticketcheater.webservice.dto.GradeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GradeCreateResponse {
    private Long id;
    private String name;

    public static GradeCreateResponse from(GradeDTO dto) {
        return new GradeCreateResponse(dto.getId(), dto.getName());
    }
}
