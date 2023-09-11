package it.unimib.fipavonline.data.source.partita;

import static it.unimib.fipavonline.util.Constants.API_DATA_NOT_FOUND_ERROR;
import static it.unimib.fipavonline.util.Constants.API_KEY_ERROR;
import static it.unimib.fipavonline.util.Constants.RETROFIT_ERROR;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimib.fipavonline.data.service.PartitaApiService;
import it.unimib.fipavonline.data.source.partita.BasePartitaRemoteDataSource;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.util.CampionatoJSONParserUtil;
import it.unimib.fipavonline.util.Constants;
import it.unimib.fipavonline.util.ServiceLocator;
import it.unimib.fipavonline.util.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class to get partita from a remote source using Retrofit.
 */
public class PartitaRemoteDataSource extends BasePartitaRemoteDataSource {

    private final PartitaApiService partitaApiService;
    private final String apiKey;
    private final Application application;

    public PartitaRemoteDataSource(Application application, String apiKey) {
        this.apiKey = apiKey;
        this.application = application;
        this.partitaApiService = ServiceLocator.getInstance().getPartitaApiService();
    }

    @Override
    public void getPartita() {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        String jsonList = sharedPreferencesUtil.readStringData(
                Constants.SHARED_PREFERENCES_FILE_NAME, Constants.FAVORITE_CAMPIONATO_LIST);
        List<Campionato> favoriteCampionatoList = CampionatoJSONParserUtil.parseJSONToList(jsonList);
        List<Long> favoriteIdList = new ArrayList<>();
        if (favoriteCampionatoList != null) {
            for (Campionato c : favoriteCampionatoList) {
                favoriteIdList.add(c.getId());
            }
        }

        Call<PartitaApiResponse> partitaResponseCall =
                partitaApiService.getPartitaWithSort(
                        CampionatoJSONParserUtil.convertLongListToJSON(favoriteIdList), "data", apiKey);

        partitaResponseCall.enqueue(new Callback<PartitaApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<PartitaApiResponse> call,
                                   @NonNull Response<PartitaApiResponse> response) {

                if (response.body() != null && response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        partitaCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());
                    } else {
                        partitaCallback.onFailureFromRemote(new Exception(API_DATA_NOT_FOUND_ERROR));
                    }
                } else {
                    partitaCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PartitaApiResponse> call, @NonNull Throwable t) {
                partitaCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
}
