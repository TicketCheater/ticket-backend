package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.aop.RequireAdmin;
import com.ticketcheater.webservice.controller.request.grade.GradeCreateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.grade.GradeCreateResponse;
import com.ticketcheater.webservice.controller.response.grade.GradeUpdateResponse;
import com.ticketcheater.webservice.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @RequireAdmin
    @PostMapping("/create")
    public Response<GradeCreateResponse> createGrade(@RequestBody GradeCreateRequest request) {
        return Response.success(GradeCreateResponse.from(gradeService.createGrade(request.getPlaceId(), request.getName())));
    }

    @RequireAdmin
    @PatchMapping("/update/{gradeId}")
    public Response<GradeUpdateResponse> updateGrade(
            @PathVariable Long gradeId,
            @RequestBody GradeCreateRequest request
    ) {
        return Response.success(GradeUpdateResponse.from(gradeService.updateGrade(gradeId, request.getPlaceId(), request.getName())));
    }

    @RequireAdmin
    @PatchMapping("/delete/{gradeId}")
    public Response<Void> deleteGrade(@PathVariable Long gradeId) {
        gradeService.deleteGrade(gradeId);
        return Response.success();
    }

    @RequireAdmin
    @PatchMapping("/restore/{gradeId}")
    public Response<Void> restoreGrade(@PathVariable Long gradeId) {
        gradeService.restoreGrade(gradeId);
        return Response.success();
    }

}
