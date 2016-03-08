package com.zacck.chattersnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

	ArrayList<String> Usernames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
		Usernames = new ArrayList<>();

		//lets get the list of users from Parse
		ParseQuery<ParseUser> mUsersQuery =  ParseUser.getQuery();
		mUsersQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
		mUsersQuery.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> chattersnapUsers, ParseException e) {
				if(e == null)
				{
					for(ParseUser eachUser : chattersnapUsers)
					{
						Usernames.add(eachUser.getUsername());
					}
				}
				else
				{
					alert(e.getMessage());
				}
			}
		});

	}
	private void alert(String s)
	{
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_user_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id)
		{

		}

		return super.onOptionsItemSelected(item);
	}
}
