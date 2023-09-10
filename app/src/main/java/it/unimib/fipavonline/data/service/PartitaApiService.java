package it.unimib.fipavonline.data.service;

import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.util.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Interface for Service to get Partita from the Web Service.
 */
public interface PartitaApiService {
    @GET(Constants.PARTITA_ENDPOINT)
    Call<PartitaApiResponse> getPartita(
            @Query("campionato_list") String campionatoList,
            @Header("Authorization") String apiKey);

    @GET(Constants.PARTITA_ENDPOINT)
    Call<PartitaApiResponse> getPartitaWithSort(
            @Query("campionato_list") String campionatoList,
            @Query("sort") String sort,
            @Header("Authorization") String apiKey);
}
