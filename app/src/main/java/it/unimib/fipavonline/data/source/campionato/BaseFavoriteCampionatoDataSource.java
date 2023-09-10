package it.unimib.fipavonline.data.source.campionato;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;

/**
 * Base class to get the user favorite campionato from a remote source.
 */
public abstract class BaseFavoriteCampionatoDataSource {

    protected CampionatoCallback campionatoCallback;

    public void setCampionatoCallback(CampionatoCallback campionatoCallback) {
        this.campionatoCallback = campionatoCallback;
    }

    public abstract void getFavoriteCampionato();
    public abstract void addFavoriteCampionato(Campionato campionato);
    public abstract void synchronizeFavoriteCampionato(List<Campionato> notSynchronizedCampionatoList);
    public abstract void deleteFavoriteCampionato(Campionato campionato);
    public abstract void deleteAllFavoriteCampionato();
}
