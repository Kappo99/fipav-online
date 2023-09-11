package it.unimib.fipavonline.data.source.partita;

import java.util.List;

import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.PartitaApiResponse;

/**
 * Base class to get partita from a local source.
 */
public abstract class BasePartitaLocalDataSource {

    protected PartitaCallback partitaCallback;

    public void setPartitaCallback(PartitaCallback partitaCallback) {
        this.partitaCallback = partitaCallback;
    }

    public abstract void getPartita();
    public abstract void insertPartita(PartitaApiResponse partitaApiResponse);
    public abstract void insertPartita(List<Partita> partitaList);
    public abstract void deleteAll();
}
