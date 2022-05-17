package jansen.tom.rps.authorisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthorisationService {

    @Autowired
    AuthorisationRepository authorisationRepository;

    public void accountAuthorisation(Authorisation authorisation) {

        // ...
        System.out.println("accountAuthorisation(" + authorisation+ ") in Service.");
        authorisationRepository.save(authorisation);
        throw new ResponseStatusException(HttpStatus.OK,
                "We have to manage the authorisation.");

    }

}
