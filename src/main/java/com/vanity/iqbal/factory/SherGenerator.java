package com.vanity.iqbal.factory;

import android.content.Context;

import com.vanity.iqbal.objects.Poem;
import com.vanity.iqbal.objects.Sher;

/**
 * Created by aghumman on 3/31/2018.
 */

// TODO: For all generators, use error codes instead of returning null
// In case something goes wrong, we will have more information or display more informative msgs

public class SherGenerator {

    // Returned boolean => isCreatedSucessfully
    public static Sher CreateSherFromYaml(Context context, String sher_id) {

        // TODO: Make this extraction logic common
        // Extract poem id from sher_id
        String[] sher_id_parts = sher_id.split("_");
        if (sher_id_parts.length != 3) {
            // Invalid sher_id
            return new Sher();
        }

        //In sher_id: X_Y_Z, X_Y is the poem_id
        String poem_id = sher_id_parts[0] + "_" + sher_id_parts[1];
        String str_sher_id = sher_id_parts[2];
        int sher_index = 0;

        // Convert String to Integer - needed for indexing sher from the poem
        try {
            sher_index = Integer.parseInt(str_sher_id);
        }
        catch (Exception ex) {
            return new Sher();
        }

        // Index starts from 0, ids start from 1
        sher_index -= 1;
        Poem poem = PoemGenerator.CreatePoemFromYaml(context, poem_id);

        // Check if the poem exists
        if (poem.getSher().size() == 0) {
            return new Sher();
        }

        // Check if sher_index is within limits
        if (sher_index < 0 || sher_index >= poem.getSher().size()) {
            return new Sher();
        }
        return poem.getSher().get(sher_index);
    }
}
