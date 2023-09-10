package it.unimib.fipavonline.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.fipavonline.data.repository.campionato.ICampionatoRepositoryWithLiveData;

/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the NewsViewModel class.
 */
public class NewsViewModelFactory implements ViewModelProvider.Factory {

    private final ICampionatoRepositoryWithLiveData iCampionatoRepositoryWithLiveData;

    public NewsViewModelFactory(ICampionatoRepositoryWithLiveData iCampionatoRepositoryWithLiveData) {
        this.iCampionatoRepositoryWithLiveData = iCampionatoRepositoryWithLiveData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsViewModel(iCampionatoRepositoryWithLiveData);
    }
}
