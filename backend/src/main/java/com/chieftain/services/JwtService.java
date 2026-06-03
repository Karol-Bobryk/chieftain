package com.chieftain.services;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.exceptions.RefreshTokenBlacklistedException;
import com.chieftain.exceptions.RefreshTokenExpNotFoundException;
import com.chieftain.exceptions.RefreshTokenJtiNotFoundException;
import com.chieftain.models.BlacklistedRefreshTokensEntity;
import com.chieftain.repositories.dto.JwtTokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final BlacklistedRefreshTokensRepositoryService blacklistedRefreshTokensRepositoryService;

  public JwtService(
      BlacklistedRefreshTokensRepositoryService blacklistedRefreshTokensRepositoryService) {
    this.blacklistedRefreshTokensRepositoryService = blacklistedRefreshTokensRepositoryService;
  }

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

  public static String createJwsAccessToken(CustomUserDetails customUserDetails) {
    byte[] decodedSecret = Decoders.BASE64.decode(System.getenv("JWT_SECRET"));
    SecretKey secretKey = Keys.hmacShaKeyFor(decodedSecret);

    Date exp = new Date();

    Calendar cal = Calendar.getInstance();
    cal.setTime(exp);
    cal.add(
        Calendar.SECOND, Integer.parseInt(System.getenv("JWT_ACCESS_EXPIRATION_DURATION_SECONDS")));
    exp = cal.getTime();

    return Jwts.builder()
        .subject(customUserDetails.getUserId().toString())
        .expiration(exp)
        .claim("email", customUserDetails.getUsername())
        .signWith(secretKey)
        .compact();
  }

  public static String createJwsRefreshToken(CustomUserDetails customUserDetails) {
    byte[] decodedSecret = Decoders.BASE64.decode(System.getenv("JWT_SECRET"));
    SecretKey secretKey = Keys.hmacShaKeyFor(decodedSecret);

    Date exp = new Date();

    Calendar cal = Calendar.getInstance();
    cal.setTime(exp);
    cal.add(
        Calendar.SECOND,
        Integer.parseInt(System.getenv("JWT_REFRESH_EXPIRATION_DURATION_SECONDS")));
    exp = cal.getTime();

    return Jwts.builder()
        .subject(customUserDetails.getUserId().toString()) // Not necessary, just a nice touch
        .expiration(exp)
        .id(UUID.randomUUID().toString())
        .signWith(secretKey)
        .compact();
  }

  public static Date getExpirationDate(String token) {
    Jws<Claims> jws = getVerifiedJwsClaims(token);
    return jws.getPayload().getExpiration();
  }

  @Transactional
  public JwtTokens refreshTokens(String refreshToken, CustomUserDetails userDetails)
      throws RefreshTokenExpNotFoundException, RefreshTokenJtiNotFoundException {
    Jws<Claims> claims = getVerifiedJwsClaims(refreshToken);

    if (blacklistedRefreshTokensRepositoryService.isBlacklisted(refreshToken)) {
      throw new RefreshTokenBlacklistedException("Cannot use blacklisted refresh token");
    }

    UUID jti = getTokenJti(refreshToken);
    Instant tokenExpiration = getTokenExp(refreshToken).toInstant();

    var blacklistedTokenEntity = new BlacklistedRefreshTokensEntity(jti, tokenExpiration);
    blacklistedRefreshTokensRepositoryService.save(blacklistedTokenEntity);

    return new JwtTokens(createJwsAccessToken(userDetails), createJwsRefreshToken(userDetails));
  }

  public static UUID getTokenJti(String token) {
    String tokenId = getVerifiedJwsClaims(token).getPayload().getId();
    if (tokenId == null) {
      throw new RefreshTokenJtiNotFoundException("No jti/id on provided refresh token");
    }

    return UUID.fromString(tokenId);
  }

  public static Date getTokenExp(String token) {
    Date tokenExpirationDate = getVerifiedJwsClaims(token).getPayload().getExpiration();
    if (tokenExpirationDate == null) {
      throw new RefreshTokenExpNotFoundException("No exp on provided refresh token");
    }

    return tokenExpirationDate;
  }

  private static Jws<Claims> getVerifiedJwsClaims(String token) {
    // Not ideal, storage of this secret is suboptimal
    byte[] decodedSecret = Decoders.BASE64.decode(System.getenv("JWT_SECRET"));
    SecretKey secretKey = Keys.hmacShaKeyFor(decodedSecret);
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
  }
}
