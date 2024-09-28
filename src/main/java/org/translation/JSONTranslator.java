package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private static final Map<String, Map<String, String>> countryInfo = new HashMap<>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.
                    get(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i=0; i < jsonArray.length(); i++){
                JSONObject row = jsonArray.getJSONObject(i);
                String countryCode3 = row.getString("alpha3");
                Set<String> keys = row.keySet();
                keys.remove("id");
                HashMap<String, String> pairs = new HashMap<>();
                for(String key : keys) {
                    pairs.put(key, row.getString(key));
                }
                pairs.remove("alpha2");
                pairs.remove("alpha3");
                countryInfo.put(countryCode3, pairs);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        ArrayList<String> result = new ArrayList<>();
        Map<String, String> pairs = countryInfo.get(country);
        Set<String> stringSet = pairs.keySet();
        for (String item : stringSet){
            result.add(item);
        }
        return result;
    }

    @Override
    public List<String> getCountries() {
        Set<String> keys = countryInfo.keySet();
        return new ArrayList<>(keys);
    }

    @Override
    public String translate(String country, String language) {
        Map<String, String> pairs = countryInfo.get(country);
        if (pairs.get(language) != null){
            return pairs.get(language);
        }else {
            return null;
        }
    }
}
