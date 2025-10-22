package learning.journalapp.platform.security.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import learning.journalapp.platform.logging.StructuredLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT token provider using Nimbus JOSE JWT library. Handles JWT token generation, validation, and
 * parsing.
 *
 * <p>Configuration properties: - jwt.secret: Secret key for signing tokens (required) -
 * jwt.expiration: Token expiration time in milliseconds (default: 24 hours)
 */
@Component
public class JwtTokenProvider {

  private static final StructuredLogger logger = StructuredLogger.getLogger(JwtTokenProvider.class);

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}") // Default 24 hours (86400000 ms)
  private long jwtExpirationMs;

  /**
   * Generate JWT token for a username.
   *
   * @param username the username to include in the token
   * @return JWT token as a string
   */
  public String generateToken(final String username) {
    return generateToken(username, new HashMap<>());
  }

  /**
   * Generate JWT token with custom claims.
   *
   * @param username the username to include in the token
   * @param additionalClaims additional claims to include in the token
   * @return JWT token as a string
   */
  public String generateToken(final String username, final Map<String, Object> additionalClaims) {
    try {
      final Instant now = Instant.now();
      final Instant expiryDate = now.plusMillis(jwtExpirationMs);

      JWTClaimsSet.Builder claimsBuilder =
          new JWTClaimsSet.Builder()
              .subject(username)
              .issueTime(Date.from(now))
              .expirationTime(Date.from(expiryDate));

      // Add additional claims
      additionalClaims.forEach(claimsBuilder::claim);

      final JWTClaimsSet claimsSet = claimsBuilder.build();

      final SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);

      final JWSSigner signer = new MACSigner(jwtSecret.getBytes());
      signedJWT.sign(signer);

      return signedJWT.serialize();

    } catch (JOSEException e) {
      logger.error("Error generating JWT token", e);
      throw new RuntimeException("Error generating JWT token", e);
    }
  }

  /**
   * Extract username from JWT token.
   *
   * @param token the JWT token
   * @return username from the token's subject claim
   */
  public String getUsernameFromToken(final String token) {
    try {
      final SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      logger.error("Error parsing username from JWT token", e);
      throw new RuntimeException("Error parsing JWT token", e);
    }
  }

  /**
   * Get a custom claim from the JWT token.
   *
   * @param token the JWT token
   * @param claimName the name of the claim to retrieve
   * @return the claim value, or null if not found
   */
  public Object getClaimFromToken(final String token, final String claimName) {
    try {
      final SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getClaim(claimName);
    } catch (Exception e) {
      logger.error("Error extracting claim '{}' from JWT token", e, Map.of("claimName", claimName));
      return null;
    }
  }

  /**
   * Validate JWT token. Checks signature validity and expiration.
   *
   * @param token the JWT token to validate
   * @return true if token is valid, false otherwise
   */
  public boolean validateToken(final String token) {
    try {
      final SignedJWT signedJWT = SignedJWT.parse(token);

      // Verify signature
      final JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
      if (!signedJWT.verify(verifier)) {
        logger.warn("JWT signature validation failed");
        return false;
      }

      // Check expiration
      final Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime == null || expirationTime.before(new Date())) {
        logger.warn("JWT token is expired");
        return false;
      }

      return true;

    } catch (Exception e) {
      logger.error("JWT token validation failed", e);
      return false;
    }
  }

  /**
   * Get expiration time from token.
   *
   * @param token the JWT token
   * @return expiration date, or null if token is invalid
   */
  public Date getExpirationDateFromToken(final String token) {
    try {
      final SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getExpirationTime();
    } catch (Exception e) {
      logger.error("Error extracting expiration date from JWT token", e);
      return null;
    }
  }
}
