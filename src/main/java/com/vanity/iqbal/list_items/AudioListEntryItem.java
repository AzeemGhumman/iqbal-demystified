package com.vanity.iqbal.list_items;

public class AudioListEntryItem implements AudioListItem {

	public final String title;
	public final String subtitle;

	public AudioListEntryItem(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
