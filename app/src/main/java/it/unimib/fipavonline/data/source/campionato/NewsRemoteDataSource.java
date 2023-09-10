package it.unimib.fipavonline.data.source.campionato;

import static it.unimib.fipavonline.util.Constants.API_KEY_ERROR;
import static it.unimib.fipavonline.util.Constants.RETROFIT_ERROR;
import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import androidx.annotation.NonNull;

import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.data.service.NewsApiService;
import it.unimib.fipavonline.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class to get news from a remote source using Retrofit.
 */
public class NewsRemoteDataSource extends BaseNewsRemoteDataSource {

    private final NewsApiService newsApiService;
    private final String apiKey;

    public NewsRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.newsApiService = ServiceLocator.getInstance().getNewsApiService();
    }

    @Override
    public void getNews(String country, int page) {
        Call<CampionatoApiResponse> newsResponseCall = newsApiService.getNews(country,
                TOP_HEADLINES_PAGE_SIZE_VALUE, page, apiKey);

        newsResponseCall.enqueue(new Callback<CampionatoApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampionatoApiResponse> call,
                                   @NonNull Response<CampionatoApiResponse> response) {

                if (response.body() != null && response.isSuccessful() &&
                        !response.body().getStatus().equals("error")) {
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
