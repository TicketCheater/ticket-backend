package com.ticketcheater.webservice.controller.request.grade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GradeCreateRequest {
    private Long placeId;
    private String name;
}
