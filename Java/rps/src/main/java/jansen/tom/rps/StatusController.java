package jansen.tom.rps;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class StatusController {

    public static class Status {

        private final Instant dateTime;
        private final UUID session;

        public Status() {
            this.dateTime = Instant.now();
            this.session = UUID.randomUUID();
        }

        public Instant getDateTime() {
            return dateTime;
        }

        public UUID getSession() {
            return session;
        }

    }

    @RequestMapping(value = "")
    public Status getStatus() {
        // Object will be json
        return new Status();
    }

}

