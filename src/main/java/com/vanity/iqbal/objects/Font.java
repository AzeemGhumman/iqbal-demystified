package com.vanity.iqbal.objects;

import com.vanity.iqbal.helper.Preferences;

/**
 * Created by aghumman on 4/16/2018.
 */

public class Font {

    private Preferences.FontType type;
    private String label;
    private boolean isNative;
    private String filename;
    private String url;

    public Font(Preferences.FontType type, String label, boolean isNative) {
        this(type, label, isNative, "", "");
    }

    public Font(Preferences.FontType type, String label, boolean isNative, String filename, String url) {

        this.type = type;
        this.label = label;
        this.isNative = isNative;
        this.filename = filename;
        this.url = url;
    }

    public Preferences.FontType getType() {
        return this.type;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isNative() {
        return this.isNative;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getUrl() {
        return this.url;
    }
}
