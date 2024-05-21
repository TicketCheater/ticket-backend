package com.ticketcheater.webservice.controller.response.place;

import com.ticketcheater.webservice.dto.PlaceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceUpdateResponse {
    private Long id;
    private String name;

    public static PlaceUpdateResponse from(PlaceDTO dto) {
        return new PlaceUpdateResponse(dto.getId(), dto.getName());
    }
}
