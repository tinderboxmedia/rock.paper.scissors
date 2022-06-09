package jansen.tom.rps;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
public class StatusController {

    public static class Status {

        private final Timestamp timestamp;
        private final UUID session;

        public Status() {
            this.timestamp = Timestamp.from(ZonedDateTime.now().toInstant());
            this.session = UUID.randomUUID();
        }

        public Timestamp getTimestamp() {
            return timestamp;
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

