package it.unimib.fipavonline.data.repository.campionato;

import androidx.lifecycle.MutableLiveData;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.Result;

public interface ICampionatoRepositoryWithLiveData {

    MutableLiveData<Result> fetchCampionato(long lastUpdate);

    void fetchCampionato();

    MutableLiveData<Result> getFavoriteCampionato(boolean firstLoading);

    void updateCampionato(Campionato campionato);

    void deleteFavoriteCampionato();
}
