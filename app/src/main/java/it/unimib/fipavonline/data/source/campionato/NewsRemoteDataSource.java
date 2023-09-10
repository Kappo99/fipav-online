package it.unimib.fipavonline.data.source.campionato;

import static it.unimib.fipavonline.util.Constants.API_KEY_ERROR;
import static it.unimib.fipavonline.util.Constants.RETROFIT_ERROR;
import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import androidx.annotation.NonNull;

import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.data.service.CampionatoApiService;
import it.unimib.fipavonline.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class to get news from a remote source using Retrofit.
 */
public class NewsRemoteDataSource extends BaseNewsRemoteDataSource {

    private final CampionatoApiService campionatoApiService;
    private final String apiKey;

    public NewsRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.campionatoApiService = ServiceLocator.getInstance().getNewsApiService();
    }

    @Override
    public void getNews() {
        Call<CampionatoApiResponse> newsResponseCall =
                campionatoApiService.getCampionatoWithSort("nome", apiKey);

        newsResponseCall.enqueue(new Callback<CampionatoApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampionatoApiResponse> call,
                                   @NonNull Response<CampionatoApiResponse> response) {

                if (response.body() != null && response.isSuccessful() &&
                        response.body().getStatus() == 200) {
                    newsCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());

                } else {
                    newsCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CampionatoApiResponse> call, @NonNull Throwable t) {
                newsCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
}
