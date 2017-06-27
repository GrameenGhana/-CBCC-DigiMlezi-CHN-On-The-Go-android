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

package org.cbccessence.activity;


import java.io.File;
import java.util.ArrayList;

import org.cbccessence.R;
import org.digitalcampus.oppia.activity.AppActivity;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.application.PermissionsManager;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.listener.InstallCourseListener;
import org.digitalcampus.oppia.listener.PostInstallListener;
import org.digitalcampus.oppia.listener.PreloadAccountsListener;
import org.digitalcampus.oppia.listener.StorageAccessListener;
import org.digitalcampus.oppia.listener.UpgradeListener;
import org.digitalcampus.oppia.model.DownloadProgress;
import org.digitalcampus.oppia.service.GCMRegistrationService;
import org.digitalcampus.oppia.task.InstallDownloadedCoursesTask;
import org.digitalcampus.oppia.task.Payload;
import org.digitalcampus.oppia.task.PostInstallTask;
import org.digitalcampus.oppia.task.UpgradeManagerTask;
import org.digitalcampus.oppia.utils.GooglePlayUtils;
import org.digitalcampus.oppia.utils.storage.Storage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartUpActivity extends AppActivity implements PostInstallListener, InstallCourseListener, UpgradeListener, PreloadAccountsListener {

	public final static String TAG = StartUpActivity.class.getSimpleName();
	private TextView tvProgress;
	private SharedPreferences prefs;
	private DbHelper dbh;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = getWindow(); // in Activity's onCreate() for instance
			w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
        

		if(getSupportActionBar() != null)
        getSupportActionBar().hide();
        setContentView(R.layout.start_up);


		Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		anim.reset();
		RelativeLayout splash = (RelativeLayout) findViewById(R.id.splash_layout);
		splash.clearAnimation();
		splash.startAnimation(anim);

		anim = AnimationUtils.loadAnimation(this, R.anim.translate_splash_screen);
		anim.reset();
		ImageView iv = (ImageView) findViewById(R.id.splash_logo);
		iv.clearAnimation();
		iv.startAnimation(anim);

/*

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					// Splash screen pause time
					while (waited < 2500) {
						sleep(100);
						waited += 100;
					}



					endStartUpScreen();




                   */
/* if(isOnline()){
                        try{
							String url = getResources().getString(R.string.serverDefaultAddress)+"/"+MobileLearning.CCH_COURSE_DETAILS_PATH;
                            Log.i("Start Up Act.", url);
							Log.i("Get course details", url);
                            if(dbh.getCourseGroups()>=0){
                                CourseDetailsTask courseDetails = new CourseDetailsTask(StartUpActivity.this);
                                courseDetails.execute(url);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }*//*


				}
				catch (InterruptedException e) {
					// do nothing
				} */
/*finally {

					finish();

				}*//*

			}
		};
		splashTread.start();
*/








	}


	@Override
	public void onResume(){
		super.onResume();

		/*boolean shouldContinue = PermissionsManager.CheckPermissionsAndInform(this);
		if (!shouldContinue) return;*/

		if (MobileLearning.DEVICEADMIN_ENABLED) {
			//We need to check again the Google Play API availability
			boolean isGooglePlayAvailable = GooglePlayUtils.checkPlayServices(this,
					new GooglePlayUtils.DialogListener() {
						@Override
						public void onErrorDialogClosed() {
							//If Google play is not available, we need to close the app
							 StartUpActivity.this.finish();
						}
					});
			if (!isGooglePlayAvailable) {
				this.finish();
				return;
			}
			// Start IntentService to register the phone with GCM.
			Intent intent = new Intent(this, GCMRegistrationService.class);
			startService(intent);
		}

		UpgradeManagerTask umt = new UpgradeManagerTask(this);
		umt.setUpgradeListener(this);
		ArrayList<Object> data = new ArrayList<>();
		Payload p = new Payload(data);
		umt.execute(p);

	}
	
	private void endStartUpScreen() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StartUpActivity.this);
		boolean isSignedIn = prefs.getBoolean("isSignedIn", false);
        // launch new activity and close splash screen
		if (!isSignedIn) {
			startActivity(new Intent(StartUpActivity.this, LoginActivity.class));
			finish();
			 overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else {
			startActivity(new Intent(StartUpActivity.this, MainScreenActivity.class));
			finish();
			 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
		}
    }



	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		PermissionsManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}


	private void updateProgress(String text){
		if(tvProgress != null){
			tvProgress.setText(text);
		}
	}





	private void installCourses(){
		File dir = new File(Storage.getDownloadPath(this));
		String[] children = dir.list();
		if (children != null) {
			ArrayList<Object> data = new ArrayList<>();
			Payload payload = new Payload(data);
			InstallDownloadedCoursesTask imTask = new InstallDownloadedCoursesTask(this);
			imTask.setInstallerListener(this);
			imTask.execute(payload);
		} else {
			preloadAccounts();

		}
	}

	private void preloadAccounts(){
		SessionManager.preloadUserAccounts(this, this);
	}

	public void upgradeComplete(final Payload p) {

		if (Storage.getStorageStrategy().needsUserPermissions(this)){
			Log.d(TAG, "Asking user for storage permissions");
			Storage.getStorageStrategy().askUserPermissions(this, new StorageAccessListener() {
				@Override
				public void onAccessGranted(boolean isGranted) {
					Log.d(TAG, "Access granted for storage: " + isGranted);
					if (!isGranted) {
						Toast.makeText(StartUpActivity.this, getString(R.string.storageAccessNotGranted), Toast.LENGTH_LONG).show();
					}
					afterUpgrade(p);
				}
			});
		}
		else{
			afterUpgrade(p);
		}
	}

	private void afterUpgrade(Payload p){
		// set up local dirs
		if(!Storage.createFolderStructure(this)){
			AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
			builder.setCancelable(false);
			builder.setTitle(R.string.error);
			builder.setMessage(R.string.error_sdcard);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				 StartUpActivity.this.finish();
				}
			});
			builder.show();
			return;
		}

		if(p.isResult()){
			Payload payload = new Payload();
			PostInstallTask piTask = new PostInstallTask(this);
			piTask.setPostInstallListener(this);
			piTask.execute(payload);
		} else {
			// now install any new courses
			this.installCourses();
		}
	}



	public void upgradeProgressUpdate(String s) { this.updateProgress(s); }
	public void postInstallComplete(Payload response) {
		this.installCourses();
	}

	public void downloadComplete(Payload p) { }
	public void downloadProgressUpdate(DownloadProgress dp) { }

	public void installComplete(Payload p) {
		if(p.getResponseData().size()>0){
			prefs.edit().putLong(PrefsActivity.PREF_LAST_MEDIA_SCAN, 0).apply();
		}
		preloadAccounts();
	}

	public void installProgressUpdate(DownloadProgress dp) {
		this.updateProgress(dp.getMessage());
	}

	@Override
	public void onPreloadAccountsComplete(Payload payload) {
		if ((payload!=null) && payload.isResult()){
			Toast.makeText(this, payload.getResultResponse(), Toast.LENGTH_LONG).show();
		}
		endStartUpScreen();
	}


 }
