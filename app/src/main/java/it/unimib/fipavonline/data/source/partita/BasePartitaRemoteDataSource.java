package it.unimib.fipavonline.data.source.partita;

import it.unimib.fipavonline.data.source.partita.PartitaCallback;

/**
 * Base class to get partita from a remote source.
 */
public abstract class BasePartitaRemoteDataSource {
    protected PartitaCallback partitaCallback;

    public void setPartitaCallback(PartitaCallback partitaCallback) {
        this.partitaCallback = partitaCallback;
    }

    public abstract void getPartita();
}
