package it.unimib.fipavonline.data.database;

import static it.unimib.fipavonline.util.Constants.DATABASE_VERSION;
import static it.unimib.fipavonline.util.Constants.FIPAV_ONLINE_DATABASE_NAME;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.util.Constants;

/**
 * Main access point for the underlying connection to the local database.
 * https://developer.android.com/reference/kotlin/androidx/room/Database
 */
@Database(entities = {Campionato.class, Partita.class}, version = DATABASE_VERSION)
public abstract class FipavOnlineRoomDatabase extends RoomDatabase {

    public abstract CampionatoDao campionatoDao();
    public abstract PartitaDao partitaDao();

    private static volatile FipavOnlineRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FipavOnlineRoomDatabase getDatabase(final Context context) {
//        context.deleteDatabase(Constants.FIPAV_ONLINE_DATABASE_NAME); // ELIMINA Database
        if (INSTANCE == null) {
            synchronized (FipavOnlineRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FipavOnlineRoomDatabase.class, FIPAV_ONLINE_DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
