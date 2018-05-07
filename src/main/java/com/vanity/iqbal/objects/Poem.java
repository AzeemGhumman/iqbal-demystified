package com.vanity.iqbal.objects;

import java.util.ArrayList;

/**
 * Created by aghumman on 3/29/2018.
 */

public class Poem{

    private String id;
    private String audioUrl;
    private ArrayList<PoemHeading> heading;
    private ArrayList<PoemDescription> description;
    private ArrayList<Sher> sher;


    public Poem() {
        heading = new ArrayList<>();
        description = new ArrayList<>();
        sher = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public ArrayList<PoemHeading> getHeading() {
        return heading;
    }

    public ArrayList<PoemDescription> getDescription() {
        return description;
    }

    public ArrayList<Sher> getSher() {
        return sher;
    }

    public void setId(String _id) {
        id = _id;
    }

    public void setAudioUrl(String _auddioUrl) {
        audioUrl = _auddioUrl;
    }

    public void setHeading(ArrayList<PoemHeading> _heading) {
        heading = new ArrayList<>(_heading);
    }

    public void setDescriptions(ArrayList<PoemDescription> _description) {
        description = new ArrayList<>(_description);
    }

    public void setSher(ArrayList<Sher> _sher) {
        sher = new ArrayList<>(_sher);
    }

    public PoemHeading getHeading(String lang) {
        for (int i = 0; i < heading.size(); i ++) {
            if (heading.get(i).getLang().equals(lang)) {
                return heading.get(i);
            }
        }
        return null;
    }

    public PoemDescription getDescription(String lang) {
        for (int i = 0; i < description.size(); i ++) {
            if (description.get(i).getLang().equals(lang)) {
                return description.get(i);
            }
        }
        return null;
    }
}
