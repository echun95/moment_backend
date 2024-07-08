package com.moment.config.jwt.provider;

import com.moment.common.exception.CommonErrorCode;
import com.moment.common.exception.CommonException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${spring.security.jwt.token.access.expire-length}")
    private String accessTokenExpirationMs;
    @Value("${spring.security.jwt.token.refresh.expire-length}")
    private String refreshTokenExpirationMs;

    private final UserDetailsService userDetailsService;

    @Value("${spring.security.jwt.secret}")
    private String secretKey;


    // 액세스 토큰 발급
    public String generateAccessTokenFromUserId(Long userId) {
        return generateToken(userId, accessTokenExpirationMs);
    }

    // 리프레쉬 토큰 발급
    public String generateRefreshTokenFromUserId(Long userId) {
        return generateToken(userId, refreshTokenExpirationMs);
    }

    public String generateToken(Long userId, String tokenExpirationMs) {
        // Claims 란 JWT의 payload 부분에 들어가는 데이터 단위라고 보면 된다.
        // Map<String, Object>를 상속하고 있기 때문에 key, value 형식으로 값을 넣을 수 있다.
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId)); // username

        // 토큰 생성 시간
        Date now = new Date();
        // 토큰 만료 시간
        Date expireDate = new Date(now.getTime() + Long.parseLong(tokenExpirationMs));
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expireDate)
            .signWith(key(), SignatureAlgorithm.HS512)
            .compact();
    }

    // 토큰 유효성 확인
    public boolean validateToken(String token){
        if(!StringUtils.hasText(token)){
            return false;
        }
        Claims claims = getClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    // 토큰 기반으로 Authentication 구현체 생성
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }

    // Claims에서 username 추출
    private String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰에서 Claims 추출
    private Claims getClaims(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        } catch (MalformedJwtException | SecurityException e) {
            log.error("유효하지 않은 구성의 토큰: {}", e.getMessage());
            throw new CommonException(CommonErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰: {}", e.getMessage());
            throw new CommonException(CommonErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 형식이나 구성의 토큰: {}", e.getMessage());
            throw new CommonException(CommonErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 입력값: {}", e.getMessage());
            throw new CommonException(CommonErrorCode.WRONG_TOKEN);
        }
        return claims;
    }
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}