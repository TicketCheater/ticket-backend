package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.place.PlaceCreateRequest;
import com.ticketcheater.webservice.controller.request.place.PlaceUpdateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.place.PlaceCreateResponse;
import com.ticketcheater.webservice.controller.response.place.PlaceUpdateResponse;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.MemberService;
import com.ticketcheater.webservice.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/places")
@RequiredArgsConstructor
public class PlaceController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlaceService placeService;

    @PostMapping("/create")
    public Response<PlaceCreateResponse> createPlace(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody PlaceCreateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(PlaceCreateResponse.from(placeService.createPlace(request.getName())));
    }

    @PatchMapping("/update/{placeId}")
    public Response<PlaceUpdateResponse> updatePlace(
            @PathVariable Long placeId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody PlaceUpdateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(PlaceUpdateResponse.from(placeService.updatePlace(placeId, request.getName())));
    }

    @PatchMapping("/delete/{placeId}")
    public Response<Void> deletePlace(
            @PathVariable Long placeId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        placeService.deletePlace(placeId);
        return Response.success();
    }

    @PatchMapping("/restore/{placeId}")
    public Response<Void> restorePlace(
            @PathVariable Long placeId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        placeService.restorePlace(placeId);
        return Response.success();
    }

}
