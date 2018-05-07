package com.vanity.iqbal.factory;

import android.content.Context;
import android.content.res.AssetManager;

import com.vanity.iqbal.objects.*;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by aghumman on 3/29/2018.
 */

public class PoemGenerator {

    // Returned boolean => isCreatedSucessfully
    public static Poem CreatePoemFromYaml(Context context, String poem_id) {

        // Using Snake-Yaml for parsing
        // https://github.com/bmoliveira/snake-yaml

        // TODO: Resolve this: either improve or document as a feature
        // Is string empty?
        // Does file exist?

        // public void testLoadFromStream() throws FileNotFoundException {

        AssetManager assetManager = context.getAssets();

        InputStream input;
        try {
            String folder = poem_id.split("_")[0];
            input = assetManager.open("poems/" + folder + "/" + poem_id + ".yaml");

            // TODO: more elegant way of reading contents of a file
            // TODO: Also, implement a functions that takes in filename
            // TODO: return data string, complains if fileNotFound or other error
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String text = new String(buffer);

            Yaml yaml = new Yaml();
            Map map = (Map) yaml.load(text);

            Poem poem = yaml.loadAs(text, Poem.class);
            return poem;
        }
        catch (IOException e) {
            e.printStackTrace();

        }
        return new Poem();
    }


}
