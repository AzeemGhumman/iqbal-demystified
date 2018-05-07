package com.vanity.iqbal.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vanity.iqbal.activities.ActivityPoem;
import com.vanity.iqbal.R;
import com.vanity.iqbal.factory.ListPoemGenerator;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.adapters.PoemListEntryAdapter;

import static com.vanity.iqbal.factory.ListPoemGenerator.CreateListPoem;
import static com.vanity.iqbal.helper.GeneralUtilities.createEntryAdapterForListPoem;
import static com.vanity.iqbal.helper.GeneralUtilities.findPoemIdGivenPositionInListView;
import static com.vanity.iqbal.helper.IntentExtras.ExtraListSelected;
import static com.vanity.iqbal.helper.IntentExtras.ExtraPoemSelected;

public class FragmentBookmarkPoem extends Fragment implements AdapterView.OnItemClickListener {

    Context context;

    ListView listview = null;

    // To load/save preferences
    SharedPreferences prefs;

    int selectedBook;
    ListPoem listPoem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_list_poem, container, false);

        //Initialize Activity
        context = getActivity().getApplicationContext();


        listview = 	(ListView)rootView.findViewById(R.id.ListView_PoemList);
        listview.setFastScrollEnabled(true);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            selectedBook = extras.getInt(ExtraListSelected, 0);
            // TODO: Set page title - based on type of list
        }

        //OnClick and OnLongClick
        listview.setOnItemClickListener(this);

        // TODO: This value()[index] usage is scary - any gaurantees on it never being invalid? outside range?
        listPoem = CreateListPoem(context, ListPoemGenerator.ListPoemTypes.values()[selectedBook]);

        PoemListEntryAdapter adapter = createEntryAdapterForListPoem(context, listPoem, "");
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        return rootView;
    }//onCreateView


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        String poemId = findPoemIdGivenPositionInListView(listPoem, position);

        Intent intent = new Intent(context, ActivityPoem.class);
        intent.putExtra(ExtraPoemSelected, poemId);
        startActivity(intent);
    }


}//end of Fragment

