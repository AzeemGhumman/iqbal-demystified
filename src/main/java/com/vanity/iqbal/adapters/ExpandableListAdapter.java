package com.vanity.iqbal.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanity.iqbal.R;
import com.vanity.iqbal.objects.Sher;

import java.util.HashMap;
import java.util.List;

import static com.vanity.iqbal.factory.SherGenerator.CreateSherFromYaml;
import static com.vanity.iqbal.helper.BookmarkUtilities.isSherBookmarked;
import static com.vanity.iqbal.helper.ExternalMemory.bookmarkedShersFilename;
import static com.vanity.iqbal.helper.InternalMemory.writeToInternalMemoryFile;
import static com.vanity.iqbal.helper.Preferences.getFontSizePreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextTypefacePreference;
import static com.vanity.iqbal.helper.GeneralUtilities.getCsvFromSherList;

public class ExpandableListAdapter extends BaseExpandableListAdapter{

    private Context _context;
	private List<String> _listDataHeader;
	private HashMap<String, List<String>> _listDataChild;
    private List<Sher> _listBookmarkedShers;
    private String _searchQuery;
    private boolean _isSearchTitleOnly;

	private String _poemId;

    private Typeface primaryTypeface;
	private int FontSize;

	public ExpandableListAdapter(Activity activity,
                                 List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData,
                                 List<Sher> listBookmarkedShers,
                                 String poemId,
								 String searchQuery,
								 boolean isSearchTitleOnly
                                 ) {
		this._context = activity.getApplicationContext();
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this._listBookmarkedShers = listBookmarkedShers;
		this._poemId = poemId;
		this._searchQuery = searchQuery;
		this._isSearchTitleOnly = isSearchTitleOnly;

		// Get preferences
        primaryTypeface = getPrimaryTextTypefacePreference(_context);
        FontSize = getFontSizePreference(_context);
	}

    @Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_item, null);
		}

        ImageView btnInfoDiscussSher = (ImageView) convertView.findViewById(R.id.btnInfoDiscussSher);
		TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

        txtListChild.setText(childText);

        // English Heading
        if (groupPosition == 0)
        {
            btnInfoDiscussSher.setVisibility(View.GONE);
            txtListChild.setTypeface(Typeface.DEFAULT_BOLD);
            txtListChild.setGravity(Gravity.CENTER);
            txtListChild.setTextColor(Color.argb(255, 30, 150, 30));
        }
        // English Description
        else if (groupPosition == 1)
        {
            btnInfoDiscussSher.setVisibility(View.GONE);
            txtListChild.setTypeface(Typeface.DEFAULT);
            txtListChild.setTextColor(Color.argb(255, 30, 30, 150));
            txtListChild.setGravity(Gravity.CENTER);
        }
        // English Translation
        else if (childPosition == 0)
        {
            btnInfoDiscussSher.setVisibility(View.VISIBLE);
            txtListChild.setTypeface(Typeface.DEFAULT);
            txtListChild.setGravity(Gravity.CENTER);
            txtListChild.setTextColor(Color.argb(255, 30, 150, 30));
             txtListChild.setPadding(50, 0, 30, 0);
        }
        // English Notes
        else if (childPosition > 0)
        {
            btnInfoDiscussSher.setVisibility(View.GONE);
            txtListChild.setTypeface(Typeface.DEFAULT);
            txtListChild.setTextColor(Color.argb(255, 20, 70, 20));
            txtListChild.setGravity(Gravity.LEFT);
            txtListChild.setPadding(50, 0, 30, 0);
        }
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, final ViewGroup parent)
	{
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		final ImageView btnBookmarkSher = (ImageView) convertView.findViewById(R.id.btnBookmarkSher);
		final TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);

        // Hide bookmark button for Heading and Description
        if (groupPosition == 0 || groupPosition == 1) {
            btnBookmarkSher.setVisibility(View.INVISIBLE);
        } else {
            btnBookmarkSher.setVisibility(View.VISIBLE);

            // Is bookmark set?
            String sherId = _poemId + "_" + String.format("%03d", groupPosition - 1);
            boolean isBookmarked = isSherBookmarked(sherId, _listBookmarkedShers);
            if (isBookmarked) {
                btnBookmarkSher.setImageResource(android.R.drawable.star_big_on);
            }
            else {
                btnBookmarkSher.setImageResource(android.R.drawable.star_big_off);
            }
        }

        // Bookmark button pressed
        btnBookmarkSher.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String sherId = _poemId + "_" + String.format("%03d", groupPosition - 1);

                // Is bookmark set?
                boolean isBookmarked = isSherBookmarked(sherId, _listBookmarkedShers);
                if (isBookmarked) {

                    // Remove this sher from _listBookmarkedShers
                    for (int indexBookmarkedSher = 0; indexBookmarkedSher < _listBookmarkedShers.size(); indexBookmarkedSher ++) {
                        if (_listBookmarkedShers.get(indexBookmarkedSher).getId().equals(sherId)) {
                            _listBookmarkedShers.remove(indexBookmarkedSher);
                            break;
                        }
                    }

                    btnBookmarkSher.setImageResource(android.R.drawable.star_big_off);

                    // Write to csv file
                    String bookmarkedShersCsv = getCsvFromSherList(_listBookmarkedShers);
                    writeToInternalMemoryFile(_context, bookmarkedShersFilename, bookmarkedShersCsv);

                } else { /* sher is not currently bookmarked */

                    // Add this poem to the bookmark-poem csv file
                    _listBookmarkedShers.add(0, CreateSherFromYaml(_context, sherId));

                    btnBookmarkSher.setImageResource(android.R.drawable.star_big_on);

                    // Write to csv file
                    String bookmarkedShersCsv = getCsvFromSherList(_listBookmarkedShers);
                    writeToInternalMemoryFile(_context, bookmarkedShersFilename, bookmarkedShersCsv);
                }
            }
        });

		lblListHeader.setText(headerTitle);

		// Urdu Heading
		if (groupPosition == 0)
		{
			lblListHeader.setTextSize(FontSize); // - set font of urdu heading 
			lblListHeader.setTypeface(primaryTypeface, Typeface.BOLD);
			lblListHeader.setTextColor(Color.BLACK);
			lblListHeader.setText(lblListHeader.getText().toString());
		}
		// The word: Introduction
		else if (groupPosition == 1)
		{
			lblListHeader.setTypeface(Typeface.DEFAULT);
			lblListHeader.setTextColor(Color.argb(255, 30, 30, 180));
		}

		// Last Empty Sher: Only added for padding
		else if (groupPosition == _listDataHeader.size() - 1) {
            btnBookmarkSher.setVisibility(View.INVISIBLE);
        }

		// Urdu Shers
		else
		{
			lblListHeader.setTextSize(FontSize); // - set font of urdu sher
			lblListHeader.setTypeface(primaryTypeface, Typeface.NORMAL);
			lblListHeader.setTextColor(Color.BLACK);
			lblListHeader.setText(lblListHeader.getText().toString());
		}

		// If user is searching in text and query is present in sher: highlight it
        boolean isContainQuery = lblListHeader.getText().toString().contains(_searchQuery);
		if (!_isSearchTitleOnly && !_searchQuery.equals("") && isContainQuery) {
			lblListHeader.setBackgroundColor(Color.YELLOW);
		} else {
			lblListHeader.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	} // getGroupView

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
