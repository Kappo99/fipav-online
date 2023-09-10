package it.unimib.fipavonline.data.source.campionato;

import static it.unimib.fipavonline.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.LAST_UPDATE;
import static it.unimib.fipavonline.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.UNEXPECTED_ERROR;

import java.util.List;

import it.unimib.fipavonline.data.database.FipavOnlineRoomDatabase;
import it.unimib.fipavonline.data.database.CampionatoDao;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.util.DataEncryptionUtil;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Class to get campionato from local database using Room.
 */
public class CampionatoLocalDataSource extends BaseCampionatoLocalDataSource {

    private final CampionatoDao campionatoDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;

    public CampionatoLocalDataSource(FipavOnlineRoomDatabase fipavOnlineRoomDatabase,
                                     SharedPreferencesUtil sharedPreferencesUtil,
                                     DataEncryptionUtil dataEncryptionUtil
                               ) {
        this.campionatoDao = fipavOnlineRoomDatabase.campionatoDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    /**
     * Gets the campionato from the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getCampionato() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            //TODO Fix this instruction
            CampionatoApiResponse campionatoApiResponse = new CampionatoApiResponse();
            campionatoApiResponse.setCampionatoList(campionatoDao.getAll());
            campionatoCallback.onSuccessFromLocal(campionatoApiResponse);
        });
    }

    @Override
    public void getFavoriteCampionato() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> favoriteCampionato = campionatoDao.getFavoriteCampionato();
            campionatoCallback.onCampionatoFavoriteStatusChanged(favoriteCampionato);
        });
    }

    @Override
    public void updateCampionato(Campionato campionato) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (campionato != null) {
                int rowUpdatedCounter = campionatoDao.updateSingleFavoritecAMPIONATO(campionato);
                // It means that the update succeeded because only one row had to be updated
                if (rowUpdatedCounter == 1) {
                    Campionato updatedCampionato = campionatoDao.getCampionato(campionato.getId());
                    campionatoCallback.onCampionatoFavoriteStatusChanged(updatedCampionato, campionatoDao.getFavoriteCampionato());
                } else {
                    campionatoCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
                }
            } else {
                // When the user deleted all favorite campionato from remote
                //TODO Check if it works fine and there are not drawbacks
                List<Campionato> allCampionato = campionatoDao.getAll();
                for (Campionato n : allCampionato) {
                    n.setSynchronized(false);
                    campionatoDao.updateSingleFavoritecAMPIONATO(n);
                }
            }
        });
    }

    @Override
    public void deleteFavoriteCampionato() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> favoriteCampionato = campionatoDao.getFavoriteCampionato();
            for (Campionato campionato : favoriteCampionato) {
                campionato.setFavorite(false);
            }
            int updatedRowsNumber = campionatoDao.updateListFavoriteCampionato(favoriteCampionato);

            // It means that the update succeeded because the number of updated rows is
            // equal to the number of the original favorite campionato
            if (updatedRowsNumber == favoriteCampionato.size()) {
                campionatoCallback.onDeleteFavoriteCampionatoSuccess(favoriteCampionato);
            } else {
                campionatoCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    /**
     * Saves the campionato in the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param campionatoApiResponse the list of campionato to be written in the local database.
     */
    @Override
    public void insertCampionato(CampionatoApiResponse campionatoApiResponse) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> allCampionato = campionatoDao.getAll();
            List<Campionato> campionatoList = campionatoApiResponse.getCampionatoList();

            if (campionatoList != null) {
                // Controlla se un Campionato è già stato scaricato prima per mantenere il suo
                // stato "preferito"
                for (Campionato campionato : allCampionato) {
                    if (campionatoList.contains(campionato)) {
                        // Campionato appena scaricati NON hanno lo stato "preferito"
                        // Se precedentemente ho già scaricato lo stesso (presente nel DB)
                        // lo sostituisco così da mantenere lo stato "preferito"
                        campionatoList.set(campionatoList.indexOf(campionato), campionato);
                    }
                }

                // Inserisci i Campionato nel Database
                campionatoDao.insertCampionatoList(campionatoList);

                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                campionatoCallback.onSuccessFromLocal(campionatoApiResponse);
            }
        });
    }

    /**
     * Saves the campionato in the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param campionatoList the list of campionato to be written in the local database.
     */
    @Override
    public void insertCampionato(List<Campionato> campionatoList) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Campionato> allCampionato = campionatoDao.getAll();

            if (campionatoList != null) {
                // Controlla se un Campionato è già stato scaricato prima per mantenere il suo
                // stato "preferito"
                for (Campionato campionato : allCampionato) {
                    if (campionatoList.contains(campionato)) {
                        // Campionato appena scaricati NON hanno lo stato "preferito"
                        // Se precedentemente ho già scaricato lo stesso (presente nel DB)
                        // lo sostituisco così da mantenere lo stato "preferito"
                        campionato.setSynchronized(true);
                        campionatoList.set(campionatoList.indexOf(campionato), campionato);
                    }
                }

                CampionatoApiResponse campionatoApiResponse = new CampionatoApiResponse();
                campionatoApiResponse.setCampionatoList(campionatoList);
                campionatoCallback.onSuccessSynchronization();
            }
        });
    }

    @Override
    public void deleteAll() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            int campionatoCounter = campionatoDao.getAll().size();
            int campionatoDeleted = campionatoDao.deleteAll();

            // It means that everything has been deleted
            if (campionatoCounter == campionatoDeleted) {
                sharedPreferencesUtil.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                campionatoCallback.onSuccessDeletion();
            }
        });
    }
}
