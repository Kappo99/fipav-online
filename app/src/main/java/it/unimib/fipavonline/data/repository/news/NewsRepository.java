package it.unimib.fipavonline.data.repository.news;

import static it.unimib.fipavonline.util.Constants.FRESH_TIMEOUT;
import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.data.database.FipavOnlineRoomDatabase;
import it.unimib.fipavonline.data.database.NewsDao;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.NewsApiResponse;
import it.unimib.fipavonline.data.service.NewsApiService;
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
    private final NewsApiService newsApiService;
    private final NewsDao newsDao;
    private final NewsResponseCallback newsResponseCallback;

    public NewsRepository(Application application, NewsResponseCallback newsResponseCallback) {
        this.application = application;
        this.newsApiService = ServiceLocator.getInstance().getNewsApiService();
        FipavOnlineRoomDatabase fipavOnlineRoomDatabase = ServiceLocator.getInstance().getNewsDao(application);
        this.newsDao = fipavOnlineRoomDatabase.newsDao();
        this.newsResponseCallback = newsResponseCallback;
    }

    @Override
    public void fetchNews(String country, int page, long lastUpdate) {

        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Call<NewsApiResponse> newsResponseCall = newsApiService.getNews(country,
                    TOP_HEADLINES_PAGE_SIZE_VALUE, page, application.getString(R.string.api_key));

            newsResponseCall.enqueue(new Callback<NewsApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<NewsApiResponse> call,
                                       @NonNull Response<NewsApiResponse> response) {

                    if (response.body() != null && response.isSuccessful() &&
                            !response.body().getStatus().equals("error")) {
                        List<Campionato> campionatoList = response.body().getNewsList();
                        saveDataInDatabase(campionatoList);
                    } else {
                        newsResponseCallback.onFailure(application.getString(R.string.error_retrieving_news));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<NewsApiResponse> call, @NonNull Throwable t) {
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
            List<Campionato> favoriteNews = newsDao.getFavoriteNews();
            for (Campionato campionato : favoriteNews) {
                campionato.setFavorite(false);
            }
            newsDao.updateListFavoriteNews(favoriteNews);
            newsResponseCallback.onSuccess(newsDao.getFavoriteNews(), System.currentTimeMillis());
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
            newsDao.updateSingleFavoriteNews(campionato);
            newsResponseCallback.onNewsFavoriteStatusChanged(campionato);
        });
    }

    /**
     * Gets the list of favorite news from the local database.
     */
    @Override
    public void getFavoriteNews() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            newsResponseCallback.onSuccess(newsDao.getFavoriteNews(), System.currentTimeMillis());
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
            List<Campionato> allNews = newsDao.getAll();

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
            List<Long> insertedNewsIds = newsDao.insertNewsList(campionatoList);
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
            newsResponseCallback.onSuccess(newsDao.getAll(), lastUpdate);
        });
    }
}
