package org.cbccessence.poc;

import org.cbccessence.R;
import org.cbccessence.activity.AboutActivity;
import org.digitalcampus.oppia.activity.HelpActivity;
import org.cbccessence.activity.MainScreenActivity;
import org.cbccessence.activity.StartUpActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity  extends AppCompatActivity {
	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.custom_action_bar, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		Intent intent;
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	 intent = new Intent(Intent.ACTION_MAIN);
	        	 intent.setClass(BaseActivity.this, MainScreenActivity.class);
	        	 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 	          startActivity(intent);
	 	          finish();
	 	         overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
	            return true;
        case R.id.action_help:
        	   	intent = new Intent(Intent.ACTION_MAIN);
	        	intent.setClass(BaseActivity.this, HelpActivity.class);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 	          startActivity(intent);
	 	          finish();
	 	         overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
	            return true;
        case R.id.action_about:
	        	intent = new Intent(Intent.ACTION_MAIN);
	        	intent.setClass(BaseActivity.this, AboutActivity.class);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 	          startActivity(intent);
	 	          finish();
	 	         overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
	        	return true;
	        	
        case R.id.action_logout:
        	logout();
        	
        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	
	
	}
	
	public void logout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
		builder.setCancelable(false);
		builder.setTitle(R.string.logout);
		builder.setMessage(R.string.logout_confirm);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				

				
				// restart the app
				BaseActivity.this.startActivity(new Intent(BaseActivity.this, StartUpActivity.class));
				BaseActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_left);
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return; // do nothing
			}
		});
		builder.show();
	}


	public void showAlertDialog(@NonNull boolean cancelable,
								@Nullable String title,
								@Nullable String message,
								@Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
 								@Nullable DialogInterface.OnClickListener onNegativeButtonClickListener
								) {
		android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.AppDialog);
		builder.setTitle(title);
		builder.setCancelable(cancelable);
		builder.setMessage(message);
		builder.setPositiveButton("OK", onPositiveButtonClickListener);
		builder.setNegativeButton("CANCEL", onNegativeButtonClickListener);
		builder.show();
	}
}
