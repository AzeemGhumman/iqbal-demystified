package com.vanity.iqbal.list_items;


public class PoemListEntryItem implements PoemListItem {

	public final String title;
	public final String subtitle;
	public boolean isBookmarked;

	public PoemListEntryItem(String title, String subtitle, Boolean isBookmarked) {
		this.title = title;
		this.subtitle = subtitle;
		this.isBookmarked = isBookmarked;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
