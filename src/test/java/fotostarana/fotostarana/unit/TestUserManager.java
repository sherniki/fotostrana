package fotostarana.fotostarana.unit;

import java.util.Collection;

import static org.junit.Assert.*;

import org.junit.Test;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.AllUsers;
import fotostrana.ru.users.filtersUsers.FilterByProfiles;

public class TestUserManager {

	@Test
	public void testUserFilter() {
		User user1 = new User("user1");
		User user2 = new User("user2");
		UserManager.USER_MANAGER.addUser(user1);
		UserManager.USER_MANAGER.addUser(user2);
		Collection<User> users = UserManager.USER_MANAGER
				.getUsers(new AllUsers());
		assertEquals(users.size(), 2);
		assertTrue(users.contains(user1));
		assertTrue(users.contains(user2));

		Collection<User> findUser1 = UserManager.USER_MANAGER
				.getUsers(new FilterByProfiles("user1"));
		assertEquals(findUser1.size(), 1);
		assertTrue(findUser1.contains(user1));
	}
}
