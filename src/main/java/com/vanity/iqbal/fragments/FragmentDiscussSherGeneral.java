package com.vanity.iqbal.fragments;

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
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.vanity.iqbal.helper.Preferences;
import com.vanity.iqbal.objects.Poem;
import com.vanity.iqbal.objects.Sher;
import com.vanity.iqbal.objects.SherContent;
import com.vanity.iqbal.objects.UserComment;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vanity.iqbal.fragments.FragmentChat.ChatType;
import static com.vanity.iqbal.helper.IntentExtras.ExtraSherSelected;
import static com.vanity.iqbal.helper.Preferences.getFontSizePreference;
import static com.vanity.iqbal.helper.Preferences.getPasswordPreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryLanguagePreference;
import static com.vanity.iqbal.helper.Preferences.getPrimaryTextTypefacePreference;
import static com.vanity.iqbal.helper.Preferences.getUsernamePreference;
import static com.vanity.iqbal.helper.Preferences.langToString;
import static com.vanity.iqbal.helper.GeneralUtilities.getPoemIdFromSherId;
import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;
import static com.vanity.iqbal.objects.UserComment.getUserCommentsFromJson;


public class FragmentDiscussSherGeneral extends Fragment {

    TextView tvDiscussTalkSher;

    Poem poem;
    Sher sher;
    Preferences.LangType primaryLanguage;
    String sherPrimaryText;

    // A ProgressDialog object - to show Loading when fetching Comments
    private ProgressDialog progressDialog;

    Activity activity;
    Context context;

    String sherSelected;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_discuss_sher_general, container, false);

		//Initialize Activity
		activity = getActivity();
        context = activity.getApplicationContext();

		//Get the English, Urdu sher and (references) from the Main Poem Page
        // TODO: Might want to sher_selected in tabsDiscussion and user prefernece or pass by constuctor so that sher_selected reaches both fragments
		Bundle extras = activity.getIntent().getExtras();
		if (extras != null) {
            sherSelected = extras.getString(ExtraSherSelected);
        }

        //Connecting the controls
        tvDiscussTalkSher = (TextView) rootView.findViewById(R.id.tvDiscussTalkSher);

        sher = SherGenerator.CreateSherFromYaml(context, sherSelected);
        String poemId = getPoemIdFromSherId(sherSelected);
        poem = PoemGenerator.CreatePoemFromYaml(context, poemId);

        primaryLanguage = getPrimaryLanguagePreference(context);
        SherContent sherContent = sher.getSherContent(langToString(primaryLanguage));
        if (sherContent == null) {
            sherPrimaryText = sher.getSherContent(langToString(Preferences.LangType.URDU)).getText();
        } else {
            sherPrimaryText = sherContent.getText();
        }

        // Set title
		 activity.setTitle(poem.getHeading(langToString(Preferences.LangType.URDU)).getText());

		//This line hides the keyboard, which will otherwise pop up because of the EditText : TxtComment
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// Set Sher in TextView
        tvDiscussTalkSher.setText(sherPrimaryText.replace("|", "\n"));
        tvDiscussTalkSher.setTextSize(getFontSizePreference(context));
        tvDiscussTalkSher.setTypeface(getPrimaryTextTypefacePreference(context), Typeface.NORMAL);

        getSherCommentsFromServer();

		return rootView;
	} // onCreateView

    public void getSherCommentsFromServer() {

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

                            // Send comments to FragmentDiscussion
                            FragmentChat fragmentChat = (FragmentChat) getChildFragmentManager().findFragmentById(R.id.fragmentDiscussionComments);
                            if (fragmentChat != null) {
                                fragmentChat.setChatParameters(ChatType.GENERAL, comments, sherSelected, sherPrimaryText);
                                fragmentChat.updateView();
                            }

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
                params.put("discussion_type", Uri.encode("general"));
                return params;
            }
        };

        //if connected to internet
        if (isConnectingToInternet(context))
        {
            try
            {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Loading Comments...");
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

