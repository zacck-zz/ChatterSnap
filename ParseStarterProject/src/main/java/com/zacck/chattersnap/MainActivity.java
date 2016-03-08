/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.zacck.chattersnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

	EditText username;

	public void signupOrLogin(View view) {

		if(!username.getText().toString().isEmpty()) {

			ParseUser.logInInBackground(String.valueOf(username.getText()), "pass", new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					if (user != null) {

						Log.i("AppInfo", "Logged In");
						gotoUsers();

					} else {

						//if login failed we will sign the user up
						ParseUser newUser = new ParseUser();
						newUser.setUsername(String.valueOf(username.getText()));
						newUser.setPassword("pass");

						newUser.signUpInBackground(new SignUpCallback() {
							public void done(ParseException e) {
								if (e == null) {

									Log.i("AppInfo", "Signed up");

								} else {

									alert("Couldn't sign you up - please try again!");

								}
							}
						});


					}
				}
			});
		}
		else
		{
			alert("Please Enter A Username to Proceed");
		}

	}

	private void alert(String s)
	{
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}

	public void gotoUsers()
	{
		Intent userListIntent = new Intent(MainActivity.this, UserList.class);
		startActivity(userListIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ParseAnalytics.trackAppOpenedInBackground(getIntent());
		username = (EditText) findViewById(R.id.editText);

		if(ParseUser.getCurrentUser() != null)
		{
			gotoUsers();
		}
	}


}
