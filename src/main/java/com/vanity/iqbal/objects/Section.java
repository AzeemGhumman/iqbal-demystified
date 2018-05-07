package com.vanity.iqbal.objects;

import java.util.ArrayList;

/**
 * Created by aghumman on 3/29/2018.
 */

public class Section {

    private ArrayList<SectionHeading> sectionName;
    private ArrayList<SectionItem> poems;

    public Section() {
        sectionName = new ArrayList<>();
        poems = new ArrayList<>();
    }

    public ArrayList<SectionHeading> getSectionName() {
        return sectionName;
    }

    public ArrayList<SectionItem> getPoems() {
        return poems;
    }

    public void setSectionName(ArrayList<SectionHeading> _sectionName) {
        sectionName = new ArrayList<>(_sectionName);
    }

    public void setPoems(ArrayList<SectionItem> _poems) {
        poems = new ArrayList<>(_poems);
    }

    public void addPoemToStartOfList(SectionItem _sectionItem) {
        poems.add(0, _sectionItem);
    }

    public void removePoem(SectionItem _sectionItem) {

        // Iterating from end to start, to avoid index moving issue due to element deletion
        for (int sectionIndex = poems.size() - 1; sectionIndex >= 0; sectionIndex --) {
            if (poems.get(sectionIndex).getId().equals(_sectionItem.getId())) {
                poems.remove(sectionIndex);
            }
        }
    }
}