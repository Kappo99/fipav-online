package it.unimib.fipavonline.data.source.partita;

/**
 * Base class to get Partita from a remote source.
 */
public abstract class BasePartitaRemoteDataSource {
    protected PartitaCallback partitaCallback;

    public void setPartitaCallback(PartitaCallback partitaCallback) {
        this.partitaCallback = partitaCallback;
    }

    public abstract void getPartita();
}
