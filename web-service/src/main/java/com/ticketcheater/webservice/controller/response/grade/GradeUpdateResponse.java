package com.ticketcheater.webservice.controller.response.grade;

import com.ticketcheater.webservice.dto.GradeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GradeUpdateResponse {
    private Long id;
    private String name;

    public static GradeUpdateResponse from(GradeDTO dto) {
        return new GradeUpdateResponse(dto.getId(), dto.getName());
    }
}
