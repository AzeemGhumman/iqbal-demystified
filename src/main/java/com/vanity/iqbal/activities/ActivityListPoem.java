package com.vanity.iqbal.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vanity.iqbal.R;
import com.vanity.iqbal.factory.ListPoemGenerator;
import com.vanity.iqbal.helper.Preferences;
import com.vanity.iqbal.objects.*;
import com.vanity.iqbal.adapters.PoemListEntryAdapter;

import static com.vanity.iqbal.factory.ListPoemGenerator.CreateListPoem;
import static com.vanity.iqbal.factory.ListPoemGenerator.CreateListPoemFromQuery;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.LIST_SEARCH_RESULTS;
import static com.vanity.iqbal.helper.IntentExtras.ExtraListSelected;
import static com.vanity.iqbal.helper.IntentExtras.ExtraPoemSelected;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSearchQuery;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSearchTitlesOnly;
import static com.vanity.iqbal.helper.Preferences.langToString;
import static com.vanity.iqbal.helper.GeneralUtilities.createEntryAdapterForListPoem;
import static com.vanity.iqbal.helper.GeneralUtilities.findPoemIdGivenPositionInListView;

public class ActivityListPoem extends AppCompatActivity implements OnItemClickListener {

    ListView listview = null;

    int selectedBook;
    String searchQuery;
    boolean isSearchTitleOnly;

    ListPoem listPoem;

	//A ProgressDialog object - to show Loading
	private ProgressDialog progressDialog;

    public void readBundle(Activity activity) {
		Bundle extras = activity.getIntent().getExtras();
		if (extras != null)
		{
			selectedBook = extras.getInt(ExtraListSelected);
			searchQuery = extras.getString(ExtraSearchQuery, "");
			isSearchTitleOnly = extras.getBoolean(ExtraSearchTitlesOnly);
		}
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_poem);

		listview = 	(ListView)findViewById(R.id.ListView_PoemList);
		listview.setFastScrollEnabled(true);

		readBundle(this);

		//set OnClick listener
		listview.setOnItemClickListener(this);

		ListPoemGenerator.ListPoemTypes selectedType = ListPoemGenerator.ListPoemTypes.values()[selectedBook];

        if (selectedType == LIST_SEARCH_RESULTS) {

            // Search for poems in the background
            try {
                TaskSearchQuery task = new TaskSearchQuery(this);
                task.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
			listPoem = CreateListPoem(getApplicationContext(), selectedType);
            setTitle(listPoem.getName(langToString(Preferences.LangType.URDU)).getText());
            PoemListEntryAdapter adapter = createEntryAdapterForListPoem(getApplicationContext(), listPoem, searchQuery);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
		}
	} //onCreate

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
    {
        String poemId = findPoemIdGivenPositionInListView(listPoem, position);
		Intent i = new Intent(getApplicationContext(), ActivityPoem.class);
        i.putExtra(ExtraPoemSelected, poemId);
        i.putExtra(ExtraSearchQuery, searchQuery);
        i.putExtra(ExtraSearchTitlesOnly, isSearchTitleOnly);
		startActivity(i);
	} // an item is clicked


    @SuppressLint("StaticFieldLeak")
    class TaskSearchQuery extends AsyncTask<String, Void, String> {

        private Context context;
        TaskSearchQuery(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Searching for " + searchQuery);
            progressDialog.show();
        }

        protected String doInBackground(String... fields)
        {
            listPoem = CreateListPoemFromQuery(getApplicationContext(), searchQuery, isSearchTitleOnly);
            return ""; // nothing to return, updated ListPoem
        } // doInBackground

        protected void onPostExecute(String result)
        {
            setTitle(listPoem.getName(langToString(Preferences.LangType.URDU)).getText());
            PoemListEntryAdapter adapter = createEntryAdapterForListPoem(getApplicationContext(), listPoem, searchQuery);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

    } // TaskSearchQuery
}
