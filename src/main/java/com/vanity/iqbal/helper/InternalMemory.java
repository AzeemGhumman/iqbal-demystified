package com.vanity.iqbal.helper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by aghumman on 5/1/2018.
 */

public class InternalMemory {

    public static void writeToInternalMemoryFile(Context context, String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readInternalMemoryFile(Context context, String filePath) {

        String fileContents = "";
        try {
            InputStream inputStream = context.openFileInput(filePath);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                fileContents = stringBuilder.toString();
            }//if
        }//try
        catch (FileNotFoundException e) {
            Log.e("readInternalMemoryFile", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("readInternalMemoryFile", "Can not read file: " + e.toString());
        }
        return fileContents;
    }
}
