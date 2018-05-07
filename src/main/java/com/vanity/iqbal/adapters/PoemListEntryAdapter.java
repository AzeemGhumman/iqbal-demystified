package com.vanity.iqbal.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vanity.iqbal.*;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.list_items.PoemListEntryItem;
import com.vanity.iqbal.list_items.PoemListItem;
import com.vanity.iqbal.list_items.PoemListSectionItem;

import static com.vanity.iqbal.helper.BookmarkUtilities.addSectionItemToBookmarkFileYaml;
import static com.vanity.iqbal.helper.BookmarkUtilities.removeSectionItemFromBookmarkFileYaml;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextTypefacePreference;
import static com.vanity.iqbal.helper.GeneralUtilities.*;

public class PoemListEntryAdapter extends ArrayAdapter<PoemListItem> {

	private Context context;
	private ArrayList<PoemListItem> items;
	private LayoutInflater vi;
	private ListPoem listPoem;
    private ListPoem listBookmarkedPoems;
    private String searchQuery;

    private Typeface primaryTypeface;

    public PoemListEntryAdapter(Context context,
								ArrayList<PoemListItem> items,
								ListPoem listPoem,
								ListPoem listBookmarkedPoems,
								String searchQuery)
	{
		super(context,0, items);
		this.context = context;
		this.items = items;
		this.listPoem = listPoem;
		this.listBookmarkedPoems = listBookmarkedPoems;
		this.searchQuery = searchQuery;
		vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get preferences
        primaryTypeface = getPrimaryTextTypefacePreference(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final PoemListItem i = items.get(position);
		if (i != null) {
			if(i.isSection()){
				PoemListSectionItem si = (PoemListSectionItem)i;
				v = vi.inflate(R.layout.list_item_section, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
				sectionView.setText(si.getTitle());

			} else {

				final PoemListEntryItem ei = (PoemListEntryItem)i;
				v = vi.inflate(R.layout.list_item_entry, null);
				final TextView title = (TextView) v.findViewById(R.id.list_item_entry_title);
				final TextView subtitle = (TextView) v.findViewById(R.id.list_item_entry_summary);
				final ImageView isBookmarked = (ImageView) v.findViewById(R.id.btnBookmarkPoem);

				if (!searchQuery.equals("") && ei.title.contains(searchQuery)) {
					title.setBackgroundColor(Color.YELLOW);
	                subtitle.setBackgroundColor(Color.YELLOW);
				}
				else {
    	            title.setBackgroundColor(Color.TRANSPARENT);
        	        subtitle.setBackgroundColor(Color.TRANSPARENT);
				}

				title.setPadding(0, 40, 0, 0);
				title.setTypeface(primaryTypeface, Typeface.NORMAL);

                title.setText(ei.title);
                subtitle.setText(ei.subtitle);

				if (ei.isBookmarked) {
                    isBookmarked.setImageResource(android.R.drawable.star_big_on);
                } else {
                    isBookmarked.setImageResource(android.R.drawable.star_big_off);
                }

                isBookmarked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // If not bookmarked, add bookmark to file
                        if (!ei.isBookmarked) {

                            // Extract sectionItem based on which position user clicked
                            com.vanity.iqbal.objects.SectionItem sectionItem = findSectionItemGivenPositionInListView(listPoem, position);

                            // add SectionItem to bookmarks (create file if not exists)
                            addSectionItemToBookmarkFileYaml(context, sectionItem, listBookmarkedPoems);

                            // update UI
                             isBookmarked.setImageResource(android.R.drawable.star_big_on);
                            ei.isBookmarked = true;

                            Toast.makeText(context, "Poem added to Bookmarks", Toast.LENGTH_SHORT).show();

                            // notifyDataSetChanged();
                        }
                        else { /* ei.isBookmarked */

                            com.vanity.iqbal.objects.SectionItem sectionItem = findSectionItemGivenPositionInListView(listPoem, position);

                            // Remove this from the list and write to file
                            removeSectionItemFromBookmarkFileYaml(context, sectionItem, listBookmarkedPoems);

                            // update UI
                            isBookmarked.setImageResource(android.R.drawable.star_big_off);
                            ei.isBookmarked = false;

                            Toast.makeText(context, "Poem removed from bookmarks", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
			}
		}
		return v;
	}
}
