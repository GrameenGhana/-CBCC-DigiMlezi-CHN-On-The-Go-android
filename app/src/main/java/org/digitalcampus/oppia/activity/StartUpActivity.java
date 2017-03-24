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


import java.io.File;
import java.util.ArrayList;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.listener.InstallCourseListener;
import org.digitalcampus.oppia.listener.PostInstallListener;
import org.digitalcampus.oppia.model.DownloadProgress;
import org.digitalcampus.oppia.task.InstallDownloadedCoursesTask;
import org.digitalcampus.oppia.task.Payload;
import org.digitalcampus.oppia.task.PostInstallTask;
import org.cbccessence.cch.tasks.CourseDetailsTask;

import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StartUpActivity extends AppCompatActivity implements PostInstallListener, InstallCourseListener {

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

                    if(isOnline()){
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
                    }

				}
				catch (InterruptedException e) {
					// do nothing
				} finally {

                    endStartUpScreen();

				}
			}
		};
		splashTread.start();






        if(!MobileLearning.createDirs()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.error_sdcard);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                    StartUpActivity.this.finish();
                }
            });
            builder.show();
            return;
        }
 		

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

	private void installCourses(){
		File dir = new File(MobileLearning.DOWNLOAD_PATH);
		String[] children = dir.list();
		if (children != null) {
			ArrayList<Object> data = new ArrayList<Object>();
     		Payload payload = new Payload(data);
			InstallDownloadedCoursesTask imTask = new InstallDownloadedCoursesTask(this);
			imTask.setInstallerListener(this);
			imTask.execute(payload);
		} else {
			endStartUpScreen();
		}
	}
	
	public void upgradeComplete(Payload p) {
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


	public void postInstallComplete(Payload response) {
		this.installCourses();
	}

	public void downloadComplete(Payload p) {
		// do nothing
		
	}

	public void downloadProgressUpdate(DownloadProgress dp) {
		// do nothing
		
	}

	public void installComplete(Payload p) {
		if(p.getResponseData().size()>0){
			Editor e = prefs.edit();
			e.putLong(getString(R.string.prefs_last_media_scan), 0);
			e.apply();
		}
		endStartUpScreen();	
	}

    @Override
    public void installProgressUpdate(DownloadProgress dp) {

    }


    public boolean isOnline() {
		 boolean haveConnectedWifi = false;
		    boolean haveConnectedMobile = false;

		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		    for (NetworkInfo ni : netInfo) {
		        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
		            if (ni.isConnected())
		                haveConnectedWifi = true;
		        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
		            if (ni.isConnected())
		                haveConnectedMobile = true;
		    }
		    return haveConnectedWifi || haveConnectedMobile;
	}
}
