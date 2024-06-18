package smart.fixsimulator;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "smart.fixsimulator.dao")
@SpringBootApplication
public class SmartFixSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartFixSimulatorApplication.class, args);
	}

}
