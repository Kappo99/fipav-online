package it.unimib.fipavonline.data.source.campionato;

import java.io.IOException;

import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.util.CampionatoJSONParserUtil;
import it.unimib.fipavonline.util.Constants;

/**
 * Class to get the campionato from a local JSON file to simulate the Web Service response.
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
                    campionatoApiResponse = campionatoJsonParserUtil.parseJSONFileWithGSon(Constants.CAMPIONATO_API_TEST_JSON_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case JSON_ERROR:
                campionatoCallback.onFailureFromRemote(new Exception(Constants.UNEXPECTED_ERROR));
                break;
        }

        if (campionatoApiResponse != null) {
            campionatoCallback.onSuccessFromRemote(campionatoApiResponse, System.currentTimeMillis());
        } else {
            campionatoCallback.onFailureFromRemote(new Exception(Constants.API_DATA_NOT_FOUND_ERROR));
        }
    }
}
