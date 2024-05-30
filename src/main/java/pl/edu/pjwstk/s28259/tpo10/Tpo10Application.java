package pl.edu.pjwstk.s28259.tpo10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.edu.pjwstk.s28259.tpo10.repository.LinkRepository;
import pl.edu.pjwstk.s28259.tpo10.validation.StaticPasswordValidator;

@SpringBootApplication
public class Tpo10Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
		SpringApplication.run(Tpo10Application.class, args);
//		passwordValidationTest();
		dbTest(context);
	}

	public static void dbTest(ConfigurableApplicationContext context) {
		LinkRepository linkRepository = context.getBean(LinkRepository.class);
		System.out.println("All urls in db at startup:");
		linkRepository.findAll().forEach(System.out::println);
	}
	public static void passwordValidationTest() {
		String[] testPasswords = {
				"short",
				"noupper123!",
				"NOLOWER123!",
				"NoSpecial123",
				"inValid123!",
				"$Valid!Password!123!"
		};

		for (String password : testPasswords) {
			var errors = StaticPasswordValidator.getErrors(password);
			if (errors.isEmpty())
				System.out.println("Password: " + password + " | Valid: true\n");
			else
				System.out.println("Password: " + password + " | Valid: false | Error: \n"
						+ String.join("\n", errors) + "\n");
		}
	}
}
