package it.unimib.fipavonline.data.source.partita;

import android.util.Log;

import java.io.IOException;

import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.util.Constants;
import it.unimib.fipavonline.util.PartitaJSONParserUtil;

/**
 * Class to get the partita from a local JSON file to simulate the Web Service response.
 */
public class PartitaMockRemoteDataSource extends BasePartitaRemoteDataSource {

    private static final String TAG = PartitaMockRemoteDataSource.class.getSimpleName();

    private final PartitaJSONParserUtil partitaJSONParserUtil;
    private final PartitaJSONParserUtil.JsonParserType jsonParserType;

    public PartitaMockRemoteDataSource(PartitaJSONParserUtil partitaJSONParserUtil,
                                       PartitaJSONParserUtil.JsonParserType jsonParserType) {

        this.partitaJSONParserUtil = partitaJSONParserUtil;
        this.jsonParserType = jsonParserType;
    }

    @Override
    public void getPartita() {
        PartitaApiResponse partitaApiResponse = null;

        Log.i(TAG, "getPartita PartitaMockRemoteDataSource CALLED");

        switch (jsonParserType) {
//            case JSON_READER:
//                try {
//                    partitaApiResponse =
//                            partitaJSONParserUtil.parseJSONFileWithJsonReader(Constants.PARTITA_API_TEST_JSON_FILE);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case JSON_OBJECT_ARRAY:
//                try {
//                    partitaApiResponse = partitaJSONParserUtil.parseJSONFileWithJSONObjectArray(Constants.PARTITA_API_TEST_JSON_FILE);
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
            case GSON:
                try {
                    partitaApiResponse = partitaJSONParserUtil.parseJSONFileWithGSon(Constants.PARTITA_API_TEST_JSON_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case JSON_ERROR:
                partitaCallback.onFailureFromRemote(new Exception(Constants.UNEXPECTED_ERROR));
                break;
        }

        if (partitaApiResponse != null) {
            Log.d(TAG,"partitaApiResponse OK from local JSON File");
            partitaCallback.onSuccessFromRemote(partitaApiResponse, System.currentTimeMillis());
        } else {
            Log.d(TAG,"partitaApiResponse ERROR from local JSON File");
            partitaCallback.onFailureFromRemote(new Exception(Constants.API_DATA_NOT_FOUND_ERROR));
        }
    }
}
