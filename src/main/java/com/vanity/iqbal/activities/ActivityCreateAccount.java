package com.vanity.iqbal.activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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

import java.util.HashMap;
import java.util.Map;

import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;
import static com.vanity.iqbal.helper.GeneralUtilities.isValidEmail;

public class ActivityCreateAccount extends AppCompatActivity{
	
	EditText TxtFirstName, TxtLastName, TxtUsername, TxtPassword, TxtPasswordAgain, TxtEmail;
	Button BtnCreateAccount;
	TextView TxtInfo;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		
		setTitle("Create New Account");

		TxtFirstName = (EditText)findViewById(R.id.TxtFirstName);
		TxtLastName = (EditText)findViewById(R.id.TxtLastName);
		TxtUsername = (EditText)findViewById(R.id.TxtUsername);
		TxtPassword = (EditText)findViewById(R.id.TxtPassword);
		TxtPasswordAgain = (EditText)findViewById(R.id.TxtPasswordAgain);
		TxtEmail = (EditText)findViewById(R.id.TxtEmail);
		BtnCreateAccount = (Button)findViewById(R.id.BtnCreateAccount);
		TxtInfo = (TextView)findViewById(R.id.TxtInfo);
		
		//This line hides the keyboard, which will otherwise pop up because of the EditText : TxtComment
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		BtnCreateAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) 
			{
				// Check basic mistakes - client side, Update the TxtInfo to show feedback
				String firstName = TxtFirstName.getText().toString().trim();
				String lastName = TxtLastName.getText().toString().trim();
				String username = TxtUsername.getText().toString().trim();
				String password = TxtPassword.getText().toString().trim();
				String passwordRepeated = TxtPasswordAgain.getText().toString().trim();
				String email = TxtEmail.getText().toString().trim();
				
				if (firstName.equals("")) {TxtInfo.setText(Html.fromHtml("Please Enter your <b>First Name</b>."));}
				else if (lastName.equals("")) {TxtInfo.setText(Html.fromHtml("Please Enter your <b>Last Name</b>."));}
				else if (username.equals("")) {TxtInfo.setText(Html.fromHtml("Please Enter a valid <b>Username</b>."));}
				else if (!Character.isLetter(username.charAt(0))) {TxtInfo.setText(Html.fromHtml("Username must start with a <b>letter</b>."));}
				else if (password.equals("")) {TxtInfo.setText(Html.fromHtml("You cannot leave your <b>Password</b> blank."));}
				else if (passwordRepeated.equals("")) {TxtInfo.setText(Html.fromHtml("Please copy your password in the <b>Password Again</b> field."));}

				else if (password.length() < 6) {TxtInfo.setText(Html.fromHtml("Password must be at least 6 characters long"));}
				else if (!password.equals(passwordRepeated)) {TxtInfo.setText(Html.fromHtml("Your <b>passwords</b> don't match."));}
				 
				else if (email.equals("")) {TxtInfo.setText(Html.fromHtml("Please enter your <b>Email Address</b>."));}
				else if (!isValidEmail(email)){TxtInfo.setText(Html.fromHtml("Please enter a <b>valid email address</b>."));}

				else /* Passed all the client side validation tests */
				{
                    // correct email address
                    String serverUrl = "http://www.icanmakemyownapp.com/iqbal/v3/create-account.php";
                    sendCreateAccountRequestToServer(serverUrl, firstName, lastName, username, password, email);
				}
			}
		}); // if the submit button is clicked
	} // onCreate

    public void sendCreateAccountRequestToServer(String serverUrl,
                                                  final String firstName, final String lastName,
                                                  final String username, final String password,
                                                  final String email) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {

                        progressDialog.dismiss();
                        serverResponse = serverResponse.trim();
                        if (serverResponse.equals("")) {
                            TxtInfo.setText("Unknown Error Occurred. Please try again later");
                        } else {
                            // Display the output to the user - Output contains messages user can understand
                            TxtInfo.setText(serverResponse);
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
                params.put("first_name", Uri.encode(firstName));
                params.put("last_name", Uri.encode(lastName));
                params.put("username", Uri.encode(username));
                params.put("password", Uri.encode(password));
                params.put("email", Uri.encode(email));
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
