package jansen.tom.rps.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public Authentication checkAccount(@RequestBody String token) {
        // Improved scope
        UUID validToken;
        try {
            validToken = UUID.fromString(token);
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "This is unfortunately not a valid authentication link.");
        }
        return authenticationService.checkToken(validToken);
    }

}
