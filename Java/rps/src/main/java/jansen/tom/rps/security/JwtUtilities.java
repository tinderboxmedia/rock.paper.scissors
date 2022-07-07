package jansen.tom.rps.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jansen.tom.rps.account.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Service
@PropertySource("classpath:authentication.properties")
public class JwtUtilities {

    @Value("${jwt.expiration.time}")
    private Integer JWT_EXPIRATION_TIME;

    @Value("${jwt.refreshing.time}")
    private Integer JWT_REFRESHING_TIME;

    @Value("${jwt.secret.token}")
    private String JWT_SECRET_TOKEN;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractAccess(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET_TOKEN).parseClaimsJws(token).getBody();
    }

    public String generateToken(Account account, Boolean refresh) {
        Timestamp expiration = Timestamp.from(
                ZonedDateTime.now().toInstant().plus(
                        (refresh) ? JWT_REFRESHING_TIME : JWT_EXPIRATION_TIME,
                        (refresh) ? ChronoUnit.DAYS : ChronoUnit.MINUTES)
        );
        return Jwts.builder()
                .setExpiration(expiration)
                .setSubject(account.getEmail())
                .setIssuer((refresh) ? "refresh" : "access")
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_TOKEN)
                .compact();
    }

    public boolean validateToken(String token, Account account, String type) {
        return (extractUsername(token).equals(account.getEmail()) && extractAccess(token).equals(type));
    }

}
