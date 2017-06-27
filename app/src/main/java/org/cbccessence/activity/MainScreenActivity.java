package org.cbccessence.activity;

import java.util.ArrayList;

import org.cbccessence.utilities.HttpHandler;
import org.cbccessence.R;
import org.digitalcampus.oppia.activity.HelpActivity;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.service.TrackerService;
import org.cbccessence.cch.activity.LearningCenterMenuActivity;
import org.cbccessence.cch.activity.ClientActivity;
import org.cbccessence.poc.PointOfCareActivity;
import org.cbccessence.cch.model.RoutineActivity;
import org.cbccessence.cch.utils.CCHTimeUtil;
import org.digitalcampus.oppia.utils.UIUtils;
import org.joda.time.DateTime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends AppCompatActivity  {

	private Context mContext;

	public static final String TAG = MainScreenActivity.class.getSimpleName();

	SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
	private DbHelper dbh;
	private SharedPreferences prefs;
	private Animation slide_up;
	private CCHTimeUtil timeUtils;
	private DateTime today;
	private String name;
	private RadioGroup reminder;
	private RadioButton weekRadioButton;
	private RadioButton dayRadioButton;
	public static Dialog dialog ;
	HttpHandler sh;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	    setContentView(R.layout.activity_new_mainscreen);

		sh = new HttpHandler(this);

	    mContext = MainScreenActivity.this;

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowHomeEnabled(true);
			getSupportActionBar().setTitle("Home");

		}


		dbh =   DbHelper.getInstance(this);
		timeUtils = new CCHTimeUtil();
		today = new DateTime();

		// set up local dirs
		/*if(!MobileLearning.createDirs()){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setTitle(R.string.error);
			builder.setMessage(R.string.error_sdcard);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					dialog.dismiss();
					finish();
				}
			});
			builder.show();
			return;
		}
*/
		 try{

			name = prefs.getString("first_name", "name");

		 }catch(Exception e){
			 e.printStackTrace();
		 }



		LinearLayout registration = (LinearLayout) findViewById(R.id.registration);
        LinearLayout poc = (LinearLayout) findViewById(R.id.poc);
        LinearLayout learning = (LinearLayout) findViewById(R.id.learning);
        LinearLayout achievements = (LinearLayout) findViewById(R.id.achievements);

        registration.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
/*
				if(!sh.checkInternetConnection()) {
					sh.showAlertDialog(MainScreenActivity.this, "No internet connection", "Please connect to the internet and try again!");

					return;
				}*/

				Intent intent=new Intent(mContext, ClientActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		});
		
		poc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {


				Intent intent=new Intent(mContext, PointOfCareActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		});
		learning.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, LearningCenterMenuActivity.class);
	            startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		});

		/*achievements.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, StayingWellActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				
			}
		});*/



		/*achievements.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(!sh.checkInternetConnection()) {
					sh.showAlertDialog(MainScreenActivity.this, "No internet connection", "Please connect to the internet and try again!");

					return;
				}
				Intent intent = new Intent(mContext, AchievementCenterActivity.class);
	            startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		});*/


	    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager2);
        
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);


        //test
        Log.i(TAG, "Current log Array is\t" + dbh.getAllLogs());

	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
        }
        
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
                Fragment fragment =  new EventsSummary();
			/*if(position==0 ){
                	 fragment= new EventsSummary();   
                }else if(position==1){
                	 fragment= new RoutineActivityDetails(mViewPager);
                }*/
                return fragment;
        }

        @Override
        public int getCount() {
                return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
}





	
	 public static class EventsSummary extends Fragment {
		 View rootView;
		 private TextView event_number;
		 private TextView textView_eventsClickHere;
		 private TextView textView_eventTargetsNumber;
		 private TextView textView_clickHere;
		 private TextView textView_routinesNumber;
		 private TextView textView_routinesClickHere;
		 private TextView tv8;
		 String due_date;
		 Context context;
		 private DbHelper dbh;
		 private TextView status;


		 private SharedPreferences prefs;
		 private String firstName;

		private int numactivities;
		 public EventsSummary(){
			 
		 }

		 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			 	rootView=inflater.inflate(R.layout.events_pager_layout,null,false);
			 	prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			    status = (TextView) rootView.findViewById(R.id.textView_status);
			    dbh =   DbHelper.getInstance(getActivity());

			    firstName = prefs.getString("first_name", "firstName");


		    	status.setText("Good " + UIUtils.getTime() + " " + firstName + "!");


				/* Routine Info */
				ArrayList<RoutineActivity> todos = new ArrayList<RoutineActivity>();

			// todos= dbh.getSWRoutineActivities();


				if(todos!=null){
					numactivities = todos.size();
				}else{
					numactivities = 0;
				}
			    textView_routinesNumber = (TextView) rootView.findViewById(R.id.textView_routinesNumber);
				textView_routinesNumber.setText(String.valueOf(numactivities));
			    tv8 = (TextView) rootView.findViewById(R.id.textView8);
			    tv8.setText(" activity(ies) this "+ UIUtils.getTime()+".");
				
			    textView_routinesClickHere = (TextView) rootView.findViewById(R.id.textView_routinesClickHere);
			    textView_routinesClickHere.setOnClickListener(new OnClickListener(){

			    	@Override
					public void onClick(View v) {
						if(numactivities > 0){
							//mViewPager.setCurrentItem(2, true);
						} else {
							 Toast.makeText(getActivity(), "No activities for this "+UIUtils.getTime(),Toast.LENGTH_SHORT).show();
						}
					}
				});
			    
			    return rootView;
		 }
	 }
	 

	 	 
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.activity_home, menu);
			return true;
		}

		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int itemId = item.getItemId();
			if (itemId == R.id.menu_settings) {
				Intent i = new Intent(this, PrefsActivity.class);
				startActivity(i);
				return true;
			} else if (itemId == R.id.menu_about) {
				startActivity(new Intent(this, AboutActivity.class));
				return true;
			} else if (itemId == R.id.menu_help) {
				startActivity(new Intent(this, HelpActivity.class));
				return true;
			} else if (itemId == R.id.menu_logout) {
				logout();
				return true;
			} else if (itemId == R.id.menu_sync) {
				Intent service = new Intent(this, TrackerService.class);
				Bundle tb = new Bundle();
				tb.putBoolean("backgroundData", true);
				service.putExtras(tb);
				//this.startService(service);
				
				return true;
			}
			
			return true;
		}

		private void logout() {

			AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialog);
			builder.setCancelable(false);
			builder.setTitle(R.string.logout);
			builder.setMessage(R.string.logout_confirm);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
																									


					Editor editor = prefs.edit();
					editor.putBoolean("isSignedIn", false);
					editor.apply();

					// restart the app
					MainScreenActivity.this.startActivity(new Intent(MainScreenActivity.this, LoginActivity.class));
					MainScreenActivity.this.finish();

				}
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return; // do nothing
				}
			});
			builder.show();
		}
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(TAG, key + " changed");
			if(key.equalsIgnoreCase(getString(R.string.prefs_server))){
				Editor editor = sharedPreferences.edit();
				if(!sharedPreferences.getString(getString(R.string.prefs_server), "").endsWith("/")){
					String newServer = sharedPreferences.getString(getString(R.string.prefs_server), "").trim()+"/";
					editor.putString(getString(R.string.prefs_server), newServer);
			    	editor.commit();
				}
			}
			if(key.equalsIgnoreCase(getString(R.string.prefs_points)) || key.equalsIgnoreCase(getString(R.string.prefs_badges))){
				supportInvalidateOptionsMenu();
			}
		}
	
	private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
	private long mBackPressed;
	
	@Override
	public void onBackPressed()
	{
	    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) 
	    { 
	        super.onBackPressed(); 
	        return;
	    }
	    else { Toast.makeText(this, "Press back button again to exit", Toast.LENGTH_SHORT).show(); }

	    mBackPressed = System.currentTimeMillis();
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
