package it.unimib.fipavonline.data.repository.user;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(List<Campionato> campionatoList);
    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}
