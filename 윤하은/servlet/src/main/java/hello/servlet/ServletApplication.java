package hello.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ServletComponentScan//서블릿 자동 등록
@SpringBootApplication
public class ServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}

	/*
	@Bean
	ViewResolver internalResourceViewResolver(){
		return new InternalResourceViewResolver("/WEB-INF/views", ".jsp");
	}*/
	//이 코드를 작성하지 않아도 스프링부트가 알아서 application.properties를 보고 prefix와 suffix를 찾아서
	//InternalResourceViewResolver라는 뷰리졸버를 등록해줌

}
