package it.unimib.fipavonline.util;

import static it.unimib.fipavonline.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.ID_TOKEN;

import android.app.Application;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.data.database.NewsRoomDatabase;
import it.unimib.fipavonline.data.repository.news.INewsRepositoryWithLiveData;
import it.unimib.fipavonline.data.repository.news.NewsRepositoryWithLiveData;
import it.unimib.fipavonline.data.repository.user.IUserRepository;
import it.unimib.fipavonline.data.repository.user.UserRepository;
import it.unimib.fipavonline.data.service.NewsApiService;
import it.unimib.fipavonline.data.source.news.BaseFavoriteNewsDataSource;
import it.unimib.fipavonline.data.source.news.BaseNewsLocalDataSource;
import it.unimib.fipavonline.data.source.news.BaseNewsRemoteDataSource;
import it.unimib.fipavonline.data.source.news.FavoriteNewsDataSource;
import it.unimib.fipavonline.data.source.news.NewsLocalDataSource;
import it.unimib.fipavonline.data.source.news.NewsMockRemoteDataSource;
import it.unimib.fipavonline.data.source.news.NewsRemoteDataSource;
import it.unimib.fipavonline.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.fipavonline.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.fipavonline.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.fipavonline.data.source.user.UserDataRemoteDataSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  Registry to provide the dependencies for the classes
 *  used in the application.
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Returns an instance of NewsApiService class using Retrofit.
     * @return an instance of NewsApiService.
     */
    public NewsApiService getNewsApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.NEWS_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(NewsApiService.class);
    }

    /**
     * Returns an instance of NewsRoomDatabase class to manage Room database.
     * @param application Param for accessing the global application state.
     * @return An instance of NewsRoomDatabase.
     */
    public NewsRoomDatabase getNewsDao(Application application) {
        return NewsRoomDatabase.getDatabase(application);
    }

    /**
     * Returns an instance of INewsRepositoryWithLiveData.
     * @param application Param for accessing the global application state.
     * @param debugMode Param to establish if the application is run in debug mode.
     * @return An instance of INewsRepositoryWithLiveData.
     */
    public INewsRepositoryWithLiveData getNewsRepository(Application application, boolean debugMode) {
        BaseNewsRemoteDataSource newsRemoteDataSource;
        BaseNewsLocalDataSource newsLocalDataSource;
        BaseFavoriteNewsDataSource favoriteNewsDataSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        if (debugMode) {
            CampionatoJSONParserUtil campionatoJsonParserUtil = new CampionatoJSONParserUtil(application);
            newsRemoteDataSource =
                    new NewsMockRemoteDataSource(campionatoJsonParserUtil, CampionatoJSONParserUtil.JsonParserType.GSON);
        } else {
            newsRemoteDataSource =
                    new NewsRemoteDataSource(application.getString(R.string.api_key));
        }

        newsLocalDataSource = new NewsLocalDataSource(getNewsDao(application),
                sharedPreferencesUtil, dataEncryptionUtil);

        try {
            favoriteNewsDataSource = new FavoriteNewsDataSource(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN
                    )
            );
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }

        return new NewsRepositoryWithLiveData(newsRemoteDataSource,
                newsLocalDataSource, favoriteNewsDataSource);
    }

    /**
     * Creates an instance of IUserRepository.
     * @return An instance of IUserRepository.
     */
    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource(sharedPreferencesUtil);

        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        BaseNewsLocalDataSource newsLocalDataSource =
                new NewsLocalDataSource(getNewsDao(application), sharedPreferencesUtil,
                        dataEncryptionUtil);

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, newsLocalDataSource);
    }
}
