/* 
 * This file is part of OppiaMobile - https://digital-campus.org/
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;


import org.cbccessence.R;
import org.digitalcampus.oppia.application.ScheduleReminders;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.listener.APIKeyRequestListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;

public class AppActivity extends AppCompatActivity implements APIKeyRequestListener {
	
	public static final String TAG = AppActivity.class.getSimpleName();

    /**
	 * @param activities: list of activities to show on the ScheduleReminders section
	 */
	public void drawReminders(ArrayList<org.digitalcampus.oppia.model.Activity> activities){
        ScheduleReminders reminders = (ScheduleReminders) findViewById(R.id.schedule_reminders);
        if (reminders != null){
            reminders.initSheduleReminders(activities);
        }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}
		return true;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // ActionBar actionBar = getSupportActionBar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            //If we are in a course-related activity, we show its title
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Bundle bundle = this.getIntent().getExtras();
            if (bundle != null) {
                org.digitalcampus.oppia.model.Course course = (org.digitalcampus.oppia.model.Course) bundle.getSerializable(org.digitalcampus.oppia.model.Course.TAG);
                if (course == null ) return;
                String title = course.getTitle(prefs.getString(PrefsActivity.PREF_LANGUAGE, Locale.getDefault().getLanguage()));
                setTitle(title);
                getSupportActionBar().setTitle(title);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //Check if the apiKey of the current user is valid
        boolean apiKeyValid = SessionManager.isUserApiKeyValid(this);
        if (!apiKeyValid){
            apiKeyInvalidated();
        }

        //We check if the user session time has expired to log him out
        if (org.digitalcampus.oppia.application.MobileLearning.SESSION_EXPIRATION_ENABLED){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            long now = System.currentTimeMillis()/1000;
            long lastTimeActive = prefs.getLong(PrefsActivity.LAST_ACTIVE_TIME, now);
            long timePassed = now - lastTimeActive;

            prefs.edit().putLong(PrefsActivity.LAST_ACTIVE_TIME, now).apply();
            if (timePassed > org.digitalcampus.oppia.application.MobileLearning.SESSION_EXPIRATION_TIMEOUT){
                Log.d(TAG, "Session timeout (passed " + timePassed + " seconds), logging out");
                logoutAndRestartApp();
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (org.digitalcampus.oppia.application.MobileLearning.SESSION_EXPIRATION_ENABLED){
            long now = System.currentTimeMillis()/1000;
            PreferenceManager
                .getDefaultSharedPreferences(this).edit()
                .putLong(PrefsActivity.LAST_ACTIVE_TIME, now).apply();
        }
    }

    public void logoutAndRestartApp(){
        SessionManager.logoutCurrentUser(this);

        Intent restartIntent = new Intent(this, StartUpActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(restartIntent);
        this.finish();
    }

    @Override
    public void apiKeyInvalidated() {
        org.digitalcampus.oppia.utils.UIUtils.showAlert(this, R.string.error, R.string.error_apikey_expired, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                logoutAndRestartApp();
                return true;
            }
        });
    }
}
