package it.unimib.fipavonline.data.repository.partita;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.fipavonline.data.source.partita.BasePartitaLocalDataSource;
import it.unimib.fipavonline.data.source.partita.BasePartitaRemoteDataSource;
import it.unimib.fipavonline.data.source.partita.PartitaCallback;
import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.model.PartitaResponse;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.util.Constants;

/**
 * Repository class to get the partita from local or from a remote source.
 */
public class PartitaRepositoryWithLiveData implements IPartitaRepositoryWithLiveData, PartitaCallback {

    private static final String TAG = PartitaRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allPartitaMutableLiveData;
    private final BasePartitaRemoteDataSource partitaRemoteDataSource;
    private final BasePartitaLocalDataSource partitaLocalDataSource;

    public PartitaRepositoryWithLiveData(BasePartitaRemoteDataSource partitaRemoteDataSource,
                                         BasePartitaLocalDataSource partitaLocalDataSource) {

        allPartitaMutableLiveData = new MutableLiveData<>();
        this.partitaRemoteDataSource = partitaRemoteDataSource;
        this.partitaLocalDataSource = partitaLocalDataSource;
        this.partitaRemoteDataSource.setPartitaCallback(this);
        this.partitaLocalDataSource.setPartitaCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchPartita(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the partita from the Web Service if the last download
        // of the partita has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > Constants.FRESH_TIMEOUT) {
            partitaRemoteDataSource.getPartita();
        } else {
            partitaLocalDataSource.getPartita();
        }
        return allPartitaMutableLiveData;
    }

    public void fetchPartita() {
        partitaRemoteDataSource.getPartita();
    }

    @Override
    public void onSuccessFromRemote(PartitaApiResponse partitaApiResponse, long lastUpdate) {
        partitaLocalDataSource.insertPartita(partitaApiResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allPartitaMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(PartitaApiResponse partitaApiResponse) {
        if (allPartitaMutableLiveData.getValue() != null && allPartitaMutableLiveData.getValue().isSuccess()) {
            List<Partita> partitaList = ((Result.PartitaResponseSuccess) allPartitaMutableLiveData.getValue()).getData().getPartitaList();
            partitaList.addAll(partitaApiResponse.getPartitaList());
//            partitaApiResponse.setPartitaList(partitaList);
            Result.PartitaResponseSuccess result = new Result.PartitaResponseSuccess(partitaApiResponse);
            allPartitaMutableLiveData.postValue(result);
        } else {
            Result.PartitaResponseSuccess result = new Result.PartitaResponseSuccess(partitaApiResponse);
            allPartitaMutableLiveData.postValue(result);
        }
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allPartitaMutableLiveData.postValue(resultError);
    }

    @Override
    public void onSuccessFromCloudReading(List<Partita> partitaList) {
        // Favorite partita got from Realtime Database the first time
        if (partitaList != null) {
            for (Partita partita : partitaList) {
                partita.setSynchronized(true);
            }
            partitaLocalDataSource.insertPartita(partitaList);
        }
    }

    @Override
    public void onSuccessFromCloudWriting(Partita partita) {
        if (partita != null) {
            partita.setSynchronized(false);
        }
    }

    @Override
    public void onSuccessSynchronization() {
        Log.d(TAG, "Partita synchronized from remote");
    }

    @Override
    public void onFailureFromCloud(Exception exception) {
    }

    @Override
    public void onSuccessDeletion() {

    }
}
