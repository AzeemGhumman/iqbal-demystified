package com.vanity.iqbal.factory;

import android.content.Context;
import com.vanity.iqbal.objects.Sher;
import java.util.ArrayList;

import static com.vanity.iqbal.factory.SherGenerator.CreateSherFromYaml;
import static com.vanity.iqbal.helper.ExternalMemory.bookmarkedShersFilename;
import static com.vanity.iqbal.helper.InternalMemory.readInternalMemoryFile;

/**
 * Created by aghumman on 3/31/2018.
 */
public class ListSherGenerator {

    public enum ListSherTypes {
        UNKNOWN_LIST,
        BOOKMARKED_SHERS, // These are locally stored bookmarked shers
        FEED_RECENT,
        FEED_POPULAR,
        FEED_BOOKMARKED_POEMS // This is a feed coming from server based on which shers user has bookmarked
    }

    public static ArrayList<Sher> CreateListSher(Context context, ListSherTypes listType) {

        if (listType == ListSherTypes.BOOKMARKED_SHERS) {
            return CreateListSherFromCsvFile(context, bookmarkedShersFilename);
        }
        return new ArrayList<>();
    }

    public static ArrayList<Sher> CreateListSherFromCsvFile(Context context, String filePath) {

        ArrayList<Sher> listSher = new ArrayList<>();

        // Read csv file contents
        String fileContents = readInternalMemoryFile(context, filePath);

        // Get list of sher ids
        String[] sherIds = fileContents.split(",");

        // Generate a Sher object for every sher id
        for (String sherId : sherIds) {
            if (sherId.length() > 0) {
                Sher sher = CreateSherFromYaml(context, sherId.trim());
                listSher.add(sher);
            }
        }
        return listSher;
    }

    public static ArrayList<Sher> CreateListSherFromCsvString(Context context, String fileContents) {

        ArrayList<Sher> listSher = new ArrayList<>();

        // Remove any new lines from fileContents
        fileContents = fileContents.replace("\n", "");

        // Get list of sher ids
        String[] sherIds = fileContents.split(",");

        // Generate a Sher object for every sher id
        for (String sherId : sherIds) {
            if (sherId.trim().length() > 0) {
                Sher sher = CreateSherFromYaml(context, sherId.trim());
                listSher.add(sher);
            }
        }
        return listSher;
    }
}