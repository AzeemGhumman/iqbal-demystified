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

import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_ARMAGHAN_AE_HIJAZ_11;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_ASRAR_AE_KHUDI_5;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_JAVED_NAMA_9;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.BOOK_PERSIAN_PAYAM_AE_MASHRIQ_7;
import static com.vanity.iqbal.helper.IntentExtras.ExtraListSelected;

public class FragmentPersianPartOne extends Fragment{

	ImageView BtnBook_Persian_Asrar_Ae_Khudi_5, BtnBook_Persian_Payam_Ae_Mashriq_7;
	ImageView BtnBook_Persian_Javed_Nama_9, BtnBook_Persian_Armaghan_Ae_Hijaz_11;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_persian_part_one, container, false);

        BtnBook_Persian_Asrar_Ae_Khudi_5 = (ImageView)rootView.findViewById(R.id.BtnBook_Persian_Asrar_Ae_Khudi_5);
        BtnBook_Persian_Payam_Ae_Mashriq_7 = (ImageView)rootView.findViewById(R.id.BtnBook_Persian_Payam_Ae_Mashriq_7);
        BtnBook_Persian_Javed_Nama_9 = (ImageView)rootView.findViewById(R.id.BtnBook_Persian_Javed_Nama_9);
        BtnBook_Persian_Armaghan_Ae_Hijaz_11 = (ImageView)rootView.findViewById(R.id.BtnBook_Persian_Armaghan_Ae_Hijaz_11);

        BtnBook_Persian_Asrar_Ae_Khudi_5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                Intent i = new Intent(getActivity(), ActivityListPoem.class);
                i.putExtra(ExtraListSelected, BOOK_PERSIAN_ASRAR_AE_KHUDI_5.ordinal());
                startActivity(i);
            }
        });

        BtnBook_Persian_Payam_Ae_Mashriq_7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                Intent i = new Intent(getActivity(), ActivityListPoem.class);
                i.putExtra(ExtraListSelected, BOOK_PERSIAN_PAYAM_AE_MASHRIQ_7.ordinal());
                startActivity(i);
            }
        });

        BtnBook_Persian_Javed_Nama_9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_PERSIAN_JAVED_NAMA_9.ordinal());
				startActivity(i);
			}
		});

        BtnBook_Persian_Armaghan_Ae_Hijaz_11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				Intent i = new Intent(getActivity(), ActivityListPoem.class);
				i.putExtra(ExtraListSelected, BOOK_PERSIAN_ARMAGHAN_AE_HIJAZ_11.ordinal());
				startActivity(i);
			}
		});

		return rootView;
	} // onCreate
}
