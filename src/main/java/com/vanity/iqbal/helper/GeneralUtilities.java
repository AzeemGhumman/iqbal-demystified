package com.vanity.iqbal.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.vanity.iqbal.adapters.AudioListEntryAdapter;
import com.vanity.iqbal.adapters.PoemListEntryAdapter;
import com.vanity.iqbal.factory.ListSherGenerator;
import com.vanity.iqbal.list_items.AudioListEntryItem;
import com.vanity.iqbal.list_items.AudioListItem;
import com.vanity.iqbal.list_items.AudioListSectionItem;
import com.vanity.iqbal.list_items.PoemListEntryItem;
import com.vanity.iqbal.list_items.PoemListItem;
import com.vanity.iqbal.list_items.PoemListSectionItem;
import com.vanity.iqbal.list_items.SherListEntryItem;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.objects.Section;
import com.vanity.iqbal.objects.SectionItem;
import com.vanity.iqbal.objects.Sher;
import com.vanity.iqbal.objects.SherContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.vanity.iqbal.factory.ListPoemGenerator.CreateListPoem;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.LIST_BOOKMARKED_POEMS;
import static com.vanity.iqbal.factory.ListSherGenerator.CreateListSher;
import static com.vanity.iqbal.helper.BookmarkUtilities.isSherBookmarked;
import static com.vanity.iqbal.helper.Preferences.getPrimaryLanguagePreference;
import static com.vanity.iqbal.helper.Preferences.langToString;

/**
 * Created by aghumman on 4/1/2018.
 */

public class GeneralUtilities {

    public static String findPoemIdGivenPositionInListView(ListPoem listPoem, int position) {

        ArrayList<Section> sections = listPoem.getSections();
        int poemPosition = position;
        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex ++) {
            int totalPoemsInCurrentSection = sections.get(sectionIndex).getPoems().size();

            if (totalPoemsInCurrentSection == 0) {
                continue;
            }

            // Caveat: Each heading is considered a line. Since each section has a heading and we have headings in 2 languages
            // Keep track of headings while calculating the actual position.
            poemPosition -= 2;

            if (poemPosition < totalPoemsInCurrentSection) {
                return sections.get(sectionIndex).getPoems().get(poemPosition).getId();
            }
            else {
                poemPosition -= totalPoemsInCurrentSection;
            }
        }
        // We get here only if position was unreachable: which should never be the case
        return "";
    }

    public static SectionItem findSectionItemGivenPositionInListView(ListPoem listPoem, int position) {

        ArrayList<Section> sections = listPoem.getSections();
        int poemPosition = position;
        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex ++) {
            int totalPoemsInCurrentSection = sections.get(sectionIndex).getPoems().size();

            if (totalPoemsInCurrentSection == 0) {
                continue;
            }
            // Caveat: Each heading is considered a line. Since each section has a heading and we have headings in 2 languages
            // Keep track of headings while calculating the actual position.
            poemPosition -= 2;

            if (poemPosition < totalPoemsInCurrentSection) {
                return sections.get(sectionIndex).getPoems().get(poemPosition);
            }
            else {
                poemPosition -= totalPoemsInCurrentSection;
            }
        }
        // We get here only if position was unreachable: which should never be the case
        return null;
    }

    public static HashSet<String> findSetOfPoemIdsInPoemList(ListPoem listPoem) {

        HashSet<String> setOfPoems = new HashSet<>();

        ArrayList<Section> sections = listPoem.getSections();
        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
            Section currentSection = sections.get(sectionIndex);
            for (int sectionItemIndex = 0; sectionItemIndex < currentSection.getPoems().size(); sectionItemIndex ++) {
                setOfPoems.add(currentSection.getPoems().get(sectionItemIndex).getId());
            }
        }
        return setOfPoems;
    }

    public static String getCsvFromSherList(List<Sher> listShers) {

        String[] sherIds = new String[listShers.size()];
        for (int sherIndex = 0; sherIndex < listShers.size(); sherIndex ++) {
            sherIds[sherIndex] = listShers.get(sherIndex).getId();
        }
        return TextUtils.join(",", sherIds);
    }

    public static PoemListEntryAdapter createEntryAdapterForListPoem(Context context, ListPoem listPoem, String searchQuery) {

        ArrayList<PoemListItem> items = new ArrayList<PoemListItem>();
        ListPoem listBookmarkedPoems = CreateListPoem(context, LIST_BOOKMARKED_POEMS);

        if (listPoem != null) {

            // Find Bookmarked Poems
            // For each poem in listPoem, find out if that poem is bookmarked or not

            // Create a set of bookmarked poem. Will do contains() to check if a poem is bookmarked or not
            HashSet<String> setOfBookmarkedPoems = new HashSet<>();

            if (listBookmarkedPoems != null) {
                setOfBookmarkedPoems = findSetOfPoemIdsInPoemList(listBookmarkedPoems);
            }

            for (int indexSection = 0; indexSection < listPoem.getSections().size(); indexSection ++) {

                Section section = listPoem.getSections().get(indexSection);

                if (section.getSectionName().size() > 0) {
                    // TODO: Use lang parameter to differentiate between english/urdu/roman
                    // Use an enum to select preferred language (eng or roman)
                    // Use a function to get a specifig langugae data from it
                    // usage: selectLang(section.getSectionName(), Lang.URDU);
                    // Support languages: Lang.URDU, Lang.ENGLISH, Lang.URDU, Lang.PREFERRED,
                    items.add(new PoemListSectionItem(section.getSectionName().get(0).getText()));
                }

                if (section.getSectionName().size() > 1) {
                    items.add(new PoemListSectionItem(section.getSectionName().get(1).getText()));
                }

                for (int indexPoem = 0; indexPoem < listPoem.getSections().get(indexSection).getPoems().size(); indexPoem ++) {

                    boolean isBookmarked = false;
                    if (setOfBookmarkedPoems.contains(section.getPoems().get(indexPoem).getId())) {
                        isBookmarked = true;
                    }

                    items.add(new PoemListEntryItem(
                            section.getPoems().get(indexPoem).getPoemName().get(0).getText(),
                            section.getPoems().get(indexPoem).getPoemName().get(1).getText(),
                            isBookmarked));
                }
            }
        }
        else {
            Toast.makeText(context, "No Poems Bookmarked", Toast.LENGTH_SHORT).show();
        }
        return new PoemListEntryAdapter(context, items, listPoem, listBookmarkedPoems, searchQuery);
    }

    public static AudioListEntryAdapter createEntryAdapterForListAudio(ListPoem listPoem, Activity activity) {

        ArrayList<AudioListItem> items = new ArrayList<>();
        if (listPoem != null) {

            for (int indexSection = 0; indexSection < listPoem.getSections().size(); indexSection ++) {

                Section section = listPoem.getSections().get(indexSection);
                if (section.getSectionName().size() > 0) {
                    items.add(new AudioListSectionItem(section.getSectionName().get(0).getText()));
                }

                if (section.getSectionName().size() > 1) {
                    items.add(new AudioListSectionItem(section.getSectionName().get(1).getText()));
                }

                for (int indexPoem = 0; indexPoem < listPoem.getSections().get(indexSection).getPoems().size(); indexPoem ++) {
                    items.add(new AudioListEntryItem(
                            section.getPoems().get(indexPoem).getPoemName().get(0).getText(),
                            section.getPoems().get(indexPoem).getPoemName().get(1).getText()));
                }
            }
        }
        else {
            Toast.makeText(activity.getApplicationContext(), "No Audios Downloaded", Toast.LENGTH_SHORT).show();
        }
        return new AudioListEntryAdapter(activity, items, listPoem);
    }

    public static ArrayList<SherListEntryItem> getListSherEntryItemFromSher(Context context, ArrayList<Sher> listShers) {

        // Find bookmarked shers - we need this to set the right state of bookmark for each sher
        ArrayList<Sher> listBookmarkedShers = CreateListSher(context, ListSherGenerator.ListSherTypes.BOOKMARKED_SHERS);

        ArrayList<SherListEntryItem> items = new ArrayList<>();

        Preferences.LangType primaryLanguage = getPrimaryLanguagePreference(context);

        for (int sherIndex = 0; sherIndex < listShers.size(); sherIndex ++) {

            String sherId = listShers.get(sherIndex).getId();
            String primarySher = "";
            String englishSher = "";

            /* Add Shers */

            // Primary Language
            SherContent primarySherContent = listShers.get(sherIndex).getSherContent(langToString(primaryLanguage));
            if (primarySherContent.getText() == null) {
                primarySher = listShers.get(sherIndex).getSherContent(langToString(Preferences.LangType.URDU)).getText();
                primarySher = primarySher.replace("|", "\n");
            } else {
                primarySher = primarySherContent.getText();
                primarySher = primarySher.replace("|", "\n");
            }

            // English sher
            SherContent englishSherContent = listShers.get(sherIndex).getSherContent(langToString(Preferences.LangType.ENGLISH));
            if (englishSherContent.getText() == null) {
                // Add missing translation
                englishSher = "# Missing translation";
            } else {
                englishSher = englishSherContent.getText();
                englishSher = englishSher.replace("|", "\n");
            }

            boolean isBookmarked = isSherBookmarked(sherId, listBookmarkedShers);
            items.add(new SherListEntryItem(sherId, primarySher, englishSher, isBookmarked));
        }
        return items;
    }

    public static void restartActivity(Activity activity)
    {
        //Restart this activity - without animation
        Intent intent = activity.getIntent();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();

        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }

    public static String getPoemIdFromSherId(String sherId) {
        String poemId = "";
        String[] sherIdParts = sherId.split("_");
        if (sherIdParts.length == 3) {
            poemId = sherIdParts[0] + "_" + sherIdParts[1];
        }
        return poemId;
    }

    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
