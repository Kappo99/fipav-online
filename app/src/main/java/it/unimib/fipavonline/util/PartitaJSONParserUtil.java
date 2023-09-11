package it.unimib.fipavonline.util;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.unimib.fipavonline.model.PartitaApiResponse;

/**
 * Utility class to show different ways to parse a JSON file.
 */
public class PartitaJSONParserUtil {

    private static final String TAG = PartitaJSONParserUtil.class.getSimpleName();

    public enum JsonParserType {
        GSON,
        JSON_ERROR
    };

    private final Application application;

    public PartitaJSONParserUtil(Application application) {
        this.application = application;
    }

    /**
     * Returns a list of Partita from a JSON file parsed using Gson.
     * Doc can be read here: https://github.com/google/gson
     * @param fileName The JSON file to be parsed.
     * @return The PartitaApiResponse object associated with the JSON file content.
     * @throws IOException
     */
    public PartitaApiResponse parseJSONFileWithGSon(String fileName) throws IOException {
        InputStream inputStream = application.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Log.i(TAG, "parseJSONFileWithGSon");

        return new Gson().fromJson(bufferedReader, PartitaApiResponse.class);
    }
}
