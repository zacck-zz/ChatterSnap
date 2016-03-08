package com.zacck.chattersnap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity implements AdapterView.OnItemClickListener {

	ArrayList<String> Usernames;
	ArrayAdapter myUsersAdapter;
	ListView userList;
	int ImageRequestCode = 999;
	Bitmap RecievedImage;

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
		checkForImages();





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
			//Get the Path to the image that was selected
			Uri selectedImagePath = data.getData();

			//try get a bitmap image from the path
			try
			{
				Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImagePath);

				//convert image to byetArray for Uploading
				ByteArrayOutputStream mImageStream = new ByteArrayOutputStream();
				mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, mImageStream);
				byte[] mImageBytes = mImageStream.toByteArray();

				ParseFile mFile = new ParseFile("image.png",mImageBytes);

				//Create an object and save it
				ParseObject sentImage = new ParseObject("Image");
				sentImage.put("senderUsername", ParseUser.getCurrentUser().getUsername());
				sentImage.put("recipientUsername", Usernames.get(requestCode));
				Log.i("image sent to", Usernames.get(requestCode));
				sentImage.put("image", mFile);
				sentImage.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if(e == null)
						{
							alert("Image Sent Succesfully");

						}
						else
						{
							alert(e.getMessage());
						}
					}
				});



			}
			catch (Exception e)
			{
				alert(e.getMessage());
			}
		}

	}

	public void checkForImages()
	{
		Log.i("i am", ParseUser.getCurrentUser().getUsername());
		ParseQuery<ParseObject> myImagesQuery = ParseQuery.getQuery("Image");
		myImagesQuery.whereEqualTo("recipientUsername", ParseUser.getCurrentUser().getUsername());
		myImagesQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> myImages, ParseException e) {
				if (e == null) {
					if (myImages.size() > 0) {
						for (ParseObject thisImage : myImages) {
							thisImage.deleteInBackground();
							//Lets  Download the file
							ParseFile mParseImage = thisImage.getParseFile("image");
							byte[] mImageBytes = new byte[0];
							try {
								mImageBytes = mParseImage.getData();
								RecievedImage = BitmapFactory.decodeByteArray(mImageBytes, 0, mImageBytes.length);

							} catch (Exception innex) {
								alert(innex.getMessage());
							}
							if (RecievedImage != null) {
								AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(UserList.this);
								mDialogBuilder.setTitle("You Have a Message");
								TextView content = new TextView(UserList.this);
								content.setText(Gravity.CENTER_HORIZONTAL);
								content.setText(thisImage.getString("senderUsername") + "Has Sent You an Image!");
								mDialogBuilder.setView(content);
								mDialogBuilder.setPositiveButton("View", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {

										Log.i(getPackageName(), "Display Image");
										AlertDialog.Builder mImageDialogBuilder = new AlertDialog.Builder(UserList.this);
										mImageDialogBuilder.setTitle("Message");
										ImageView content = new ImageView(UserList.this);
										mImageDialogBuilder.setView(content);
										mImageDialogBuilder.setPositiveButton("View", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {

												Log.i(getPackageName(), "Display Image");

											}
										});

										mImageDialogBuilder.show();

									}
								});

								mDialogBuilder.show();
							}


						}

					} else {
						alert("No Images");
					}

				} else {
					alert(e.getMessage());
				}
			}
		});


	}


}
