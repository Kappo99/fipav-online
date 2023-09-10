package it.unimib.fipavonline.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.data.repository.campionato.ICampionatoRepositoryWithLiveData;

/**
 * ViewModel to manage the list of Campionato and the list of favorite Campionato.
 */
public class NewsViewModel extends ViewModel {

    private static final String TAG = NewsViewModel.class.getSimpleName();

    private final ICampionatoRepositoryWithLiveData newsRepositoryWithLiveData;
    private int page;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;
    private boolean firstLoading;
    private MutableLiveData<Result> newsListLiveData;
    private MutableLiveData<Result> favoriteNewsListLiveData;

    public NewsViewModel(ICampionatoRepositoryWithLiveData iCampionatoRepositoryWithLiveData) {
        this.newsRepositoryWithLiveData = iCampionatoRepositoryWithLiveData;
        this.page = 1;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    /**
     * Returns the LiveData object associated with the
     * news list to the Fragment/Activity.
     * @return The LiveData object associated with the news list.
     */
    public MutableLiveData<Result> getNews(long lastUpdate) {
        if (newsListLiveData == null) {
            fetchNews(lastUpdate);
        }
        return newsListLiveData;
    }

    /**
     * Returns the LiveData object associated with the
     * list of favorite news to the Fragment/Activity.
     * @return The LiveData object associated with the list of favorite news.
     */
    public MutableLiveData<Result> getFavoriteNewsLiveData(boolean isFirstLoading) {
        if (favoriteNewsListLiveData == null) {
            getFavoriteNews(isFirstLoading);
        }
        return favoriteNewsListLiveData;
    }

    /**
     * Updates the campionato status.
     * @param campionato The campionato to be updated.
     */
    public void updateNews(Campionato campionato) {
        newsRepositoryWithLiveData.updateCampionato(campionato);
    }

    public void fetchNews() {
        newsRepositoryWithLiveData.fetchCampionato();
    }

    /**
     * It uses the Repository to download the news list
     * and to associate it with the LiveData object.
     */
    private void fetchNews(long lastUpdate) {
        newsListLiveData = newsRepositoryWithLiveData.fetchCampionato(lastUpdate);
    }

    /**
     * It uses the Repository to get the list of favorite news
     * and to associate it with the LiveData object.
     */
    private void getFavoriteNews(boolean firstLoading) {
        favoriteNewsListLiveData = newsRepositoryWithLiveData.getFavoriteCampionato(firstLoading);
    }

    /**
     * Removes the campionato from the list of favorite campionato.
     * @param campionato The campionato to be removed from the list of favorite campionato.
     */
    public void removeFromFavorite(Campionato campionato) {
        newsRepositoryWithLiveData.updateCampionato(campionato);
    }

    /**
     * Clears the list of favorite news.
     */
    public void deleteAllFavoriteNews() {
        newsRepositoryWithLiveData.deleteFavoriteCampionato();
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

    public MutableLiveData<Result> getNewsResponseLiveData() {
        return newsListLiveData;
    }
}
