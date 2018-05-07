package com.vanity.iqbal.services;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;

import static com.vanity.iqbal.helper.IntentExtras.ExtraFilename;
import static com.vanity.iqbal.helper.IntentExtras.ExtraReceiver;
import static com.vanity.iqbal.helper.IntentExtras.ExtraURL;

public class DownloadService extends IntentService {

	public static final int UPDATE_PROGRESS = 8344;

	public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String stringUrl = intent.getStringExtra(ExtraURL);
        String filename = intent.getStringExtra(ExtraFilename);

        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(ExtraReceiver);
        try {
            URL url = new URL(stringUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // Download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = null;
            output = new FileOutputStream(filename); //Poem name is actually font name here

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) 
            {
                total += count;
                // publishing the progress....
                Bundle resultData = new Bundle();
                resultData.putInt("progress" ,(int) (total * 100 / fileLength));
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }
            Log.e("Service", "Done!");
            output.flush();
            output.close();
            input.close();
        } // try
        catch (IOException e) {
            e.printStackTrace();
        }
        Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        receiver.send(UPDATE_PROGRESS, resultData);
    } // onHandleIntent
}