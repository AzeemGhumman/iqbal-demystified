package com.vanity.iqbal.objects;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aghumman on 4/30/2018.
 */

public class UserComment {

    private String id;
    private String username;
    private String text;
    private int score;
    private int currentUserFeedback; // -1 = dislike, 0 = no interaction, 1 = like
    private String timestamp;
    private int wordPosition; // -1 if no word position: will be the case with comment pertains to the whole sher

    public UserComment(String id, String username, String text, int score, int currentUserFeedback, String timestamp, int wordPosition) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.score = score;
        this.currentUserFeedback = currentUserFeedback;
        this.timestamp = timestamp;
        this.wordPosition = wordPosition;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentUserFeedback() {
        return currentUserFeedback;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getWordPosition() {
        return wordPosition;
    }

    // Helper function to generate List of UserComment from JSONArray coming from server
    public static List<UserComment> getUserCommentsFromJson(JSONArray jsonArray, Context context) {

        List<UserComment> comments = new ArrayList<>();

        for (int indexComment = 0; indexComment < jsonArray.length(); indexComment ++) {
            try {
                JSONObject jsonComment = (JSONObject) jsonArray.get(indexComment);
                String id = (String) jsonComment.get("id");
                String username = (String) jsonComment.get("username");
                String text = (String) jsonComment.get("text");
                int score = Integer.parseInt((String) jsonComment.get("score"));
                int currentUserFeedback = Integer.parseInt((String) jsonComment.get("islike"));
                String timestamp = (String) jsonComment.get("timestamp");

                int wordPosition; // this is a special case: since it will be "null" for sher level discussion
                if (jsonComment.isNull("wordposition")) {
                    wordPosition = -1;
                } else {
                    wordPosition = Integer.parseInt((String)jsonComment.get("wordposition"));
                }

                UserComment comment = new UserComment(id, username, text, score, currentUserFeedback, timestamp, wordPosition);
                comments.add(comment);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing comments", Toast.LENGTH_SHORT).show();
            }
        }
        return comments;
    }
}
