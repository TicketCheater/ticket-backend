package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.grade.GradeCreateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.grade.GradeCreateResponse;
import com.ticketcheater.webservice.controller.response.grade.GradeUpdateResponse;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.GradeService;
import com.ticketcheater.webservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public Response<GradeCreateResponse> createGrade(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody GradeCreateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(GradeCreateResponse.from(gradeService.createGrade(request.getPlaceId(), request.getName())));
    }

    @PatchMapping("/update/{gradeId}")
    public Response<GradeUpdateResponse> updateGrade(
            @PathVariable Long gradeId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody GradeCreateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        return Response.success(GradeUpdateResponse.from(gradeService.updateGrade(gradeId, request.getPlaceId(), request.getName())));
    }

    @PatchMapping("/delete/{gradeId}")
    public Response<Void> deleteGrade(
            @PathVariable Long gradeId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        gradeService.deleteGrade(gradeId);
        return Response.success();
    }

    @PatchMapping("/restore/{gradeId}")
    public Response<Void> restoreGrade(
            @PathVariable Long gradeId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        gradeService.restoreGrade(gradeId);
        return Response.success();
    }

}
