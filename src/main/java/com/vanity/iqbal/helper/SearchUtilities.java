package com.vanity.iqbal.helper;

import android.content.Context;
import android.util.Log;

import com.vanity.iqbal.objects.ListHeading;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.objects.Poem;
import com.vanity.iqbal.objects.Section;
import com.vanity.iqbal.objects.SectionHeading;
import com.vanity.iqbal.objects.SectionItem;
import com.vanity.iqbal.objects.Sher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.vanity.iqbal.factory.ListPoemGenerator.CreateListPoemFromYaml;
import static com.vanity.iqbal.factory.PoemGenerator.CreatePoemFromYaml;
import static com.vanity.iqbal.helper.Preferences.langToString;

/**
 * Created by aghumman on 4/17/2018.
 */

public class SearchUtilities {

    public static ArrayList<SectionItem> findQueryInPoemHeadings(Context context, String query, Preferences.LangType langType) {

        List<String> bookList = getFilenamesOfAllBooks(context);

        ArrayList<SectionItem> searchResults = new ArrayList<>();
        for (String book : bookList) {
            ListPoem listPoem = CreateListPoemFromYaml(context, book);
            List<Section> sections = listPoem.getSections();
            for (Section section : sections) {
                List<SectionItem> items = section.getPoems();
                for (SectionItem item : items) {
                    String heading = item.getPoemName(langToString(langType)).getText();
                    if (heading.contains(query)) {
                        searchResults.add(item);
                    }
                }
            }
        }
        return searchResults;
    }

    public static ArrayList<SectionItem> findQueryInPoemHeadingsAndText(Context context, String query, Preferences.LangType langType) {

        List<String> poemList = getFilenamesOfAllPoems(context);

        ArrayList<SectionItem> searchResults = new ArrayList<>();

        for (String poemString : poemList) {
            Poem poem = CreatePoemFromYaml(context, poemString);
            String heading = poem.getHeading(langToString(langType)).getText();
            if (heading.contains(query)) {
                SectionItem sectionItem = createSectionItemFromPoem(poem);
                searchResults.add(sectionItem);
            }
            else {
                for (Sher sher : poem.getSher()) {
                    String sherContent = sher.getSherContent(langToString(langType)).getText();
                    if (sherContent.contains(query)) {
                        SectionItem sectionItem = createSectionItemFromPoem(poem);
                        searchResults.add(sectionItem);
                        break;
                    }
                }
            }
        }

    return searchResults;
    }

    public static SectionItem createSectionItemFromPoem(Poem poem) {

        // Create SectionItem from Poem object
        SectionItem sectionItem = new SectionItem();
        sectionItem.setText(poem.getHeading());
        sectionItem.setId(poem.getId());
        return sectionItem;
    }

    public static ListPoem generateListPoemFromListSectionItems(ArrayList<SectionItem> sectionItems, String urduText, String englishText)
    {
        Section section = new Section();
        section.setPoems(sectionItems);

        SectionHeading urduSectionHeading = new SectionHeading();
        urduSectionHeading.setLang(langToString(Preferences.LangType.ENGLISH));
        urduSectionHeading.setText(englishText);

        SectionHeading englishSectionHeading = new SectionHeading();
        englishSectionHeading.setLang(langToString(Preferences.LangType.URDU));
        englishSectionHeading.setText(urduText);

        ArrayList<SectionHeading> sectionHeadings = new ArrayList<>();
        sectionHeadings.add(urduSectionHeading);
        sectionHeadings.add(englishSectionHeading);

        section.setSectionName(sectionHeadings);

        ArrayList<Section> listSection = new ArrayList<>();
        listSection.add(section);

        ListPoem generatedList = new ListPoem();
        generatedList.setSections(listSection);

        ListHeading urduHeading = new ListHeading();
        urduHeading.setLang(langToString(Preferences.LangType.URDU));
        urduHeading.setText(urduText);

        ListHeading englishHeading = new ListHeading();
        englishHeading.setLang(langToString(Preferences.LangType.ENGLISH));
        englishHeading.setText("Search Results");

        ArrayList<ListHeading> headings = new ArrayList<>();
        headings.add(urduHeading);
        headings.add(englishHeading);
        generatedList.setName(headings);

        return generatedList;
    }

    public static List<String> getFilenamesOfAllBooks(Context context)
    {
        List<String> bookNames = new ArrayList<>();
        try {
            String[] list = context.getAssets().list("lists");
            for (String file : list) {
                if (file.matches("List_\\d{3}.yaml")) {
                    bookNames.add("lists" + "/" + file);
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return bookNames;
    }

    public static List<String> getFilenamesOfAllPoems(Context context)
    {
        List<String> poemNames = new ArrayList<>();
        try {
            String[] listOfBooks = context.getAssets().list("poems");
            for (String folder : listOfBooks)
            {
                String[] poems = context.getAssets().list("poems" + "/" + folder);
                for (String file: poems)
                {
                    // Remove the .yaml extension
                    if (file.endsWith(".yaml")) {
                        poemNames.add(file.substring(0, file.length() - 5));
                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return poemNames;
    }

}
