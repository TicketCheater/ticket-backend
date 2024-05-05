package com.ticketcheater.webservice.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberValidateRequest {
    private String name;
    private String password;
}
