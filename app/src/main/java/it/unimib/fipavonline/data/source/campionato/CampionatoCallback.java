package it.unimib.fipavonline.data.source.campionato;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.CampionatoApiResponse;

/**
 * Interface to send data from DataSource to Repositories
 * that implement ICampionatoRepositoryWithLiveData interface.
 */
public interface CampionatoCallback {
    void onSuccessFromRemote(CampionatoApiResponse campionatoApiResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(CampionatoApiResponse campionatoApiResponse);
    void onFailureFromLocal(Exception exception);
    void onCampionatoFavoriteStatusChanged(Campionato campionato, List<Campionato> favoriteNews);
    void onCampionatoFavoriteStatusChanged(List<Campionato> campionatoes);
    void onDeleteFavoriteCampionatoSuccess(List<Campionato> favoriteNews);
    void onSuccessFromCloudReading(List<Campionato> campionatoList);
    void onSuccessFromCloudWriting(Campionato campionato);
    void onFailureFromCloud(Exception exception);
    void onSuccessSynchronization();
    void onSuccessDeletion();
}
