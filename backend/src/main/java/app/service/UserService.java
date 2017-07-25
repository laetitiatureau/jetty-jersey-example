package app.service;

import app.data.User;

public interface UserService {

    User authenticate(String userName, String password);
}
