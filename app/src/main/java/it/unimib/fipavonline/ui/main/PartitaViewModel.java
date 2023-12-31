package it.unimib.fipavonline.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.fipavonline.data.repository.partita.IPartitaRepositoryWithLiveData;
import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.Result;

/**
 * ViewModel to manage the list of Partita
 */
public class PartitaViewModel extends ViewModel {

    private static final String TAG = PartitaViewModel.class.getSimpleName();

    private final IPartitaRepositoryWithLiveData partitaRepositoryWithLiveData;
    private boolean isLoading;
    private boolean firstLoading;
    private MutableLiveData<Result> partitaListLiveData;

    public PartitaViewModel(IPartitaRepositoryWithLiveData iPartitaRepositoryWithLiveData) {
        this.partitaRepositoryWithLiveData = iPartitaRepositoryWithLiveData;
        this.firstLoading = true;
    }

    /**
     * Returns the LiveData object associated with the
     * partita list to the Fragment/Activity.
     * @return The LiveData object associated with the partita list.
     */
    public MutableLiveData<Result> getPartita(long lastUpdate) {
        if (partitaListLiveData == null) {
            fetchPartita(lastUpdate);
        }
        return partitaListLiveData;
    }

    public void fetchPartita() {
        partitaRepositoryWithLiveData.fetchPartita();
    }

    /**
     * It uses the Repository to download the partita list
     * and to associate it with the LiveData object.
     */
    private void fetchPartita(long lastUpdate) {
        partitaListLiveData = partitaRepositoryWithLiveData.fetchPartita(lastUpdate);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isFirstLoading() {
        return firstLoading;
    }

    public void setFirstLoading(boolean firstLoading) {
        this.firstLoading = firstLoading;
    }

    public MutableLiveData<Result> getPartitaResponseLiveData() {
        return partitaListLiveData;
    }

    public void resetPartitaResponseLiveData() {
        this.partitaListLiveData = null;
    }
}
