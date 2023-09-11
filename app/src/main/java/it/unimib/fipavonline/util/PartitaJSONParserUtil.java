package it.unimib.fipavonline.util;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class to show different ways to parse a JSON file.
 */
public class PartitaJSONParserUtil {

    private static final String TAG = PartitaJSONParserUtil.class.getSimpleName();

    public enum JsonParserType {
        JSON_READER,
        JSON_OBJECT_ARRAY,
        GSON,
        JSON_ERROR
    };

    private final Application application;

    private final String statusParameter = "status";
    private final String infoParameter = "info";
    private final String resultsParameter = "results";
    private final String bodyParameter = "body";
    private final String idParameter = "id";
    private final String nomeParameter = "nome";
    private final String sessoParameter = "sesso";

    public PartitaJSONParserUtil(Application application) {
        this.application = application;
    }

    /**
     * Returns a list of Partita from a JSON file parsed using JsonReader class.
     * Doc can be read here: https://developer.android.com/reference/android/util/JsonReader
     * @param fileName The JSON file to be parsed.
     * @return The PartitaApiResponse object associated with the JSON file content.
     * @throws IOException
     */
//    public PartitaApiResponse parseJSONFileWithJsonReader(String fileName) throws IOException {
//        InputStream inputStream = application.getAssets().open(fileName);
//        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
//        PartitaApiResponse PartitaApiResponse = new PartitaApiResponse();
//        List<Partita> partitaList = null;
//
//        jsonReader.beginObject(); // Beginning of JSON root
//
//        while (jsonReader.hasNext()) {
//            String rootJSONParam = jsonReader.nextName();
//            if (rootJSONParam.equals(statusParameter)) {
//                PartitaApiResponse.setStatus(jsonReader.nextInt());
//            } else if (rootJSONParam.equals(infoParameter)) {
//                PartitaApiResponse.setInfo(jsonReader.nextString());
//            } else if (rootJSONParam.equals(resultsParameter)) {
//                PartitaApiResponse.setResults(jsonReader.nextInt());
//            } else if (rootJSONParam.equals(bodyParameter)) {
//                jsonReader.beginArray(); // Beginning of articles array
//                partitaList = new ArrayList<>();
//                while (jsonReader.hasNext()) {
//                    jsonReader.beginObject(); // Beginning of article object
//                    Partita partita = new Partita();
//                    while (jsonReader.hasNext()) {
//                        String articleJSONParam = jsonReader.nextName();
//                        if (jsonReader.peek() != JsonToken.NULL &&
//                                articleJSONParam.equals(idParameter)) {
//                            int id = jsonReader.nextInt();
//                            partita.setId(id);
//                        } else if (jsonReader.peek() != JsonToken.NULL &&
//                                articleJSONParam.equals(nomeParameter)) {
//                            String nome = jsonReader.nextString();
//                            partita.setNome(nome);
//                        } else if (jsonReader.peek() != JsonToken.NULL &&
//                                articleJSONParam.equals(sessoParameter)) {
//                            String sesso = jsonReader.nextString();
//                            partita.setSesso(sesso);
//                        } else {
//                            jsonReader.skipValue();
//                        }
//                    }
//                    jsonReader.endObject(); // End of article object
//                    partitaList.add(partita);
//                }
//                jsonReader.endArray(); // End of articles array
//            }
//        }
//        jsonReader.endObject(); // End of JSON object
//
//        PartitaApiResponse.setPartitaList(partitaList);
//
//        return PartitaApiResponse;
//    }

    /**
     * Returns a list of Partita from a JSON file parsed using JSONObject and JSONReader classes.
     * Doc of JSONObject: https://developer.android.com/reference/org/json/JSONObject
     * Doc of JSONArray: https://developer.android.com/reference/org/json/JSONArray
     * @param fileName The JSON file to be parsed.
     * @return The PartitaApiResponse object associated with the JSON file content.
     * @throws IOException
     * @throws JSONException
     */
//    public PartitaApiResponse parseJSONFileWithJSONObjectArray(String fileName)
//            throws IOException, JSONException {
//
//        InputStream inputStream = application.getAssets().open(fileName);
//        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//
//        JSONObject rootJSONObject = new JSONObject(content);
//
//        PartitaApiResponse PartitaApiResponse = new PartitaApiResponse();
//        PartitaApiResponse.setStatus(rootJSONObject.getInt(statusParameter));
//        PartitaApiResponse.setInfo(rootJSONObject.getString(infoParameter));
//        PartitaApiResponse.setResults(rootJSONObject.getInt(resultsParameter));
//
//        JSONArray bodyJSONArray = rootJSONObject.getJSONArray(bodyParameter);
//
//        List<Partita> partitaList = null;
//        int articlesCount = bodyJSONArray.length();
//
//        if (articlesCount > 0) {
//            partitaList = new ArrayList<>();
//            Partita partita;
//            for (int i = 0; i < articlesCount; i++) {
//                JSONObject articleJSONObject = bodyJSONArray.getJSONObject(i);
//                partita = new Partita();
//                partita.setId(articleJSONObject.getInt(idParameter));
//                partita.setNome(articleJSONObject.getString(nomeParameter));
//                partita.setSesso(articleJSONObject.getString(sessoParameter));
//                partitaList.add(partita);
//            }
//        }
//        PartitaApiResponse.setPartitaList(partitaList);
//
//        return PartitaApiResponse;
//    }

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
