/*

updated php script v3/login.php
enumerate all responses from server - all very simple
Before update, we were storing user-id when the user logs in
The reason I was doing that was that I was saving a join when user posts comment or votes
This is scary because if you do commment = this and id = XYZ, if id is ok, it will work

Fix: store the username and password
// For every request to post comment or vote, send a post request with username and password
// Update the PHP scripts so that they authenticate before accepting the new post or vote

// After fixing this, clean ProfilePage. Move the leaderboard to a separate fragment and just add that
// fragment on the same page as ProfilePage. This will be good practice for setting up audio fragment.

// Clean the MyDownloads page and make it functional again. ListView with delete button instead of bookmark?
// Or implement long-click delete with bookmark button on the side

// Make an Audio fragment and test extensively in the downloads page

// Put the exact same audio fragment on the poem page

// Clean the Discuss page with both fragments.

// Clean repository.

 */

package com.vanity.iqbal.activities;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vanity.iqbal.R;

import static com.vanity.iqbal.helper.Preferences.setPasswordPreference;
import static com.vanity.iqbal.helper.Preferences.setUsernamePreference;
import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;

public class ActivitySignIn extends AppCompatActivity implements OnClickListener{

	Button BtnSignIn, BtnCreateAccount, BtnAudioDownloads;
	EditText TxtUsername, TxtPassword;
	TextView TxtForgotPassword;

    private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		setTitle("Sign In");
		
		// Connect the controls
        TxtUsername = (EditText)findViewById(R.id.TxtUsername);
        TxtPassword = (EditText)findViewById(R.id.TxtPassword);
		BtnSignIn = (Button)findViewById(R.id.BtnSignIn);
		BtnCreateAccount = (Button)findViewById(R.id.BtnCreateAccount);
        BtnAudioDownloads = (Button)findViewById(R.id.BtnNavigateToAudioDownloads);
		TxtForgotPassword = (TextView)findViewById(R.id.TxtForgotPassword);
		BtnSignIn.setOnClickListener(this);
		BtnCreateAccount.setOnClickListener(this);
        BtnAudioDownloads.setOnClickListener(this);
		TxtForgotPassword.setOnClickListener(this);
		
		// This line hides the keyboard, which will otherwise pop up because of the EditText : TxtComment
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	} // onCreate

	@Override
	public void onClick(View view)
	{
		if (view == BtnSignIn)
		{
			String username = TxtUsername.getText().toString().trim();
			String password = TxtPassword.getText().toString().trim();
			if (username.equals("")) {
				Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
			}
			else if (password.equals("")) {
				Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
			}
			else {
                String serverUrl = "http://www.icanmakemyownapp.com/iqbal/v3/login.php";
                sendSignInRequestToServer(serverUrl, username, password);
			}
		}
		else if (view == BtnCreateAccount) {
			Intent i = new Intent(getApplicationContext(), ActivityCreateAccount.class);
			startActivity(i);
		}
        else if (view == BtnAudioDownloads) {
            Intent i = new Intent(getApplicationContext(), ActivityDownloads.class);
            startActivity(i);
        }
		else if (view == TxtForgotPassword) {
			Intent i = new Intent(getApplicationContext(), ActivityForgotPassword.class);
			startActivity(i);
		}
	} // onClick

    public void sendSignInRequestToServer(String serverUrl, final String username, final String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {

                        progressDialog.dismiss();
                        // Responses = ["username not exists", "wrong password", "email not verified", "done", ""]
                        serverResponse = serverResponse.trim();
                        switch (serverResponse) {
                            case "username not exists":
                                Toast.makeText(getApplicationContext(), "Username does not exist", Toast.LENGTH_LONG).show();
                                break;
                            case "wrong password":
                                Toast.makeText(getApplicationContext(), "Wrong Password. Try again!", Toast.LENGTH_LONG).show();
                                break;
                            case "email not verified":
                                Toast.makeText(getApplicationContext(), "Your account is not verified yet. Please check your email and click on the verification link in an email we sent you when you created a new account", Toast.LENGTH_LONG).show();
                                break;
                            case "done":
                                // Log in was successful
                                setUsernamePreference(username, getApplicationContext());
                                setPasswordPreference(password, getApplicationContext());
                                finish();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "Error Occurred! Please try again later", Toast.LENGTH_LONG).show();
                                break;
                        }
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
                params.put("username", Uri.encode(username));
                params.put("password", Uri.encode(password));
                return params;
            }
        };

        //if connected to internet
        if (isConnectingToInternet(getApplicationContext())) {
            try {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Talking to sever...");
                progressDialog.show();
                queue.add(stringRequest);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
        }
    }
} // class
