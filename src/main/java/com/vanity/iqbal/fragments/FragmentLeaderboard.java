package com.vanity.iqbal.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vanity.iqbal.adapters.LeaderboardAdapter;
import com.vanity.iqbal.R;

import java.util.ArrayList;
import java.util.List;

import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;

// TODO: This fragment could be refactored even further. Since it's isolated and not very critical, I am not going to spend too much time improving this.

/**
 * Created by aghumman on 4/23/2018.
 */

public class FragmentLeaderboard extends Fragment implements AdapterView.OnItemSelectedListener {

    Context context;

    ListView ListView_Leaderboard;
    TextView tvLabel;
    Spinner spinnerCategory;

    List<String> leadersDiscussion;
    List<String> scoresDiscussion;

    List<String> leadersWordMeaning;
    List<String> scoresWordMeaning;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        context = getActivity().getApplicationContext();

        ListView_Leaderboard = (ListView) rootView.findViewById(R.id.ListView_Leaderboard);
        spinnerCategory = (Spinner) rootView.findViewById(R.id.spinner_leaderboard_category);

        tvLabel = (TextView) rootView.findViewById(R.id.tv_LeaderBoard);

        leadersDiscussion = new ArrayList<>();
        scoresDiscussion = new ArrayList<>();

        leadersWordMeaning = new ArrayList<>();
        scoresWordMeaning = new ArrayList<>();

        spinnerCategory.setOnItemSelectedListener(this);

        // Get leaderboard from server
        String serverUrl = "http://www.icanmakemyownapp.com/iqbal/v3/leaderboard.php";
        getLeaderboardFromServer(serverUrl);

        // Disable the spinner, so that user does not try to change categories while we load data from internet the first time
        spinnerCategory.setEnabled(false);

        return rootView;
    } // onCreateView


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        // TODO: Enum the positions for Discussion and word meaning
        if (position == 0) {
            ListView_Leaderboard.setAdapter(new LeaderboardAdapter(context, leadersDiscussion, scoresDiscussion));
        } else if (position == 1) {
            ListView_Leaderboard.setAdapter(new LeaderboardAdapter(context, leadersWordMeaning, scoresWordMeaning));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void getLeaderboardFromServer(String serverUrl) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {

                        // TODO: Parse the response separately: hide and document parser implementation details
                        String[] parts = serverResponse.split(",");
                        if (parts.length != 10 * 4) {
                            Toast.makeText(context, "Error loading leaderboard. Please try again later", Toast.LENGTH_SHORT).show();
                        } else {
                            leadersDiscussion = new ArrayList<>();
                            scoresDiscussion = new ArrayList<>();
                            leadersWordMeaning = new ArrayList<>();
                            scoresWordMeaning = new ArrayList<>();
                            for (int i = 0; i < 10; i ++) {
                                leadersDiscussion.add(parts[i * 2]);
                                scoresDiscussion.add(parts[i * 2 + 1]);
                                leadersWordMeaning.add(parts[20 + i * 2]);
                                scoresWordMeaning.add(parts[20 + i * 2 + 1]);
                            }
                        }

                        // Set adapter to discussion category
                        ListView_Leaderboard.setAdapter(new LeaderboardAdapter(context, leadersDiscussion, scoresDiscussion));
                        spinnerCategory.setEnabled(true);
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
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading Leaderboard...");
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
} // end of Fragment
