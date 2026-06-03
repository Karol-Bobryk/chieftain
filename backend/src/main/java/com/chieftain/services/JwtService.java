package com.chieftain.services;

import com.chieftain.adapters.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtService {
  public static UUID getUserId(String token) {
    Jws<Claims> jws = getVerifiedJwsClaims(token);
    return UUID.fromString(jws.getPayload().getSubject());
  }

  public static String getEmailAddress(String token) {
    Jws<Claims> jws = getVerifiedJwsClaims(token);
    return jws.getPayload().get("email", String.class);
  }

  public static Boolean isTokenValidForUser(String token, CustomUserDetails customUserDetails) {
    UUID userId = getUserId(token);
    Date tokenExpirationDate = getExpirationDate(token);

    if (userId != customUserDetails.getUserId()) return false;

    return tokenExpirationDate != null && !tokenExpirationDate.before(new Date());
  }

  public static String createJwsToken(CustomUserDetails customUserDetails) {
    byte[] decodedSecret = Decoders.BASE64.decode(System.getenv("JWT_SECRET"));
    SecretKey secretKey = Keys.hmacShaKeyFor(decodedSecret);

    Date exp = new Date();

    Calendar cal = Calendar.getInstance();
    cal.setTime(exp);
    cal.add(Calendar.SECOND, Integer.parseInt(System.getenv("JWT_EXPIRATION_DURATION_SECONDS")));
    exp = cal.getTime();

    return Jwts.builder()
        .subject(customUserDetails.getUserId().toString())
        .expiration(exp)
        .claim("email", customUserDetails.getUsername())
        .signWith(secretKey)
        .compact();
  }

  public static Date getExpirationDate(String token) {
    Jws<Claims> jws = getVerifiedJwsClaims(token);
    return jws.getPayload().getExpiration();
  }

  private static Jws<Claims> getVerifiedJwsClaims(String token) {
    // Not ideal, storage of this secret is suboptimal
    byte[] decodedSecret = Decoders.BASE64.decode(System.getenv("JWT_SECRET"));
    SecretKey secretKey = Keys.hmacShaKeyFor(decodedSecret);
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
  }
}
