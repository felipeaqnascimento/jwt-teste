package com.fdan.jwtteste.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TokenAuthenticationService {

    // Expiration time = 10 days
    private static final long EXPIRATION_TIME = 860_000_000;
    private static final String SECRET = "MySecret";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";
    public static final String ROLES = "roles";

    public static void addAuthentication(HttpServletResponse response, String username, List<String> authorities) {
        Claims claims = Jwts.claims();
        claims
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .put(ROLES, authorities);

        String jwt = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
            String user = claims.getSubject();
            List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(claims);
            Object credentials = null; //Geralmente o password -> neste caso não é necessário, pois estamos autenticanco por token

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, credentials, grantedAuthorities);
            }
        }

        return null;
    }

    private static List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        return (List<GrantedAuthority>) claims.get(ROLES, List.class).stream()
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toList());
    }

}
