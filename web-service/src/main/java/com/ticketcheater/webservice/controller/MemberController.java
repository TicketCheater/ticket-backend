package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.MemberSignupRequest;
import com.ticketcheater.webservice.controller.request.MemberValidateRequest;
import com.ticketcheater.webservice.controller.response.MemberSignupResponse;
import com.ticketcheater.webservice.controller.response.MemberValidateResponse;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/web/members/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public Response<MemberSignupResponse> signup(@RequestBody MemberSignupRequest request) {
        return Response.success(MemberSignupResponse.from(memberService.signup(
                request.getName(), request.getPassword(), request.getEmail(), request.getNickname()
        )));
    }

    @PostMapping("/validate")
    public Response<MemberValidateResponse> validate(@RequestBody MemberValidateRequest request) {
        return Response.success(MemberValidateResponse.from(memberService.validateMember(
                request.getName(), request.getPassword()
        )));
    }

}
