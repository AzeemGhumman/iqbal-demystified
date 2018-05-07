package com.vanity.iqbal.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanity.iqbal.R;
import com.vanity.iqbal.list_items.SherListEntryItem;
import com.vanity.iqbal.objects.Sher;

import java.util.ArrayList;
import java.util.List;

import static com.vanity.iqbal.factory.SherGenerator.CreateSherFromYaml;
import static com.vanity.iqbal.helper.ExternalMemory.bookmarkedShersFilename;
import static com.vanity.iqbal.helper.InternalMemory.writeToInternalMemoryFile;
import static com.vanity.iqbal.helper.Preferences.getFontSizePreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextTypefacePreference;
import static com.vanity.iqbal.helper.GeneralUtilities.getCsvFromSherList;

public class SherListEntryAdapter extends ArrayAdapter<SherListEntryItem> {

	private Context _context;
	private ArrayList<SherListEntryItem> items;
    private List<Sher> _listBookmarkedShers;
	private LayoutInflater vi;

    Typeface primaryTypeface;
	private int FontSize;

	public SherListEntryAdapter(Context context,
                                ArrayList<SherListEntryItem> items,
                                List<Sher> listBookmarkedShers) {
		super(context,0, items);
		this._context = context;
		this.items = items;
		this._listBookmarkedShers = listBookmarkedShers;
		vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get preferences
        primaryTypeface = getPrimaryTextTypefacePreference(context);
        FontSize = getFontSizePreference(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final SherListEntryItem i = items.get(position);
		if (i != null) {

            final SherListEntryItem ei = (SherListEntryItem)i;
            v = vi.inflate(R.layout.poem_list_item_entry, null);

            final TextView primaryText = (TextView)v.findViewById(R.id.tvSherListItemEntryPrimary);
            final TextView secondaryText = (TextView)v.findViewById(R.id.tvSherListItemEntrySecondary);
            final ImageView btnBookmark = (ImageView) v.findViewById(R.id.btnSherListItemEntryBookmark);

            primaryText.setPadding(0, 40, 0, 0);
            primaryText.setTypeface(primaryTypeface, Typeface.NORMAL);
            primaryText.setTextSize(FontSize);

            primaryText.setText(ei.getPrimaryText());
            secondaryText.setText(ei.getSecondaryText());

            if (ei.isBookmarked()) {
                btnBookmark.setImageResource(android.R.drawable.star_big_on);
            } else {
                btnBookmark.setImageResource(android.R.drawable.star_big_off);
            }

            btnBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // If not bookmarked, add bookmark to file
                    if (!ei.isBookmarked()) {

                        // Add this poem to the bookmark-poem csv file
                        _listBookmarkedShers.add(0, CreateSherFromYaml(_context, ei.getSherId()));

                        // update UI
                        btnBookmark.setImageResource(android.R.drawable.star_big_on);
                        ei.setBookmark(true);

                        // Write to csv file
                        String bookmarkedShersCsv = getCsvFromSherList(_listBookmarkedShers);
                        writeToInternalMemoryFile(_context, bookmarkedShersFilename, bookmarkedShersCsv);
                    }
                    else { /* ei.isBookmarked */

                        // Remove this sher from _listBookmarkedShers
                        for (int indexBookmarkedSher = 0; indexBookmarkedSher < _listBookmarkedShers.size(); indexBookmarkedSher ++) {
                            if (_listBookmarkedShers.get(indexBookmarkedSher).getId().equals(ei.getSherId())) {
                                _listBookmarkedShers.remove(indexBookmarkedSher);
                                break;
                            }
                        }
                        // update UI
                        btnBookmark.setImageResource(android.R.drawable.star_big_off);
                        ei.setBookmark(false);

                        // Write to csv file
                        String bookmarkedShersCsv = getCsvFromSherList(_listBookmarkedShers);
                        writeToInternalMemoryFile(_context, bookmarkedShersFilename, bookmarkedShersCsv);
                    }
                }
            });
		}
		return v;
	}
}
