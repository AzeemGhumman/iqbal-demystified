package com.vanity.iqbal.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vanity.iqbal.R;
import com.vanity.iqbal.adapters.SherListEntryAdapter;
import com.vanity.iqbal.list_items.SherListEntryItem;
import com.vanity.iqbal.tabs.tab_activities.TabsDiscuss;
import com.vanity.iqbal.factory.ListPoemGenerator;
import com.vanity.iqbal.factory.ListSherGenerator;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.objects.Section;
import com.vanity.iqbal.objects.Sher;

import java.util.ArrayList;

import static com.vanity.iqbal.factory.ListPoemGenerator.CreateListPoem;
import static com.vanity.iqbal.factory.ListSherGenerator.CreateListSher;
import static com.vanity.iqbal.factory.ListSherGenerator.CreateListSherFromCsvString;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSherSelected;
import static com.vanity.iqbal.helper.Preferences.getCachedDiscussionFeedPreference;
import static com.vanity.iqbal.helper.Preferences.setCachedDiscussionFeedPreference;
import static com.vanity.iqbal.helper.GeneralUtilities.getListSherEntryItemFromSher;
import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;

public class FragmentListSher extends Fragment implements OnItemClickListener  {

    Activity activity;
    Context context;

    ListView listview = null;

    // Type of feed
    int selectedListTypeInt;

    //A ProgressDialog object - to show Loading
    private ProgressDialog progressDialog;

    ArrayList<Sher> listBookmarkedShers;
    ArrayList<Sher> listShers;
    ArrayList<SherListEntryItem> items = new ArrayList<>();

    public static FragmentListSher newInstance(ListSherGenerator.ListSherTypes _listType) {
        Bundle bundle = new Bundle();
        bundle.putInt("list_sher_type", _listType.ordinal());

        FragmentListSher fragment = new FragmentListSher();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_sher, container, false);

        activity = getActivity();
        context = activity.getApplicationContext();

        //Populate controls from page
        listview = 	(ListView) rootView.findViewById(R.id.listView_listShers);
        listview.setFastScrollEnabled(true);
        listview.setOnItemClickListener(this);

        // Read type of feed
        Bundle extras = getArguments();
        if (extras != null) {
            this.selectedListTypeInt = extras.getInt("list_sher_type");
        }

        // Find bookmarked shers
        listBookmarkedShers = CreateListSher(context, ListSherGenerator.ListSherTypes.BOOKMARKED_SHERS);

        ListSherGenerator.ListSherTypes selectedListType = ListSherGenerator.ListSherTypes.values()[selectedListTypeInt];
        if (selectedListType == ListSherGenerator.ListSherTypes.FEED_RECENT) {
            loadDiscussionFeed(selectedListType, "http://icanmakemyownapp.com/iqbal/v3/feed.php?type=recent");
        }
        else if (selectedListType == ListSherGenerator.ListSherTypes.FEED_POPULAR) {
            loadDiscussionFeed(selectedListType, "http://icanmakemyownapp.com/iqbal/v3/feed.php?type=popular");
        }
        else if (selectedListType == ListSherGenerator.ListSherTypes.FEED_BOOKMARKED_POEMS) {
            ListPoem listBookmarkedPoems = CreateListPoem(context, ListPoemGenerator.ListPoemTypes.LIST_BOOKMARKED_POEMS);
            // Get list of bookmarked poems
            ArrayList<String> poemList = new ArrayList<>();

            // Add bookmarked PoemIds to list
            for (int sectionIndex = 0; sectionIndex < listBookmarkedPoems.getSections().size(); sectionIndex ++) {
                Section section = listBookmarkedPoems.getSections().get(sectionIndex);
                for (int poemIndex = 0; poemIndex < section.getPoems().size(); poemIndex ++) {
                    String poemId = section.getPoems().get(poemIndex).getId();
                    poemList.add(poemId);
                }
            }
            String poemListString = TextUtils.join(",", poemList);
            loadDiscussionFeed(selectedListType, "http://icanmakemyownapp.com/iqbal/v3/feed.php?type=bookmarked&poemList=" + poemListString);
        }

        else if (selectedListType == ListSherGenerator.ListSherTypes.BOOKMARKED_SHERS) {
            // Find bookmarked shers
            listShers = CreateListSher(context, ListSherGenerator.ListSherTypes.BOOKMARKED_SHERS);

            // Create List<SherEntryItem>
            items = getListSherEntryItemFromSher(context, listShers);

            if (listBookmarkedShers.size() == 0) {
                Toast.makeText(context, "No Shers Bookmarked", Toast.LENGTH_SHORT).show();
            }
            SherListEntryAdapter adapter = new SherListEntryAdapter(getActivity(), items, listBookmarkedShers);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        return rootView;
    } //onCreateView

    public void loadDiscussionFeed(ListSherGenerator.ListSherTypes listType, String serverUrl)
    {
        String cached = getCachedDiscussionFeedPreference(context, listType);
        if (!cached.equals("")) {

            listShers = CreateListSherFromCsvString(context, cached);
            items = getListSherEntryItemFromSher(context, listShers);

            SherListEntryAdapter adapter = new SherListEntryAdapter(activity, items, listBookmarkedShers);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else {
            getDiscussionFeedFromServer(listType, serverUrl);
        }
    }

    public void getDiscussionFeedFromServer(final ListSherGenerator.ListSherTypes listType, String serverUrl) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {

                        // Generate shers from string
                        listShers = CreateListSherFromCsvString(context, serverResponse);

                        // Cache results

                        // If server returns nothing, we still want to put something in serverResponse
                        // This is because if serverResponse is empty, next time when we access if data
                        // is cached or not, the app will ask the server for data again
                        // Putting comma here since the response is csv and a single comma will be safely ignored
                        if (serverResponse.equals("")) {
                            serverResponse = ",";
                        }

                        // Create List<SherEntryItem>
                        items = getListSherEntryItemFromSher(context, listShers);

                        // Store cache only if response has valid non-zero results
                        if (items != null && items.size() != 0 && items.get(0) != null) {
                            setCachedDiscussionFeedPreference(context, listType, serverResponse);
                        }

                        SherListEntryAdapter adapter = new SherListEntryAdapter(activity, items, listBookmarkedShers);
                        listview.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        //if connected to internet
        if (isConnectingToInternet(context))
        {
            try
            {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Loading Feed...");
                progressDialog.show();

                queue.add(stringRequest);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
    {
        String sherId = String.valueOf(listShers.get(position).getId());
        Intent intent = new Intent(context, TabsDiscuss.class);
        intent.putExtra(ExtraSherSelected, sherId);
        activity.startActivity(intent);
    }
} // end of Fragment

