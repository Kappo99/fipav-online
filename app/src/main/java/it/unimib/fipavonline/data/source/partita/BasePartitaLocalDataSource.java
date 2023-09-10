package it.unimib.fipavonline.data.source.partita;

import it.unimib.fipavonline.model.PartitaApiResponse;

/**
 * Base class to get Partita from a local source.
 */
public abstract class BasePartitaLocalDataSource {

    protected PartitaCallback partitaCallback;

    public void setPartitaCallback(PartitaCallback partitaCallback) {
        this.partitaCallback = partitaCallback;
    }

    public abstract void getPartita();
    public abstract void insertPartita(PartitaApiResponse partitaApiResponse);
}
