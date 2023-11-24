package vn.iostar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import vn.iostar.contants.RoleName;
import vn.iostar.entity.Role;
import vn.iostar.service.RoleService;
import vn.iostar.service.impl.RoleServiceImpl;

import java.util.Optional;

@SpringBootApplication
public class UtealoApplication {
	public static void main(String[] args) {
		SpringApplication.run(UtealoApplication.class, args);
	}

}
