package it.unimib.fipavonline.data.repository.campionato;

import static it.unimib.fipavonline.util.Constants.FRESH_TIMEOUT;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.data.database.CampionatoDao;
import it.unimib.fipavonline.data.database.FipavOnlineRoomDatabase;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.data.service.CampionatoApiService;
import it.unimib.fipavonline.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository to get the news using the API
 * provided by NewsApi.org (https://newsapi.org).
 */
public class NewsRepository implements INewsRepository {

    private static final String TAG = NewsRepository.class.getSimpleName();

    private final Application application;
    private final CampionatoApiService campionatoApiService;
    private final CampionatoDao campionatoDao;
    private final NewsResponseCallback newsResponseCallback;

    public NewsRepository(Application application, NewsResponseCallback newsResponseCallback) {
        this.application = application;
        this.campionatoApiService = ServiceLocator.getInstance().getCampionatoApiService();
        FipavOnlineRoomDatabase fipavOnlineRoomDatabase = ServiceLocator.getInstance().getFipavOnlineDao(application);
        this.campionatoDao = fipavOnlineRoomDatabase.campionatoDao();
        this.newsResponseCallback = newsResponseCallback;
    }

    @Override
    public void fetchNews(String country, int page, long lastUpdate) {

        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Call<CampionatoApiResponse> newsResponseCall = campionatoApiService.getCampionato(
                    application.getString(R.string.api_key));

            newsResponseCall.enqueue(new Callback<CampionatoApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<CampionatoApiResponse> call,
                                       @NonNull Response<CampionatoApiResponse> response) {

                    if (response.body() != null && response.isSuccessful() &&
                            response.body().getStatus() == 200) {
                        List<Campionato> campionatoList = response.body().getCampionatoList();
                        saveDataInDatabase(campionatoList);
                    } else {
                        newsResponseCallback.onFailure(application.getString(R.string.error_retrieving_news));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CampionatoApiResponse> call, @NonNull Throwable t) {
                    newsResponseCallback.onFailure(t.getMessage());
                }
            });
        } else {
            Log.d(TAG, application.getString(R.string.data_read_from_local_database));
            readDataFromDatabase(lastUpdate);
        }
    }

    /**
     * Marks the favorite news as not favorite.
     */
    @Override
    public void deleteFavoriteNews() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> favoriteNews = campionatoDao.getFavoriteCampionato();
            for (Campionato campionato : favoriteNews) {
                campionato.setFavorite(false);
            }
            campionatoDao.updateListFavoriteCampionato(favoriteNews);
            newsResponseCallback.onSuccess(campionatoDao.getFavoriteCampionato(), System.currentTimeMillis());
        });
    }

    /**
     * Update the campionato changing the status of "favorite"
     * in the local database.
     * @param campionato The campionato to be updated.
     */
    @Override
    public void updateNews(Campionato campionato) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            campionatoDao.updateSingleFavoritecAMPIONATO(campionato);
            newsResponseCallback.onNewsFavoriteStatusChanged(campionato);
        });
    }

    /**
     * Gets the list of favorite news from the local database.
     */
    @Override
    public void getFavoriteNews() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            newsResponseCallback.onSuccess(campionatoDao.getFavoriteCampionato(), System.currentTimeMillis());
        });
    }

    /**
     * Saves the news in the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param campionatoList the list of news to be written in the local database.
     */
    private void saveDataInDatabase(List<Campionato> campionatoList) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Campionato> allNews = campionatoDao.getAll();

            // Checks if the news just downloaded has already been downloaded earlier
            // in order to preserve the news status (marked as favorite or not)
            for (Campionato campionato : allNews) {
                // This check works because Campionato and NewsSource classes have their own
                // implementation of equals(Object) and hashCode() methods
                if (campionatoList.contains(campionato)) {
                    // The primary key and the favorite status is contained only in the Campionato objects
                    // retrieved from the database, and not in the Campionato objects downloaded from the
                    // Web Service. If the same campionato was already downloaded earlier, the following
                    // line of code replaces the the Campionato object in campionatoList with the corresponding
                    // Campionato object saved in the database, so that it has the primary key and the
                    // favorite status.
                    campionatoList.set(campionatoList.indexOf(campionato), campionato);
                }
            }

            // Writes the news in the database and gets the associated primary keys
            List<Long> insertedNewsIds = campionatoDao.insertCampionatoList(campionatoList);
            for (int i = 0; i < campionatoList.size(); i++) {
                // Adds the primary key to the corresponding object Campionato just downloaded so that
                // if the user marks the news as favorite (and vice-versa), we can use its id
                // to know which news in the database must be marked as favorite/not favorite
                campionatoList.get(i).setId(insertedNewsIds.get(i));
            }

            newsResponseCallback.onSuccess(campionatoList, System.currentTimeMillis());
        });
    }

    /**
     * Gets the news from the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    private void readDataFromDatabase(long lastUpdate) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            newsResponseCallback.onSuccess(campionatoDao.getAll(), lastUpdate);
        });
    }
}
