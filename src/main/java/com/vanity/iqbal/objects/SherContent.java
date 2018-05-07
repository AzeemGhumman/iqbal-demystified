package com.vanity.iqbal.objects;

import java.util.ArrayList;

/**
 * Created by aghumman on 3/29/2018.
 */

public class SherContent {

    private String lang;
    private String text;
    private ArrayList<Note> notes;

    public SherContent() {
        notes = new ArrayList<>();
    }

    public String getLang() {
        return lang;
    }

    public String getText() {
        return text;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setLang(String _lang) {
        lang = _lang;
    }

    public void setText(String _text) {
        text = _text;
    }

    public void  setNotes(ArrayList<Note> _notes) {
        notes = new ArrayList<>(_notes);
    }
}
