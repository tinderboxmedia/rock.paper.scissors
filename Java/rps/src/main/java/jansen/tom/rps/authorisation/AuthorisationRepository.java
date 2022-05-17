package jansen.tom.rps.authorisation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorisationRepository extends JpaRepository<Authorisation, Long> {

    // ...

}
