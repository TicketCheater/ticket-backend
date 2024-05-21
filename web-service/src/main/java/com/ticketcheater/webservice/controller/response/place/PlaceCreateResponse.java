package com.ticketcheater.webservice.controller.response.place;

import com.ticketcheater.webservice.dto.PlaceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceCreateResponse {
    private Long id;
    private String name;

    public static PlaceCreateResponse from(PlaceDTO dto) {
        return new PlaceCreateResponse(dto.getId(), dto.getName());
    }
}
