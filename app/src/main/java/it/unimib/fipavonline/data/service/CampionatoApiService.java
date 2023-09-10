package it.unimib.fipavonline.data.service;

import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.util.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Interface for Service to get news from the Web Service.
 */
public interface CampionatoApiService {
    @GET(Constants.CAMPIONATO_ENDPOINT)
    Call<CampionatoApiResponse> getCampionato(
            @Header("Authorization") String apiKey);

    @GET(Constants.CAMPIONATO_ENDPOINT)
    Call<CampionatoApiResponse> getCampionatoWithSort(
            @Query("sort") String sort,
            @Header("Authorization") String apiKey);
}
