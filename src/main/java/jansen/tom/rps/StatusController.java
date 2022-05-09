package jansen.tom.rps;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class StatusController {

    @RequestMapping(value = "")
    public String status() {
        String token = UUID.randomUUID().toString();
        return LocalDateTime.now() + " " + token;
    }

}
