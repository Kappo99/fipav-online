package it.unimib.fipavonline.data.source.news;

import static it.unimib.fipavonline.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.LAST_UPDATE;
import static it.unimib.fipavonline.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.UNEXPECTED_ERROR;

import java.util.List;

import it.unimib.fipavonline.data.database.NewsDao;
import it.unimib.fipavonline.data.database.NewsRoomDatabase;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.NewsApiResponse;
import it.unimib.fipavonline.util.DataEncryptionUtil;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Class to get news from local database using Room.
 */
public class NewsLocalDataSource extends BaseNewsLocalDataSource {

    private final NewsDao newsDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;

    public NewsLocalDataSource(NewsRoomDatabase newsRoomDatabase,
                               SharedPreferencesUtil sharedPreferencesUtil,
                               DataEncryptionUtil dataEncryptionUtil
                               ) {
        this.newsDao = newsRoomDatabase.newsDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    /**
     * Gets the news from the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getNews() {
        NewsRoomDatabase.databaseWriteExecutor.execute(() -> {
            //TODO Fix this instruction
            NewsApiResponse newsApiResponse = new NewsApiResponse();
            newsApiResponse.setNewsList(newsDao.getAll());
            newsCallback.onSuccessFromLocal(newsApiResponse);
        });
    }

    @Override
    public void getFavoriteNews() {
        NewsRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> favoriteNews = newsDao.getFavoriteNews();
            newsCallback.onNewsFavoriteStatusChanged(favoriteNews);
        });
    }

    @Override
    public void updateNews(Campionato campionato) {
        NewsRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (campionato != null) {
                int rowUpdatedCounter = newsDao.updateSingleFavoriteNews(campionato);
                // It means that the update succeeded because only one row had to be updated
                if (rowUpdatedCounter == 1) {
                    Campionato updatedCampionato = newsDao.getNews(campionato.getId());
                    newsCallback.onNewsFavoriteStatusChanged(updatedCampionato, newsDao.getFavoriteNews());
                } else {
                    newsCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
                }
            } else {
                // When the user deleted all favorite campionato from remote
                //TODO Check if it works fine and there are not drawbacks
                List<Campionato> allNews = newsDao.getAll();
                for (Campionato n : allNews) {
                    n.setSynchronized(false);
                    newsDao.updateSingleFavoriteNews(n);
                }
            }
        });
    }

    @Override
    public void deleteFavoriteNews() {
        NewsRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> favoriteNews = newsDao.getFavoriteNews();
            for (Campionato campionato : favoriteNews) {
                campionato.setFavorite(false);
            }
            int updatedRowsNumber = newsDao.updateListFavoriteNews(favoriteNews);

            // It means that the update succeeded because the number of updated rows is
            // equal to the number of the original favorite news
            if (updatedRowsNumber == favoriteNews.size()) {
                newsCallback.onDeleteFavoriteNewsSuccess(favoriteNews);
            } else {
                newsCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    /**
     * Saves the news in the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param newsApiResponse the list of news to be written in the local database.
     */
    @Override
    public void insertNews(NewsApiResponse newsApiResponse) {
        NewsRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Campionato> allNews = newsDao.getAll();
            List<Campionato> campionatoList = newsApiResponse.getNewsList();

            if (campionatoList != null) {

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

                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                newsCallback.onSuccessFromLocal(newsApiResponse);
            }
        });
    }

    /**
     * Saves the news in the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param campionatoList the list of news to be written in the local database.
     */
    @Override
    public void insertNews(List<Campionato> campionatoList) {
        NewsRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (campionatoList != null) {

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
                        campionato.setSynchronized(true);
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

                NewsApiResponse newsApiResponse = new NewsApiResponse();
                newsApiResponse.setNewsList(campionatoList);
                newsCallback.onSuccessSynchronization();
            }
        });
    }

    @Override
    public void deleteAll() {
        NewsRoomDatabase.databaseWriteExecutor.execute(() -> {
            int newsCounter = newsDao.getAll().size();
            int newsDeletedNews = newsDao.deleteAll();

            // It means that everything has been deleted
            if (newsCounter == newsDeletedNews) {
                sharedPreferencesUtil.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                newsCallback.onSuccessDeletion();
            }
        });
    }
}
