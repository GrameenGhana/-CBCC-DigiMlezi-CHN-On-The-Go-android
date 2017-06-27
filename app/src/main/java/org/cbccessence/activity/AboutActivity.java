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

import java.util.Locale;

import org.cbccessence.R;
import org.cbccessence.utilities.FileUtils;
 import org.digitalcampus.oppia.activity.AppActivity;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
 import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;

public class AboutActivity extends AppActivity {
	DbHelper databaseHelper;


	private Long start_time;
	private Long end_time;
	String log_type = "module_usage";

	public static final String TAG = AboutActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		databaseHelper =   DbHelper.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String url = "file:///android_asset/" + FileUtils.getLocalizedFilePath(this,prefs.getString(getString(R.string.prefs_language), Locale.getDefault().getLanguage()) , "about.html");
		WebView wv = (WebView) findViewById(R.id.about_webview);
		wv.loadUrl(url);
		
		TextView versionNo = (TextView)  findViewById(R.id.about_versionno);
		try {
			String no = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			versionNo.setText(getString(R.string.version,no));
		} catch (NameNotFoundException e) {

			e.printStackTrace();
			/*if(!MobileLearning.DEVELOPER_MODE){
				BugSenseHandler.sendException(e);
			} else {
				e.printStackTrace();
			}*/
		}
		
	}






	@Override
	public void onStart(){
		super.onStart();

		start_time = System.currentTimeMillis();



	}


	@Override
	public void onStop(){
		super.onStop();
		end_time = System.currentTimeMillis();
		try {

			JSONObject log = new JSONObject();
			log.put("name", TAG);
			log.put("start_time", start_time);
			log.put("end_time", end_time );
			log.put("time_taken", end_time - start_time );

			if(databaseHelper.addLog(log_type, log.toString()))
				Log.i(TAG, "Log added with data\t" + log);


		}catch(Exception e){
			e.printStackTrace();
		}


	}
}
