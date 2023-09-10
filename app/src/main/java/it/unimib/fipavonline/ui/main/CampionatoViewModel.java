package it.unimib.fipavonline.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.data.repository.campionato.ICampionatoRepositoryWithLiveData;

/**
 * ViewModel to manage the list of Campionato and the list of favorite Campionato.
 */
public class CampionatoViewModel extends ViewModel {

    private static final String TAG = CampionatoViewModel.class.getSimpleName();

    private final ICampionatoRepositoryWithLiveData campionatoRepositoryWithLiveData;
    private int page;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;
    private boolean firstLoading;
    private MutableLiveData<Result> campionatoListLiveData;
    private MutableLiveData<Result> favoriteCampionatoListLiveData;

    public CampionatoViewModel(ICampionatoRepositoryWithLiveData iCampionatoRepositoryWithLiveData) {
        this.campionatoRepositoryWithLiveData = iCampionatoRepositoryWithLiveData;
        this.page = 1;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    /**
     * Returns the LiveData object associated with the
     * campionato list to the Fragment/Activity.
     * @return The LiveData object associated with the campionato list.
     */
    public MutableLiveData<Result> getCampionato(long lastUpdate) {
        if (campionatoListLiveData == null) {
            fetchCampionato(lastUpdate);
        }
        return campionatoListLiveData;
    }

    /**
     * Returns the LiveData object associated with the
     * list of favorite campionato to the Fragment/Activity.
     * @return The LiveData object associated with the list of favorite campionato.
     */
    public MutableLiveData<Result> getFavoriteCampionatoLiveData(boolean isFirstLoading) {
        if (favoriteCampionatoListLiveData == null) {
            getFavoriteCampionato(isFirstLoading);
        }
        return favoriteCampionatoListLiveData;
    }

    /**
     * Updates the campionato status.
     * @param campionato The campionato to be updated.
     */
    public void updateCampionato(Campionato campionato) {
        campionatoRepositoryWithLiveData.updateCampionato(campionato);
    }

    public void fetchCampionato() {
        campionatoRepositoryWithLiveData.fetchCampionato();
    }

    /**
     * It uses the Repository to download the campionato list
     * and to associate it with the LiveData object.
     */
    private void fetchCampionato(long lastUpdate) {
        campionatoListLiveData = campionatoRepositoryWithLiveData.fetchCampionato(lastUpdate);
    }

    /**
     * It uses the Repository to get the list of favorite campionato
     * and to associate it with the LiveData object.
     */
    private void getFavoriteCampionato(boolean firstLoading) {
        favoriteCampionatoListLiveData = campionatoRepositoryWithLiveData.getFavoriteCampionato(firstLoading);
    }

    /**
     * Removes the campionato from the list of favorite campionato.
     * @param campionato The campionato to be removed from the list of favorite campionato.
     */
    public void removeFromFavorite(Campionato campionato) {
        campionatoRepositoryWithLiveData.updateCampionato(campionato);
    }

    /**
     * Clears the list of favorite campionato.
     */
    public void deleteAllFavoriteCampionato() {
        campionatoRepositoryWithLiveData.deleteFavoriteCampionato();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentResults() {
        return currentResults;
    }

    public void setCurrentResults(int currentResults) {
        this.currentResults = currentResults;
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

    public MutableLiveData<Result> getCampionatoResponseLiveData() {
        return campionatoListLiveData;
    }
}
