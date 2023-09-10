package it.unimib.fipavonline.data.source.campionato;

import static it.unimib.fipavonline.util.Constants.API_KEY_ERROR;
import static it.unimib.fipavonline.util.Constants.RETROFIT_ERROR;

import androidx.annotation.NonNull;

import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.data.service.CampionatoApiService;
import it.unimib.fipavonline.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class to get campionato from a remote source using Retrofit.
 */
public class CampionatoRemoteDataSource extends BaseCampionatoRemoteDataSource {

    private final CampionatoApiService campionatoApiService;
    private final String apiKey;

    public CampionatoRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.campionatoApiService = ServiceLocator.getInstance().getCampionatoApiService();
    }

    @Override
    public void getCampionato() {
        Call<CampionatoApiResponse> campionatoResponseCall =
                campionatoApiService.getCampionatoWithSort("nome", apiKey);

        campionatoResponseCall.enqueue(new Callback<CampionatoApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampionatoApiResponse> call,
                                   @NonNull Response<CampionatoApiResponse> response) {

                if (response.body() != null && response.isSuccessful() &&
                        response.body().getStatus() == 200) {
                    campionatoCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());

                } else {
                    campionatoCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CampionatoApiResponse> call, @NonNull Throwable t) {
                campionatoCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
}
