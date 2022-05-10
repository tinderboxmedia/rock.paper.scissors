package jansen.tom.rps;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class StatusController {

    @RequestMapping(value = "")
    public String getStatus() {
        String uniTime = Instant.now().toString();
        String token = UUID.randomUUID().toString();
        return uniTime + " " + token;
    }

}
