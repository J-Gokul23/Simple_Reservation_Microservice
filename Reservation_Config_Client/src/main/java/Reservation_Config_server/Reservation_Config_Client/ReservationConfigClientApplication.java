package Reservation_Config_server.Reservation_Config_Client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ReservationConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationConfigClientApplication.class, args);
	}

}
