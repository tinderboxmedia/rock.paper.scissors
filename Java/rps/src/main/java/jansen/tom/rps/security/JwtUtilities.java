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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@PropertySource({
        "classpath:authentication.properties",
        "classpath:application.properties"
})
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

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET_TOKEN).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Timestamp.from(ZonedDateTime.now().toInstant()));
    }

    public String generateAccessToken(Account account) {
        Map<String, Object> claims = new HashMap<>();
        Timestamp expiration = Timestamp.from(
                ZonedDateTime.now().toInstant().plus(JWT_EXPIRATION_TIME, ChronoUnit.MINUTES)
        );
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .setSubject(account.getEmail())
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_TOKEN)
                .compact();
    }

    public String generateRefreshToken(Account account) {
        Timestamp expiration = Timestamp.from(
                ZonedDateTime.now().toInstant().plus(JWT_REFRESHING_TIME, ChronoUnit.DAYS)
        );
        return Jwts.builder()
                .setExpiration(expiration)
                .setSubject(account.getEmail())
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_TOKEN)
                .compact();
    }

    public boolean validateToken(String token, Account account) {
        return extractUsername(token).equals(account.getEmail());
    }

}
