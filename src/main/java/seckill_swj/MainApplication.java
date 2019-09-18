package seckill_swj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
public class MainApplication {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MainApplication.class, args); 
	}
}
/* 打war包需要更改
@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer{
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MainApplication.class, args); 
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MainApplication.class);
	}
}
*/
