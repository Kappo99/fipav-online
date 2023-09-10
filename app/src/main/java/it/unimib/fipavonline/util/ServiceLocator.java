package it.unimib.fipavonline.util;

import static it.unimib.fipavonline.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.ID_TOKEN;

import android.app.Application;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.data.database.FipavOnlineRoomDatabase;
import it.unimib.fipavonline.data.repository.campionato.ICampionatoRepositoryWithLiveData;
import it.unimib.fipavonline.data.repository.campionato.CampionatoRepositoryWithLiveData;
import it.unimib.fipavonline.data.repository.user.IUserRepository;
import it.unimib.fipavonline.data.repository.user.UserRepository;
import it.unimib.fipavonline.data.service.CampionatoApiService;
import it.unimib.fipavonline.data.source.campionato.BaseFavoriteCampionatoDataSource;
import it.unimib.fipavonline.data.source.campionato.BaseCampionatoLocalDataSource;
import it.unimib.fipavonline.data.source.campionato.BaseCampionatoRemoteDataSource;
import it.unimib.fipavonline.data.source.campionato.FavoriteCampionatoDataSource;
import it.unimib.fipavonline.data.source.campionato.CampionatoLocalDataSource;
import it.unimib.fipavonline.data.source.campionato.CampionatoMockRemoteDataSource;
import it.unimib.fipavonline.data.source.campionato.CampionatoRemoteDataSource;
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
     * Returns an instance of CampionatoApiService class using Retrofit.
     * @return an instance of CampionatoApiService.
     */
    public CampionatoApiService getCampionatoApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.FIPAV_ONLINE_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(CampionatoApiService.class);
    }

    /**
     * Returns an instance of FipavOnlineRoomDatabase class to manage Room database.
     * @param application Param for accessing the global application state.
     * @return An instance of FipavOnlineRoomDatabase.
     */
    public FipavOnlineRoomDatabase getFipavOnlineDao(Application application) {
        return FipavOnlineRoomDatabase.getDatabase(application);
    }

    /**
     * Returns an instance of ICampionatoRepositoryWithLiveData.
     * @param application Param for accessing the global application state.
     * @param debugMode Param to establish if the application is run in debug mode.
     * @return An instance of ICampionatoRepositoryWithLiveData.
     */
    public ICampionatoRepositoryWithLiveData getNewsRepository(Application application, boolean debugMode) {
        BaseCampionatoRemoteDataSource newsRemoteDataSource;
        BaseCampionatoLocalDataSource newsLocalDataSource;
        BaseFavoriteCampionatoDataSource favoriteNewsDataSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        if (debugMode) {
            CampionatoJSONParserUtil campionatoJsonParserUtil = new CampionatoJSONParserUtil(application);
            newsRemoteDataSource =
                    new CampionatoMockRemoteDataSource(campionatoJsonParserUtil, CampionatoJSONParserUtil.JsonParserType.GSON);
        } else {
            newsRemoteDataSource =
                    new CampionatoRemoteDataSource(application.getString(R.string.api_key));
        }

        newsLocalDataSource = new CampionatoLocalDataSource(getFipavOnlineDao(application),
                sharedPreferencesUtil, dataEncryptionUtil);

        try {
            favoriteNewsDataSource = new FavoriteCampionatoDataSource(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN
                    )
            );
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }

        return new CampionatoRepositoryWithLiveData(newsRemoteDataSource,
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

        BaseCampionatoLocalDataSource newsLocalDataSource =
                new CampionatoLocalDataSource(getFipavOnlineDao(application), sharedPreferencesUtil,
                        dataEncryptionUtil);

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, newsLocalDataSource);
    }
}
