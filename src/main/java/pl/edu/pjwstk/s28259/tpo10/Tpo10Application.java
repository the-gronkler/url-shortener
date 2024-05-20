package pl.edu.pjwstk.s28259.tpo10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.edu.pjwstk.s28259.tpo10.repository.LinkRepository;

@SpringBootApplication
public class Tpo10Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
		SpringApplication.run(Tpo10Application.class, args);
		dbTest(context);
	}

	public static void dbTest(ConfigurableApplicationContext context) {
		LinkRepository linkRepository = context.getBean(LinkRepository.class);
		System.out.println("All urls in db at startup:");
		linkRepository.findAll().forEach(System.out::println);
	}

}
