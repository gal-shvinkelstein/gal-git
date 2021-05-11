package my_game_lobby.server_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
//@ComponentScan({"my_game_lobby.server_spring.BackupClientList"})
////@ComponentScan({"my_game_lobby.server_spring.CommandLineAppStartupRunner"})
//@EntityScan("my_game_lobby.server_spring.ClientData")
//@EnableJpaRepositories("my_game_lobby.server_spring.clientRep")
public class ServerSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run (ServerSpringApplication.class, args);
	}
}


