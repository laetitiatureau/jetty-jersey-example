package app.service;

import app.data.User;

import javax.ws.rs.NotAuthorizedException;
import java.util.Collections;

public class DummyUserService implements UserService {
    @Override
    public User authenticate(String userName, String password) {

        if (!"joe@example.com".equals(userName) || !"password1".equals(password)) {
            throw new NotAuthorizedException("user not authorized");
        }

        User user = new User();
        user.setName(userName);
        user.setVersion(0);
        user.setRoles(Collections.singleton("user"));
        return user;
    }
}
