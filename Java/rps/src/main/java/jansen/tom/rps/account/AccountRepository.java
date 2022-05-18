package jansen.tom.rps.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByStatus(Account.AccountStatus status);
    Optional<Account> findByEmailIgnoreCase(String email);
}
