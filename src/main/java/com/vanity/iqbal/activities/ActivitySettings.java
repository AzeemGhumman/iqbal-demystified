package com.vanity.iqbal.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vanity.iqbal.R;
import com.vanity.iqbal.services.DownloadService;
import com.vanity.iqbal.objects.Font;
import com.vanity.iqbal.helper.Preferences;

import static com.vanity.iqbal.helper.ExternalMemory.createExternalFolderIfNotExists;
import static com.vanity.iqbal.helper.ExternalMemory.getAllFonts;
import static com.vanity.iqbal.helper.ExternalMemory.getExternalFilePath;
import static com.vanity.iqbal.helper.ExternalMemory.getExternalFolderPath;
import static com.vanity.iqbal.helper.ExternalMemory.isFontAvailable;
import static com.vanity.iqbal.helper.IntentExtras.ExtraFilename;
import static com.vanity.iqbal.helper.IntentExtras.ExtraReceiver;
import static com.vanity.iqbal.helper.IntentExtras.ExtraURL;
import static com.vanity.iqbal.helper.Permissions.REQUEST_WRITE_STORAGE;
import static com.vanity.iqbal.helper.Permissions.hasWriteExternalStoragePermissionOtherwiseRequest;
import static com.vanity.iqbal.helper.Preferences.getPrimaryLanguagePreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextFontPreference;
import static com.vanity.iqbal.helper.Preferences.setPrimaryLanguagePreference;
import static com.vanity.iqbal.helper.Preferences.setPrimaryTextFontPreference;
import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;

public class ActivitySettings extends AppCompatActivity implements android.view.View.OnClickListener{

	EditText tvComments;
	Button btnSubmitComments;
	ImageView BtnOurFacebookPage;
	ImageView BtnGithubAndroidApp, BtnGithubDataset, BtnGithubWebClient;
    ImageButton BtnIqbalWebsite1, BtnIqbalWebsite2;

	RadioGroup radioGroupFonts;
    List<RadioButton> radioFonts;

	TextView TxtContribute;
	RadioButton radioRoman, radioUrdu;
	
	ProgressDialog mProgressDialog;

	String FontBeingDownloaded; //this is to cancel downloading if user cancels: deletes the font from memory

    List<Font> fontObjects;

    Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	
		setTitle("Settings");
		
		//Don't show keyboard at start of activity
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        activity = this;

		//Attach Controls 
		tvComments = (EditText)findViewById(R.id.tvComments);
		btnSubmitComments = (Button)findViewById(R.id.btnSubmitComments);
		BtnOurFacebookPage = (ImageView)findViewById(R.id.BtnOurFacebookPage);
        BtnGithubAndroidApp = (ImageView)findViewById(R.id.BtnGithubAndroidApp);
        BtnGithubDataset = (ImageView)findViewById(R.id.BtnGithubDataset);
        BtnGithubWebClient = (ImageView)findViewById(R.id.BtnGithubWebClient);
		BtnIqbalWebsite1 = (ImageButton)findViewById(R.id.BtnIqbalWebsite1);
		BtnIqbalWebsite2 = (ImageButton)findViewById(R.id.BtnIqbalWebsite2);
		TxtContribute = (TextView)findViewById(R.id.TxtContribute);
		radioRoman = (RadioButton)findViewById(R.id.radioRoman);
		radioUrdu = (RadioButton)findViewById(R.id.radioUrdu);
		
		//CheckBestFont = (CheckBox)findViewById(R.id.CheckBestFont);
		radioGroupFonts = (RadioGroup) findViewById(R.id.radioGroup_Fonts);

		// Create the Fonts Radio Buttons
        fontObjects = getAllFonts();
        radioFonts = new ArrayList<>();

        for(int fontIndex = 0; fontIndex < fontObjects.size(); fontIndex ++) {
            RadioButton radioBtn = new RadioButton(this);

            String fontName = fontObjects.get(fontIndex).getLabel();
            boolean isFontAvailable = isFontAvailable(fontObjects.get(fontIndex).getType());

            if (fontObjects.get(fontIndex).isNative()) {
                radioBtn.setText(fontName);
            } else if (isFontAvailable) {
                radioBtn.setText(fontName + " - NEW");
                radioBtn.setTextColor(Color.parseColor("#55AA33"));
            } else {
                radioBtn.setText(fontName + " - NEW");
                radioBtn.setTextColor(Color.GRAY);
            }
            radioFonts.add(radioBtn);
            // add radio buttons to the group
            radioGroupFonts.addView(radioBtn);
        }

        // Select the font by reading user's preferences
        Preferences.FontType fontSelected = getPrimaryTextFontPreference(this);
        for(int fontIndex = 0; fontIndex < fontObjects.size(); fontIndex ++) {
            // Check the state from user's preference
            if (fontSelected == fontObjects.get(fontIndex).getType()) {
                radioFonts.get(fontIndex).setChecked(true);
            }
        }

		//Attach Events
		btnSubmitComments.setOnClickListener(this);
		BtnOurFacebookPage.setOnClickListener(this);
		BtnGithubAndroidApp.setOnClickListener(this);
        BtnGithubDataset.setOnClickListener(this);
        BtnGithubWebClient.setOnClickListener(this);
		BtnIqbalWebsite1.setOnClickListener(this);
		BtnIqbalWebsite2.setOnClickListener(this);
		
		tvComments.setTextColor(Color.argb(255, 30, 120, 40));
		
		//ProgressBar for showing Download Progress
        mProgressDialog = new ProgressDialog(ActivitySettings.this);
        mProgressDialog.setMessage("Downloading Font");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // If Progress Dialog Cancelled
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            	File file = new File(getExternalFolderPath(), FontBeingDownloaded);
            	boolean isDeleted = file.delete();
                if (isDeleted) {
                    Toast.makeText(getApplicationContext(), "Downloading Cancelled!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Find PreferencePrimaryLanguage
        Preferences.LangType primaryLanguage = getPrimaryLanguagePreference(this);
        if (primaryLanguage == Preferences.LangType.ROMAN) {
            radioRoman.setChecked(true);
        }

        // User changes the Font selection
        radioGroupFonts.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // For all the radio buttons, find the one that was clicked
                for (int fontIndex = 0; fontIndex < fontObjects.size(); fontIndex ++) {
                    // If fontIndex is the one that was clicked
                    if (checkedId == radioFonts.get(fontIndex).getId()) {

                        String fontName = fontObjects.get(fontIndex).getLabel();
                        String fontUrl = fontObjects.get(fontIndex).getUrl();
                        String filename = fontObjects.get(fontIndex).getFilename();
                        boolean isNative = fontObjects.get(fontIndex).isNative();
                        boolean isAlreadyDownloaded = isFontAvailable(fontObjects.get(fontIndex).getType());
                        if (isNative || isAlreadyDownloaded)
                        {
                            setPrimaryTextFontPreference(fontObjects.get(fontIndex).getType(), getApplicationContext());
                            Toast.makeText(getApplicationContext(), fontName + " Selected!", Toast.LENGTH_SHORT).show();
                        }
                        else { /* font needs to be downloaded */

                            // Create External folder if not exists
                            boolean hasPermissionWriteExternal = hasWriteExternalStoragePermissionOtherwiseRequest(activity);
                            if (hasPermissionWriteExternal) {
                                createExternalFolderIfNotExists(getApplicationContext(), getExternalFolderPath());
                            }
                            // If connected to internet, download the font
                            if (isConnectingToInternet(getApplicationContext())) {
                                DownloadFont(fontName, fontUrl, getExternalFilePath(filename));
                            } else {
                                Toast.makeText(getApplicationContext(), "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } // if fontIndex was clicked
                } // for all the radio buttons

            }//onCheckedChanged

        });

		radioRoman.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if (radioRoman.isChecked())
				{
					Toast.makeText(getApplicationContext(), "Roman Text Selected!", Toast.LENGTH_SHORT).show();
					setPrimaryLanguagePreference(Preferences.LangType.ROMAN, getApplicationContext());
				}//if it's roman text is checked

				else if (radioUrdu.isChecked())
				{
					Toast.makeText(getApplicationContext(), "Urdu Text Selected!", Toast.LENGTH_SHORT).show();
                    setPrimaryLanguagePreference(Preferences.LangType.URDU, getApplicationContext());
				}//if it's urdu text is checked
			}
		});

	}//onCreate

	@Override
	public void onClick(View v) 
	{
		if (v == btnSubmitComments)
		{
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"azeemghumman3@gmail.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, "Iqbal Demystified App - User Email");
			i.putExtra(Intent.EXTRA_TEXT   , tvComments.getText().toString());
			
			try 
			{
			    startActivity(Intent.createChooser(i, "Send email using..."));
			}
			catch (android.content.ActivityNotFoundException ex) 
			{
			    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		}
		else if (v == BtnOurFacebookPage)
		{
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/Iqbal-Demystified/671684026235997"));
			startActivity(browserIntent);
		}
		
		else if (v == BtnIqbalWebsite1)
		{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://iqbal.com.pk/"));
            startActivity(browserIntent);
		}
		else if (v == BtnIqbalWebsite2)
		{
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.iap.gov.pk/"));
			startActivity(browserIntent);
		}
        else if (v == BtnGithubAndroidApp)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AzeemGhumman/iqbal-demystified-android-app"));
            startActivity(browserIntent);
        }
        else if (v == BtnGithubDataset)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AzeemGhumman/iqbal-demystified-dataset"));
            startActivity(browserIntent);
        }
        else if (v == BtnGithubWebClient)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AzeemGhumman/iqbal-demystified-web-client"));
            startActivity(browserIntent);
        }
	} // some button is clicked

    public void DownloadFont(String label, String url, String filename)
    {
        mProgressDialog.show();
        mProgressDialog.setMessage("Downloading " + label);
        Intent intent = new Intent(getApplicationContext(), DownloadService.class);
        intent.putExtra(ExtraURL, url);
        intent.putExtra(ExtraFilename, filename);
        intent.putExtra(ExtraReceiver, new DownloadReceiver(new Handler()));

        FontBeingDownloaded = label;
        stopService(intent); // if this line is remove, next download will be done simultaneously with the old ones.
        startService(intent);

    }//DownloadFont

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
	            	// Find the font that just got downloaded
                    for(int fontIndex = 0; fontIndex < fontObjects.size(); fontIndex ++) {
                        if (FontBeingDownloaded.equals(fontObjects.get(fontIndex).getLabel()))
                        {
                            setPrimaryTextFontPreference(fontObjects.get(fontIndex).getType(), getApplicationContext());
                            Toast.makeText(getApplicationContext(), "Font Downloaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
	            } // if done downloading
	        }
	    }
	}//DownloadReceiver class

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    createExternalFolderIfNotExists(getApplicationContext(), getExternalFolderPath());
                } else
                {
                    Toast.makeText(this, "Unable to create Downloads folder", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}//Info Activity class
