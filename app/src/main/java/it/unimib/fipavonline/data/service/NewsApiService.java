package it.unimib.fipavonline.data.service;

import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_COUNTRY_PARAMETER;
import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_ENDPOINT;
import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_PAGE_PARAMETER;
import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_PAGE_SIZE_PARAMETER;

import it.unimib.fipavonline.model.CampionatoApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Interface for Service to get news from the Web Service.
 */
public interface NewsApiService {
    @GET(TOP_HEADLINES_ENDPOINT)
    Call<CampionatoApiResponse> getNews(
            @Query(TOP_HEADLINES_COUNTRY_PARAMETER) String country,
            @Query(TOP_HEADLINES_PAGE_SIZE_PARAMETER) int pageSize,
            @Query(TOP_HEADLINES_PAGE_PARAMETER) int page,
            @Header("Authorization") String apiKey);
}
