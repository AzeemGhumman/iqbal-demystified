package com.vanity.iqbal.helper;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.vanity.iqbal.objects.Font;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aghumman on 4/16/2018.
 */

public class ExternalMemory {

    public static final String FolderDownloads = "Iqbal-Demystified";

    public static String fontFajerFilename = "fajer_noori_nastalique.ttf";
    public static String fontJameelFilename = "jameel_noori_nastaleeq.ttf";
    public static String fontJameelKasheedaFilename = "jameel_noori_nastaleeq_kasheeda.ttf";
    public static String fontPakFilename = "pak_nastaleeq_beta_release.ttf";

    public static final String bookmarkedPoemsFilename = "bookmarked-poems.yaml";
    public static final String bookmarkedShersFilename = "bookmarked-shers.txt";

    private static String fontFajerUrl = "http://icanmakemyownapp.com/iqbal/fonts/fajer_noori_nastalique.ttf";
    private static String fontJameelUrl = "http://icanmakemyownapp.com/iqbal/fonts/jameel_noori_nastaleeq.ttf";
    private static String fontJameelKasheedaUrl = "http://icanmakemyownapp.com/iqbal/fonts/jameel_noori_nastaleeq_kasheeda.ttf";
    private static String fontPakUrl = "http://icanmakemyownapp.com/iqbal/fonts/pak_nastaleeq_beta_release.ttf";

    public static List<Font> getAllFonts()
    {
        List<Font> fonts = new ArrayList<>();
        fonts.add(new Font(Preferences.FontType.FONT_NATIVE_BASIC, "Basic Font", true));
        fonts.add(new Font(Preferences.FontType.FONT_NATIVE_FANCY, "Noori Nastaleeq Font", true));
        fonts.add(new Font(Preferences.FontType.FONT_FAJER, "Fajer Font", false, fontFajerFilename, fontFajerUrl));
        fonts.add(new Font(Preferences.FontType.FONT_JAMEEL, "Jameel Noori Font", false, fontJameelFilename, fontJameelUrl));
        fonts.add(new Font(Preferences.FontType.FONT_JAMEEL_KASHEEDA, "Jameel Noori Kasheeda Font", false, fontJameelKasheedaFilename, fontJameelKasheedaUrl));
        fonts.add(new Font(Preferences.FontType.FONT_PAK, "Pak Nastaleeq Font", false, fontPakFilename, fontPakUrl));
        return fonts;
    }

    public static void createExternalFolderIfNotExists(Context context, String folderPath) {
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            boolean is_created = folder.mkdir();
            if (is_created) {
                Toast.makeText(context, "Folder created to save fonts!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error creating folder for fonts!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getExternalFolderPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/" + FolderDownloads;
    }

    public static String getExternalFilePath(String filename) {
        return getExternalFolderPath() + "/" + filename;
    }


    public static boolean isFontAvailable(Preferences.FontType fontType) {
        switch (fontType) {
            case FONT_NATIVE_BASIC:
                return true;
            case FONT_NATIVE_FANCY:
                return true;
            case FONT_FAJER:
                return new File(getExternalFolderPath(), fontFajerFilename).exists();
            case FONT_JAMEEL:
                return new File(getExternalFolderPath(), fontJameelFilename).exists();
            case FONT_JAMEEL_KASHEEDA:
                return new File(getExternalFolderPath(), fontJameelKasheedaFilename).exists();
            case FONT_PAK:
                return new File(getExternalFolderPath(), fontPakFilename).exists();
        }
        return false;
    }

    public static List<String> getDownloadedAudioFilenames() {

        List<String> audioFiles = new ArrayList<>();
        File currentFolder = new File(getExternalFolderPath());

        File[] filesSelected = currentFolder.listFiles();
        if (filesSelected != null) {
            for (File file : filesSelected) {
                String filename = file.getName();
                if (filename.endsWith(".mp3")) {
                    audioFiles.add(filename.substring(0, filename.length() - 4));
                }
            }
        }
        return audioFiles;
    }

    public static boolean deleteAudioFile(String filename) {
        File file = new File(getExternalFolderPath(), filename);
        return file.delete();
    }

    public static boolean isAudioDownloaded(String poemId) {

        File currentFolder = new File(getExternalFolderPath());
        File[] filesSelected = currentFolder.listFiles();
        if (filesSelected != null) {
            for (File file : filesSelected) {
                if (file.getName().equals(poemId + ".mp3")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getAudioFilename(String poemId) {
        return getExternalFolderPath() + "/" + poemId + ".mp3";
    }
}
