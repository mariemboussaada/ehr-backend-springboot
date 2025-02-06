package com.pfe;

import org.springframework.boot.SpringApplication;

public class TestPfeApplication {

	public static void main(String[] args) {
		SpringApplication.from(PfeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
