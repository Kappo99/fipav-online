package it.unimib.fipavonline.data.source.campionato;

import static it.unimib.fipavonline.util.Constants.API_KEY_ERROR;
import static it.unimib.fipavonline.util.Constants.CAMPIONATO_API_TEST_JSON_FILE;
import static it.unimib.fipavonline.util.Constants.UNEXPECTED_ERROR;

import java.io.IOException;

import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.util.CampionatoJSONParserUtil;

/**
 * Class to get the news from a local JSON file to simulate the Web Service response.
 */
public class CampionatoMockRemoteDataSource extends BaseCampionatoRemoteDataSource {

    private final CampionatoJSONParserUtil campionatoJsonParserUtil;
    private final CampionatoJSONParserUtil.JsonParserType jsonParserType;

    public CampionatoMockRemoteDataSource(CampionatoJSONParserUtil campionatoJsonParserUtil,
                                          CampionatoJSONParserUtil.JsonParserType jsonParserType) {

        this.campionatoJsonParserUtil = campionatoJsonParserUtil;
        this.jsonParserType = jsonParserType;
    }

    @Override
    public void getCampionato() {
        CampionatoApiResponse campionatoApiResponse = null;

        switch (jsonParserType) {
            case GSON:
                try {
                    campionatoApiResponse = campionatoJsonParserUtil.parseJSONFileWithGSon(CAMPIONATO_API_TEST_JSON_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case JSON_ERROR:
                campionatoCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
                break;
        }

        if (campionatoApiResponse != null) {
            campionatoCallback.onSuccessFromRemote(campionatoApiResponse, System.currentTimeMillis());
        } else {
            campionatoCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
        }
    }
}
