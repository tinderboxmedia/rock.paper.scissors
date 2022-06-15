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

    public String generateToken(Account account) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, account.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Timestamp issuedAt = Timestamp.from(ZonedDateTime.now().toInstant());
        Timestamp expiration = Timestamp.from(issuedAt.toInstant().plus(JWT_EXPIRATION_TIME, ChronoUnit.MINUTES));
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(issuedAt).setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_TOKEN).compact();
    }

    public boolean validateToken(String token, Account account) {
        return extractUsername(token).equals(account.getEmail());
    }

}
