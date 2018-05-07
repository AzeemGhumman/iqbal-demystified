package com.vanity.iqbal.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vanity.iqbal.R;
import com.vanity.iqbal.adapters.AudioListEntryAdapter;
import com.vanity.iqbal.fragments.FragmentAudioPlayer;
import com.vanity.iqbal.helper.Preferences;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.objects.Poem;
import com.vanity.iqbal.objects.PoemHeading;
import com.vanity.iqbal.objects.SectionItem;

import java.util.ArrayList;
import java.util.List;

import static com.vanity.iqbal.factory.PoemGenerator.CreatePoemFromYaml;
import static com.vanity.iqbal.helper.ExternalMemory.createExternalFolderIfNotExists;
import static com.vanity.iqbal.helper.ExternalMemory.getDownloadedAudioFilenames;
import static com.vanity.iqbal.helper.ExternalMemory.getExternalFolderPath;
import static com.vanity.iqbal.helper.Permissions.REQUEST_WRITE_STORAGE;
import static com.vanity.iqbal.helper.Permissions.hasWriteExternalStoragePermissionOtherwiseRequest;
import static com.vanity.iqbal.helper.Preferences.langToString;
import static com.vanity.iqbal.helper.SearchUtilities.generateListPoemFromListSectionItems;
import static com.vanity.iqbal.helper.GeneralUtilities.createEntryAdapterForListAudio;

/**
 * Created by aghumman on 4/24/2018.
 */

public class ActivityDownloads extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listview = null;
    ListPoem listPoem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        listview = 	(ListView)findViewById(R.id.ListView_AudioList);
        listview.setFastScrollEnabled(true);

        //set OnClick listener
        listview.setOnItemClickListener(this);

        setTitle("Audios");

        boolean hasPermissionWriteExternal = hasWriteExternalStoragePermissionOtherwiseRequest(this);
        if (hasPermissionWriteExternal) {
            createExternalFolderIfNotExists(getApplicationContext(), getExternalFolderPath());
            populateAudioFiles();
        }

    } // onCreate

    public void populateAudioFiles() {

        List<String> audioFiles = getDownloadedAudioFilenames();
        ArrayList<SectionItem> audioSectionItems = new ArrayList<>();

        for (String file : audioFiles) {
            Poem poem = CreatePoemFromYaml(getApplicationContext(), file);
            if (poem.getId() != null) {
                SectionItem sectionItem = new SectionItem();
                sectionItem.setId(poem.getId());
                ArrayList<PoemHeading> headings = new ArrayList<>();
                headings.add(poem.getHeading(langToString(Preferences.LangType.URDU)));
                headings.add(poem.getHeading(langToString(Preferences.LangType.ENGLISH)));
                sectionItem.setText(headings);
                audioSectionItems.add(sectionItem);
            }
        }

        listPoem = generateListPoemFromListSectionItems(audioSectionItems, "آڈیو", "Downloaded Audios");
        AudioListEntryAdapter adapter = createEntryAdapterForListAudio(listPoem, this);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        String poemId = listPoem.getSections().get(0).getPoems().get(position - 2).getId();

        // Play audio
        FragmentAudioPlayer fragmentAudioPlayer = (FragmentAudioPlayer) getSupportFragmentManager().findFragmentById(R.id.fragmentAudio);
        if (fragmentAudioPlayer != null) {
            fragmentAudioPlayer.setPoemId(poemId);
            fragmentAudioPlayer.playAudio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    createExternalFolderIfNotExists(getApplicationContext(), getExternalFolderPath());
                    populateAudioFiles();
                } else
                {
                    Toast.makeText(this, "App does not have permission to locate audios", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}