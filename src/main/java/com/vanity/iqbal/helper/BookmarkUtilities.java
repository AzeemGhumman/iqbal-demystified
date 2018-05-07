package com.vanity.iqbal.helper;

import android.content.Context;

import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.objects.Section;
import com.vanity.iqbal.objects.SectionItem;
import com.vanity.iqbal.objects.Sher;

import java.util.List;

import static com.vanity.iqbal.helper.ExternalMemory.bookmarkedPoemsFilename;
import static com.vanity.iqbal.helper.InternalMemory.writeToInternalMemoryFile;

/**
 * Created by aghumman on 5/2/2018.
 */

public class BookmarkUtilities {

    public static void addSectionItemToBookmarkFileYaml(Context context, SectionItem sectionItem, ListPoem listBookmarkedPoems) {

        if (listBookmarkedPoems.getSections().size() == 0) {
            // Create a ListPoem with one section
            listBookmarkedPoems = new ListPoem();
            listBookmarkedPoems.addSection(new Section());
            listBookmarkedPoems.addSection(new Section());
        }

        // TODO: Making the assumption that at least one element exists
        listBookmarkedPoems.getSections().get(1).addPoemToStartOfList(sectionItem);

        String yamlText = generateBookmarkYamlFromListPoem(listBookmarkedPoems);
        writeToInternalMemoryFile(context, bookmarkedPoemsFilename, yamlText);
    }

    public static void removeSectionItemFromBookmarkFileYaml(Context context, SectionItem sectionItem, ListPoem listBookmarkedPoems) {

        if (listBookmarkedPoems.getSections().size() == 0) {
            // Create a ListPoem with one section
            listBookmarkedPoems = new ListPoem();
            listBookmarkedPoems.addSection(new Section());
            listBookmarkedPoems.addSection(new Section());
        }

        // TODO: Making the assumption that at least one element exists
        listBookmarkedPoems.getSections().get(1).removePoem(sectionItem);
        String yamlText = generateBookmarkYamlFromListPoem(listBookmarkedPoems);
        writeToInternalMemoryFile(context, bookmarkedPoemsFilename, yamlText);
    }

    public static String generateBookmarkYamlFromListPoem(ListPoem listBookmarkedPoems) {

        // TODO: This is ugly. At least add comments on what's going on
        // First, I tried yaml.dump(listBookmarkedPoems) but that produces meta stuff that the yaml parser does not like

        // TODO: Making the assumption that at least one element exists
        if (listBookmarkedPoems.getSections().get(1).getPoems().size() == 0) {
            return "";
        }
        // Header
        StringBuilder yamlText = new StringBuilder(
                "---\nname:\n- lang: ur\n  text: 'بک مارکس'\n- lang: en\n  " +
                        "text: 'Bookmarked Poems'\n" + "sections:\n- sectionName:\n  " +
                        "- lang: ur\n    text: 'بک مارکس'\n  - lang: en\n    " +
                        "text: 'Bookmarked Poems'\n- poems:\n");

        // TODO: Making the assumption that at least one element exists
        int totalBookmarkedPoems = listBookmarkedPoems.getSections().get(1).getPoems().size();
        for (int poemIndex = 0; poemIndex < totalBookmarkedPoems; poemIndex++) {
            SectionItem currentSectionItem = listBookmarkedPoems.getSections().get(1).getPoems().get(poemIndex);
            String id = currentSectionItem.getId();
            yamlText.append("  - id: '" + id + "'\n");
            yamlText.append("    poemName:\n");

            for (int nameIndex = 0; nameIndex < currentSectionItem.getPoemName().size(); nameIndex++) {
                yamlText.append("    - lang: \"" + currentSectionItem.getPoemName().get(nameIndex).getLang() + "\"\n");
                yamlText.append("      text: \"" + currentSectionItem.getPoemName().get(nameIndex).getText() + "\"\n");
            }
        }
        return yamlText.toString();
    }

    public static boolean isSherBookmarked(String sherId, List<Sher> listBookmarkedShers) {

        for (int bookmarkedShersIndex = 0; bookmarkedShersIndex < listBookmarkedShers.size(); bookmarkedShersIndex ++) {
            if (listBookmarkedShers.get(bookmarkedShersIndex).getId().equals(sherId)) {
                return true;
            }
        }
        return false;
    }
}
