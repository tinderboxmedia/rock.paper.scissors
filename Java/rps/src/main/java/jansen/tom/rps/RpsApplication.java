package jansen.tom.rps;

import jansen.tom.rps.account.role.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude={UserDetailsServiceAutoConfiguration.class})
public class RpsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpsApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleService roleService) {
		// Creates roles
		return args -> {
			roleService.saveRole("ADMIN");
			roleService.saveRole("USER");
		};
	}

}
