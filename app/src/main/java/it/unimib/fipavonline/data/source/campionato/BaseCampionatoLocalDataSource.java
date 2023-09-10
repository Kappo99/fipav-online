package it.unimib.fipavonline.data.source.campionato;

import java.util.List;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.CampionatoApiResponse;

/**
 * Base class to get campionato from a local source.
 */
public abstract class BaseCampionatoLocalDataSource {

    protected CampionatoCallback campionatoCallback;

    public void setCampionatoCallback(CampionatoCallback campionatoCallback) {
        this.campionatoCallback = campionatoCallback;
    }

    public abstract void getCampionato();
    public abstract void getFavoriteCampionato();
    public abstract void updateCampionato(Campionato campionato);
    public abstract void deleteFavoriteCampionato();
    public abstract void insertCampionato(CampionatoApiResponse campionatoApiResponse);
    public abstract void insertCampionato(List<Campionato> campionatoList);
    public abstract void deleteAll();
}
