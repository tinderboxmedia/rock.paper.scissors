package jansen.tom.rps.authentication;

import jansen.tom.rps.account.Account;
import jansen.tom.rps.account.AccountRepository;
import jansen.tom.rps.authorisation.Authorisation;
import jansen.tom.rps.authorisation.AuthorisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@PropertySource("classpath:authentication.properties")
public class AuthenticationService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthorisationService authorisationService;

    @Autowired
    AccountRepository accountRepository;

    @Value("${authentication.expiration.time}")
    public Integer AUTH_LINK_EXPIRATION_TIME;

    public Authentication checkToken(UUID token) {
        Optional<Authentication> authentication = authenticationRepository.findByToken(token);
        // Check if token is a valid one
        if(authentication.isPresent()) {
            // From the authentication optional get account and get id
            Long accountId = authentication.get().getAccount().getId();
            Optional<Account> account = accountRepository.findById(accountId);
            // Token actually assigned
            if (account.isPresent()) {
                isUsedOrExpired(authentication.get(), account.get());
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Could not find the token or the associated account.");
    }

    private void isUsedOrExpired(Authentication authentication, Account account) {
        authentication = checkExpiredAndUpdate(authentication);
        if(authentication.getStatus() == Authentication.AuthenticationStatus.USED) {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED,
                    "This authentication link has already been used.");
        } else if (authentication.getStatus() == Authentication.AuthenticationStatus.EXPIRED) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT,
                    "This authentication link has unfortunately expired.");
        }
        // The token is not expired but is used hence we change status now
        authentication.setStatus(Authentication.AuthenticationStatus.USED);
        authenticationRepository.save(authentication);
        if(account.getStatus() == Account.AccountStatus.INACTIVE) {
            // If the account was still inactive we now verify
            account.setStatus(Account.AccountStatus.VERIFIED);
            accountRepository.save(account);
        }
        // As the authentication worked we now proceed to authorise account
        authorisationService.accountAuthorisation(new Authorisation(account));
    }

    // We also call this method on the scheduled annotation to keep db fresh
    public Authentication checkExpiredAndUpdate(Authentication authentication) {
        if(authentication.getStatus() == Authentication.AuthenticationStatus.AWAIT) {
            Timestamp currentTimestamp = Timestamp.from(ZonedDateTime.now().toInstant());
            Timestamp expireTime = Timestamp.from(authentication.getCreationTime().toInstant()
                    .plus(AUTH_LINK_EXPIRATION_TIME, ChronoUnit.MINUTES));
            if (currentTimestamp.compareTo(expireTime) > 0) {
                authentication.setStatus(Authentication.AuthenticationStatus.EXPIRED);
                return authenticationRepository.save(authentication);
            }
        }
        return authentication;
    }

}
