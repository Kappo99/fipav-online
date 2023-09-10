package it.unimib.fipavonline.data.repository.campionato;

import it.unimib.fipavonline.model.Campionato;

/**
 * Interface for Repositories that manage Campionato objects.
 */
public interface INewsRepository {

    void fetchNews(String country, int page, long lastUpdate);

    void updateNews(Campionato campionato);

    void getFavoriteNews();

    void deleteFavoriteNews();
}
