package jansen.tom.rps.account;

import jansen.tom.rps.account.hashing.TokenHash;
import jansen.tom.rps.authentication.Authentication;
import jansen.tom.rps.authentication.AuthenticationRepository;
import jansen.tom.rps.authentication.sending.SendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@PropertySource({
        "classpath:authentication.properties",
        "classpath:application.properties"
})
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Value("${authentication.account.limit}")
    public Integer AUTH_ACCOUNT_REQUEST_LIMIT;

    @Value("${authentication.system.limit}")
    public Integer AUTH_SYSTEM_REQUEST_LIMIT;

    @Value("${authentication.system.time}")
    public Integer AUTH_SYSTEM_REQUEST_TIME;

    @Value("${authentication.expiration.time}")
    public Integer AUTH_LINK_EXPIRATION_TIME;

    @Value("${account.authentication.salt}")
    public String AUTHENTICATION_LINK_SALT;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Account checkAccount(Account account) {
        String email = account.getEmail();
        Optional<Account> foundAccount = accountRepository.findByEmailIgnoreCase(email);
        if(foundAccount.isPresent()) {
            Account oldAccount = foundAccount.get();
            if (oldAccount.getStatus() != Account.AccountStatus.LOCKED) {
                authenticateAccount(oldAccount);
            }
            throw new ResponseStatusException(HttpStatus.LOCKED,
                    "It seems like that this account may be locked.");
        }
        if(isValid(email)) {
            // Address length is valid
            if(email.length() <= 64) {
                Account newAccount = new Account(email.toLowerCase());
                authenticateAccount(accountRepository.save(newAccount));
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "The email address that you tried to use is too long.");
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "This input is unfortunately not a valid email address.");
    }

    private boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z\\d_+&*-]+(?:\\." + "[a-zA-Z\\d_+&*-]+)*@"
                + "(?:[a-zA-Z\\d-]+\\.)+[a-z" + "A-Z]{2,15}$";
        if(email != null && !email.equals("")) {
            Pattern pattern = Pattern.compile(emailRegex);
            return pattern.matcher(email).matches();
        }
        // None valid
        return false;
    }

    private void authenticateAccount(Account account) {
        if(!accountMayAuthenticate(account)) {
            Integer authLimit = AUTH_ACCOUNT_REQUEST_LIMIT;
            String stringSuffix = (authLimit == 1) ? "" : "s";
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "You can send one authentication request every " + authLimit + " minute" + stringSuffix + ".");
        }
        if(!systemMayAuthenticate()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "The authentication service is overloaded and not reachable.");
        }
        String email = account.getEmail();
        String salt = AUTHENTICATION_LINK_SALT;
        Authentication newAuthentication = new Authentication(account, uniqueToken());
        new SendingService(authenticationRepository.save(newAuthentication).getToken(),
                email, TokenHash.getHash(email, salt), AUTH_LINK_EXPIRATION_TIME);
    }

    private boolean systemMayAuthenticate() {
        // AUTH_SYSTEM_REQUEST_LIMIT is allowed but no more
        int systemOverloads = AUTH_SYSTEM_REQUEST_LIMIT + 1;
        Timestamp compareTime = Timestamp.from(ZonedDateTime.now().toInstant()
                .minus(AUTH_SYSTEM_REQUEST_TIME, ChronoUnit.MINUTES));
        List<Authentication> limitedList = authenticationRepository.findByCreationTimeGreaterThan(
                compareTime, PageRequest.of(0, systemOverloads));
        return limitedList.size() < systemOverloads;
    }

    private boolean accountMayAuthenticate(Account account) {
        Optional<Authentication> lastEntry = authenticationRepository.findFirstByAccountOrderByIdDesc(account);
        if(lastEntry.isPresent()) {
            // Check if an account did not reach their auth request limits
            Timestamp authCreationTime = lastEntry.get().getCreationTime();
            Timestamp currentTimestamp = Timestamp.from(ZonedDateTime.now().toInstant()
                    .minus(AUTH_ACCOUNT_REQUEST_LIMIT, ChronoUnit.MINUTES));
            return authCreationTime.compareTo(currentTimestamp) < 0;
        }
        return true;
    }

    private UUID uniqueToken() {
        // Check for possible duplicate
        UUID token = UUID.randomUUID();
        if(authenticationRepository.existsByToken(token)) {
            uniqueToken();
        }
        return token;
    }

}
