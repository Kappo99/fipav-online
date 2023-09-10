package it.unimib.fipavonline.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;

/**
 * Data Access Object (DAO) that provides methods that can be used to query,
 * update, insert, and delete data in the database.
 * https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
public interface CampionatoDao {
    @Query("SELECT * FROM Campionato ORDER BY nome")
    List<Campionato> getAll();

    @Query("SELECT * FROM Campionato WHERE id = :id")
    Campionato getCampionato(long id);

    @Query("SELECT * FROM Campionato WHERE is_favorite = 1 ORDER BY nome")
    List<Campionato> getFavoriteCampionato();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCampionatoList(List<Campionato> campionatoList);

    @Insert
    void insertAll(Campionato... campionatoes);

    @Update
    int updateSingleFavoriteCampionato(Campionato campionato);

    @Update
    int updateListFavoriteCampionato(List<Campionato> campionatoes);

    @Delete
    void delete(Campionato campionato);

    @Delete
    void deleteAllWithoutQuery(Campionato... campionatoes);

    @Query("DELETE FROM Campionato")
    int deleteAll();

    @Query("DELETE FROM Campionato WHERE is_favorite = 0")
    void deleteNotFavoriteCampionato();
}
