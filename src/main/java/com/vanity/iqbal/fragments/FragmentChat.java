package com.vanity.iqbal.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vanity.iqbal.R;
import com.vanity.iqbal.adapters.CommentAdapter;
import com.vanity.iqbal.objects.UserComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;
import static com.vanity.iqbal.helper.GeneralUtilities.restartActivity;
import static com.vanity.iqbal.helper.Preferences.getPasswordPreference;
import static com.vanity.iqbal.helper.Preferences.getUsernamePreference;

public class FragmentChat extends Fragment {

    public static enum ChatType {
        GENERAL,
        WORD_MEANINGS
    }

    LinearLayout LayoutComments;
    ImageView BtnShare, BtnComment;
    EditText TxtComment;
    TextView tvPromptToContribute;
    LinearLayout.LayoutParams params;

    // To show the comments
    private ListView listView;
    CommentAdapter adapter;

    // A ProgressDialog object - to show Loading when fetching and posting comments
    private ProgressDialog progressDialog;

    Activity activity;
    Context context;

    String username;
    String password;

    List<UserComment> comments = new ArrayList<>();
    String sherSelected;
    ChatType chatType;
    int indexWordSelected;
    String urduSherToShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize Activity
        activity = getActivity();
        context = activity.getApplicationContext();

        // Connecting the controls
        LayoutComments = (LinearLayout) rootView.findViewById(R.id.LayoutComments);
        BtnShare = (ImageView) rootView.findViewById(R.id.BtnShare);
        BtnComment = (ImageView) rootView.findViewById(R.id.BtnComment);
        TxtComment = (EditText) rootView.findViewById(R.id.TxtComment);
        //Setting up ListView variable for comments
        listView = (ListView) rootView.findViewById(R.id.listComments);

        // Get username and password
        username = getUsernamePreference(context);
        password = getPasswordPreference(context);

        //Event - Button Clicked (Share and Comment)
        BtnShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Iqbal Demystified");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, urduSherToShare);
                startActivity(Intent.createChooser(sharingIntent, "Share with your Friends!"));
            }
        }); // Share Button Clicked

        BtnComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectingToInternet(activity)) {
                    // Try Posting this Comment on Server
                    try {
                        String userComment = TxtComment.getText().toString().trim();
                        if (userComment.equals("")) {
                            Toast.makeText(activity, "Please write something before posting!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Post user comment to server
                            postCommentOnServer(userComment);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } // if connected to internet
                else {
                    Toast.makeText(activity, "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (username.equals("")) {
            TxtComment.setText("Please Sign In to post a comment!");
            TxtComment.setEnabled(false);
            TxtComment.setGravity(Gravity.CENTER_HORIZONTAL);
            BtnComment.setEnabled(false);
        }

        // Create prompt user to contribute
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 10, 0, 0);
        tvPromptToContribute = new TextView(context);
        tvPromptToContribute.setTextSize(15);
        tvPromptToContribute.setLayoutParams(params);
        tvPromptToContribute.setGravity(Gravity.CENTER);
        tvPromptToContribute.setTextColor(Color.rgb(255, 165, 0));
        tvPromptToContribute.setTypeface(null, Typeface.BOLD);

        return rootView;
    } // onCreateView

    // Getter and Setters
    public int getIndexWordSelected() {
        return indexWordSelected;
    }

    public void setIndexWordSelected (int indexWordSelected) {
        this.indexWordSelected = indexWordSelected;
    }

    public void setChatParameters(ChatType chatType, List<UserComment> comments, String sherSelected, String urduSherToShare) {
        this.chatType = chatType;
        this.comments = comments;
        this.sherSelected = sherSelected;
        this.urduSherToShare = urduSherToShare.replace("|", "\n") + "\n\n" + "(Iqbal Demystified Android App)";
    }

    public void updateView() {
        if (chatType == ChatType.GENERAL) {
            if (comments.size() > 0) {
                adapter = new CommentAdapter(comments, chatType, activity);
                listView.setItemsCanFocus(false);
                listView.setAdapter(adapter);
                ((LinearLayout) LayoutComments).removeView(tvPromptToContribute);
            } else {
                createTextViewAskingUserToContribute("Need help in understanding?\nAll you have to do is ask!");
                listView.setAdapter(new CommentAdapter(new ArrayList<UserComment>(), chatType, activity));
            }
        } else if (chatType == ChatType.WORD_MEANINGS) {

            List<UserComment> commentsForSelectedWord = filterCommentsForSelectedWord(comments, indexWordSelected);
            if (commentsForSelectedWord.size() > 0) {
                adapter = new CommentAdapter(commentsForSelectedWord, chatType, activity);
                listView.setItemsCanFocus(false);
                listView.setAdapter(adapter);
                ((LinearLayout) LayoutComments).removeView(tvPromptToContribute);
            } else {
                createTextViewAskingUserToContribute("Do you know the meaning of this word?\nIf yes, please help others by writing it below!");
                listView.setAdapter(new CommentAdapter(new ArrayList<UserComment>(), chatType, activity));
            }
        }
    }

    public List<UserComment> filterCommentsForSelectedWord(List<UserComment> allComments, int indexWordSelected) {

        List<UserComment> filteredComments = new ArrayList<>();
        for (UserComment comment : allComments) {
            if (comment.getWordPosition() == indexWordSelected + 1) // +1 since the database stores words starting from index 1
            {
                filteredComments.add(comment);
            }
        }
        return filteredComments;
    }

    public void createTextViewAskingUserToContribute(String promptText) {
        tvPromptToContribute.setText(promptText);
        try {
            LayoutComments.addView(tvPromptToContribute);
        } catch (Exception ex) {
            // This try catch is there so that if the user keeps clicking on the same word with no meaning, we don't add the prompt over and over
        }
    }

    public void postCommentOnServer(final String userComment) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://icanmakemyownapp.com/iqbal/v3/post-comment.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {
                        if (serverResponse.trim().startsWith("created")) {
                            // Extract comment id
                            String[] parts = serverResponse.trim().split(" ");
                            if (parts.length < 2) {
                                Toast.makeText(context, "Error: " + serverResponse, Toast.LENGTH_SHORT).show();
                            } else {
                                // Clear text field
                                TxtComment.setText("");
                                // Add new comment to list of comments
                                String commentID = parts[1]; // second part is commentID
                                comments.add(new UserComment(commentID, username, userComment, 0, -1, "Just Now", indexWordSelected + 1));
                                // Update view
                                updateView();
                            }
                        } else {
                            Toast.makeText(context, "Error: " + serverResponse, Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", Uri.encode(username));
                params.put("password", Uri.encode(password));
                params.put("sher", Uri.encode(sherSelected));
                params.put("comment_text", Uri.encode(userComment));

                if (chatType == ChatType.GENERAL) {
                    params.put("discussion_type", Uri.encode("general"));
                } else if (chatType == ChatType.WORD_MEANINGS) {
                    params.put("discussion_type", Uri.encode("word-meanings"));
                    params.put("word_position", Uri.encode(String.valueOf(indexWordSelected + 1)));
                }
                return params;
            }
        };

        // If connected to internet
        if (isConnectingToInternet(context)) {
            try {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Loading Comments...");
                progressDialog.show();

                queue.add(stringRequest);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
        }
    }
} // end of Fragment

