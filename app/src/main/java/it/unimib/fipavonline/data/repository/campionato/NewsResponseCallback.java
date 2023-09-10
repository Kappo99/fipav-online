package it.unimib.fipavonline.data.repository.campionato;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;

/**
 * Interface to send data from Repositories that implement
 * INewsRepository interface to Activity/Fragment.
 */
public interface NewsResponseCallback {
    void onSuccess(List<Campionato> campionatoList, long lastUpdate);
    void onFailure(String errorMessage);
    void onNewsFavoriteStatusChanged(Campionato campionato);
}
