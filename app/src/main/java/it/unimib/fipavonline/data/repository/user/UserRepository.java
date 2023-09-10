package it.unimib.fipavonline.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Set;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.model.User;
import it.unimib.fipavonline.data.source.campionato.BaseCampionatoLocalDataSource;
import it.unimib.fipavonline.data.source.campionato.CampionatoCallback;
import it.unimib.fipavonline.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.fipavonline.data.source.user.BaseUserDataRemoteDataSource;

/**
 * Repository class to get the user information.
 */
public class UserRepository implements IUserRepository, UserResponseCallback, CampionatoCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseCampionatoLocalDataSource campionatoLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userFavoriteNewsMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseCampionatoLocalDataSource campionatoLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.campionatoLocalDataSource = campionatoLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userFavoriteNewsMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.campionatoLocalDataSource.setCampionatoCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserFavoriteNews(String idToken) {
        userDataRemoteDataSource.getUserFavoriteNews(idToken);
        return userFavoriteNewsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        userDataRemoteDataSource.getUserPreferences(idToken);
        return userPreferencesMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        userDataRemoteDataSource.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Campionato> campionatoList) {
        campionatoLocalDataSource.insertCampionato(campionatoList);
    }

    @Override
    public void onSuccessFromGettingUserPreferences() {
        userPreferencesMutableLiveData.postValue(new Result.UserResponseSuccess(null));
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        campionatoLocalDataSource.deleteAll();
    }

    @Override
    public void onSuccessFromLocal(CampionatoApiResponse campionatoApiResponse) {
        Result.CampionatoResponseSuccess result = new Result.CampionatoResponseSuccess(campionatoApiResponse);
        userFavoriteNewsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessDeletion() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessSynchronization() {
        userFavoriteNewsMutableLiveData.postValue(new Result.CampionatoResponseSuccess(null));
    }

    @Override
    public void onSuccessFromRemote(CampionatoApiResponse campionatoApiResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onCampionatoFavoriteStatusChanged(Campionato campionato, List<Campionato> favoriteNews) {

    }

    @Override
    public void onCampionatoFavoriteStatusChanged(List<Campionato> campionatoes) {

    }

    @Override
    public void onDeleteFavoriteCampionatoSuccess(List<Campionato> favoriteNews) {

    }

    @Override
    public void onSuccessFromCloudReading(List<Campionato> campionatoList) {

    }

    @Override
    public void onSuccessFromCloudWriting(Campionato campionato) {

    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }
}
