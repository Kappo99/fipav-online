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
 * Repository class to get the news from local or from a remote source.
 */
public class CampionatoRepositoryWithLiveData implements INewsRepositoryWithLiveData, CampionatoCallback {

    private static final String TAG = CampionatoRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allNewsMutableLiveData;
    private final MutableLiveData<Result> favoriteNewsMutableLiveData;
    private final BaseCampionatoRemoteDataSource newsRemoteDataSource;
    private final BaseCampionatoLocalDataSource newsLocalDataSource;
    private final BaseFavoriteCampionatoDataSource backupDataSource;

    public CampionatoRepositoryWithLiveData(BaseCampionatoRemoteDataSource newsRemoteDataSource,
                                            BaseCampionatoLocalDataSource newsLocalDataSource,
                                            BaseFavoriteCampionatoDataSource favoriteNewsDataSource) {

        allNewsMutableLiveData = new MutableLiveData<>();
        favoriteNewsMutableLiveData = new MutableLiveData<>();
        this.newsRemoteDataSource = newsRemoteDataSource;
        this.newsLocalDataSource = newsLocalDataSource;
        this.backupDataSource = favoriteNewsDataSource;
        this.newsRemoteDataSource.setNewsCallback(this);
        this.newsLocalDataSource.setCampionatoCallback(this);
        this.backupDataSource.setCampionatoCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchNews(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            newsRemoteDataSource.getCampionato();
        } else {
            newsLocalDataSource.getCampionato();
        }
        return allNewsMutableLiveData;
    }

    public void fetchNews() {
        newsRemoteDataSource.getCampionato();
    }

    @Override
    public MutableLiveData<Result> getFavoriteNews(boolean isFirstLoading) {
        // The first time the user launches the app, check if she
        // has previously saved favorite news on the cloud
        if (isFirstLoading) {
            backupDataSource.getFavoriteCampionato();
        } else {
            newsLocalDataSource.getFavoriteCampionato();
        }
        return favoriteNewsMutableLiveData;
    }

    @Override
    public void updateNews(Campionato campionato) {
        newsLocalDataSource.updateCampionato(campionato);
        if (campionato.isFavorite()) {
            backupDataSource.addFavoriteCampionato(campionato);
        } else {
            backupDataSource.deleteFavoriteCampionato(campionato);
        }
    }

    @Override
    public void deleteFavoriteNews() {
        newsLocalDataSource.deleteFavoriteCampionato();
    }

    @Override
    public void onSuccessFromRemote(CampionatoApiResponse campionatoApiResponse, long lastUpdate) {
        newsLocalDataSource.insertCampionato(campionatoApiResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allNewsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(CampionatoApiResponse campionatoApiResponse) {
        if (allNewsMutableLiveData.getValue() != null && allNewsMutableLiveData.getValue().isSuccess()) {
            List<Campionato> campionatoList = ((Result.NewsResponseSuccess)allNewsMutableLiveData.getValue()).getData().getCampionatoList();
            campionatoList.addAll(campionatoApiResponse.getCampionatoList());
            campionatoApiResponse.setCampionatoList(campionatoList);
            Result.NewsResponseSuccess result = new Result.NewsResponseSuccess(campionatoApiResponse);
            allNewsMutableLiveData.postValue(result);
        } else {
            Result.NewsResponseSuccess result = new Result.NewsResponseSuccess(campionatoApiResponse);
            allNewsMutableLiveData.postValue(result);
        }
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allNewsMutableLiveData.postValue(resultError);
        favoriteNewsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onCampionatoFavoriteStatusChanged(Campionato campionato, List<Campionato> favoriteNews) {
        Result allNewsResult = allNewsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Campionato> oldAllNews = ((Result.NewsResponseSuccess)allNewsResult).getData().getCampionatoList();
            if (oldAllNews.contains(campionato)) {
                oldAllNews.set(oldAllNews.indexOf(campionato), campionato);
                allNewsMutableLiveData.postValue(allNewsResult);
            }
        }
        favoriteNewsMutableLiveData.postValue(new Result.NewsResponseSuccess(new CampionatoResponse(favoriteNews)));
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

        favoriteNewsMutableLiveData.postValue(new Result.NewsResponseSuccess(new CampionatoResponse(campionatoList)));
    }

    @Override
    public void onDeleteFavoriteCampionatoSuccess(List<Campionato> favoriteNews) {
        Result allNewsResult = allNewsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Campionato> oldAllNews = ((Result.NewsResponseSuccess)allNewsResult).getData().getCampionatoList();
            for (Campionato campionato : favoriteNews) {
                if (oldAllNews.contains(campionato)) {
                    oldAllNews.set(oldAllNews.indexOf(campionato), campionato);
                }
            }
            allNewsMutableLiveData.postValue(allNewsResult);
        }

        if (favoriteNewsMutableLiveData.getValue() != null &&
                favoriteNewsMutableLiveData.getValue().isSuccess()) {
            favoriteNews.clear();
            Result.NewsResponseSuccess result = new Result.NewsResponseSuccess(new CampionatoResponse(favoriteNews));
            favoriteNewsMutableLiveData.postValue(result);
        }

        backupDataSource.deleteAllFavoriteCampionato();
    }

    @Override
    public void onSuccessFromCloudReading(List<Campionato> campionatoList) {
        // Favorite news got from Realtime Database the first time
        if (campionatoList != null) {
            for (Campionato campionato : campionatoList) {
                campionato.setSynchronized(true);
            }
            newsLocalDataSource.insertCampionato(campionatoList);
            favoriteNewsMutableLiveData.postValue(new Result.NewsResponseSuccess(new CampionatoResponse(campionatoList)));
        }
    }

    @Override
    public void onSuccessFromCloudWriting(Campionato campionato) {
        if (campionato != null && !campionato.isFavorite()) {
            campionato.setSynchronized(false);
        }
        newsLocalDataSource.updateCampionato(campionato);
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
