package app.service;

import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;

public interface UserService {

    User authenticate(String userName, String password) throws UnauthorizedException;

    User getUser(String userName) throws EntityNotFoundException;

    User getAnonymousUser();
}
