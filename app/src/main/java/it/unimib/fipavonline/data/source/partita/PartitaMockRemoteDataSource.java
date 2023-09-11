package it.unimib.fipavonline.data.source.partita;

import java.io.IOException;

import it.unimib.fipavonline.data.source.partita.BasePartitaRemoteDataSource;
import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.util.PartitaJSONParserUtil;
import it.unimib.fipavonline.util.Constants;

/**
 * Class to get the partita from a local JSON file to simulate the Web Service response.
 */
public class PartitaMockRemoteDataSource extends BasePartitaRemoteDataSource {

    private final PartitaJSONParserUtil partitaJsonParserUtil;
    private final PartitaJSONParserUtil.JsonParserType jsonParserType;

    public PartitaMockRemoteDataSource(PartitaJSONParserUtil partitaJsonParserUtil,
                                       PartitaJSONParserUtil.JsonParserType jsonParserType) {

        this.partitaJsonParserUtil = partitaJsonParserUtil;
        this.jsonParserType = jsonParserType;
    }

    @Override
    public void getPartita() {
        PartitaApiResponse partitaApiResponse = null;

        switch (jsonParserType) {
            case GSON:
                try {
                    partitaApiResponse = partitaJsonParserUtil.parseJSONFileWithGSon(Constants.PARTITA_API_TEST_JSON_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case JSON_ERROR:
                partitaCallback.onFailureFromRemote(new Exception(Constants.UNEXPECTED_ERROR));
                break;
        }

        if (partitaApiResponse != null) {
            partitaCallback.onSuccessFromRemote(partitaApiResponse, System.currentTimeMillis());
        } else {
            partitaCallback.onFailureFromRemote(new Exception(Constants.API_KEY_ERROR));
        }
    }
}
