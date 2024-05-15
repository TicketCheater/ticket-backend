package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.member.*;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.member.*;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.jwt.TokenDTO;
import com.ticketcheater.webservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/members/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public Response<MemberSignupResponse> signup(@RequestBody MemberSignupRequest request) {
        return Response.success(MemberSignupResponse.from(memberService.signup(
                request.getName(), request.getPassword(), request.getEmail(), request.getNickname()
        )));
    }

    @PostMapping("/login")
    public Response<MemberLoginResponse> login(@RequestBody MemberLoginRequest request) {
        TokenDTO token = memberService.login(request.getName(), request.getPassword());
        return Response.success(MemberLoginResponse.from(token.accessToken(), token.refreshToken()));
    }

    @PostMapping("/validate")
    public Response<MemberValidateResponse> validate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody MemberValidateRequest request
    ) {
        return Response.success(MemberValidateResponse.from(memberService.validateMember(
                jwtTokenProvider.getName(header), request.getPassword()
        )));
    }

    @PostMapping("/reissue")
    public Response<MemberReissueResponse> reissue(@RequestBody MemberReissueRequest request) {
        return Response.success(MemberReissueResponse.from(jwtTokenProvider.reissueAccessToken(
                request.getRefreshToken()
        )));
    }

    @PostMapping("/logout")
    public Response<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        memberService.logout(jwtTokenProvider.getName(header));
        return Response.success();
    }

    @PatchMapping("/update")
    public Response<MemberUpdateResponse> update(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody MemberUpdateRequest request
    ) {
        return Response.success(MemberUpdateResponse.from(memberService.updateMember(
                jwtTokenProvider.getName(header), request.getPassword(), request.getNickname()
        )));
    }

    @PatchMapping("/delete")
    public Response<Void> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        memberService.deleteMember(jwtTokenProvider.getName(header));
        return Response.success();
    }

}
