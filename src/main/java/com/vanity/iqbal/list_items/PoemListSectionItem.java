package com.vanity.iqbal.list_items;

public class PoemListSectionItem implements PoemListItem {

	private final String title;
	
	public PoemListSectionItem(String title) {
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
