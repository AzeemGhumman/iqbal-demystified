package com.vanity.iqbal.objects;

import java.util.ArrayList;

/**
 * Created by aghumman on 3/29/2018.
 */

public class SectionItem {

    private String id;
    private ArrayList<PoemHeading> poemName;

    public SectionItem() {
        poemName = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public ArrayList<PoemHeading> getPoemName() {
        return poemName;
    }

    public void setId(String _id) {
        id = _id;
    }

    public void setText(ArrayList<PoemHeading> _poemName) {
        poemName = new ArrayList<>(_poemName);
    }

    public PoemHeading getPoemName(String lang) {
        for (int i = 0; i < poemName.size(); i ++) {
            if (poemName.get(i).getLang().equals(lang)) {
                return poemName.get(i);
            }
        }
        return null;
    }
}
