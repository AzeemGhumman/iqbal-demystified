package com.vanity.iqbal.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ParseException;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vanity.iqbal.R;
import com.vanity.iqbal.fragments.FragmentChat;
import com.vanity.iqbal.objects.UserComment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;
import static com.vanity.iqbal.helper.Preferences.getPasswordPreference;
import static com.vanity.iqbal.helper.Preferences.getUsernamePreference;

public class CommentAdapter extends ArrayAdapter<UserComment> {

    private static enum VoteState{
        VOTED_UP,
        NEUTRAL,
        VOTED_DOWN
    }

    private Activity activity;
    private Context context;
    private static final int rowResourceId = R.layout.comment;
    private List<UserComment> comments = new ArrayList<>();
    private FragmentChat.ChatType chatType;

    private ProgressDialog progressDialog;

    public CommentAdapter(List<UserComment> comments, FragmentChat.ChatType chatType, Activity activity) {
        super(activity.getApplicationContext(), rowResourceId, comments);
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.comments = comments;
        this.chatType = chatType;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
            row = inflater.inflate(rowResourceId, parent, false);
            holder = new ViewHolder();
            holder.tvMessage = (TextView) row.findViewById(R.id.TxtMessage);
            holder.tvUsername = (TextView) row.findViewById(R.id.TxtUsername);
            holder.tvTime = (TextView) row.findViewById(R.id.TxtTime);
            holder.BtnUpVote = (ImageButton) row.findViewById(R.id.BtnVoteUp);
            holder.BtnDownVote = (ImageButton) row.findViewById(R.id.BtnVoteDown);
            holder.tvScore = (TextView) row.findViewById(R.id.TxtScore);
            holder.LayoutForScore = (RelativeLayout) row.findViewById(R.id.LayoutForScore);
            holder.LayoutForMessage = (LinearLayout) row.findViewById(R.id.LayoutForMessage);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final UserComment comment = comments.get(position);
        holder.tvMessage.setText(Uri.decode(comment.getText()));
        holder.tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvUsername.setText(comment.getUsername());
        holder.tvScore.setText(String.valueOf(comment.getScore()));

        try {
            String formattedTime = formatCommentTime(comment.getTimestamp());
            holder.tvTime.setText(formattedTime);
        } catch (Exception ex) {
            ex.printStackTrace();
            holder.tvTime.setText(comment.getTimestamp());
        }

        // Smuggle the holder into ClickEvents as a tag
        holder.BtnUpVote.setTag(holder);
        holder.BtnDownVote.setTag(holder);

        // Color - messaging look
        holder.tvMessage.setTextColor(Color.rgb(16, 78, 139));

        //Visual feedback that user has VOTED (up or down) before
        //Voted Up Feedback
        if (comment.getCurrentUserFeedback() == 1) {
            updateVoteOnClient(holder, VoteState.VOTED_UP);
        }
        // Voted Down Feedback
        else if (comment.getCurrentUserFeedback() == 0) {
            updateVoteOnClient(holder, VoteState.VOTED_DOWN);
        }
        // User has not voted yet
        else {
            updateVoteOnClient(holder, VoteState.NEUTRAL);
        }

        View.OnLongClickListener partOfCommentClicked = new OnLongClickListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onLongClick(View v) {
                copyCommentToClipboard(comment.getText());
                return false;
            }
        };

        // LayoutForMessage Long Clicked
        holder.LayoutForMessage.setOnLongClickListener(partOfCommentClicked);
        holder.tvUsername.setOnLongClickListener(partOfCommentClicked);
        holder.tvTime.setOnLongClickListener(partOfCommentClicked);
        holder.tvMessage.setOnLongClickListener(partOfCommentClicked);


        final ViewHolder finalHolder = holder;
        holder.BtnUpVote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalHolder.voteState == VoteState.NEUTRAL) {
                    // Vote Up
                    postVoteOnServer("1", "0", comment.getId());
                    incrementScore(finalHolder.tvScore);
                    updateVoteOnClient(finalHolder, VoteState.VOTED_UP);
                } else if (finalHolder.voteState == VoteState.VOTED_UP) {
                    // Cancel Vote Up
                    postVoteOnServer("1", "1", comment.getId());
                    decrementScore(finalHolder.tvScore);
                    updateVoteOnClient(finalHolder, VoteState.NEUTRAL);
                } else if (finalHolder.voteState == VoteState.VOTED_DOWN) {
                    // Prompt user to cancel the vote first before Voting Down
                    Toast.makeText(context, "Currently Voted Down.\nPress Vote Down again to cancel previous vote!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.BtnDownVote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalHolder.voteState == VoteState.NEUTRAL) {
                    // Vote Down
                    postVoteOnServer("0", "0", comment.getId());
                    decrementScore(finalHolder.tvScore);
                    updateVoteOnClient(finalHolder, VoteState.VOTED_DOWN);
                } else if (finalHolder.voteState == VoteState.VOTED_DOWN) {
                    // Cancel Vote Down
                    postVoteOnServer("0", "1", comment.getId());
                    incrementScore(finalHolder.tvScore);
                    updateVoteOnClient(finalHolder, VoteState.NEUTRAL);
                } else if (finalHolder.voteState == VoteState.VOTED_UP) {
                    // Prompt user to cancel the vote first before Voting Up
                    Toast.makeText(context, "Currently Voted Up.\nPress vote Up again to cancel previous vote!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return row;
    } // getView()

    private void updateVoteOnClient(ViewHolder holder, VoteState voteState) {

        holder.voteState = voteState;
        if (voteState == VoteState.VOTED_UP) {
            holder.LayoutForScore.setBackgroundColor(Color.rgb(0, 164, 0));
        } else if (voteState == VoteState.VOTED_DOWN) {
            holder.LayoutForScore.setBackgroundColor(Color.rgb(255, 165, 0));
        } else if (voteState == VoteState.NEUTRAL) {
            holder.LayoutForScore.setBackgroundColor(Color.WHITE);
        }
    }

    private void incrementScore(TextView tvScore) {
        int currentValue = Integer.parseInt(tvScore.getText().toString());
        tvScore.setText(String.valueOf(currentValue + 1));
    }

    private void decrementScore(TextView tvScore) {
        int currentValue = Integer.parseInt(tvScore.getText().toString());
        tvScore.setText(String.valueOf(currentValue - 1));
    }

    static class ViewHolder {
        TextView tvMessage;
        TextView tvUsername;
        TextView tvTime;
        ImageButton BtnUpVote;
        ImageButton BtnDownVote;
        TextView tvScore;
        RelativeLayout LayoutForScore;
        LinearLayout LayoutForMessage;
        VoteState voteState;
        String commentID;
    }

    private void copyCommentToClipboard(String comment) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", comment);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
    }

    private String formatCommentTime(String input) throws java.text.ParseException
    {
        //input: 2014-11-20 09:00:40
        //output: Nov 20, 2014 - 09:00 AM
        try {
            String dateStr = input;
            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            srcDf.setTimeZone(TimeZone.getTimeZone("GMT-6")); //location of server : set by HostGator
            Date date = srcDf.parse(dateStr);
            // parse the date string into Date object

            // DateFormat destDf = new SimpleDateFormat("MMM d, yyyy - h:mm a");
            DateFormat destDf = new SimpleDateFormat("MMM d, yyyy");

            TimeZone destTz = TimeZone.getDefault();
            destDf.setTimeZone(destTz);

            // format the date into another format
            dateStr = destDf.format(date);
            return dateStr;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return input;
    }

    private void postVoteOnServer(final String isLike, final String isCancel, final String commentID) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://icanmakemyownapp.com/iqbal/v3/vote.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {
                        Toast.makeText(context, serverResponse, Toast.LENGTH_SHORT).show();
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
                params.put("username", Uri.encode(getUsernamePreference(context)));
                params.put("password", Uri.encode(getPasswordPreference(context)));
                params.put("comment_id", Uri.encode(commentID));
                params.put("is_like", Uri.encode(isLike));
                params.put("is_cancel", Uri.encode(isCancel));

                if (chatType == FragmentChat.ChatType.GENERAL) {
                    params.put("discussion_type", Uri.encode("general"));
                } else if (chatType == FragmentChat.ChatType.WORD_MEANINGS) {
                    params.put("discussion_type", Uri.encode("word-meanings"));
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

}




