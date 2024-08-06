package com.nicha.identityservice.controller;


import com.nicha.identityservice.dto.request.ApiResponse;
import com.nicha.identityservice.dto.request.AuthenticationRequest;
import com.nicha.identityservice.dto.request.IntrospectRequest;
import com.nicha.identityservice.dto.response.AuthenticationResponse;
import com.nicha.identityservice.dto.response.IntrospectResponse;
import com.nicha.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request) throws JOSEException {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticated(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
