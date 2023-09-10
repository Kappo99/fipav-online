package it.unimib.fipavonline.util;

import android.app.Application;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.unimib.fipavonline.model.CampionatoApiResponse;

/**
 * Utility class to show different ways to parse a JSON file.
 */
public class CampionatoJSONParserUtil {

    private static final String TAG = CampionatoJSONParserUtil.class.getSimpleName();

    public enum JsonParserType {
        GSON,
        JSON_ERROR
    };

    private final Application application;

    public CampionatoJSONParserUtil(Application application) {
        this.application = application;
    }

    /**
     * Returns a list of Campionato from a JSON file parsed using Gson.
     * Doc can be read here: https://github.com/google/gson
     * @param fileName The JSON file to be parsed.
     * @return The CampionatoApiResponse object associated with the JSON file content.
     * @throws IOException
     */
    public CampionatoApiResponse parseJSONFileWithGSon(String fileName) throws IOException {
        InputStream inputStream = application.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new Gson().fromJson(bufferedReader, CampionatoApiResponse.class);
    }
}
