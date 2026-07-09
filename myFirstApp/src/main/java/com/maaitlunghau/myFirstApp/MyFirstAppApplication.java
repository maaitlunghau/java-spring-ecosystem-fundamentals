package com.maaitlunghau.myFirstApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MyFirstAppApplication {

	public static void main(String[] args) {

		// NO IoC
		// Dev dev = new Dev();
		// dev.build();

		ApplicationContext context = SpringApplication.run(MyFirstAppApplication.class, args);

		Dev obj = context.getBean(Dev.class);
		obj.build();
		
		/**
		 * Other way to get bean: Dependency Injection (DI)
		 */
		// private final Dev dev;

		// public MyFirstAppApplicationDI(Dev dev) {
		// 	this.dev = dev;
		// }

		// @Override
		// public void run(String... args) {
		// 	dev.build();
		// }
	}
}
