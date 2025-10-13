package com.example.oauth2.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  public String extractUsername(final String token) {
    try {
      final SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (final ParseException e) {
      throw new RuntimeException("Invalid JWT token", e);
    }
  }

  public Date extractExpiration(final String token) {
    try {
      final SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getExpirationTime();
    } catch (final ParseException e) {
      throw new RuntimeException("Invalid JWT token", e);
    }
  }

  private Boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(final String username) {
    try {
      final JWTClaimsSet claimsSet =
          new JWTClaimsSet.Builder()
              .subject(username)
              .issueTime(new Date())
              .expirationTime(new Date(System.currentTimeMillis() + expiration))
              .build();

      final SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

      final JWSSigner signer = new MACSigner(secret.getBytes());
      signedJWT.sign(signer);

      return signedJWT.serialize();
    } catch (final JOSEException e) {
      throw new RuntimeException("Error generating JWT token", e);
    }
  }

  public Boolean validateToken(final String token, final UserDetails userDetails) {
    try {
      final SignedJWT signedJWT = SignedJWT.parse(token);
      final JWSVerifier verifier = new MACVerifier(secret.getBytes());

      if (!signedJWT.verify(verifier)) {
        return false;
      }

      final String username = signedJWT.getJWTClaimsSet().getSubject();
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    } catch (final ParseException | JOSEException e) {
      return false;
    }
  }
}
