package com.vanity.iqbal.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vanity.iqbal.R;
import com.vanity.iqbal.factory.PoemGenerator;
import com.vanity.iqbal.factory.SherGenerator;
import com.vanity.iqbal.helper.PredicateLayout;
import com.vanity.iqbal.helper.Preferences;
import com.vanity.iqbal.objects.Poem;
import com.vanity.iqbal.objects.Sher;
import com.vanity.iqbal.objects.UserComment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vanity.iqbal.helper.IntentExtras.ExtraSherSelected;
import static com.vanity.iqbal.helper.Preferences.getFontSizePreference;
import static com.vanity.iqbal.helper.Preferences.getPasswordPreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextTypefacePreference;
import static com.vanity.iqbal.helper.Preferences.getUsernamePreference;
import static com.vanity.iqbal.helper.Preferences.langToString;
import static com.vanity.iqbal.helper.GeneralUtilities.getPoemIdFromSherId;
import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;
import static com.vanity.iqbal.objects.UserComment.getUserCommentsFromJson;

public class FragmentDiscussSherWords extends Fragment implements OnClickListener {

    TextView tvWordSelected;

    Poem poem;
    Sher sher;
    String urduSher;

    LinearLayout layoutParentPredicates;
    List<PredicateLayout> layoutSherLines;

     // A ProgressDialog object - to show Loading when fetching Comments
     private ProgressDialog progressDialog;

    Activity activity;
    Context context;

    List<TextView> tvWords;
    List<Boolean> hasWordMeaning;

    String sherSelected;

    Typeface typeface;
    int fontSize;

    FragmentChat fragmentChat;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_discuss_sher_words, container, false);

		//Initialize Activity
		activity = getActivity();
		context = activity.getApplicationContext();

        Bundle extras = activity.getIntent().getExtras();
        if (extras != null) {
            sherSelected = extras.getString(ExtraSherSelected);
        }

		//This line hides the keyboard, which will otherwise pop up because of the EditText : TxtComment
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// Connecting the controls
        layoutParentPredicates = (LinearLayout) rootView.findViewById(R.id.layoutParentPredicates);
        layoutSherLines = new ArrayList<>();
        tvWords = new ArrayList<>();
        hasWordMeaning = new ArrayList<>();

		typeface = getPrimaryTextTypefacePreference(context);
		fontSize = getFontSizePreference(context);

		tvWordSelected = (TextView) rootView.findViewById(R.id.tvWordSelected);
		tvWordSelected.setTextSize(fontSize);
		tvWordSelected.setTypeface(typeface, Typeface.NORMAL);

        sher = SherGenerator.CreateSherFromYaml(context, sherSelected);
        String poemId = getPoemIdFromSherId(sherSelected);
        poem = PoemGenerator.CreatePoemFromYaml(context, poemId);

        urduSher = sher.getSherContent(langToString(Preferences.LangType.URDU)).getText();

        fragmentChat = (FragmentChat) getChildFragmentManager().findFragmentById(R.id.fragmentDiscussionWords);

        // Remove special characters and whitespace
        urduSher = cleanSherBeforeFormingWords(urduSher);

        String sherLines[] = urduSher.split("\n");
        for (String sherLine : sherLines) {

            sherLine = sherLine.trim(); // remove extra spaces within each line
            String words[] = sherLine.split(" ");

            // Add new PredicateLayout
            PredicateLayout layoutSherLine = new PredicateLayout(getActivity());
            layoutSherLine.setRotationY(180);

            for (String word : words) {
                TextView tvWord = new TextView(getActivity());
                tvWord = new TextView(activity);
                tvWord.setText("    " + word + "    ");
                tvWord.setTextSize(fontSize);
                tvWord.setTypeface(typeface);
                tvWord.setPadding(10, 10, 10, 10);
                tvWord.setRotationY(180f);
                tvWord.setOnClickListener(this);
                layoutSherLine.addView(tvWord, new PredicateLayout.LayoutParams(5, 5));
                tvWords.add(tvWord);
                hasWordMeaning.add(false);
            }
            layoutSherLines.add(layoutSherLine);
        } // for all sherLines

        // Add all predicateLayouts to parent linearLayout
        for (PredicateLayout layout : layoutSherLines) {
            layoutParentPredicates.addView(layout);
        }

        // Setting hasWordMeanings to false for all words initially
        for (int i = 0; i < hasWordMeaning.size(); i ++) {
            hasWordMeaning.set(i, false);
        }
        updateWordsBackground(0);

        // Get comments from server
         getSherWordMeaningsFromServer();

		return rootView;
		
	} // onCreateView

    @Override
	public void onClick(View v) 
	{
        TextView tvClicked = (TextView) v;
	    for (int i = 0; i < tvWords.size(); i ++) {
            if (tvClicked == tvWords.get(i)) {
                updateWordsBackground(i);
                if (i != fragmentChat.getIndexWordSelected()) {
                    fragmentChat.setIndexWordSelected(i);
                    fragmentChat.updateView();
                }
            }
        }
	} // word clicked

    public void updateWordsBackground(int indexWordSelected) {

	    for (int i = 0; i < tvWords.size(); i ++) {
	        // Set appropriate background image
            if (i == indexWordSelected) {
                // Update Selected Word
                tvWordSelected.setText(tvWords.get(i).getText());

                if (hasWordMeaning.get(i)) {
                    tvWords.get(i).setBackgroundResource(R.drawable.word_background_has_meaning_selected);
                } else {
                    tvWords.get(i).setBackgroundResource(R.drawable.word_background_no_meaning_selected);
                }
            } else {
                if (hasWordMeaning.get(i)) {
                    tvWords.get(i).setBackgroundResource(R.drawable.word_background_has_meaning);
                } else {
                    tvWords.get(i).setBackgroundResource(R.drawable.word_background_no_meaning);
                }
            }
        }
    }

    public void getSherWordMeaningsFromServer() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://icanmakemyownapp.com/iqbal/v3/get-discussion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {

                        try {
                            JSONArray jsonComments = new JSONArray(serverResponse);
                            List<UserComment> comments = getUserCommentsFromJson(jsonComments, context);

                            // Update hasWordMeaning
                            for (int indexComment = 0; indexComment < comments.size(); indexComment ++) {
                                int wordIndex = comments.get(indexComment).getWordPosition();
                                hasWordMeaning.set(wordIndex - 1, true);
                            }
                            updateWordsBackground(0);

                            // Send comments to FragmentDiscussion
                            fragmentChat.setChatParameters(FragmentChat.ChatType.WORD_MEANINGS, comments, sherSelected, urduSher);
                            fragmentChat.setIndexWordSelected(0);
                            fragmentChat.updateView();

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error: " + serverResponse, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("sher", Uri.encode(sherSelected));
                params.put("username", Uri.encode(getUsernamePreference(context)));
                params.put("password", Uri.encode(getPasswordPreference(context)));
                params.put("discussion_type", Uri.encode("word-meanings"));
                return params;
            }
        };

        //if connected to internet
        if (isConnectingToInternet(context))
        {
            try
            {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Loading Word Meanings...");
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

    private String cleanSherBeforeFormingWords(String urduSher) {

        // Get Words from Urdu Text - Follow Python rules used for creating database
        // Note: This must match server side, otherwise words will be misaligned
        // Also, be cautious when you fix typos in original text, that can mess up word indices

        urduSher = urduSher.replace("|", "\n");

        //Remove special characters:
        //، ! ؟ ' @ ( )
        urduSher = urduSher.replace("،", "");
        urduSher = urduSher.replace("!", "");
        urduSher = urduSher.replace("؟", "");
        urduSher = urduSher.replace("'", "");
        urduSher = urduSher.replace("@", "");
        urduSher = urduSher.replace("(", "");
        urduSher = urduSher.replace(")", "");

        urduSher = urduSher.trim().replaceAll("\n+", "\n"); //remove extra empty lines if any
        urduSher = urduSher.trim().replaceAll(" +", " "); //remove extra spaces
        urduSher = urduSher.trim();
        return urduSher;
    }

} // end of Fragment


	
	