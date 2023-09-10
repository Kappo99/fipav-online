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
import it.unimib.fipavonline.data.source.campionato.BaseFavoriteNewsDataSource;
import it.unimib.fipavonline.data.source.campionato.BaseNewsLocalDataSource;
import it.unimib.fipavonline.data.source.campionato.BaseNewsRemoteDataSource;
import it.unimib.fipavonline.data.source.campionato.NewsCallback;

/**
 * Repository class to get the news from local or from a remote source.
 */
public class NewsRepositoryWithLiveData implements INewsRepositoryWithLiveData, NewsCallback {

    private static final String TAG = NewsRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allNewsMutableLiveData;
    private final MutableLiveData<Result> favoriteNewsMutableLiveData;
    private final BaseNewsRemoteDataSource newsRemoteDataSource;
    private final BaseNewsLocalDataSource newsLocalDataSource;
    private final BaseFavoriteNewsDataSource backupDataSource;

    public NewsRepositoryWithLiveData(BaseNewsRemoteDataSource newsRemoteDataSource,
                                      BaseNewsLocalDataSource newsLocalDataSource,
                                      BaseFavoriteNewsDataSource favoriteNewsDataSource) {

        allNewsMutableLiveData = new MutableLiveData<>();
        favoriteNewsMutableLiveData = new MutableLiveData<>();
        this.newsRemoteDataSource = newsRemoteDataSource;
        this.newsLocalDataSource = newsLocalDataSource;
        this.backupDataSource = favoriteNewsDataSource;
        this.newsRemoteDataSource.setNewsCallback(this);
        this.newsLocalDataSource.setNewsCallback(this);
        this.backupDataSource.setNewsCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchNews(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            newsRemoteDataSource.getNews();
        } else {
            newsLocalDataSource.getNews();
        }
        return allNewsMutableLiveData;
    }

    public void fetchNews() {
        newsRemoteDataSource.getNews();
    }

    @Override
    public MutableLiveData<Result> getFavoriteNews(boolean isFirstLoading) {
        // The first time the user launches the app, check if she
        // has previously saved favorite news on the cloud
        if (isFirstLoading) {
            backupDataSource.getFavoriteNews();
        } else {
            newsLocalDataSource.getFavoriteNews();
        }
        return favoriteNewsMutableLiveData;
    }

    @Override
    public void updateNews(Campionato campionato) {
        newsLocalDataSource.updateNews(campionato);
        if (campionato.isFavorite()) {
            backupDataSource.addFavoriteNews(campionato);
        } else {
            backupDataSource.deleteFavoriteNews(campionato);
        }
    }

    @Override
    public void deleteFavoriteNews() {
        newsLocalDataSource.deleteFavoriteNews();
    }

    @Override
    public void onSuccessFromRemote(CampionatoApiResponse campionatoApiResponse, long lastUpdate) {
        newsLocalDataSource.insertNews(campionatoApiResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allNewsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(CampionatoApiResponse campionatoApiResponse) {
        if (allNewsMutableLiveData.getValue() != null && allNewsMutableLiveData.getValue().isSuccess()) {
            List<Campionato> campionatoList = ((Result.NewsResponseSuccess)allNewsMutableLiveData.getValue()).getData().getNewsList();
            campionatoList.addAll(campionatoApiResponse.getNewsList());
            campionatoApiResponse.setNewsList(campionatoList);
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
    public void onNewsFavoriteStatusChanged(Campionato campionato, List<Campionato> favoriteNews) {
        Result allNewsResult = allNewsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Campionato> oldAllNews = ((Result.NewsResponseSuccess)allNewsResult).getData().getNewsList();
            if (oldAllNews.contains(campionato)) {
                oldAllNews.set(oldAllNews.indexOf(campionato), campionato);
                allNewsMutableLiveData.postValue(allNewsResult);
            }
        }
        favoriteNewsMutableLiveData.postValue(new Result.NewsResponseSuccess(new CampionatoResponse(favoriteNews)));
    }

    @Override
    public void onNewsFavoriteStatusChanged(List<Campionato> campionatoList) {

        List<Campionato> notSynchronizedCampionatoList = new ArrayList<>();

        for (Campionato campionato : campionatoList) {
            if (!campionato.isSynchronized()) {
                notSynchronizedCampionatoList.add(campionato);
            }
        }

        if (!notSynchronizedCampionatoList.isEmpty()) {
            backupDataSource.synchronizeFavoriteNews(notSynchronizedCampionatoList);
        }

        favoriteNewsMutableLiveData.postValue(new Result.NewsResponseSuccess(new CampionatoResponse(campionatoList)));
    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Campionato> favoriteNews) {
        Result allNewsResult = allNewsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Campionato> oldAllNews = ((Result.NewsResponseSuccess)allNewsResult).getData().getNewsList();
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

        backupDataSource.deleteAllFavoriteNews();
    }

    @Override
    public void onSuccessFromCloudReading(List<Campionato> campionatoList) {
        // Favorite news got from Realtime Database the first time
        if (campionatoList != null) {
            for (Campionato campionato : campionatoList) {
                campionato.setSynchronized(true);
            }
            newsLocalDataSource.insertNews(campionatoList);
            favoriteNewsMutableLiveData.postValue(new Result.NewsResponseSuccess(new CampionatoResponse(campionatoList)));
        }
    }

    @Override
    public void onSuccessFromCloudWriting(Campionato campionato) {
        if (campionato != null && !campionato.isFavorite()) {
            campionato.setSynchronized(false);
        }
        newsLocalDataSource.updateNews(campionato);
        backupDataSource.getFavoriteNews();
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
