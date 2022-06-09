package jansen.tom.rps.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
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
        String validHash;
        UUID validToken;
        try {
            String[] split = token.split("-");
            validToken = UUID.fromString(String.join("-",Arrays.copyOfRange(split, 0, 5)));
            validHash = split[5];
            if (validHash.length() != 64) {
                throw new Exception();
            }
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "This is unfortunately not a valid authentication link.");
        }
        return authenticationService.checkToken(validToken, validHash);
    }

}
