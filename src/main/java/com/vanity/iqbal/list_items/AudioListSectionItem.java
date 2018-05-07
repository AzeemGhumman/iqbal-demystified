package com.vanity.iqbal.list_items;

public class AudioListSectionItem implements AudioListItem {

	private final String title;
	
	public AudioListSectionItem(String title) {
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	@Override
	public boolean isSection() {
		return true;
	}

}
