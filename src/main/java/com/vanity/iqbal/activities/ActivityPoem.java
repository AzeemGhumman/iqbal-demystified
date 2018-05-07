package com.vanity.iqbal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.vanity.iqbal.adapters.ExpandableListAdapter;
import com.vanity.iqbal.R;
import com.vanity.iqbal.tabs.tab_activities.TabsDiscuss;
import com.vanity.iqbal.factory.ListSherGenerator;
import com.vanity.iqbal.factory.PoemGenerator;
import com.vanity.iqbal.fragments.FragmentAudioPlayer;
import com.vanity.iqbal.helper.Preferences;
import com.vanity.iqbal.objects.Note;
import com.vanity.iqbal.objects.Poem;
import com.vanity.iqbal.objects.PoemDescription;
import com.vanity.iqbal.objects.PoemHeading;
import com.vanity.iqbal.objects.Sher;
import com.vanity.iqbal.objects.SherContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.vanity.iqbal.factory.ListSherGenerator.CreateListSher;
import static com.vanity.iqbal.helper.IntentExtras.ExtraPoemHeadingUrdu;
import static com.vanity.iqbal.helper.IntentExtras.ExtraPoemSelected;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSearchQuery;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSearchTitlesOnly;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSherSelected;
import static com.vanity.iqbal.helper.Preferences.getFontSizePreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryLanguagePreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextFontPreference;
import static com.vanity.iqbal.helper.Preferences.langToString;
import static com.vanity.iqbal.helper.Preferences.setFontSizePreference;
import static com.vanity.iqbal.helper.GeneralUtilities.restartActivity;

public class ActivityPoem extends AppCompatActivity implements ExpandableListView.OnChildClickListener {

    Poem poem;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	ArrayList<Sher> listBookmarkedShers;

    Preferences.LangType primaryLanguage;
    Preferences.FontType fontType;
    int fontSize;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Wake Lock - keep app on - since downloading large files
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_poem);

        //Adding Controls to the Page
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
 		
 		expListView.setFastScrollEnabled(true);

        // Read preferences
        primaryLanguage = getPrimaryLanguagePreference(this);
        fontType = getPrimaryTextFontPreference(this);
        fontSize = getFontSizePreference(this);

        //Get the Poem Information from the SelectPoem Page
        String poemSelected = "";
        String searchQuery = "";
        boolean isSearchTitleOnly = false;

        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			poemSelected = extras.getString(ExtraPoemSelected, "");
			searchQuery = extras.getString(ExtraSearchQuery, "");
			isSearchTitleOnly = extras.getBoolean(ExtraSearchTitlesOnly, false);
		}

        // This function reads the Poem and inserts the contents in a list adapter
        ExpandableListAdapter listAdapter = generateExpandableListAdapter(poemSelected, searchQuery, isSearchTitleOnly);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        // To improve chances of people going to discussion page, open up the title and first sher English Translation
		if (listDataHeader.size() > 0) {
			expListView.expandGroup(0);
			expListView.expandGroup(2);
		}

        expListView.setOnChildClickListener(this);

        setTitle(poem.getHeading(langToString(Preferences.LangType.URDU)).getText());

		// Send poemId to FragmentAudio
        FragmentAudioPlayer fragmentAudioPlayer = (FragmentAudioPlayer) getSupportFragmentManager().findFragmentById(R.id.fragmentAudio);
        if (fragmentAudioPlayer != null) {
            fragmentAudioPlayer.setPoemId(poemSelected);
            fragmentAudioPlayer.setAudioUrl(poem.getAudioUrl());
        }
    } // onCreate

	private ExpandableListAdapter generateExpandableListAdapter(String poemSelected, String searchQuery, boolean isSearchTitleOnly) {

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		poem = PoemGenerator.CreatePoemFromYaml(getApplicationContext(), poemSelected);

		/* Add Headings */
		// Primary Language
        PoemHeading primaryHeading = poem.getHeading(langToString(primaryLanguage));
        if (primaryHeading == null) {
            listDataHeader.add(poem.getHeading(langToString(Preferences.LangType.URDU)).getText());
        } else {
            listDataHeader.add(primaryHeading.getText());
        }

		// English
		List<String> listEnglishHeading = new ArrayList<String>();
		listEnglishHeading.add(poem.getHeading(langToString(Preferences.LangType.ENGLISH)).getText());
		listDataChild.put(listDataHeader.get(0), listEnglishHeading);

		/* Add Introductions */
		// Urdu
		listDataHeader.add("Introduction");

		// English
		List<String> listEnglishDescription = new ArrayList<String>();
        PoemDescription englishPoemDescription = poem.getDescription(langToString(Preferences.LangType.ENGLISH));
        if (englishPoemDescription != null) {
            listEnglishDescription.add(poem.getDescription(langToString(Preferences.LangType.ENGLISH)).getText());
        }
        else {
            listEnglishDescription.add("No Introduction found. Click here to Contribute!");
        }

		listDataChild.put(listDataHeader.get(1), listEnglishDescription);

		/* Add Shers */
        ArrayList<Sher> shers =  poem.getSher();
		for (int sherIndex = 0; sherIndex < shers.size(); sherIndex ++) {

            // Primary Language
            SherContent primarySherContent = shers.get(sherIndex).getSherContent(langToString(primaryLanguage));
            if (primarySherContent == null) {
                String urduSher = shers.get(sherIndex).getSherContent(langToString(Preferences.LangType.URDU)).getText();
                urduSher = urduSher.replace("|", "\n");
                listDataHeader.add(urduSher);
            } else {
                String primarySher = primarySherContent.getText();
                primarySher = primarySher.replace("|", "\n");
                listDataHeader.add(primarySher);
            }

			// English sher
            List<String> listEnglishSher = new ArrayList<String>();
            SherContent englishSherContent = shers.get(sherIndex).getSherContent(langToString(Preferences.LangType.ENGLISH));
            if (englishSherContent == null) {
                // Add missing translation
                listEnglishSher.add("# Missing translation\n# Missing translation");
            } else {
                String englishSher = englishSherContent.getText();
                englishSher = englishSher.replace("|", "\n");
                listEnglishSher.add(englishSher);
            }

            // English notes
            if (englishSherContent != null) {
                ArrayList<Note> englishNotes = englishSherContent.getNotes();
                for (int notesIndex = 0; notesIndex < englishNotes.size(); notesIndex ++) {
                    String noteText = englishNotes.get(notesIndex).getPhrase() + ": " + englishNotes.get(notesIndex).getMeaning();
                    listEnglishSher.add(noteText);
                }
            }
            listDataChild.put(listDataHeader.get(sherIndex + 2), listEnglishSher);

		} // for all shers

        // Add one extra empty sher at the bottom - this will pad the bottom so that last sher is not obstructed by audio fragment
        listDataHeader.add("\n");
		// Add an empty child for our empty sher
        listDataChild.put(listDataHeader.get(shers.size() + 2), new ArrayList<String>());

        // Find bookmarked shers
        listBookmarkedShers = CreateListSher(getApplicationContext(), ListSherGenerator.ListSherTypes.BOOKMARKED_SHERS);

		// return ExpandableListAdapter
		return new ExpandableListAdapter(this,
                                                listDataHeader,
                                                listDataChild,
                                                listBookmarkedShers,
                                                poemSelected,
                                                searchQuery,
                                                isSearchTitleOnly);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.poem_page, menu);
        return true;
    }

    /* Menu Options */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_navigate_to_settings_page) {
            Intent i = new Intent(getApplicationContext(), ActivitySettings.class);
            startActivity(i);
            // When control comes back to PoemPage, onResume is called: updated page to propagate changes made on the settings (Info) page
        } else if (id == R.id.action_zoom_in) {
            if (fontSize > 50) {
                Toast.makeText(getApplicationContext(), "This is the biggest font size!", Toast.LENGTH_SHORT).show();
            } else {
                fontSize += 3;
                setFontSizePreference(fontSize, getApplicationContext());
                // Restart Activity
                restartActivity(this);
            }//if this font increase is within the allowed values
            return true;
        }

        else if (id == R.id.action_zoom_out) {
            if (fontSize < 16) {
                Toast.makeText(getApplicationContext(), "This is the smallest font size!", Toast.LENGTH_SHORT).show();
            } else {
                fontSize -= 3;
                setFontSizePreference(fontSize, getApplicationContext());
                // Restart Activity
                restartActivity(this);
            }//if this font increase is within the allowed values
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //OnResume: If user goes to settings page and then comes back, I want to propagate the changes
    @Override
    public void onResume()
    {
        super.onResume();
        // Check the display preferences to access if anything changed and is there a need to update the view
        Preferences.LangType updatedPrimaryLanguage = getPrimaryLanguagePreference(this);
        Preferences.FontType updatedFontType = getPrimaryTextFontPreference(this);
        int updatedFontSize = getFontSizePreference(this);

        // Any preference changed that effects the view
        if ((primaryLanguage != updatedPrimaryLanguage) || (fontType != updatedFontType) || (fontSize != updatedFontSize)) {

            // Update the preferences variables
            primaryLanguage = updatedPrimaryLanguage;
            fontType = updatedFontType;
            fontSize = updatedFontSize;

            // Restart Activity
            restartActivity(this);
        }
    } // onResume

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

        if (groupPosition == 1 && poem.getDescription().size() == 0)
        {
            //Go to Contribution page
            Intent i = new Intent(getApplicationContext(), ActivityContribute.class);
            i.putExtra(ExtraPoemHeadingUrdu, poem.getHeading(langToString(Preferences.LangType.URDU)).getText());
            startActivity(i);
        }

        // If user clicks an English Translation, move to Discusison page
        if (groupPosition >= 2 && childPosition == 0) {
            String sherId = poem.getId() + "_" + String.format("%03d", groupPosition - 1);
            Intent intent = new Intent(getApplicationContext(), TabsDiscuss.class);
            intent.putExtra(ExtraSherSelected, sherId);
            startActivity(intent);
        }
        return false;
    }
}
