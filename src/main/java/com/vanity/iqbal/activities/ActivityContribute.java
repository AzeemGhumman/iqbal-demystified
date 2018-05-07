package com.vanity.iqbal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vanity.iqbal.R;

import static com.vanity.iqbal.helper.IntentExtras.ExtraPoemHeadingUrdu;

public class ActivityContribute extends AppCompatActivity implements OnClickListener{

	EditText tvComments;
	Button btnSubmitComments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contribute);
		
		setTitle("Contribute!");

		//Don't show keyboard at start of activity
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		String poemHeading = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			poemHeading = extras.getString(ExtraPoemHeadingUrdu, "");
		}
		
		tvComments = (EditText)findViewById(R.id.tvCommentsContribution);
		btnSubmitComments = (Button)findViewById(R.id.btnSubmitCommentsContribution);
		
		btnSubmitComments.setOnClickListener(this);

		//If user is coming from a Poem page, populate the Comments box
		if (!poemHeading.equals("")) {
			tvComments.setText("Asalamualikum, I want to add the Introduction of the Poem: " + poemHeading + "\n\n\n\n\n\n");
		}
	}//onCreate

	@Override
	public void onClick(View v) 
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"azeemghumman3@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Iqbal Demystified App - User Email");
		intent.putExtra(Intent.EXTRA_TEXT   , tvComments.getText().toString());
		
		try {
		    startActivity(Intent.createChooser(intent, "Send email using..."));
		}
		catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}