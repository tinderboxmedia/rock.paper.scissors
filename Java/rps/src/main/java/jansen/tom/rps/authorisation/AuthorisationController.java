package jansen.tom.rps.authorisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api/authorisation")
public class AuthorisationController {

    @Autowired
    private AuthorisationService authorisationService;

}
