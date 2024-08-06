package com.nicha.identityservice.service;

import com.nicha.identityservice.dto.request.AuthenticationRequest;
import com.nicha.identityservice.dto.response.AuthenticationResponse;
import com.nicha.identityservice.dto.request.IntrospectRequest;
import com.nicha.identityservice.dto.response.IntrospectResponse;
import com.nicha.identityservice.entity.User;
import com.nicha.identityservice.exception.AppException;
import com.nicha.identityservice.exception.ErrorCode;
import com.nicha.identityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY = null;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        var verified = signedJWT.verify(verifier);

        Date expDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        return IntrospectResponse.builder()
                .valid(verified && expDate.after(new Date()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws JOSEException {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .authenticated(true)
                .build();
    }

    private String generateToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("nicha.com")
                .expirationTime(new Date(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaimsSet.toJSONObject()));

        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

        return jwsObject.serialize();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
//            user.getRoles().forEach(stringJoiner::add);
        }
        return stringJoiner.toString();
    }

}
