package it.unimib.fipavonline.data.repository.campionato;

import static it.unimib.fipavonline.util.Constants.FRESH_TIMEOUT;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.model.CampionatoResponse;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.data.source.campionato.BaseFavoriteCampionatoDataSource;
import it.unimib.fipavonline.data.source.campionato.BaseCampionatoLocalDataSource;
import it.unimib.fipavonline.data.source.campionato.BaseCampionatoRemoteDataSource;
import it.unimib.fipavonline.data.source.campionato.CampionatoCallback;

/**
 * Repository class to get the campionato from local or from a remote source.
 */
public class CampionatoRepositoryWithLiveData implements ICampionatoRepositoryWithLiveData, CampionatoCallback {

    private static final String TAG = CampionatoRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allCampionatoMutableLiveData;
    private final MutableLiveData<Result> favoriteCampionatoMutableLiveData;
    private final BaseCampionatoRemoteDataSource campionatoRemoteDataSource;
    private final BaseCampionatoLocalDataSource campionatoLocalDataSource;
    private final BaseFavoriteCampionatoDataSource backupDataSource;

    public CampionatoRepositoryWithLiveData(BaseCampionatoRemoteDataSource campionatoRemoteDataSource,
                                            BaseCampionatoLocalDataSource campionatoLocalDataSource,
                                            BaseFavoriteCampionatoDataSource favoriteCampionatoDataSource) {

        allCampionatoMutableLiveData = new MutableLiveData<>();
        favoriteCampionatoMutableLiveData = new MutableLiveData<>();
        this.campionatoRemoteDataSource = campionatoRemoteDataSource;
        this.campionatoLocalDataSource = campionatoLocalDataSource;
        this.backupDataSource = favoriteCampionatoDataSource;
        this.campionatoRemoteDataSource.setCampionatoCallback(this);
        this.campionatoLocalDataSource.setCampionatoCallback(this);
        this.backupDataSource.setCampionatoCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchCampionato(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the campionato from the Web Service if the last download
        // of the campionato has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            campionatoRemoteDataSource.getCampionato();
        } else {
            campionatoLocalDataSource.getCampionato();
        }
        return allCampionatoMutableLiveData;
    }

    public void fetchCampionato() {
        campionatoRemoteDataSource.getCampionato();
    }

    @Override
    public MutableLiveData<Result> getFavoriteCampionato(boolean isFirstLoading) {
        // The first time the user launches the app, check if she
        // has previously saved favorite campionato on the cloud
        if (isFirstLoading) {
            backupDataSource.getFavoriteCampionato();
        } else {
            campionatoLocalDataSource.getFavoriteCampionato();
        }
        return favoriteCampionatoMutableLiveData;
    }

    @Override
    public void updateCampionato(Campionato campionato) {
        campionatoLocalDataSource.updateCampionato(campionato);
        if (campionato.isFavorite()) {
            backupDataSource.addFavoriteCampionato(campionato);
        } else {
            backupDataSource.deleteFavoriteCampionato(campionato);
        }
    }

    @Override
    public void deleteFavoriteCampionato() {
        campionatoLocalDataSource.deleteFavoriteCampionato();
    }

    @Override
    public void onSuccessFromRemote(CampionatoApiResponse campionatoApiResponse, long lastUpdate) {
        campionatoLocalDataSource.insertCampionato(campionatoApiResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allCampionatoMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(CampionatoApiResponse campionatoApiResponse) {
        if (allCampionatoMutableLiveData.getValue() != null && allCampionatoMutableLiveData.getValue().isSuccess()) {
            List<Campionato> campionatoList = ((Result.CampionatoResponseSuccess) allCampionatoMutableLiveData.getValue()).getData().getCampionatoList();
            campionatoList.addAll(campionatoApiResponse.getCampionatoList());
            campionatoApiResponse.setCampionatoList(campionatoList);
            Result.CampionatoResponseSuccess result = new Result.CampionatoResponseSuccess(campionatoApiResponse);
            allCampionatoMutableLiveData.postValue(result);
        } else {
            Result.CampionatoResponseSuccess result = new Result.CampionatoResponseSuccess(campionatoApiResponse);
            allCampionatoMutableLiveData.postValue(result);
        }
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allCampionatoMutableLiveData.postValue(resultError);
        favoriteCampionatoMutableLiveData.postValue(resultError);
    }

    @Override
    public void onCampionatoFavoriteStatusChanged(Campionato campionato, List<Campionato> favoriteCampionato) {
        Result allCampionatoResult = allCampionatoMutableLiveData.getValue();

        if (allCampionatoResult != null && allCampionatoResult.isSuccess()) {
            List<Campionato> oldAllCampionato = ((Result.CampionatoResponseSuccess)allCampionatoResult).getData().getCampionatoList();
            if (oldAllCampionato.contains(campionato)) {
                oldAllCampionato.set(oldAllCampionato.indexOf(campionato), campionato);
                allCampionatoMutableLiveData.postValue(allCampionatoResult);
            }
        }
        favoriteCampionatoMutableLiveData.postValue(new Result.CampionatoResponseSuccess(new CampionatoResponse(favoriteCampionato)));
    }

    @Override
    public void onCampionatoFavoriteStatusChanged(List<Campionato> campionatoList) {

        List<Campionato> notSynchronizedCampionatoList = new ArrayList<>();

        for (Campionato campionato : campionatoList) {
            if (!campionato.isSynchronized()) {
                notSynchronizedCampionatoList.add(campionato);
            }
        }

        if (!notSynchronizedCampionatoList.isEmpty()) {
            backupDataSource.synchronizeFavoriteCampionato(notSynchronizedCampionatoList);
        }

        favoriteCampionatoMutableLiveData.postValue(new Result.CampionatoResponseSuccess(new CampionatoResponse(campionatoList)));
    }

    @Override
    public void onDeleteFavoriteCampionatoSuccess(List<Campionato> favoriteCampionato) {
        Result allCampionatoResult = allCampionatoMutableLiveData.getValue();

        if (allCampionatoResult != null && allCampionatoResult.isSuccess()) {
            List<Campionato> oldAllCampionato = ((Result.CampionatoResponseSuccess)allCampionatoResult).getData().getCampionatoList();
            for (Campionato campionato : favoriteCampionato) {
                if (oldAllCampionato.contains(campionato)) {
                    oldAllCampionato.set(oldAllCampionato.indexOf(campionato), campionato);
                }
            }
            allCampionatoMutableLiveData.postValue(allCampionatoResult);
        }

        if (favoriteCampionatoMutableLiveData.getValue() != null &&
                favoriteCampionatoMutableLiveData.getValue().isSuccess()) {
            favoriteCampionato.clear();
            Result.CampionatoResponseSuccess result = new Result.CampionatoResponseSuccess(new CampionatoResponse(favoriteCampionato));
            favoriteCampionatoMutableLiveData.postValue(result);
        }

        backupDataSource.deleteAllFavoriteCampionato();
    }

    @Override
    public void onSuccessFromCloudReading(List<Campionato> campionatoList) {
        // Favorite campionato got from Realtime Database the first time
        if (campionatoList != null) {
            for (Campionato campionato : campionatoList) {
                campionato.setSynchronized(true);
            }
            campionatoLocalDataSource.insertCampionato(campionatoList);
            favoriteCampionatoMutableLiveData.postValue(new Result.CampionatoResponseSuccess(new CampionatoResponse(campionatoList)));
        }
    }

    @Override
    public void onSuccessFromCloudWriting(Campionato campionato) {
        if (campionato != null && !campionato.isFavorite()) {
            campionato.setSynchronized(false);
        }
        campionatoLocalDataSource.updateCampionato(campionato);
        backupDataSource.getFavoriteCampionato();
    }

    @Override
    public void onSuccessSynchronization() {
        Log.d(TAG, "Campionato synchronized from remote");
    }

    @Override
    public void onFailureFromCloud(Exception exception) {
    }

    @Override
    public void onSuccessDeletion() {

    }
}
