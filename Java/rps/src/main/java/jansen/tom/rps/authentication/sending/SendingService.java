package jansen.tom.rps.authentication.sending;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class SendingService {

    public SendingService(UUID token, String email, Integer expire) {

        // Placeholder until we can really send auth links
        System.out.println("Account [" + email + "] and " +
                "authentication [" + token + "] are saved. Link is valid " +
                "for " + expire + " minute" + ((expire == 1) ? "" : "s") + ".");
        // Only if the sending itself is also successful
        throw new ResponseStatusException(HttpStatus.OK,
                "If this is a valid email address we have send you a magic link.");

    }

}
