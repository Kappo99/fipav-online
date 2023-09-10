package it.unimib.fipavonline.data.repository.partita;

import androidx.lifecycle.MutableLiveData;

import it.unimib.fipavonline.model.Result;

public interface IPartitaRepositoryWithLiveData {

    MutableLiveData<Result> fetchPartita(long lastUpdate);

    void fetchPartita();
}
