package it.unimib.fipavonline.data.repository.news;

import static it.unimib.fipavonline.util.Constants.NEWS_API_TEST_JSON_FILE;

import android.app.Application;

import java.io.IOException;
import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.data.database.FipavOnlineRoomDatabase;
import it.unimib.fipavonline.data.database.CampionatoDao;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.NewsApiResponse;
import it.unimib.fipavonline.util.CampionatoJSONParserUtil;
import it.unimib.fipavonline.util.ServiceLocator;

/**
 * Mock Repository that gets the news from the local JSON file newsapi-test.json,
 * that is saved in "assets" folder.
 */
public class NewsMockRepository implements INewsRepository {

    private final Application application;
    private final NewsResponseCallback newsResponseCallback;
    private final CampionatoDao campionatoDao;
    private final CampionatoJSONParserUtil.JsonParserType jsonParserType;

    public NewsMockRepository(Application application, NewsResponseCallback newsResponseCallback,
                              CampionatoJSONParserUtil.JsonParserType jsonParserType) {
        this.application = application;
        this.newsResponseCallback = newsResponseCallback;
        FipavOnlineRoomDatabase fipavOnlineRoomDatabase = ServiceLocator.getInstance().getNewsDao(application);
        this.campionatoDao = fipavOnlineRoomDatabase.newsDao();
        this.jsonParserType = jsonParserType;
    }

    @Override
    public void fetchNews(String country, int page, long lastUpdate) {

        NewsApiResponse newsApiResponse = null;
        CampionatoJSONParserUtil campionatoJsonParserUtil = new CampionatoJSONParserUtil(application);

        switch (jsonParserType) {
            case GSON:
                try {
                    newsApiResponse = campionatoJsonParserUtil.parseJSONFileWithGSon(NEWS_API_TEST_JSON_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case JSON_ERROR:
                newsResponseCallback.onFailure(application.getString(R.string.error_retrieving_news));
                break;
        }

        if (newsApiResponse != null) {
            saveDataInDatabase(newsApiResponse.getNewsList());
        } else {
            newsResponseCallback.onFailure(application.getString(R.string.error_retrieving_news));
        }
    }

    /**
     * Update the campionato changing the status of "favorite"
     * in the local database.
     * @param campionato The campionato to be updated.
     */
    @Override
    public void updateNews(Campionato campionato) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            campionatoDao.updateSingleFavoriteNews(campionato);
            newsResponseCallback.onNewsFavoriteStatusChanged(campionato);
        });
    }

    /**
     * Gets the list of favorite news from the local database.
     */
    @Override
    public void getFavoriteNews() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            newsResponseCallback.onSuccess(campionatoDao.getFavoriteNews(), System.currentTimeMillis());
        });
    }

    /**
     * Marks the favorite news as not favorite.
     */
    @Override
    public void deleteFavoriteNews() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> favoriteNews = campionatoDao.getFavoriteNews();
            for (Campionato campionato : favoriteNews) {
                campionato.setFavorite(false);
            }
            campionatoDao.updateListFavoriteNews(favoriteNews);
            newsResponseCallback.onSuccess(campionatoDao.getFavoriteNews(), System.currentTimeMillis());
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
            List<Long> insertedNewsIds = campionatoDao.insertNewsList(campionatoList);
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
