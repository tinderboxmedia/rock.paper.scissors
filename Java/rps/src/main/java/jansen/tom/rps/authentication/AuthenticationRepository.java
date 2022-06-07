package jansen.tom.rps.authentication;

import jansen.tom.rps.account.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {

    List<Authentication> findByStatusAndCreationTimeLessThan(Authentication.AuthenticationStatus status, Timestamp time);
    List<Authentication> findByCreationTimeGreaterThan(Timestamp time, Pageable pageable);

    Optional<Authentication> findFirstByAccountOrderByIdDesc(Account account);
    Optional<Authentication> findByToken(UUID token);

    boolean existsByAccountAndStatus(Account account, Authentication.AuthenticationStatus status);
    boolean existsByToken(UUID token);

}
