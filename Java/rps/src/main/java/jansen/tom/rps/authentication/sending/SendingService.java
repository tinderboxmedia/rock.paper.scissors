package jansen.tom.rps.authentication.sending;

import jansen.tom.rps.account.Account;
import jansen.tom.rps.authentication.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SendingService {

    public static void sendAuthentication(Account account, Authentication authentication) {

        // Placeholder until we can actually send out the authentication
        System.out.println("Account [" + account.getEmail() + "] and " +
                "authentication [" + authentication.getToken() + "] are saved.");
        // Only if the sending itself is also successful
        throw new ResponseStatusException(HttpStatus.OK,
                "Success message. We still need to get send feedback.");

    }

}
