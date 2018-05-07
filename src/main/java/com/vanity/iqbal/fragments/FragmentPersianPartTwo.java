package com.vanity.iqbal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vanity.iqbal.activities.ActivityListPoem;
import com.vanity.iqbal.R;

import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_ASRAR_AE_KHUDI_5;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_PAS_CHEH_BAYAD_KARD_10;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_RUMUZ_AE_BEKHUDI_6;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_ZABUR_AE_AJAM_8;
import static com.vanity.iqbal.helper.IntentExtras.ExtraListSelected;

public class FragmentPersianPartTwo extends Fragment {

	ImageView BtnBook_Persian_Rumuz_Ae_Bekhudi_6;
	ImageView BtnBook_Persian_Zabur_Ae_Ajam_8;
	ImageView BtnBook_Persian_Pas_Cheh_Bayad_Kard_10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_persian_part_two, container, false);

		BtnBook_Persian_Rumuz_Ae_Bekhudi_6 = (ImageView)rootView.findViewById(R.id.BtnBook_Persian_Rumuz_Ae_Bekhudi_6);
		BtnBook_Persian_Zabur_Ae_Ajam_8 = (ImageView)rootView.findViewById(R.id.BtnBook_Persian_Zabur_Ae_Ajam_8);
		BtnBook_Persian_Pas_Cheh_Bayad_Kard_10 = (ImageView)rootView.findViewById(R.id.BtnBook_Persian_Pas_Cheh_Bayad_Kard_10);

		BtnBook_Persian_Pas_Cheh_Bayad_Kard_10.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) 
					{
						Intent i = new Intent(getActivity(), ActivityListPoem.class);
						i.putExtra(ExtraListSelected, BOOK_PERSIAN_PAS_CHEH_BAYAD_KARD_10.ordinal());
						startActivity(i);
					}
				});

		BtnBook_Persian_Rumuz_Ae_Bekhudi_6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0)
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_PERSIAN_RUMUZ_AE_BEKHUDI_6.ordinal());
				startActivity(i);
			}
		});

		BtnBook_Persian_Zabur_Ae_Ajam_8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0)
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_PERSIAN_ZABUR_AE_AJAM_8.ordinal());
				startActivity(i);
			}
		});

		return rootView;
	} // onCreateView
}