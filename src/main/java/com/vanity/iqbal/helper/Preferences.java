package com.vanity.iqbal.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;
import android.widget.Toast;

import com.vanity.iqbal.factory.ListSherGenerator;

import static com.vanity.iqbal.helper.ExternalMemory.fontFajerFilename;
import static com.vanity.iqbal.helper.ExternalMemory.fontJameelFilename;
import static com.vanity.iqbal.helper.ExternalMemory.fontJameelKasheedaFilename;
import static com.vanity.iqbal.helper.ExternalMemory.fontPakFilename;
import static com.vanity.iqbal.helper.ExternalMemory.getExternalFolderPath;
import static com.vanity.iqbal.helper.Preferences.FontType.*;

/**
 * Created by aghumman on 4/16/2018.
 */

public class Preferences {

    public enum FontType {
        FONT_NATIVE_BASIC,
        FONT_NATIVE_FANCY,
        FONT_FAJER,
        FONT_JAMEEL,
        FONT_JAMEEL_KASHEEDA,
        FONT_PAK
    }

    public enum LangType {
        UNKNOWN,
        URDU,
        ENGLISH,
        ROMAN
    }

    public static String langToString(LangType langType) {
        if (langType == LangType.URDU) {
            return "ur";
        }
        else if (langType == LangType.ENGLISH) {
            return "en";
        }
        else if (langType == LangType.ROMAN) {
            return "ro";
        }
        return "";
    }

    private static final String sharedPreferenceString = "com.vanity.iqbal";

    private static final String preferenceFont = "FontNumber";
    private static final String preferenceFontSize = "FontSize";
    private static final String preferencePrimaryLanguage = "PrimaryLanguage"; //options => 'ur' or 'ro' for original text

    private static final String preferenceCachedRecent = "CachedRecent";
    private static final String preferenceCachedPopular = "CachedPopular";
    private static final String preferenceCachedBookmarkedShers = "CachedBookmarkedShers";

    private static final String preferenceUsername = "Username";
    private static final String preferencePassword = "Password";

    private static final String preferenceTimesAppOpened = "TimesAppOpened";

    public static String getCachedDiscussionFeedPreference( Context context, ListSherGenerator.ListSherTypes listType) {

        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        switch (listType) {
            case FEED_RECENT:
                return prefs.getString(preferenceCachedRecent, "");
            case FEED_POPULAR:
                return prefs.getString(preferenceCachedPopular, "");
            case FEED_BOOKMARKED_POEMS:
                return prefs.getString(preferenceCachedBookmarkedShers, "");
        }
        return null; // using null here because an actual feed (for bookmarkedshers) can be an empty string
    }

    public static void setCachedDiscussionFeedPreference(Context context, ListSherGenerator.ListSherTypes listType, String serverResponse) {

        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        switch (listType) {
            case FEED_RECENT:
                prefs.edit().putString(preferenceCachedRecent, serverResponse).apply();
                break;
            case FEED_POPULAR:
                prefs.edit().putString(preferenceCachedPopular, serverResponse).apply();
                break;
            case FEED_BOOKMARKED_POEMS:
                prefs.edit().putString(preferenceCachedBookmarkedShers, serverResponse).apply();
                break;
        }
    }

    // Primary Language (GET)
    public static LangType getPrimaryLanguagePreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        int lang = prefs.getInt(preferencePrimaryLanguage, LangType.URDU.ordinal());
        return LangType.values()[lang];
    }

    // Primary Language (SET)
    public static void setPrimaryLanguagePreference(LangType langType, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        prefs.edit().putInt(preferencePrimaryLanguage, langType.ordinal()).apply();
    }

    // Primary Text Font (GET)
    public static void setPrimaryTextFontPreference(FontType fontType, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        prefs.edit().putInt(preferenceFont, fontType.ordinal()).apply();
    }

    // Primary Text Font (SET)
    public static FontType getPrimaryTextFontPreference(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        int fontTypeInt = prefs.getInt(preferenceFont, FONT_NATIVE_BASIC.ordinal());
        return FontType.values()[fontTypeInt];
    }

    // Get Primary Text Typeface
    public static Typeface getPrimaryTextTypefacePreference(Context context) {

        //The logic here is :
		/*if it is not a feed list, choose from asunaskh or nastaleeq
		 * if it is not a feed and text type is roman, then choose default font, if text type is not roman, choose from two urdu fonts
		 */

        FontType fontSelected = getPrimaryTextFontPreference(context);

        Typeface primaryTypeface = Typeface.DEFAULT;
        String basicFontPath = "fonts/asunaskh.ttf";
        String fancyFontPath = "fonts/nastaleeq.ttf";

        if (fontSelected == FONT_NATIVE_BASIC) {
            primaryTypeface = Typeface.createFromAsset(context.getAssets(), basicFontPath);
        } else if (fontSelected == FONT_NATIVE_FANCY) {
            primaryTypeface = Typeface.createFromAsset(context.getAssets(), fancyFontPath);
        } else if (fontSelected == FONT_FAJER) {
            try {
                primaryTypeface = Typeface.createFromFile(getExternalFolderPath() + "/" + fontFajerFilename);
            } catch (Exception ex) {
                primaryTypeface = Typeface.createFromAsset(context.getAssets(), basicFontPath);
                Toast.makeText(context, "Cannot Load Correct Font!", Toast.LENGTH_SHORT).show();
            }
        } else if (fontSelected == FONT_JAMEEL) {
            try {
                primaryTypeface = Typeface.createFromFile(getExternalFolderPath() + "/" + fontJameelFilename);
            } catch (Exception ex) {
                primaryTypeface = Typeface.createFromAsset(context.getAssets(), basicFontPath);
                Toast.makeText(context, "Cannot Load Correct Font!", Toast.LENGTH_SHORT).show();
            }
        } else if (fontSelected == FONT_JAMEEL_KASHEEDA) {
            try {
                primaryTypeface = Typeface.createFromFile(getExternalFolderPath() + "/" + fontJameelKasheedaFilename);
            } catch (Exception ex) {
                primaryTypeface = Typeface.createFromAsset(context.getAssets(), basicFontPath);
                Toast.makeText(context, "Cannot Load Correct Font!", Toast.LENGTH_SHORT).show();
            }
        } else if (fontSelected == FONT_PAK) {
            try {
                primaryTypeface = Typeface.createFromFile(getExternalFolderPath() + "/" + fontPakFilename);
            } catch (Exception ex) {
                primaryTypeface = Typeface.createFromAsset(context.getAssets(), basicFontPath);
                Toast.makeText(context, "Cannot Load Correct Font!", Toast.LENGTH_SHORT).show();
            }
        }
        return primaryTypeface;
    }

    // Font Size (GET)
    public static int getFontSizePreference(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        return prefs.getInt(preferenceFontSize, 18);
    }

    // Font Size (SET)
    public static void setFontSizePreference(int fontSize, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        prefs.edit().putInt(preferenceFontSize, fontSize).apply();
    }

    // Username (GET)
    public static String getUsernamePreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        return prefs.getString(preferenceUsername, "");
    }

    // Username (SET)
    public static void setUsernamePreference(String username, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        prefs.edit().putString(preferenceUsername, username).apply();
    }

    // Password (GET)
    public static String getPasswordPreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        return prefs.getString(preferencePassword, "");
    }

    // Password (SET)
    public static void setPasswordPreference(String password, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        prefs.edit().putString(preferencePassword, password).apply();
    }

    // TimesAppOpened (GET)
    public static int getTimesAppOpenedPreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        return prefs.getInt(preferenceTimesAppOpened, 0);
    }

    // TimesAppOpened (SET)
    public static void setTimesAppOpenedPreference(int timesAppOpened, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPreferenceString, Context.MODE_PRIVATE);
        prefs.edit().putInt(preferenceTimesAppOpened, timesAppOpened).apply();
    }
}
