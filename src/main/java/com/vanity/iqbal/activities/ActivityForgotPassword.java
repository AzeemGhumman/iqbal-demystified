package com.vanity.iqbal.activities;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vanity.iqbal.R;

import static com.vanity.iqbal.helper.GeneralUtilities.isConnectingToInternet;
import static com.vanity.iqbal.helper.GeneralUtilities.isValidEmail;

public class ActivityForgotPassword extends AppCompatActivity{

	EditText TxtEmail;
	Button BtnForgotPasswordEmail;

	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		
		setTitle("Forgot Password");

		TxtEmail = (EditText)findViewById(R.id.TxtEmailAddress);
		BtnForgotPasswordEmail = (Button)findViewById(R.id.Btn_ForgotPasswordSendEmail);
		
		//This line hides the keyboard, which will otherwise pop up because of the EditText : TxtComment
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		BtnForgotPasswordEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
                String userEmail = TxtEmail.getText().toString().trim();

				if (userEmail.equals("")) {
					Toast.makeText(getApplicationContext(), "Email cannot be empty!", Toast.LENGTH_SHORT).show();
				}
                else if (!isValidEmail(userEmail)) {
					Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
				}
                else {
                    // correct email address
                    String serverUrl = "http://www.icanmakemyownapp.com/iqbal/v3/forgot-password.php";
                    sendForgotPasswordRequestToServer(serverUrl, userEmail);
                }
			}
		});
	} // onCreate

    public void sendForgotPasswordRequestToServer(String serverUrl, final String email) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {

                        progressDialog.dismiss();

                        // TODO: make an enum from them or something and move them somewhere? do write the strings again and again
                        // Possible Responses: ["email sent", "error sending email", "email not found", ""]

                        serverResponse = serverResponse.trim();
                        switch (serverResponse) {
                            case "email sent":
                                Toast.makeText(getApplicationContext(), "Email sent with the new password. Please check your email", Toast.LENGTH_LONG).show();
                                break;
                            case "error sending email":
                                Toast.makeText(getApplicationContext(), "Error sending email. Please try again later", Toast.LENGTH_LONG).show();
                                break;
                            case "email not found":
                                Toast.makeText(getApplicationContext(), "Cannot find email in our system. Please double-check your email address or create a new account", Toast.LENGTH_LONG).show();
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
}
