package it.unimib.fipavonline.data.repository.campionato;

import androidx.lifecycle.MutableLiveData;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.Result;

public interface INewsRepositoryWithLiveData {

    MutableLiveData<Result> fetchNews(long lastUpdate);

    void fetchNews();

    MutableLiveData<Result> getFavoriteNews(boolean firstLoading);

    void updateNews(Campionato campionato);

    void deleteFavoriteNews();
}
