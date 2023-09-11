package it.unimib.fipavonline.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.fipavonline.data.repository.partita.IPartitaRepositoryWithLiveData;

/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the PartitaViewModel class.
 */
public class PartitaViewModelFactory implements ViewModelProvider.Factory {

    private final IPartitaRepositoryWithLiveData iPartitaRepositoryWithLiveData;

    public PartitaViewModelFactory(IPartitaRepositoryWithLiveData iPartitaRepositoryWithLiveData) {
        this.iPartitaRepositoryWithLiveData = iPartitaRepositoryWithLiveData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PartitaViewModel(iPartitaRepositoryWithLiveData);
    }
}
