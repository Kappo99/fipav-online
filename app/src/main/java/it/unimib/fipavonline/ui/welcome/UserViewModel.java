package it.unimib.fipavonline.ui.welcome;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Set;

import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.model.User;
import it.unimib.fipavonline.data.repository.user.IUserRepository;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userFavoriteCampionatoMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserFavoriteCampionatoMutableLiveData(String idToken) {
        if (userFavoriteCampionatoMutableLiveData == null) {
            getUserFavoriteCampionato(idToken);
        }
        return userFavoriteCampionatoMutableLiveData;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    private void getUserFavoriteCampionato(String idToken) {
        userFavoriteCampionatoMutableLiveData = userRepository.getUserFavoriteCampionato(idToken);
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }
}
