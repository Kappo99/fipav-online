package it.unimib.fipavonline.data.source.user;

import java.util.Set;

import it.unimib.fipavonline.model.User;
import it.unimib.fipavonline.data.repository.user.UserResponseCallback;

/**
 * Base class to get the user data from a remote source.
 */
public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void getUserFavoriteCampionato(String idToken);
}
