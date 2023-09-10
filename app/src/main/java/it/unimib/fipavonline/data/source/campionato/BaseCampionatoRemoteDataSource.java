package it.unimib.fipavonline.data.source.campionato;

/**
 * Base class to get campionato from a remote source.
 */
public abstract class BaseCampionatoRemoteDataSource {
    protected CampionatoCallback campionatoCallback;

    public void setNewsCallback(CampionatoCallback campionatoCallback) {
        this.campionatoCallback = campionatoCallback;
    }

    public abstract void getCampionato();
}
