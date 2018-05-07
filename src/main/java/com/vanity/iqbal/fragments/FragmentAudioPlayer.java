package com.vanity.iqbal.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vanity.iqbal.R;
import com.vanity.iqbal.helper.AudioUtilities;
import com.vanity.iqbal.services.DownloadService;

import java.io.File;

import static com.vanity.iqbal.helper.ExternalMemory.createExternalFolderIfNotExists;
import static com.vanity.iqbal.helper.ExternalMemory.getAudioFilename;
import static com.vanity.iqbal.helper.ExternalMemory.getExternalFilePath;
import static com.vanity.iqbal.helper.ExternalMemory.getExternalFolderPath;
import static com.vanity.iqbal.helper.ExternalMemory.isAudioDownloaded;
import static com.vanity.iqbal.helper.IntentExtras.ExtraFilename;
import static com.vanity.iqbal.helper.IntentExtras.ExtraReceiver;
import static com.vanity.iqbal.helper.IntentExtras.ExtraURL;
import static com.vanity.iqbal.helper.Permissions.REQUEST_WRITE_STORAGE;
import static com.vanity.iqbal.helper.Permissions.hasWriteExternalStoragePermissionOtherwiseRequest;
import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;

/**
 * Created by aghumman on 4/25/2018.
 */

public class FragmentAudioPlayer extends Fragment implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener
{
    Context context;

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private TextView txtHideAudioBox;
    private TextView txtShowAudioBox;
    private RelativeLayout layoutAudioBox;

    private SeekBar audioProgressBar;
    private TextView audioCurrentDurationLabel;
    private TextView audioTotalDurationLabel;

    // Media Player
    private MediaPlayer mp;

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private AudioUtilities utils;

    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds

    private boolean IsMediaPrepared = false;
    private boolean IsAudioNeverPlayedForThisPoem = true;

    private String poemId;
    private String audioUrl;

    ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_audio_player, container, false);
        context = getActivity().getApplicationContext();

        // Audio Controls
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        btnForward = (ImageButton) rootView.findViewById(R.id.btnForward);
        btnBackward = (ImageButton) rootView.findViewById(R.id.btnBackward);
        txtHideAudioBox = (TextView)rootView.findViewById(R.id.txtHideAudioBox);
        txtShowAudioBox = (TextView)rootView.findViewById(R.id.txtShowAudioBox);
        layoutAudioBox = (RelativeLayout) rootView.findViewById(R.id.layoutAudioBox);

        audioProgressBar = (SeekBar) rootView.findViewById(R.id.audioProgressBar);
        audioCurrentDurationLabel = (TextView) rootView.findViewById(R.id.audioCurrentDurationLabel);
        audioTotalDurationLabel = (TextView) rootView.findViewById(R.id.audioTotalDurationLabel);

        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBackward.setOnClickListener(this);
        txtHideAudioBox.setOnClickListener(this);
        txtShowAudioBox.setOnClickListener(this);

        txtShowAudioBox.setVisibility(View.GONE);

        //ProgressBar for showing Download Progress
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Downloading Audio");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        poemId = "";
        audioUrl = "";

        // If Progress Dialog Cancelled
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                File file = new File(getExternalFolderPath(),poemId + ".mp3");
                boolean isDeleted = file.delete();
                if (isDeleted) {
                    Toast.makeText(context, "Downloading Cancelled!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Media Player
        mp = new MediaPlayer();
        utils = new AudioUtilities();

        // Listeners
        audioProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);

        //Play Audio Events
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer _media_player)
            {
                IsMediaPrepared = true;
                _media_player.start();

                // Changing Button Image to pause image
                btnPlay.setImageResource(R.drawable.audio_player_pause);

                // set Progress bar values
                audioProgressBar.setProgress(0);
                audioProgressBar.setMax(100);

                // Updating progress bar
                updateProgressBar();
            }
        });

        return rootView;
    } // onCreateView

    public void setPoemId(String poemId) {
        this.poemId  = poemId;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
        if (audioUrl.equals("")) {
            btnPlay.setImageResource(R.drawable.audio_player_add_sound);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(mp != null) {
            mp.seekTo(0);
            mp.pause();
            btnPlay.setImageResource(R.drawable.audio_player_play);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == btnPlay)
        {
            if (mp != null) {
                if(mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    btnPlay.setImageResource(R.drawable.audio_player_play);
                }
                else {
                    if(IsAudioNeverPlayedForThisPoem)
                    {
                        if (poemId.equals("")) {
                            Toast.makeText(context, "Please select a poem first", Toast.LENGTH_SHORT).show();
                        }
                        else if (isAudioDownloaded(poemId)) {
                        playAudio();
                        btnPlay.setImageResource(R.drawable.audio_player_pause);
                        IsAudioNeverPlayedForThisPoem = false;
                        }
                        else if (!audioUrl.equals("")) {

                            // Get External Storage permissions from user
                            boolean hasPermissionWriteExternal = hasWriteExternalStoragePermissionOtherwiseRequest(getActivity());
                            if (hasPermissionWriteExternal) {
                                createExternalFolderIfNotExists(context, getExternalFolderPath());
                                if (isConnectingToInternet(context)) {
                                    downloadAudioFromInternet();
                                } else {
                                    Toast.makeText(context, "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else {
                            // Prompt user to contribute by adding new audio
                            promptUserToContributeAudio();
                        }
                    }
                    else {
                        // Resume audio
                        mp.start();
                        btnPlay.setImageResource(R.drawable.audio_player_pause);
                    }
                } // if mp is NOT playing
            } // if mp != null

        } // Play/Pause Button
        else if (view == btnForward)
        {
            // get current audio position
            int currentPosition = mp.getCurrentPosition();
            // check if seekForward time is lesser than audio duration
            if(currentPosition + seekForwardTime <= mp.getDuration()) {
                // forward audio
                mp.seekTo(currentPosition + seekForwardTime);
            }
            else {
                // forward to end position
                mp.seekTo(mp.getDuration());
            }
        } // Forward Button
        else if (view == btnBackward)
        {
            // get current audio position
            int currentPosition = mp.getCurrentPosition();
            // check if seekBackward time is greater than 0 sec
            if(currentPosition - seekBackwardTime >= 0) {
                // forward audio
                mp.seekTo(currentPosition - seekBackwardTime);
            }
            else {
                // backward to starting position
                mp.seekTo(0);
            }
        } // Backward Button

        else if (view == txtHideAudioBox)
        {
            layoutAudioBox.setVisibility(View.GONE);
            txtHideAudioBox.setVisibility(View.GONE);
            txtShowAudioBox.setVisibility(View.VISIBLE);
        } // Hide Audio Box
        else if (view == txtShowAudioBox) {
            layoutAudioBox.setVisibility(View.VISIBLE);
            txtHideAudioBox.setVisibility(View.VISIBLE);
            txtShowAudioBox.setVisibility(View.GONE);
        } // Show Audio Box
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //Nothing Here
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (IsMediaPrepared) {
            mHandler.removeCallbacks(mUpdateTimeTask);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (IsMediaPrepared) {
            if (mp != null) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mp.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mp.release();
        mp = null;
    }

    public void playAudio()
    {
        if (poemId.equals("")) {
            Toast.makeText(context, "Please select a poem first", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mp.reset();
            mp.setDataSource(getAudioFilename(poemId)); //audio path
            mp.prepare();
            mp.start();
            IsMediaPrepared = true;
        } // try
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (IsMediaPrepared)
            {
                if (mp != null)
                {
                    long totalDuration = mp.getDuration();
                    long currentDuration = mp.getCurrentPosition();

                    audioTotalDurationLabel.setVisibility(View.VISIBLE);
                    audioCurrentDurationLabel.setVisibility(View.VISIBLE);

                    // Displaying Total Duration time
                    audioTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                    // Displaying time completed playing
                    audioCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
                    audioProgressBar.setProgress(progress);

                    // Running this thread after 100 milliseconds
                    mHandler.postDelayed(this, 100);
                }
            } // if media is prepared
        }
    };

    private void promptUserToContributeAudio() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.soundcloud.com"));
                        startActivity(browserIntent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //Do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upload a Recording!").setMessage("We need your recording of this poem.\n " +
                "Please upload an audio recording on SoundCloud and share with us on our Facebook Page.\n" +
                "If your recording is selected, we will include it in the next version of the app!")
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("Go to SoundCloud", dialogClickListener).show();
    }

    private void downloadAudioFromInternet() {

        mProgressDialog.show();
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(ExtraURL, audioUrl);
        intent.putExtra(ExtraFilename, getExternalFilePath(poemId + ".mp3"));
        intent.putExtra(ExtraReceiver, new DownloadReceiver(new Handler()));

        getActivity().stopService(intent); // if this line is remove, next download will be done simultaneously with the old ones.
        getActivity().startService(intent);
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {

                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);

                //if time difference between 2 messages > n, say: error downloading and delete the file

                int progress = resultData.getInt("progress");

                Log.i("Progress", String.valueOf(progress));

                mProgressDialog.setProgress(progress);
                //mProgressDialog.setTitle(String.valueOf(progress));

                if (progress == 100) {

                    mProgressDialog.dismiss();
                    playAudio();
                } // if done downloading
            }
        }
    } // DownloadReceiver class

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    createExternalFolderIfNotExists(context, getExternalFolderPath());
                    if (isConnectingToInternet(context)) {
                        downloadAudioFromInternet();
                    } else {
                        Toast.makeText(context, "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    Toast.makeText(context, "Unable to create Downloads folder", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
