package jansen.tom.rps.authentication.schedule;

import jansen.tom.rps.account.Account;
import jansen.tom.rps.account.AccountRepository;
import jansen.tom.rps.authentication.Authentication;
import jansen.tom.rps.authentication.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@PropertySource("classpath:authentication.properties")
public class CleaningSchedule {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Value("${authentication.expiration.time}")
    public Integer AUTH_LINK_EXPIRATION_TIME;

    @Scheduled(fixedDelayString = "${cleaning.in.milliseconds}")
    public void TokensAndAccount() {
        Timestamp expireCheck = Timestamp.from(ZonedDateTime.now().toInstant()
                .minus(AUTH_LINK_EXPIRATION_TIME, ChronoUnit.MINUTES));
        List<Authentication> expiredList = authenticationRepository.findByStatusAndCreationTimeLessThan(
                Authentication.AuthenticationStatus.AWAIT, expireCheck);
        if(!expiredList.isEmpty()) {
            // Tokens that are expired are updated
            for(Authentication auth : expiredList) {
                auth.setStatus(Authentication.AuthenticationStatus.EXPIRED);
            }
            authenticationRepository.saveAll(expiredList);
        }
        // Get the base list of the accounts that are inactive
        List<Account> baseAccountList = accountRepository.findByStatus(Account.AccountStatus.INACTIVE);
        List<Account> uselessAccounts = new ArrayList<>();
        // Creates useless accounts list
        if(!baseAccountList.isEmpty()) {
            for(Account account : baseAccountList) {
                if(!authenticationRepository.existsByAccountAndStatus(account,
                        Authentication.AuthenticationStatus.AWAIT)) {
                    uselessAccounts.add(account);
                }
            }
        }
        // Cleaning up the database now
        if(!uselessAccounts.isEmpty()) {
            int uselessSize = uselessAccounts.size();
            accountRepository.deleteAll(uselessAccounts);
            Timestamp time = Timestamp.from(ZonedDateTime.now().toInstant());
            System.out.println("[" + time + "] Removed " + uselessSize + " account" +
                    ((uselessSize == 1) ? "" : "s") + " and their associated data.");
        }
    }

}
