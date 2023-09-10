package it.unimib.fipavonline.data.repository.news;

import java.util.List;

import it.unimib.fipavonline.model.News;

/**
 * Interface to send data from Repositories that implement
 * INewsRepository interface to Activity/Fragment.
 */
public interface NewsResponseCallback {
    void onSuccess(List<News> newsList, long lastUpdate);
    void onFailure(String errorMessage);
    void onNewsFavoriteStatusChanged(News news);
}
