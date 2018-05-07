package com.vanity.iqbal.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.vanity.iqbal.R;

import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.LIST_SEARCH_RESULTS;
import static com.vanity.iqbal.helper.IntentExtras.ExtraListSelected;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSearchQuery;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSearchTitlesOnly;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextTypefacePreference;

public class ActivitySearch extends AppCompatActivity implements OnClickListener{

	RadioButton RadioTitle, RadioTitleAndText;
	EditText TxtSearchUrdu;
	Button A_BACK, A_KH, A_H, A_CH, A_J, A_SEY, A_TAY, A_TY, A_P, A_B, A_AAA, A_A;
	Button A_ZUAD, A_SUAD, A_SHEEN, A_SEEN, A_SAAL, A_ZAAL, A_RREY, A_RAY, A_ZAA, A_DDAA, A_DAA;
	Button A_GAAN, A_N, A_M, A_L, A_G, A_KAAF, A_KUAF, A_F, A_GAIN, A_AYIN, A_ZUAA, A_TUAA;
	Button A_CLEAR, A_BARI_YE, A_CHOTI_YE, A_HAMZA, A_SPACE, A_HEYKIBANI, A_GOL_HA, A_WOW, A_SEARCH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		setTitle("Search");

		Typeface primaryTypeface = getPrimaryTextTypefacePreference(this);

		RadioTitle = (RadioButton)findViewById(R.id.RadioTitle);
		RadioTitleAndText = (RadioButton)findViewById(R.id.RadioTitleAndText);
		
		TxtSearchUrdu = (EditText)findViewById(R.id.TxtSearchUrdu);

        // Keyboard: 1st Row - left to right
		A_BACK = (Button)findViewById(R.id.A_BACK);
		A_KH = (Button)findViewById(R.id.A_KH);
		A_H = (Button)findViewById(R.id.A_H);
		A_CH = (Button)findViewById(R.id.A_CH);
		A_J = (Button)findViewById(R.id.A_J);
		A_SEY = (Button)findViewById(R.id.A_SEY);
		A_TAY = (Button)findViewById(R.id.A_TAY);
		A_TY = (Button)findViewById(R.id.A_TY);
		A_P = (Button)findViewById(R.id.A_P);
		A_B = (Button)findViewById(R.id.A_B);
		A_AAA = (Button)findViewById(R.id.A_AAA);
		A_A = (Button)findViewById(R.id.A_A);

        // Keyboard: 2nd Row - left to right
		A_ZUAD = (Button)findViewById(R.id.A_ZUAD);
		A_SUAD = (Button)findViewById(R.id.A_SUAD);
		A_SHEEN = (Button)findViewById(R.id.A_SHEEN);
		A_SEEN = (Button)findViewById(R.id.A_SEEN);
		A_SAAL = (Button)findViewById(R.id.A_SAAL);
		A_ZAAL = (Button)findViewById(R.id.A_ZAAL);
		A_RREY = (Button)findViewById(R.id.A_RREY);
		A_RAY = (Button)findViewById(R.id.A_RAY);
		A_ZAA = (Button)findViewById(R.id.A_ZAA);
		A_DDAA = (Button)findViewById(R.id.A_DDAA);
		A_DAA = (Button)findViewById(R.id.A_DAA);

        // Keyboard: 3rd Row - left to right
		A_GAAN = (Button)findViewById(R.id.A_GAAN);
		A_N = (Button)findViewById(R.id.A_N);
		A_M = (Button)findViewById(R.id.A_M);
		A_L = (Button)findViewById(R.id.A_L);
		A_G = (Button)findViewById(R.id.A_G);
		A_KAAF = (Button)findViewById(R.id.A_KAAF);
		A_KUAF = (Button)findViewById(R.id.A_KUAF);
		A_F = (Button)findViewById(R.id.A_F);
		A_GAIN = (Button)findViewById(R.id.A_GAIN);
		A_AYIN = (Button)findViewById(R.id.A_AYIN);
		A_ZUAA = (Button)findViewById(R.id.A_ZUAA);
		A_TUAA = (Button)findViewById(R.id.A_TUAA);

        // Keyboard: 4th Row - left to right
		A_CLEAR = (Button)findViewById(R.id.A_CLEAR);
		A_BARI_YE = (Button)findViewById(R.id.A_BARI_YE);
		A_CHOTI_YE = (Button)findViewById(R.id.A_CHOTI_YE);
		A_HAMZA = (Button)findViewById(R.id.A_HAMZA);
		A_SPACE = (Button)findViewById(R.id.A_SPACE);
		A_HEYKIBANI = (Button)findViewById(R.id.A_HEYKIBANI);
		A_GOL_HA = (Button)findViewById(R.id.A_GOL_HA);
		A_WOW = (Button)findViewById(R.id.A_WOW);
		A_SEARCH = (Button)findViewById(R.id.A_SEARCH);

		//Button Clicked - Event
		A_BACK.setOnClickListener(this); 
		A_KH.setOnClickListener(this); 
		A_H.setOnClickListener(this); 
		A_CH.setOnClickListener(this); 
		A_J.setOnClickListener(this); 
		A_SEY.setOnClickListener(this); 
		A_TAY.setOnClickListener(this); 
		A_TY.setOnClickListener(this); 
		A_P.setOnClickListener(this); 
		A_B.setOnClickListener(this); 
		A_AAA.setOnClickListener(this); 
		A_A.setOnClickListener(this);
		A_ZUAD.setOnClickListener(this); 
		A_SUAD.setOnClickListener(this); 
		A_SHEEN.setOnClickListener(this); 
		A_SEEN.setOnClickListener(this); 
		A_SAAL.setOnClickListener(this); 
		A_ZAAL.setOnClickListener(this); 
		A_RREY.setOnClickListener(this); 
		A_RAY.setOnClickListener(this); 
		A_ZAA.setOnClickListener(this); 
		A_DDAA.setOnClickListener(this); 
		A_DAA.setOnClickListener(this);
		A_GAAN.setOnClickListener(this); 
		A_N.setOnClickListener(this); 
		A_M.setOnClickListener(this); 
		A_L.setOnClickListener(this); 
		A_G.setOnClickListener(this); 
		A_KAAF.setOnClickListener(this); 
		A_KUAF.setOnClickListener(this); 
		A_F.setOnClickListener(this); 
		A_GAIN.setOnClickListener(this); 
		A_AYIN.setOnClickListener(this); 
		A_ZUAA.setOnClickListener(this); 
		A_TUAA.setOnClickListener(this);
		A_CLEAR.setOnClickListener(this); 
		A_BARI_YE.setOnClickListener(this); 
		A_CHOTI_YE.setOnClickListener(this); 
		A_HAMZA.setOnClickListener(this); 
		A_SPACE.setOnClickListener(this); 
		A_HEYKIBANI.setOnClickListener(this); 
		A_GOL_HA.setOnClickListener(this); 
		A_WOW.setOnClickListener(this); 
		A_SEARCH.setOnClickListener(this);

		TxtSearchUrdu.setTypeface(primaryTypeface, Typeface.NORMAL);

		//A_BACK.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_KH.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_H.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_CH.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_J.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_SEY.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_TAY.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_TY.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_P.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_B.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_AAA.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_A.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_ZUAD.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_SUAD.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_SHEEN.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_SEEN.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_SAAL.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_ZAAL.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_RREY.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_RAY.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_ZAA.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_DDAA.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_DAA.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_GAAN.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_N.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_M.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_L.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_G.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_KAAF.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_KUAF.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_F.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_GAIN.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_AYIN.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_ZUAA.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_TUAA.setTypeface(primaryTypeface, Typeface.NORMAL);
		//A_CLEAR.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_BARI_YE.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_CHOTI_YE.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_HAMZA.setTypeface(primaryTypeface, Typeface.NORMAL);
		//A_SPACE.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_HEYKIBANI.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_GOL_HA.setTypeface(primaryTypeface, Typeface.NORMAL);
		A_WOW.setTypeface(primaryTypeface, Typeface.NORMAL);
		//A_SEARCH.setTypeface(primaryTypeface, Typeface.NORMAL);

	}//OnCreate

	@Override
	public void onClick(View v) 
	{
		if (v == A_BACK) 
        {
            if (!TxtSearchUrdu.getText().toString().equals("")) //if not empty
            TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString().substring(0, TxtSearchUrdu.getText().toString().length() - 1));
        }

		if (v == A_KH) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "خ");
		if (v == A_H) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ح");
		if (v == A_CH) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "چ");
		if (v == A_J) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ج");
		if (v == A_SEY) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ث");
		if (v == A_TAY) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ٹ");
		if (v == A_TY) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ت");
		if (v == A_P) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "پ");
		if (v == A_B) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ب");
		if (v == A_AAA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ا");
		if (v == A_A) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "آ");
		if (v == A_ZUAD) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ض");
		if (v == A_SUAD) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ص");
		if (v == A_SHEEN) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ش");
		if (v == A_SEEN) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "س");
		if (v == A_SAAL) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ژ");
		if (v == A_ZAAL) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ز");
		if (v == A_RREY) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ڑ");
		if (v == A_RAY) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ر");
		if (v == A_ZAA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ذ");
		if (v == A_DDAA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ڈ");
		if (v == A_DAA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "د");
		if (v == A_GAAN) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ں");
		if (v == A_N) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ن");
		if (v == A_M) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "م");
		if (v == A_L) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ل");
		if (v == A_G) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "گ");
		if (v == A_KAAF) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ک");
		if (v == A_KUAF) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ق");
		if (v == A_F) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ف");
		if (v == A_GAIN) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "غ");
		if (v == A_AYIN) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ع");
		if (v == A_ZUAA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ظ");
		if (v == A_TUAA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ط");
		
		if (v == A_CLEAR) TxtSearchUrdu.setText("");
		
		if (v == A_BARI_YE) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ے");
		if (v == A_CHOTI_YE) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ي");
		if (v == A_HAMZA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ء");
		
		if (v == A_SPACE) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + " ");
		
		if (v == A_HEYKIBANI) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ھ");
		if (v == A_GOL_HA) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "ہ");
		if (v == A_WOW) TxtSearchUrdu.setText(TxtSearchUrdu.getText().toString() + "و");
		
		if (v == A_SEARCH)
		{
            searchQuery(TxtSearchUrdu.getText().toString());
		}
	}//button clicked

	public void searchQuery(String query)
	{
		query = query.trim();
		if (!query.equals(""))
		{
            // Check if user wants to search only titles or titles + text
            boolean is_title_only = true;
            if (RadioTitleAndText.isChecked()) {
                is_title_only = false;
            }

            Intent i = new Intent(getApplicationContext(), ActivityListPoem.class);
            i.putExtra(ExtraListSelected, LIST_SEARCH_RESULTS.ordinal());
            i.putExtra(ExtraSearchQuery,query);
            i.putExtra(ExtraSearchTitlesOnly, is_title_only);
            startActivity(i);
		} // if query not empty
		else
		{
			Toast.makeText(getApplicationContext(), "Please type something before searching!", Toast.LENGTH_SHORT).show();
		}
	} // searchQuery
} // class
