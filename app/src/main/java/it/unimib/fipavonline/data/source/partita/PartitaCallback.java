package it.unimib.fipavonline.data.source.partita;

import java.util.List;

import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.PartitaApiResponse;

/**
 * Interface to send data from DataSource to Repositories
 * that implement IPartitaRepositoryWithLiveData interface.
 */
public interface PartitaCallback {
    void onSuccessFromRemote(PartitaApiResponse partitaApiResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(PartitaApiResponse partitaApiResponse);
    void onFailureFromLocal(Exception exception);
    void onPartitaFavoriteStatusChanged(Partita partita, List<Partita> favoritePartite);
    void onPartitaFavoriteStatusChanged(List<Partita> partite);
    void onDeleteFavoritePartitaSuccess(List<Partita> favoritePartite);
}
