package it.unimib.fipavonline.data.repository.news;

import androidx.lifecycle.MutableLiveData;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.Result;

public interface INewsRepositoryWithLiveData {

    MutableLiveData<Result> fetchNews(String country, int page, long lastUpdate);

    void fetchNews(String country, int page);

    MutableLiveData<Result> getFavoriteNews(boolean firstLoading);

    void updateNews(Campionato campionato);

    void deleteFavoriteNews();
}
