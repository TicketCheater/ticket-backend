package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.member.*;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.member.*;
import com.ticketcheater.webservice.token.JwtProvider;
import com.ticketcheater.webservice.token.JwtDTO;
import com.ticketcheater.webservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/members/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @PostMapping("/signup")
    public Response<MemberSignupResponse> signup(@RequestBody MemberSignupRequest request) {
        return Response.success(MemberSignupResponse.from(memberService.signup(
                request.getName(), request.getPassword(), request.getEmail(), request.getNickname()
        )));
    }

    @PostMapping("/login")
    public Response<MemberLoginResponse> login(@RequestBody MemberLoginRequest request) {
        JwtDTO token = memberService.login(request.getName(), request.getPassword());
        return Response.success(MemberLoginResponse.from(token.accessToken(), token.refreshToken()));
    }

    @PostMapping("/validate")
    public Response<MemberValidateResponse> validate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody MemberValidateRequest request
    ) {
        return Response.success(MemberValidateResponse.from(memberService.validateMember(
                jwtProvider.getName(header), request.getPassword()
        )));
    }

    @PostMapping("/reissue")
    public Response<MemberReissueResponse> reissue(@RequestBody MemberReissueRequest request) {
        return Response.success(MemberReissueResponse.from(jwtProvider.reissueAccessToken(
                request.getRefreshToken()
        )));
    }

    @PostMapping("/logout")
    public Response<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        memberService.logout(jwtProvider.getName(header));
        return Response.success();
    }

    @PatchMapping("/update")
    public Response<MemberUpdateResponse> update(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody MemberUpdateRequest request
    ) {
        return Response.success(MemberUpdateResponse.from(memberService.updateMember(
                jwtProvider.getName(header), request.getPassword(), request.getNickname()
        )));
    }

    @PatchMapping("/delete")
    public Response<Void> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        memberService.deleteMember(jwtProvider.getName(header));
        return Response.success();
    }

}
