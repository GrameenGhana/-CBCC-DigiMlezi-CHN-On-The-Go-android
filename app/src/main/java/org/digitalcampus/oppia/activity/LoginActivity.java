/* 
 * This file is part of OppiaMobile - http://oppia-mobile.org/
 * 
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.oppia.activity;

import java.util.ArrayList;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.listener.SubmitListener;
import org.digitalcampus.oppia.model.User;
import org.digitalcampus.oppia.task.LoginTask;
import org.digitalcampus.oppia.task.Payload;
import org.digitalcampus.oppia.utils.UIUtils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity implements SubmitListener  {

	public static final String TAG = LoginActivity.class.getSimpleName();
	private SharedPreferences prefs;

	private TextInputLayout usernameField;
	private TextInputLayout passwordField;
	public static ProgressDialog pDialog;
	DbHelper Db;

	static String[] PERMISSIONS = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.VIBRATE,
			Manifest.permission.WAKE_LOCK,
			Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.RECEIVE_BOOT_COMPLETED,
			Manifest.permission.READ_CALENDAR,
			Manifest.permission.WRITE_CALENDAR

	};

	static Boolean hasAllPermissions = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = getWindow(); // in Activity's onCreate() for instance
			w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		}
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_login_new);

		if(getSupportActionBar() != null)
        getSupportActionBar().hide();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Db = new DbHelper(LoginActivity.this);

		usernameField = (TextInputLayout) findViewById(R.id.email_layout);
        passwordField = (TextInputLayout) findViewById(R.id.password_layout);


		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.login_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				onLoginClick();

			}
		});


		passwordField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				InputMethodManager imm = (InputMethodManager) getSystemService(LoginActivity.this.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);



				onLoginClick();

				return true;

			}
		});

	}




	public void onLoginClick() {


		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		launchMultiplePermissions(LoginActivity.this);
		else performLoginTask();



}

	public void onRegisterClick(View view){
		startActivity(new Intent(this, RegisterActivity.class));
		finish();
	}

	public void setUserPreferences(User u)
	{
		String username = usernameField.getEditText().getText().toString();
		String[] usernameSplit = username.split(" ");
		String firstName;
		if(usernameSplit.length >= 0){
		firstName = usernameSplit[0];
		}else {
			firstName = username;
		}
		// set params
		Editor editor = prefs.edit();
    	editor.putString(getString(R.string.prefs_username), username);
    	editor.putString(getString(R.string.prefs_api_key), u.getApi_key());
    	editor.putString(getString(R.string.prefs_display_name), u.getDisplayName());
    	editor.putString("first_name", firstName);
    	System.out.println("Username: " + firstName);
    	editor.putInt(getString(R.string.prefs_points), u.getPoints());
    	editor.putInt(getString(R.string.prefs_badges), u.getBadges());
    	editor.putBoolean(getString(R.string.prefs_scoring_enabled), u.isScoringEnabled());
    	editor.commit();
	}
	
	
	public void submitComplete(Payload response) {
		try {
			pDialog.dismiss();
		} catch (IllegalArgumentException iae){
			//
		}
		if(response.isResult()){

        //Start the activity
            Editor prefsEditor = prefs.edit();
            prefsEditor.putBoolean("isSignedIn", true);
            prefsEditor.putBoolean("isFirstSignIn", false);
            prefsEditor.apply();

            startActivity(new Intent(this, MainScreenActivity.class));
			finish();
		} else {
			UIUtils.showAlert(this, R.string.title_login, response.getResultResponse());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_settings) {
			Intent i = new Intent(this, PrefsActivity.class);
			startActivity(i);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean isInternetConnectionPresent() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}




	public static boolean launchMultiplePermissions(Activity context) {
		Boolean hasPermissions = null;

		for(String permission : PERMISSIONS){
			if(!hasPermission(context, permission)){


				if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
					ActivityCompat.requestPermissions(context, PERMISSIONS, 30);
				} else {
					ActivityCompat.requestPermissions(context, PERMISSIONS, 30);
				}

				return false;
			}


		}

		return true;
	}


	public static boolean hasPermission(Context context, String PERMISSION) {

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {

			if (ActivityCompat.checkSelfPermission(context, PERMISSION) != PackageManager.PERMISSION_GRANTED) {

				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, PERMISSION)) {


				}
				return false;
			}

		}
		return true;
	}


	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permission, grantResults);



		if(grantResults[0] != PackageManager.PERMISSION_GRANTED ) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppAlertDialog)
					.setTitle("Permissions")
					.setMessage("Digimlezi requires all permissions to work effectively")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

							dialogInterface.dismiss();
							onLoginClick();
						}
					}).setNegativeButton("Quit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.cancel();
							dialogInterface.dismiss();
							LoginActivity.this.finish();

						}
					});

			AlertDialog alert = alertDialog.create();
			alert.show();
		}else {

			performLoginTask();


			}
	}


	public void performLoginTask(){

		String username = usernameField.getEditText().getText().toString().toLowerCase().trim();
		String password = passwordField.getEditText().getText().toString().trim();

		//This is should be a valid STAFF ID
		if (username.length() == 0) {
			UIUtils.showAlert(this, R.string.error, R.string.error_no_username);
			return;
		} else if (password.length() == 0) {
			UIUtils.showAlert(this, R.string.error, R.string.error_no_password);
			return;
		} else if (username.isEmpty() && password.isEmpty()) {
			UIUtils.showAlert(this, R.string.error, R.string.error_no_username_or_password);
			return;
		}


		// show progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setTitle(this.getString(R.string.login_process));
		pDialog.setMessage(this.getString(R.string.please_wait));
		pDialog.setCancelable(false);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.show();


		//User u = new User();
		//u.setUsername(username);
		//u.setPassword(password);

		//u = Db.checkUserExists(u);



		ArrayList<Object> users = new ArrayList<Object>();
		User un = new User();
		un.setUsername(username);
		un.setPassword(password);
		users.add(un);

		Payload p = new Payload(users);


		LoginTask lt = new LoginTask(this); //Authentication is done here
		lt.setLoginListener(this);
		lt.execute(p);


	}

}