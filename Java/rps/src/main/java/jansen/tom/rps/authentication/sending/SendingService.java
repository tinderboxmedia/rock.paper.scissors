package jansen.tom.rps.authentication.sending;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendingService {

    public SendingService(UUID token,
                          String email,
                          String hash,
                          String link,
                          String access,
                          Integer expire,
                          Boolean development) {
        String tokenHash = String.join("-", token.toString(), hash);
        if(development) {
            System.out.println("Account [" + email + "] and " +
                    "authentication [" + tokenHash + "] are saved. Link is valid " +
                    "for " + expire + " minute" + ((expire == 1) ? "" : "s") + ".");
            throw new ResponseStatusException(HttpStatus.TEMPORARY_REDIRECT,
                    "The authentication service is in development mode.");
        } else {
            int timeout = 5000;
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofMillis(timeout))
                    .setReadTimeout(Duration.ofMillis(timeout))
                    .build();
            HttpHeaders header = new HttpHeaders();
            header.add("access-token", access);
            Map<String, String> body = new HashMap<>();
            body.put("authentication", tokenHash);
            body.put("destination", email);
            try {
                // Create the query that is needed to send authentication
                HttpEntity<Object> entity = new HttpEntity<>(body, header);
                restTemplate.exchange(link, HttpMethod.POST, entity, String.class);
            } catch(Exception error) {
                // We both send the exception to the log and update the end user
                Timestamp time = Timestamp.from(ZonedDateTime.now().toInstant());
                System.out.println("[" + time + "] " + error.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Something went wrong while sending you your magic link.");
            }
            throw new ResponseStatusException(HttpStatus.OK,
                    "If this is a valid email address we will send you your magic link.");
        }
    }

}
