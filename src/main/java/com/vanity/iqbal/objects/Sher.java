package com.vanity.iqbal.objects;

import java.util.ArrayList;

/**
 * Created by aghumman on 3/29/2018.
 */

public class Sher {

    private String id;
    private boolean meta;
    private ArrayList<SherContent> sherContent;

    public Sher() {
        sherContent = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public boolean getMeta() {
        return meta;
    }

    public ArrayList<SherContent> getSherContent() {
        return sherContent;
    }

    public void setId(String _id) {
        id = _id;
    }

    public void setMeta(boolean _meta) {
        meta = _meta;
    }

    public void setSherContent(ArrayList<SherContent> _sherContent) {
        sherContent = new ArrayList<>(_sherContent);
    }

    public SherContent getSherContent(String lang) {
        for (int i = 0; i < sherContent.size(); i ++) {
            if (sherContent.get(i).getLang().equals(lang)) {
                return sherContent.get(i);
            }
        }
        return null;
    }
}
