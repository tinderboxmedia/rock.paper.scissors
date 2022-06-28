package jansen.tom.rps.authentication.sending;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@PropertySource("classpath:authentication.properties")
public class SendingService {

    @Value("${authentication.expiration.time}")
    private Integer AUTH_LINK_EXPIRATION_TIME;

    @Value("${sending.service.development}")
    private Boolean SENDING_SERVICE_MODES;

    @Value("${sending.service.access}")
    private String SENDING_ACCESS_TOKEN;

    @Value("${sending.service.url}")
    private String SENDING_ACCESS_URL;

    @Value("${sending.service.timeout}")
    private Integer NEW_SERVICE_TIMEOUT;

    public void sendAuthentication(UUID token, String email, String hash) {
        String tokenHash = String.join("-", token.toString(), hash);
        if(SENDING_SERVICE_MODES) {
            Integer expire = AUTH_LINK_EXPIRATION_TIME;
            System.out.println("Account [" + email + "] and " +
                    "authentication [" + tokenHash + "] are saved. Link is valid " +
                    "for " + expire + " minute" + ((expire == 1) ? "" : "s") + ".");
            throw new ResponseStatusException(HttpStatus.TEMPORARY_REDIRECT,
                    "The authentication service is in development mode.");
        } else {
            Integer timeout = NEW_SERVICE_TIMEOUT;
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofMillis(timeout))
                    .setReadTimeout(Duration.ofMillis(timeout))
                    .build();
            HttpHeaders header = new HttpHeaders();
            header.add("access-token", SENDING_ACCESS_TOKEN);
            Map<String, String> body = new HashMap<>();
            body.put("authentication", tokenHash);
            body.put("destination", email);
            try {
                // Create the query that is needed to send authentication
                HttpEntity<Object> entity = new HttpEntity<>(body, header);
                restTemplate.exchange(SENDING_ACCESS_URL, HttpMethod.POST, entity, String.class);
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
