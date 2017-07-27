package app.service;

import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;

import java.util.Collections;

public class DummyUserService implements UserService {
    @Override
    public User authenticate(final String userName, final String password) throws UnauthorizedException {
        if (!"joe@example.com".equals(userName) || !"password1".equals(password)) {
            throw new UnauthorizedException();
        }

        final User user = new User();
        user.setName(userName);
        user.setRoles(Collections.singleton("user"));
        return user;
    }

    @Override
    public User getUser(final String userName) throws EntityNotFoundException {
        if (!"joe@example.com".equals(userName)) {
            throw new EntityNotFoundException();
        }

        final User user = new User();
        user.setName(userName);
        user.setRoles(Collections.singleton("user"));
        return user;
    }

    @Override
    public User getAnonymousUser() {
        final User user = new User();
        user.setRoles(Collections.emptySet());
        user.setName("anonymous");
        return user;
    }
}
