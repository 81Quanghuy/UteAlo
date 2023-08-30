package vn.iostar.utealo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"vn.iostar.entity"})
public class UtealoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtealoApplication.class, args);
	}

}
