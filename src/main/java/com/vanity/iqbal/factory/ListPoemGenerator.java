package com.vanity.iqbal.factory;

import android.content.Context;
import android.content.res.AssetManager;

import com.vanity.iqbal.helper.Preferences;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.objects.SectionItem;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_URDU_BAL_AE_JABREEL_2;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_URDU_BANG_AE_DARA_1;
import static com.vanity.iqbal.helper.ExternalMemory.bookmarkedPoemsFilename;
import static com.vanity.iqbal.helper.InternalMemory.readInternalMemoryFile;
import static com.vanity.iqbal.helper.SearchUtilities.findQueryInPoemHeadings;
import static com.vanity.iqbal.helper.SearchUtilities.findQueryInPoemHeadingsAndText;
import static com.vanity.iqbal.helper.SearchUtilities.generateListPoemFromListSectionItems;

/**
 * Created by aghumman on 3/30/2018.
 */

public class ListPoemGenerator {

    public enum ListPoemTypes {
        UNKNOWN_LIST,
        LIST_BOOKMARKED_POEMS,
        LIST_EDITORS_PICK,
        LIST_SEARCH_RESULTS,
        BOOK_URDU_BANG_AE_DARA_1,
        BOOK_URDU_BAL_AE_JABREEL_2,
        BOOK_URDU_ZARB_AE_KALEEM_3,
        BOOK_URDU_ARMAGHAN_AE_HIJAZ_4,
        BOOK_PERSIAN_ASRAR_AE_KHUDI_5,
        BOOK_PERSIAN_RUMUZ_AE_BEKHUDI_6,
        BOOK_PERSIAN_PAYAM_AE_MASHRIQ_7,
        BOOK_PERSIAN_ZABUR_AE_AJAM_8,
        BOOK_PERSIAN_JAVED_NAMA_9,
        BOOK_PERSIAN_PAS_CHEH_BAYAD_KARD_10,
        BOOK_PERSIAN_ARMAGHAN_AE_HIJAZ_11
    }

    public static ListPoem CreateListPoem(Context context, ListPoemTypes listType) {

        if (listType == ListPoemTypes.LIST_BOOKMARKED_POEMS) {
            return CreateListPoemFromInternalMemoryYaml(context, bookmarkedPoemsFilename);
        }
        else if (listType == ListPoemTypes.LIST_EDITORS_PICK) {
            return CreateListPoemFromYaml(context, "lists/" + "List_Editor_Pick.yaml");
        }
        else if (listType == BOOK_URDU_BANG_AE_DARA_1) {
            return CreateListPoemFromYaml(context, "lists/" + "List_001.yaml");
        }
        else if (listType == BOOK_URDU_BAL_AE_JABREEL_2) {
            return CreateListPoemFromYaml(context, "lists/" + "List_002.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_URDU_ZARB_AE_KALEEM_3) {
            return CreateListPoemFromYaml(context, "lists/" + "List_003.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_URDU_ARMAGHAN_AE_HIJAZ_4) {
            return CreateListPoemFromYaml(context, "lists/" + "List_004.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_PERSIAN_ASRAR_AE_KHUDI_5) {
            return CreateListPoemFromYaml(context, "lists/" + "List_005.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_PERSIAN_RUMUZ_AE_BEKHUDI_6) {
            return CreateListPoemFromYaml(context, "lists/" + "List_006.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_PERSIAN_PAYAM_AE_MASHRIQ_7) {
            return CreateListPoemFromYaml(context, "lists/" + "List_007.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_PERSIAN_ZABUR_AE_AJAM_8) {
            return CreateListPoemFromYaml(context, "lists/" + "List_008.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_PERSIAN_JAVED_NAMA_9) {
            return CreateListPoemFromYaml(context, "lists/" + "List_009.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_PERSIAN_PAS_CHEH_BAYAD_KARD_10) {
            return CreateListPoemFromYaml(context, "lists/" + "List_010.yaml");
        }
        else if (listType == ListPoemTypes.BOOK_PERSIAN_ARMAGHAN_AE_HIJAZ_11) {
            return CreateListPoemFromYaml(context, "lists/" + "List_011.yaml");
        }
        return new ListPoem();
    }

    public static ListPoem CreateListPoemFromInternalMemoryYaml(Context context, String filePath) {

        String fileContents = readInternalMemoryFile(context, filePath);
        if (fileContents.length() > 0) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(fileContents, ListPoem.class);
        }
        return new ListPoem();
    }

    public static ListPoem CreateListPoemFromYaml(Context context, String filePath) {

        AssetManager assetManager = context.getAssets();
        InputStream input;
        try {
            input = assetManager.open(filePath);

            // TODO: more elegant way of reading contents of a file
            // TODO: Also, implement a functions that takes in filename
            // TODO: return data string, complains if fileNotFound or other error
            // Also: this part is being  duplicated in multiple Generators
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String text = new String(buffer);

            Yaml yaml = new Yaml();
            return yaml.loadAs(text, ListPoem.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new ListPoem();
    }

    public static ListPoem CreateListPoemFromQuery(Context context, String query, boolean isSearchTitleOnly) {

        // [FEATURE]: Add support to search English and Roman text
        // Easily to compute those by passing in different language as input to findQueryInPoemHeadings(...)
        // Hard part is the Search View. The view needs to support changing keyboards or modes to english/roman/urdu

        if (isSearchTitleOnly) {
            ArrayList<SectionItem> searchResults = findQueryInPoemHeadings(context, query, Preferences.LangType.URDU);
            return generateListPoemFromListSectionItems(searchResults, "تلاش کے نتائج", "Search Results");
        }
        else {
            ArrayList<SectionItem> searchResults = findQueryInPoemHeadingsAndText(context, query, Preferences.LangType.URDU);
            return generateListPoemFromListSectionItems(searchResults, "تلاش کے نتائج", "Search Results");
        }
    }

}
