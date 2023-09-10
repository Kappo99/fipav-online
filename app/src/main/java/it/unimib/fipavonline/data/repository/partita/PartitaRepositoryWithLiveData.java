package it.unimib.fipavonline.data.repository.partita;

import static it.unimib.fipavonline.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.fipavonline.data.source.partita.BasePartitaLocalDataSource;
import it.unimib.fipavonline.data.source.partita.BasePartitaRemoteDataSource;
import it.unimib.fipavonline.data.source.partita.PartitaCallback;
import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.model.PartitaResponse;
import it.unimib.fipavonline.model.Result;

/**
 * Repository class to get the Partita from local or from a remote source.
 */
public class PartitaRepositoryWithLiveData implements IPartitaRepositoryWithLiveData, PartitaCallback {

    private static final String TAG = PartitaRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allPartitaMutableLiveData;
    private final MutableLiveData<Result> favoritePartitaMutableLiveData;
    private final BasePartitaRemoteDataSource partitaRemoteDataSource;
    private final BasePartitaLocalDataSource partitaLocalDataSource;

    public PartitaRepositoryWithLiveData(BasePartitaRemoteDataSource partitaRemoteDataSource,
                                         BasePartitaLocalDataSource partitaLocalDataSource) {

        allPartitaMutableLiveData = new MutableLiveData<>();
        favoritePartitaMutableLiveData = new MutableLiveData<>();
        this.partitaRemoteDataSource = partitaRemoteDataSource;
        this.partitaLocalDataSource = partitaLocalDataSource;
        this.partitaRemoteDataSource.setPartitaCallback(this);
        this.partitaLocalDataSource.setPartitaCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchPartita(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the Partita from the Web Service if the last download
        // of the Partita has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
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
            partitaApiResponse.setPartitaList(partitaList);
        }
        Result.PartitaResponseSuccess result = new Result.PartitaResponseSuccess(partitaApiResponse);
        allPartitaMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allPartitaMutableLiveData.postValue(resultError);
        favoritePartitaMutableLiveData.postValue(resultError);
    }

    @Override
    public void onPartitaFavoriteStatusChanged(Partita partita, List<Partita> favoriteCampionati) {
        Result allPartitaResult = allPartitaMutableLiveData.getValue();

        if (allPartitaResult != null && allPartitaResult.isSuccess()) {
            List<Partita> oldAllPartita = ((Result.PartitaResponseSuccess)allPartitaResult).getData().getPartitaList();
            if (oldAllPartita.contains(partita)) {
                oldAllPartita.set(oldAllPartita.indexOf(partita), partita);
                allPartitaMutableLiveData.postValue(allPartitaResult);
            }
        }
        favoritePartitaMutableLiveData.postValue(new Result.PartitaResponseSuccess(new PartitaResponse(favoriteCampionati)));
    }

    @Override
    public void onPartitaFavoriteStatusChanged(List<Partita> partitaList) {
        favoritePartitaMutableLiveData.postValue(new Result.PartitaResponseSuccess(new PartitaResponse(partitaList)));
    }

    @Override
    public void onDeleteFavoritePartitaSuccess(List<Partita> favoriteCampionati) {
        Result allPartitaResult = allPartitaMutableLiveData.getValue();

        if (allPartitaResult != null && allPartitaResult.isSuccess()) {
            List<Partita> oldAllPartita = ((Result.PartitaResponseSuccess)allPartitaResult).getData().getPartitaList();
            for (Partita partita : favoriteCampionati) {
                if (oldAllPartita.contains(partita)) {
                    oldAllPartita.set(oldAllPartita.indexOf(partita), partita);
                }
            }
            allPartitaMutableLiveData.postValue(allPartitaResult);
        }

        if (favoritePartitaMutableLiveData.getValue() != null &&
                favoritePartitaMutableLiveData.getValue().isSuccess()) {
            favoriteCampionati.clear();
            Result.PartitaResponseSuccess result = new Result.PartitaResponseSuccess(new PartitaResponse(favoriteCampionati));
            favoritePartitaMutableLiveData.postValue(result);
        }
    }
}
