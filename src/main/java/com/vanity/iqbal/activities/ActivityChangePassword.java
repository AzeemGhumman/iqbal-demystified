package com.vanity.iqbal.activities;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

import static com.vanity.iqbal.helper.Preferences.getUsernamePreference;
import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;

public class ActivityChangePassword extends AppCompatActivity{

	EditText TxtOldPassword, TxtNewPassword, TxtNewPasswordRepeat;
	Button BtnChangePassword;
	TextView TxtInfo;

    private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

        setTitle("Change Password");

		TxtOldPassword = (EditText)findViewById(R.id.TxtChangePassword_Old);
		TxtNewPassword = (EditText)findViewById(R.id.TxtChangePassword_New);
		TxtNewPasswordRepeat = (EditText)findViewById(R.id.TxtChangePassword_New_Repeat);
		BtnChangePassword = (Button)findViewById(R.id.BtnChangePassword);
		TxtInfo = (TextView)findViewById(R.id.TxtChangePassword_Info);

		//Read the username from SharedPreference in Username
        final String username = getUsernamePreference(getApplicationContext());

		//When Change Password button clicked
		BtnChangePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				// Validate EditTexts
				String oldPassword = TxtOldPassword.getText().toString().trim();
				String newPassword = TxtNewPassword.getText().toString().trim();
				String newPasswordRepeated = TxtNewPasswordRepeat.getText().toString().trim();

				if (oldPassword.equals("")) {TxtInfo.setText(Html.fromHtml("You cannot leave your <b>current Password</b> blank."));}
				else if (newPassword.equals("")) {TxtInfo.setText(Html.fromHtml("You cannot leave your <b>new Password</b> blank."));}
				else if (newPasswordRepeated.equals("")) {TxtInfo.setText(Html.fromHtml("You cannot leave your <b>new Password</b> blank."));}
				else if (newPassword.length() < 6) {TxtInfo.setText(Html.fromHtml("Password must be at least 6 characters long"));}
				else if (!newPassword.equals(newPasswordRepeated)) {TxtInfo.setText(Html.fromHtml("Your new passwords don't match."));}

				// Passed all the test, send request to server to change the password
				else {
                    String serverUrl = "http://www.icanmakemyownapp.com/iqbal/v3/change-password.php";
                    sendChangePasswordRequestToServer(serverUrl, username, oldPassword, newPassword);
				} //check server if password is correct
			}
		}); // if change password button is clicked
	} // onCreate()

    public void sendChangePasswordRequestToServer(String serverUrl, final String username, final String oldPassword, final String newPassword) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {

                        progressDialog.dismiss();

                        // TODO: make an enum from them or something and move them somewhere? do write the strings again and again
                        // Possible Responses: ['done', 'wrong password', 'invalid username', '']

                        serverResponse = serverResponse.trim();
                        switch (serverResponse) {
                            case "invalid username":
                                TxtInfo.setText(Html.fromHtml("<b>Username does not exists.</b> Try again."));
                                break;
                            case "wrong password":
                                TxtInfo.setText(Html.fromHtml("<b>Incorrect Password.</b> Try again."));
                                break;
                            case "done":
                                Toast.makeText(getApplicationContext(), "Password Updated!", Toast.LENGTH_LONG).show();
                                //Go back to Profile
                                finish();
                                break;
                            default:
                                TxtInfo.setText(Html.fromHtml("<b>Error Occured!</b> Try again."));
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
                params.put("old_password", Uri.encode(oldPassword));
                params.put("new_password", Uri.encode(newPassword));
                return params;
            }
        };

        //if connected to internet
        if (isConnectingToInternet(getApplicationContext()))
        {
            try
            {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Talking to sever...");
                progressDialog.show();
                queue.add(stringRequest);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Cannot connect to the internet!", Toast.LENGTH_SHORT).show();
        }
    }
} // class
