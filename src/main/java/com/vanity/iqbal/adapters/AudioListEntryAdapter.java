package com.vanity.iqbal.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vanity.iqbal.*;
import com.vanity.iqbal.list_items.AudioListEntryItem;
import com.vanity.iqbal.list_items.AudioListItem;
import com.vanity.iqbal.list_items.AudioListSectionItem;
import com.vanity.iqbal.objects.ListPoem;

import static com.vanity.iqbal.helper.ExternalMemory.deleteAudioFile;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextTypefacePreference;
import static com.vanity.iqbal.helper.GeneralUtilities.restartActivity;

public class AudioListEntryAdapter extends ArrayAdapter<AudioListItem> {

    private Activity activity;
    private Context context;
    private ArrayList<AudioListItem> items;
    private ListPoem listPoem;
    private LayoutInflater vi;

    private Typeface primaryTypeface;

    public AudioListEntryAdapter(Activity activity, ArrayList<AudioListItem> items, ListPoem listPoem)
    {
        super(activity.getApplicationContext(),0, items);
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.items = items;
        this.listPoem = listPoem;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get preferences
        primaryTypeface = getPrimaryTextTypefacePreference(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final AudioListItem i = items.get(position);
        if (i != null) {
            if(i.isSection()){
                AudioListSectionItem si = (AudioListSectionItem)i;
                v = vi.inflate(R.layout.list_item_section, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText(si.getTitle());

            } else {

                final AudioListEntryItem ei = (AudioListEntryItem)i;
                v = vi.inflate(R.layout.list_audio_entry, null);
                final TextView title = (TextView) v.findViewById(R.id.list_audio_entry_title);
                final TextView subtitle = (TextView) v.findViewById(R.id.list_audio_entry_summary);
                final ImageView btnDelete = (ImageView) v.findViewById(R.id.btnDeleteAudio);

                title.setPadding(0, 40, 0, 0);
                title.setTypeface(primaryTypeface, Typeface.NORMAL);

                title.setText(ei.title);
                subtitle.setText(ei.subtitle);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new AlertDialog.Builder(activity)
                            .setMessage("Are you sure you want to delete this audio?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Delete audio and refresh view
                                    String audioFilename = listPoem.getSections().get(0).getPoems().get(position - 2).getId() + ".mp3";
                                    boolean isDeleted = deleteAudioFile(audioFilename);
                                    if (!isDeleted) {
                                        Toast.makeText(context, "Unable to delete file. Please try again later", Toast.LENGTH_SHORT).show();
                                    } else {
                                        restartActivity(activity);
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    }
                });
            }
        }
        return v;
    }
}
