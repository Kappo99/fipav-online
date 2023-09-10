package it.unimib.fipavonline.data.source.campionato;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;

/**
 * Base class to get the user favorite news from a remote source.
 */
public abstract class BaseFavoriteNewsDataSource {

    protected NewsCallback newsCallback;

    public void setNewsCallback(NewsCallback newsCallback) {
        this.newsCallback = newsCallback;
    }

    public abstract void getFavoriteNews();
    public abstract void addFavoriteNews(Campionato campionato);
    public abstract void synchronizeFavoriteNews(List<Campionato> notSynchronizedCampionatoList);
    public abstract void deleteFavoriteNews(Campionato campionato);
    public abstract void deleteAllFavoriteNews();
}
