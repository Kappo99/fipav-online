package it.unimib.fipavonline.data.source.partita;

import java.util.List;

import it.unimib.fipavonline.data.database.FipavOnlineRoomDatabase;
import it.unimib.fipavonline.data.database.PartitaDao;
import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.util.Constants;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Class to get Partita from local database using Room.
 */
public class PartitaLocalDataSource extends BasePartitaLocalDataSource {

    private final PartitaDao partitaDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public PartitaLocalDataSource(FipavOnlineRoomDatabase fipavOnlineRoomDatabase,
                                  SharedPreferencesUtil sharedPreferencesUtil) {
        this.partitaDao = fipavOnlineRoomDatabase.partitaDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    /**
     * Gets the Partita from the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getPartita() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            // TODO: Fix this instruction ?!
            PartitaApiResponse partitaApiResponse = new PartitaApiResponse();
            partitaApiResponse.setPartitaList(partitaDao.getAll());
            partitaCallback.onSuccessFromLocal(partitaApiResponse);
        });
    }

    /**
     * Saves the Partita in the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param partitaApiResponse the list of Partita to be written in the local database.
     */
    @Override
    public void insertPartita(PartitaApiResponse partitaApiResponse) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Partita> partitaList = partitaApiResponse.getPartitaList();

            if (partitaList != null) {
                // Inserisci i Partita nel Database
                partitaDao.insertPartitaList(partitaList);

                sharedPreferencesUtil.writeStringData(Constants.SHARED_PREFERENCES_FILE_NAME,
                        Constants.LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                partitaCallback.onSuccessFromLocal(partitaApiResponse);
            }
        });
    }
}
