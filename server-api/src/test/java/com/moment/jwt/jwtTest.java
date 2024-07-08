//package com.moment.jwt;
//
//import com.moment.config.jwt.provider.JwtProvider;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.security.Key;
//import java.util.Date;
//
//@ExtendWith(MockitoExtension.class)
//public class jwtTest {
//    private String secretKey = "bW9tZW50and0c2VjcmV0IUA=bW9tZW50and0c2VjcmV0IUA=bW9tZW50and0c2VjcmV0IUA=bW9tZW50and0c2VjcmV0IUA=";
//    private String accessTokenExpirationMs = "300000";
//    private String refreshTokenExpirationMs = "600000";
//    @InjectMocks
//    JwtProvider jwtProvider;
//    @Mock
//    private UserDetailsService userDetailsService;
//    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
////    @BeforeEach
////    void setUp(){
////        jwtProvider = new JwtProvider(userDetailsService);
////    }
//
//
//    private String generateToken(String userEmail, String tokenExpirationMs) {
//        // Claims 란 JWT의 payload 부분에 들어가는 데이터 단위라고 보면 된다.
//        // Map<String, Object>를 상속하고 있기 때문에 key, value 형식으로 값을 넣을 수 있다.
//        Claims claims = Jwts.claims().setSubject(userEmail); // username
//
//        // 토큰 생성 시간
//        Date now = new Date();
//        // 토큰 만료 시간
//        Date expireDate = new Date(now.getTime() + Long.parseLong(tokenExpirationMs));
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expireDate)
//                .signWith(key(), SignatureAlgorithm.HS512)
//                .compact();
//    }
//    private Key key() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
//    }
//
//    @Test
//    @DisplayName("토큰 생성 테스트")
//    void generateTokenTest(){
//        String accessToken = generateToken("dldydcns123@gmail.com", accessTokenExpirationMs);
//        System.out.println(accessToken);
//    }
//
//    @Test
//    @DisplayName("토큰 유효성 검사 - 성공")
//    void success_validateToken(){
////        String accessToken = generateToken("dldydcns2@naver.com", accessTokenExpirationMs);
//        boolean check = jwtProvider.validateToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzIiwiaWF0IjoxNzIwNDQ2MDM2LCJleHAiOjE3MjA0NDYzMzZ9.a0DMthnarnCGBQtyMfMJCa5udB5CkAX0-Sp6g7N_fFD-okb2sWg90EM9KyvgUQ98vLTlI_rDTQ2wwcaxihlzmA");
//        Assertions.assertThat(check).isTrue();
//    }
//    @Test
//    void password(){
//        String password = passwordEncoder.encode("1234");
//        System.out.println(password);
//    }
//
//}
