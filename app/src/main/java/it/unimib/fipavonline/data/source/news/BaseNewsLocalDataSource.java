package it.unimib.fipavonline.data.source.news;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.NewsApiResponse;

/**
 * Base class to get news from a local source.
 */
public abstract class BaseNewsLocalDataSource {

    protected NewsCallback newsCallback;

    public void setNewsCallback(NewsCallback newsCallback) {
        this.newsCallback = newsCallback;
    }

    public abstract void getNews();
    public abstract void getFavoriteNews();
    public abstract void updateNews(Campionato campionato);
    public abstract void deleteFavoriteNews();
    public abstract void insertNews(NewsApiResponse newsApiResponse);
    public abstract void insertNews(List<Campionato> campionatoList);
    public abstract void deleteAll();
}
