package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
		Film film = Film.builder().name("sadas").build();
		System.out.println(film);
		User user = User.builder()
				.id(1)
				.login("Росомаха")
				.email("email@email.ru")
				.name("")
				.birthday(LocalDate.of(3000,5,5))
				.build();
		System.out.println(user);


		User user1 = user.toBuilder().name("Логан").build();
		System.out.println(user1);

		User user2 = user.toBuilder().name("    ").build();

		System.out.println(user2);

	}

}
