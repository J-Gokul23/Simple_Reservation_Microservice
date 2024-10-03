package Reservation_Eureka_Server.Reservation_Eureka_Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ReservationEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationEurekaServerApplication.class, args);
	}

}
