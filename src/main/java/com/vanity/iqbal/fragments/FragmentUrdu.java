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

import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_URDU_ARMAGHAN_AE_HIJAZ_4;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_URDU_BAL_AE_JABREEL_2;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_URDU_BANG_AE_DARA_1;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_URDU_ZARB_AE_KALEEM_3;
import static com.vanity.iqbal.helper.IntentExtras.ExtraListSelected;

public class FragmentUrdu extends Fragment{

	ImageView BtnBook_Urdu_Bang_Ae_Dara_1, BtnBook_Urdu_Bal_Ae_Jabreel_2;
	ImageView BtnBook_Urdu_Zarb_Ae_Kaleem_3, BtnBook_Urdu_Armaghan_Ae_Hijaz_4;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_urdu, container, false);

		BtnBook_Urdu_Bang_Ae_Dara_1 = (ImageView)rootView.findViewById(R.id.BtnBook_Urdu_Bang_Ae_Dara_1);
		BtnBook_Urdu_Bal_Ae_Jabreel_2 = (ImageView)rootView.findViewById(R.id.BtnBook_Urdu_Bal_Ae_Jabreel_2);
		BtnBook_Urdu_Zarb_Ae_Kaleem_3 = (ImageView)rootView.findViewById(R.id.BtnBook_Urdu_Zarb_Ae_Kaleem_3);
		BtnBook_Urdu_Armaghan_Ae_Hijaz_4 = (ImageView)rootView.findViewById(R.id.BtnBook_Urdu_Armaghan_Ae_Hijaz_4);


        BtnBook_Urdu_Bang_Ae_Dara_1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_URDU_BANG_AE_DARA_1.ordinal());
				startActivity(i);
			}
		});

		BtnBook_Urdu_Bal_Ae_Jabreel_2.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View arg0) 
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_URDU_BAL_AE_JABREEL_2.ordinal());
				startActivity(i);
			}
		});

		BtnBook_Urdu_Zarb_Ae_Kaleem_3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_URDU_ZARB_AE_KALEEM_3.ordinal());
				startActivity(i);
			}
		});

		BtnBook_Urdu_Armaghan_Ae_Hijaz_4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_URDU_ARMAGHAN_AE_HIJAZ_4.ordinal());
				startActivity(i);
			}
		});
		return rootView;
	} // onCreateView
}