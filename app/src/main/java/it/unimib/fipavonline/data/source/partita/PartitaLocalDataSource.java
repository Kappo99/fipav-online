package it.unimib.fipavonline.data.source.partita;

import java.util.List;

import it.unimib.fipavonline.data.database.PartitaDao;
import it.unimib.fipavonline.data.database.FipavOnlineRoomDatabase;
import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.util.Constants;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Class to get partita from local database using Room.
 */
public class PartitaLocalDataSource extends BasePartitaLocalDataSource {

    private final PartitaDao partitaDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public PartitaLocalDataSource(FipavOnlineRoomDatabase fipavOnlineRoomDatabase,
                                  SharedPreferencesUtil sharedPreferencesUtil
                               ) {
        this.partitaDao = fipavOnlineRoomDatabase.partitaDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    /**
     * Gets the partita from the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getPartita() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            //TODO Fix this instruction
            PartitaApiResponse partitaApiResponse = new PartitaApiResponse();
            partitaApiResponse.setPartitaList(partitaDao.getAll());
            partitaCallback.onSuccessFromLocal(partitaApiResponse);
        });
    }

    /**
     * Saves the partita in the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param partitaApiResponse the list of partita to be written in the local database.
     */
    @Override
    public void insertPartita(PartitaApiResponse partitaApiResponse) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Partita> allPartita = partitaDao.getAll();
            List<Partita> partitaList = partitaApiResponse.getPartitaList();

            if (partitaList != null) {
                // Controlla se un Partita è già stato scaricato prima per mantenere il suo
                // stato "preferito"
                for (Partita partita : allPartita) {
                    if (partitaList.contains(partita)) {
                        // Partita appena scaricati NON hanno lo stato "preferito"
                        // Se precedentemente ho già scaricato lo stesso (presente nel DB)
                        // lo sostituisco così da mantenere lo stato "preferito"
                        partitaList.set(partitaList.indexOf(partita), partita);
                    }
                }

                // Inserisci i Partita nel Database
                partitaDao.insertPartitaList(partitaList);

                sharedPreferencesUtil.writeStringData(Constants.SHARED_PREFERENCES_FILE_NAME,
                        Constants.LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                partitaCallback.onSuccessFromLocal(partitaApiResponse);
            }
        });
    }

    /**
     * Saves the partita in the local database.
     * The method is executed with an ExecutorService defined in FipavOnlineRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param partitaList the list of partita to be written in the local database.
     */
    @Override
    public void insertPartita(List<Partita> partitaList) {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Partita> allPartita = partitaDao.getAll();

            if (partitaList != null) {
                // Controlla se un Partita è già stato scaricato prima per mantenere il suo
                // stato "preferito"
                for (Partita partita : allPartita) {
                    if (partitaList.contains(partita)) {
                        // Partita appena scaricati NON hanno lo stato "preferito"
                        // Se precedentemente ho già scaricato lo stesso (presente nel DB)
                        // lo sostituisco così da mantenere lo stato "preferito"
                        partita.setSynchronized(true);
                        partitaList.set(partitaList.indexOf(partita), partita);
                    }
                }

                PartitaApiResponse partitaApiResponse = new PartitaApiResponse();
                partitaApiResponse.setPartitaList(partitaList);
                partitaCallback.onSuccessSynchronization();
            }
        });
    }

    @Override
    public void deleteAll() {
        FipavOnlineRoomDatabase.databaseWriteExecutor.execute(() -> {
            int partitaCounter = partitaDao.getAll().size();
            int partitaDeleted = partitaDao.deleteAll();

            // It means that everything has been deleted
            if (partitaCounter == partitaDeleted) {
                sharedPreferencesUtil.deleteAll(Constants.SHARED_PREFERENCES_FILE_NAME);
                partitaCallback.onSuccessDeletion();
            }
        });
    }
}
