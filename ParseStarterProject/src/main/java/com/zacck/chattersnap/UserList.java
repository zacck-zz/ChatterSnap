package com.zacck.chattersnap;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity implements AdapterView.OnItemClickListener {

	ArrayList<String> Usernames;
	ArrayAdapter myUsersAdapter;
	ListView userList;
	int ImageRequestCode = 999;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
		Usernames = new ArrayList<>();
		userList = (ListView)findViewById(R.id.mUserChatterSnapUserList);

		//lets make an adapter for the users
		myUsersAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Usernames);
		userList.setAdapter(myUsersAdapter);
		userList.setOnItemClickListener(this);
		getUsers();





	}
	private void alert(String s)
	{
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}

	public void getUsers()
	{
		//lets get the list of users from Parse
		ParseQuery<ParseUser> mUsersQuery =  ParseUser.getQuery();
		mUsersQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
		mUsersQuery.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> chattersnapUsers, ParseException e) {
				if (e == null) {
					if (chattersnapUsers.size() > 0) {
						//clear array list incase we are doing an update
						Usernames.clear();
						//add each Users UserName to list
						for (ParseUser eachUser : chattersnapUsers) {
							Usernames.add(eachUser.getUsername());
						}
						myUsersAdapter.notifyDataSetChanged();
					} else {
						alert("No Users on System Yet ...Hmmm");
					}

				} else {
					alert(e.getMessage());
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getUsers();
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

	//selects a user then we can send this user an image
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//lets send the User position so we can use it in activity for result
		startActivityForResult(pickImage, position);


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && data != null)
		{
			Uri selectedImagePath = data.getData();
		}

	}
}
