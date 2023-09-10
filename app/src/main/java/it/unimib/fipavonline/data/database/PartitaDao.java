package it.unimib.fipavonline.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.fipavonline.model.Partita;

/**
 * Data Access Object (DAO) that provides methods that can be used to query,
 * update, insert, and delete data in the database.
 * https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
public interface PartitaDao {
    @Query("SELECT * FROM partita")
    List<Partita> getAll();

    @Query("SELECT * FROM partita WHERE id = :id")
    Partita getPartita(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertPartitaList(List<Partita> partitaList);

    @Insert
    void insertAll(Partita... partita);

    @Delete
    void delete(Partita partita);

    @Query("DELETE FROM partita")
    void deleteAll();

    @Delete
    void deleteAllWithoutQuery(Partita... partita);
}
