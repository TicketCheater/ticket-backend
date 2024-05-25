package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.aop.RequireAdmin;
import com.ticketcheater.webservice.controller.request.place.PlaceCreateRequest;
import com.ticketcheater.webservice.controller.request.place.PlaceUpdateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.place.PlaceCreateResponse;
import com.ticketcheater.webservice.controller.response.place.PlaceUpdateResponse;
import com.ticketcheater.webservice.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @RequireAdmin
    @PostMapping("/create")
    public Response<PlaceCreateResponse> createPlace(@RequestBody PlaceCreateRequest request) {
        return Response.success(PlaceCreateResponse.from(placeService.createPlace(request.getName())));
    }

    @RequireAdmin
    @PatchMapping("/update/{placeId}")
    public Response<PlaceUpdateResponse> updatePlace(
            @PathVariable Long placeId,
            @RequestBody PlaceUpdateRequest request
    ) {
        return Response.success(PlaceUpdateResponse.from(placeService.updatePlace(placeId, request.getName())));
    }

    @RequireAdmin
    @PatchMapping("/delete/{placeId}")
    public Response<Void> deletePlace(@PathVariable Long placeId) {
        placeService.deletePlace(placeId);
        return Response.success();
    }

    @RequireAdmin
    @PatchMapping("/restore/{placeId}")
    public Response<Void> restorePlace(@PathVariable Long placeId) {
        placeService.restorePlace(placeId);
        return Response.success();
    }

}
