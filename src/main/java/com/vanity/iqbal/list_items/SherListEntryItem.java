package com.vanity.iqbal.list_items;

public class SherListEntryItem {

	private String sherId;
	private String primaryText;
	private String secondaryText;
	private boolean isBookmarked;

	public SherListEntryItem(String sherId, String primaryText, String secondaryText, boolean isBookmarked) {
		this.sherId = sherId;
		this.primaryText = primaryText;
		this.secondaryText = secondaryText;
		this.isBookmarked = isBookmarked;
	}

	public String getSherId() {
	    return this.sherId;
    }

    public String getPrimaryText() {
        return this.primaryText;
    }

    public String getSecondaryText() {
        return this.secondaryText;
    }

    public boolean isBookmarked() {
        return this.isBookmarked;
    }

    public void setBookmark(boolean state) {
        this.isBookmarked = state;
    }
}
