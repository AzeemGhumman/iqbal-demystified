package com.vanity.iqbal.objects;

import java.util.ArrayList;

/**
 * Created by aghumman on 3/29/2018.
 */

public class ListPoem {

    private ArrayList<ListHeading> name;
    private ArrayList<Section> sections;

    public ListPoem() {
        name = new ArrayList<>();
        sections = new ArrayList<>();
    }

    public ArrayList<ListHeading> getName() {
        return name;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void setName(ArrayList<ListHeading> _name) {
        name = new ArrayList<>(_name);
    }

    public void setSections(ArrayList<Section> _sections) {
        sections = new ArrayList<>(_sections);
    }

    public void addSection(Section _section) {
        sections.add(_section);
    }

    public ListHeading getName(String lang) {
        for (int i = 0; i < name.size(); i ++) {
            if (name.get(i).getLang().equals(lang)) {
                return name.get(i);
            }
        }
        return null;
    }
}