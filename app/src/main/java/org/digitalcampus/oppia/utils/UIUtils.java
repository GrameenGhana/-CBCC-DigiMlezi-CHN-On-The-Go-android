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

package org.digitalcampus.oppia.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.cbccessence.R;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.activity.ScorecardActivity;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.exception.UserNotFoundException;
import org.digitalcampus.oppia.model.CbccUser;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.model.Lang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

public class UIUtils {

	public final static String TAG = UIUtils.class.getSimpleName();
	

	public static void showUserData(Menu menu, final Context ctx, @Nullable final Course courseInContext) {
		MenuItem pointsItem = menu.findItem(R.id.points);
      	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		
		DbHelper db = DbHelper.getInstance(ctx);
		CbccUser u;
		try {
			u = db.getUser(SessionManager.getUsername(ctx));
			Log.d(TAG,"username: " + u.getUsername());
			Log.d(TAG,"points: " + u.getPoints());
		} catch (UserNotFoundException e) {
			return;
		}
		
		if(pointsItem == null){
			return;
		}
		
		TextView points = (TextView) pointsItem.getActionView().findViewById(R.id.userpoints);
		TextView badges = (TextView) pointsItem.getActionView().findViewById(R.id.userbadges);

		if(points == null || badges == null){
			return;
		}
		
		
		boolean scoringEnabled = prefs.getBoolean(PrefsActivity.PREF_SCORING_ENABLED, true);
		if (scoringEnabled) {
			points.setVisibility(View.VISIBLE);
			points.setText(String.valueOf(u.getPoints()));
            points.setClickable(true);
            points.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, ScorecardActivity.class);
                    Bundle tb = new Bundle();
                    tb.putString(ScorecardActivity.TAB_TARGET, ScorecardActivity.TAB_TARGET_POINTS);
                    if (courseInContext != null){
                        tb.putSerializable(Course.TAG, courseInContext);
                    }
                    i.putExtras(tb);
                    ctx.startActivity(i);
                }
            });
		} else {
			points.setVisibility(View.GONE);
		}
		
		boolean badgingEnabled = prefs.getBoolean(PrefsActivity.PREF_BADGING_ENABLED, true);
		if (badgingEnabled) {
			badges.setVisibility(View.VISIBLE);
			badges.setText(String.valueOf(u.getBadges()));
            badges.setClickable(true);
            badges.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, ScorecardActivity.class);
                    Bundle tb = new Bundle();
                    tb.putString(ScorecardActivity.TAB_TARGET, ScorecardActivity.TAB_TARGET_BADGES);
                    if (courseInContext != null){
                        tb.putSerializable(Course.TAG, courseInContext);
                    }
                    i.putExtras(tb);
                    ctx.startActivity(i);
                }
            });
		} else {
			badges.setVisibility(View.GONE);
		}
	}


	public static void showUserData(Menu menu, Context ctx) {
		MenuItem pointsItem = menu.findItem(R.id.points);

		if(pointsItem == null){
			return;
		}

		TextView points = (TextView) pointsItem.getActionView().findViewById(R.id.userpoints);
		TextView badges = (TextView) pointsItem.getActionView().findViewById(R.id.userbadges);

		if(points == null || badges == null){
			return;
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean scoringEnabled = prefs.getBoolean(ctx.getString(R.string.prefs_scoring_enabled), true);
		if (scoringEnabled) {
			points.setVisibility(View.VISIBLE);
			badges.setVisibility(View.VISIBLE);
			points.setText(String.valueOf(prefs.getInt(ctx.getString(R.string.prefs_points), 0)));
			badges.setText(String.valueOf(prefs.getInt(ctx.getString(R.string.prefs_badges), 0)));
		} else {
			points.setVisibility(View.GONE);
			badges.setVisibility(View.GONE);
		}
	}



	/**
	 * @param ctx
	 * @param title
	 * @param msg
	 * @return
	 */
	public static AlertDialog showAlert(Context ctx, int title, int msg) {
		return UIUtils.showAlert(ctx, ctx.getString(title), ctx.getString(msg), ctx.getString(R.string.close));
	}

	/**
	 * @param ctx
	 * @param title
	 * @param msg
	 * @return
	 */
	public static AlertDialog showAlert(Context ctx, int title, int msg, int btnText) {
		return UIUtils.showAlert(ctx, ctx.getString(title), ctx.getString(msg), ctx.getString(btnText));
	}
	
	/**
	 * @param ctx
	 * @param Res
	 * @param msg
	 * @return
	 */
	public static AlertDialog showAlert(Context ctx, int Res, String msg) {
		return UIUtils.showAlert(ctx, ctx.getString(Res), msg, ctx.getString(R.string.close));
	}

	public static AlertDialog showAlert(Context ctx, String title, String msg) {
		return UIUtils.showAlert(ctx, title, msg, ctx.getString(R.string.close));
	}
	
	/**
	 * @param ctx
	 * @param title
	 * @param msg
	 * @return
	 */
	public static AlertDialog showAlert(Context ctx, String title, String msg, String btnText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setNeutralButton(btnText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
        alert.show();
		return alert;
	}

	/**
	 * @param ctx
	 * @param title
	 * @param msg
	 * @param funct
	 * @return
	 */
	public static void showAlert(Context ctx, int title, int msg, Callable<Boolean> funct) {
        UIUtils.showAlert(ctx, ctx.getString(title), ctx.getString(msg), funct);
	}

	public static void showAlert(Context ctx, int title, int msg, int btnText, Callable<Boolean> funct) {
		UIUtils.showAlert(ctx, ctx.getString(title), ctx.getString(msg),ctx.getString(btnText), funct);
	}
	/**
	 * @param ctx
	 * @param R
	 * @param msg
	 * @param funct
	 * @return
	 */
	public static void showAlert(Context ctx, int R, CharSequence msg, Callable<Boolean> funct) {
		UIUtils.showAlert(ctx, ctx.getString(R), msg, funct);
	}

	public static void showAlert(Context ctx, String title, CharSequence msg, final Callable<Boolean> funct) {
		UIUtils.showAlert(ctx, title, msg, ctx.getString(R.string.close),funct);
	}
	/**
	 * @param ctx
	 * @param title
	 * @param msg
	 * @param funct
	 * @return
	 */
	public static void showAlert(Context ctx, String title, CharSequence msg, String btnText, final Callable<Boolean> funct) {
        if ( ctx instanceof Activity) {
            Activity activity = (Activity) ctx;
            if ( activity.isFinishing() ) { return; }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setNeutralButton(btnText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				try {
					funct.call();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		AlertDialog alert = builder.create();
        alert.show();
	}
	
	
	/**
	 * 
	 * @param ctx
	 * @param langs
	 * @param prefs
	 * @param funct
	 */
	public static void createLanguageDialog(Context ctx, ArrayList<Lang> langs, final SharedPreferences prefs, final Callable<Boolean> funct) {
        ArrayList<String> langStringList = new ArrayList<String>();
        final ArrayList<Lang> languagesList = new ArrayList<Lang>();
		
		// make sure there aren't any duplicates
		for(Lang lang: langs){
			boolean found = false;
			for(Lang ln: languagesList){
				if(ln.getLang().equals(lang.getLang())){
					found = true;
                    break;
				}
			}
			if(!found){ languagesList.add(lang); }
		}
		
		int prefLangPosition = -1;
		int i = 0;

        String prefLanguage = prefs.getString(PrefsActivity.PREF_LANGUAGE, Locale.getDefault().getLanguage());
		for(Lang lang: languagesList){
			Locale locale = new Locale(lang.getLang());
			String langDisp = locale.getDisplayLanguage(locale);
			langStringList.add(langDisp);
			if (lang.getLang().equals(prefLanguage)) {
                prefLangPosition = i;
			}
			i++;
		}
		
		// only show if at least one language
		if (i > 0) {
			ArrayAdapter<String> arr = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_singlechoice,langStringList);
			AlertDialog mAlertDialog = new AlertDialog.Builder(ctx)
					.setSingleChoiceItems(arr, prefLangPosition, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String newLang = languagesList.get(whichButton).getLang();
							Editor editor = prefs.edit();
							editor.putString(PrefsActivity.PREF_LANGUAGE, newLang);
							editor.commit();
							dialog.dismiss();
							try {
								funct.call();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).setTitle(ctx.getString(R.string.change_language))
					.setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
	
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
	
					}).create();
			mAlertDialog.show();
		}
	}


	public String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd-MM-yyyy", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}


	public static String getTime()
	{
		Time time = new Time();
		time.setToNow();

		if (time.hour < 12)
		{
			return "morning";
		}
		else if (time.hour >= 12 && time.hour <= 17)
		{
			return "afternoon";
		}
		else if (time.hour > 17 && time.hour < 23)
		{
			return "evening";
		}
		else
		{
			return "";
		}
	}


}
