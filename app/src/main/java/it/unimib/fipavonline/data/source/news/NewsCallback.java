package it.unimib.fipavonline.data.source.news;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.NewsApiResponse;

/**
 * Interface to send data from DataSource to Repositories
 * that implement INewsRepositoryWithLiveData interface.
 */
public interface NewsCallback {
    void onSuccessFromRemote(NewsApiResponse newsApiResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(NewsApiResponse newsApiResponse);
    void onFailureFromLocal(Exception exception);
    void onNewsFavoriteStatusChanged(Campionato campionato, List<Campionato> favoriteNews);
    void onNewsFavoriteStatusChanged(List<Campionato> campionatoes);
    void onDeleteFavoriteNewsSuccess(List<Campionato> favoriteNews);
    void onSuccessFromCloudReading(List<Campionato> campionatoList);
    void onSuccessFromCloudWriting(Campionato campionato);
    void onFailureFromCloud(Exception exception);
    void onSuccessSynchronization();
    void onSuccessDeletion();
}
