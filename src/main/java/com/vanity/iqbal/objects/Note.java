package com.vanity.iqbal.objects;

/**
 * Created by aghumman on 3/29/2018.
 */

public class Note {

    private String phrase;
    private String meaning;
    private int occurrence;

    public Note() {}

    public String getPhrase() {
        return phrase;
    }

    public String getMeaning() {
        return meaning;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setPhrase(String _phrase) {
        phrase = _phrase;
    }

    public void setMeaning(String _meaning) {
        meaning = _meaning;
    }

    public void setOccurrence(int _occurrence) {
        occurrence = _occurrence;
    }


}
