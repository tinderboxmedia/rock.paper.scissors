package jansen.tom.rps.authentication;

import jansen.tom.rps.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    List<Authentication> findByAccountOrderByIdDesc(Account account);
    List<Authentication> findByCreationTimeGreaterThanEqual(Timestamp time);
    Optional<Authentication> findByToken(UUID token);
    boolean existsByToken(UUID token);
}
