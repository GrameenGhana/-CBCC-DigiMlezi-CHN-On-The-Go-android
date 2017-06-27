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

package org.digitalcampus.oppia.application;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;


import org.cbccessence.cch.model.User;
import org.cbccessence.models.POCSection;
import org.cbccessence.models.SubSection;
import org.cbccessence.models.Topic;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.exception.InvalidXMLException;
import org.digitalcampus.oppia.exception.UserNotFoundException;
import org.digitalcampus.oppia.listener.DBListener;
import org.digitalcampus.oppia.model.Activity;
import org.digitalcampus.oppia.model.ActivitySchedule;
import org.digitalcampus.oppia.model.CbccUser;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.model.QuizAttempt;
import org.digitalcampus.oppia.model.QuizStats;
import org.digitalcampus.oppia.model.SearchResult;
import org.digitalcampus.oppia.model.TrackerLog;
import org.digitalcampus.oppia.task.Payload;
import org.digitalcampus.oppia.utils.xmlreaders.CourseXMLReader;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

	static final String TAG = DbHelper.class.getSimpleName();
	static final String DB_NAME = "mobilelearning.db";
	static final int DB_VERSION = 1;

    private static DbHelper instance;
	private SQLiteDatabase db;
	private SharedPreferences prefs;


	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " integer";
	private static final String COMMA_SEP = ",";


	
	private static final String COURSE_TABLE = "Module";
	private static final String COURSE_C_ID = BaseColumns._ID;
	private static final String COURSE_C_VERSIONID = "versionid";
	private static final String COURSE_C_TITLE = "title";
	private static final String COURSE_C_DESC = "description";
	private static final String COURSE_C_SHORTNAME = "shortname";
	private static final String COURSE_C_LOCATION = "location";
	private static final String COURSE_C_SCHEDULE = "schedule";
	private static final String COURSE_C_IMAGE = "imagelink";
	private static final String COURSE_C_LANGS = "langs";
	private static final String COURSE_C_ORDER_PRIORITY = "orderpriority";
    private static final String COURSE_C_SEQUENCING = "sequencing";
	
	private static final String ACTIVITY_TABLE = "Activity";
	private static final String ACTIVITY_C_ID = BaseColumns._ID;
	private static final String ACTIVITY_C_COURSEID = "modid"; // reference to COURSE_C_ID
	private static final String ACTIVITY_C_SECTIONID = "sectionid";
	private static final String ACTIVITY_C_ACTID = "activityid";
	private static final String ACTIVITY_C_ACTTYPE = "activitytype";
	private static final String ACTIVITY_C_ACTIVITYDIGEST = "digest";
	private static final String ACTIVITY_C_STARTDATE = "startdate";
	private static final String ACTIVITY_C_ENDDATE = "enddate";
	private static final String ACTIVITY_C_TITLE = "title";

	private static final String TRACKER_LOG_TABLE = "TrackerLog";
	private static final String TRACKER_LOG_C_ID = BaseColumns._ID;
	private static final String TRACKER_LOG_C_COURSEID = "modid"; // reference to COURSE_C_ID
	private static final String TRACKER_LOG_C_DATETIME = "logdatetime";
	private static final String TRACKER_LOG_C_ACTIVITYDIGEST = "digest";
	private static final String TRACKER_LOG_C_DATA = "logdata";
	private static final String TRACKER_LOG_C_SUBMITTED = "logsubmitted";
	private static final String TRACKER_LOG_C_INPROGRESS = "loginprogress";
	private static final String TRACKER_LOG_C_COMPLETED = "completed";
	private static final String TRACKER_LOG_C_USERID = "userid";
	private static final String TRACKER_LOG_C_TYPE = "type";
	
	private static final String QUIZATTEMPTS_TABLE = "results";
	private static final String QUIZATTEMPTS_C_ID = BaseColumns._ID;
	private static final String QUIZATTEMPTS_C_DATETIME = "resultdatetime";
	private static final String QUIZATTEMPTS_C_DATA = "content";
	private static final String QUIZATTEMPTS_C_SENT = "submitted";
	private static final String QUIZATTEMPTS_C_COURSEID = "moduleid";
	private static final String QUIZATTEMPTS_C_USERID = "userid";
	private static final String QUIZATTEMPTS_C_SCORE = "score";
	private static final String QUIZATTEMPTS_C_MAXSCORE = "maxscore";
	private static final String QUIZATTEMPTS_C_PASSED = "passed";
	private static final String QUIZATTEMPTS_C_ACTIVITY_DIGEST = "actdigest";

	private static final String SEARCH_TABLE = "search";
	private static final String SEARCH_C_TEXT = "fulltext";
	private static final String SEARCH_C_COURSETITLE = "coursetitle";
	private static final String SEARCH_C_SECTIONTITLE = "sectiontitle";
	private static final String SEARCH_C_ACTIVITYTITLE = "activitytitle";
	
	private static final String CBCC_USER_TABLE = "cbcc_user";
	private static final String USER_C_ID = BaseColumns._ID;
	private static final String USER_C_USERNAME = "username";
	private static final String USER_C_FIRSTNAME = "firstname";
	private static final String USER_C_LASTNAME = "lastname";
	private static final String USER_C_PASSWORDENCRYPTED = "passwordencrypted";
	private static final String USER_C_APIKEY = "apikey";
	private static final String USER_C_LAST_LOGIN_DATE = "lastlogin";
	private static final String USER_C_NO_LOGINS = "nologins";
	private static final String USER_C_POINTS = "points";
	private static final String USER_C_BADGES = "badges";

	private static final String USER_PREFS_TABLE = "userprefs";
    private static final String USER_PREFS_C_USERNAME = "username";
    private static final String USER_PREFS_C_PREFKEY = "preference";
    private static final String USER_PREFS_C_PREFVALUE = "value";





	//Projects Table
	private static final String PROJECTS_TABLE = "projects";
	private static final String PROJECT_ID = "id";
	private static final String PROJECT_OWNER_ID = "owner_id";
	private static final String PROJECT_NAME = "name";
	private static final String PROJECT_DATE_CREATED = "date_created";
	private static final String PROJECT_DATE_UPDATED = "date_updated";



	//Sections Table
	private static final String SECTIONS_TABLE = "sections";

	private static final String SECTION_ID = "section_id";
	private static final String SECTION_TEAM_ID = "team_id";
	private static final String SECTION_MODIFIER = "modded_by";
	private static final String SECTION_NAME = "name";
	private static final String SECTION_IMAGE_URL = "image_url";
	private static final String SECTION_DATE_CREATED = "date_created";
	private static final String SECTION_DATE_UPDATED = "date_updated";



	//Users Table

	private static final String USERS_TABLE = "registered_users";
	private static final String USER_FIRST_NAME = "first_name";
	private static final String USER_LAST_NAME = "last_name";
	private static final String USER_PHONE_NUMBER = "phone_number";
	private static final String USER_DOB = "date_of_birth";
	private static final String USER_GENDER = "gender";
	private static final String USER_NATIONAL_ID = "national_id_number";
	private static final String USER_CLIENT_TYPE = "client_type";
	private static final String USER_AFYA_CHANNEL = "afya_communication_channel";
	private static final String USER_AFYA_STARTWEEK = "afya_start_week";
	private static final String USER_FACILITY_ID = "facility_id";
	private static final String USER_IS_INSURED = "insured";
	private static final String USER_LANGUAGE = "language";
	private static final String USER_LOCATION = "location";
	private static final String USER_EDUCATION = "education";
	private static final String USER_RELATIVE_PHONE_NUMBER = "relative_phone_number";
	private static final String TIMESTAMP = "timestamp";
	private static final String SYNC_STATUS = "sync_status";
	private static final String UID = "uid";
	private static final String IS_AFYA = "is_afya";




	//SubSections Table
	private static final String SUB_SECTIONS_TABLE = "sub_sections";
	private static final String SUB_SECTION_ID = "sub_section_id";
	private static final String SUB_SECTION_TEAM_ID = "team_id";
	private static final String SUB_SECTION_SID = "section_id";
	private static final String SUB_SECTION_MODIFIER = "modded_by";
	private static final String SUB_SECTION_NAME = "name";
	private static final String SUB_SECTION_S_NAME = "section_name";
	private static final String SUB_SECTION_IMAGE_URL = "image_url";
	private static final String SUB_SECTION_DATE_CREATED = "date_created";
	private static final String SUB_SECTION_DATE_UPDATED = "date_updated";


	//Topics Table
	private static final String TOPICS_TABLE = "topics";
	private static final String TOPIC_ID = "topic_id";
	private static final String TOPIC_TEAM_ID = "team_id";
	private static final String TOPIC_SECTION_ID = "section_id";
	private static final String TOPIC_SUB_SECTION_ID = "sub_section_id";
	private static final String TOPIC_MODIFIER = "modded_by";
	private static final String TOPIC_NAME = "name";
	private static final String TOPIC_SHORT_NAME = "short_name";
	private static final String TOPIC_DESCRIPTION = "description";
	private static final String TOPIC_UPLOAD_STATUS = "upload_status";
	private static final String TOPIC_SECTION_NAME = "section_name";
	private static final String TOPIC_SUB_SECTION_NAME = "sub_section_name";
	private static final String TOPIC_FILE_URL = "file_url";
	private static final String TOPIC_DATE_CREATED = "date_created";
	private static final String TOPIC_DATE_UPDATED = "date_updated";




	//CCH Learning References  Table
	public static final String LEARNING_REFERENCES_TABLE="learning_references";
	public static final String CCH_REFERENCE_DESC="reference_desc";
	public static final String CCH_SHORTNAME="shortname";
	public static final String CCH_REFERENCE_URL="reference_url";
	public static final String CCH_REFERENCE_SIZE="size";











	private static final String LOG_TABLE = "Log";
	private static final String LOG_ID = BaseColumns._ID;

	private static final String LOG_TYPE_NAME = "log_type";
	private static final String LOG_DATA = "data";










	// Constructor
	private DbHelper(Context ctx) { //
		super(ctx, DB_NAME, null, DB_VERSION);
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        db = this.getWritableDatabase();
	}

    public static synchronized DbHelper getInstance(Context ctx){
        if (instance == null){
            instance = new DbHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    public synchronized void resetDatabase(){
        //Remove the data from all the tables
        List<String> tables = Arrays.asList(USER_PREFS_TABLE, CBCC_USER_TABLE, SEARCH_TABLE, QUIZATTEMPTS_TABLE,
                                            TRACKER_LOG_TABLE, ACTIVITY_TABLE, COURSE_TABLE);
        for (String tablename : tables){
            db.delete(tablename, null, null);
        }
    }

	@Override
	public void onCreate(SQLiteDatabase db) {

		createCourseTable(db);
		createActivityTable(db);
		createLogTable(db);
		createQuizAttemptsTable(db);
		createSearchTable(db);
		createCbccUserTable(db);
        createUserPrefsTable(db);
		createLearningReferencesTable(db);
		//Projects Table
		createProjectsTable(db);
		createUsersTable(db);
		//Sections Table
		createSectionsTable(db);
		//SubSections Table
		createSubSectionsTable(db);

		//Topic Table
		createTopicsTable(db);

		//Log table
		createCbccLogTable(db);
	}

    public void beginTransaction(){
        db.beginTransaction();
    }
    public void endTransaction(boolean success){
        if (success){
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

	public void createCourseTable(SQLiteDatabase db){
		String m_sql = "create table " + COURSE_TABLE + " (" + COURSE_C_ID + " integer primary key autoincrement, "
				+ COURSE_C_VERSIONID + " int, " + COURSE_C_TITLE + " text, " + COURSE_C_LOCATION + " text, "
				+ COURSE_C_SHORTNAME + " text," + COURSE_C_SCHEDULE + " int,"
				+ COURSE_C_IMAGE + " text,"
				+ COURSE_C_DESC + " text,"
				+ COURSE_C_ORDER_PRIORITY + " integer default 0, " 
				+ COURSE_C_LANGS + " text, "
                + COURSE_C_SEQUENCING + " text default '" + Course.SEQUENCING_MODE_NONE + "' )";
		db.execSQL(m_sql);
	}
	
	public void createActivityTable(SQLiteDatabase db){
		String a_sql = "create table " + ACTIVITY_TABLE + " (" +
									ACTIVITY_C_ID + " integer primary key autoincrement, " + 
									ACTIVITY_C_COURSEID + " int, " + 
									ACTIVITY_C_SECTIONID + " int, " + 
									ACTIVITY_C_ACTID + " int, " + 
									ACTIVITY_C_ACTTYPE + " text, " + 
									ACTIVITY_C_STARTDATE + " datetime null, " + 
									ACTIVITY_C_ENDDATE + " datetime null, " + 
									ACTIVITY_C_ACTIVITYDIGEST + " text, "+
									ACTIVITY_C_TITLE + " text)";
		db.execSQL(a_sql);
	}
	
	public void createLogTable(SQLiteDatabase db){
		String l_sql = "create table " + TRACKER_LOG_TABLE + " (" +
				TRACKER_LOG_C_ID + " integer primary key autoincrement, " + 
				TRACKER_LOG_C_COURSEID + " integer, " + 
				TRACKER_LOG_C_DATETIME + " datetime default current_timestamp, " + 
				TRACKER_LOG_C_ACTIVITYDIGEST + " text, " + 
				TRACKER_LOG_C_DATA + " text, " + 
				TRACKER_LOG_C_SUBMITTED + " integer default 0, " + 
				TRACKER_LOG_C_INPROGRESS + " integer default 0, " +
				TRACKER_LOG_C_COMPLETED + " integer default 0, " + 
				TRACKER_LOG_C_USERID + " integer default 0, " +
				TRACKER_LOG_C_TYPE + " text " +
				")";
		db.execSQL(l_sql);
	}

	public void createQuizAttemptsTable(SQLiteDatabase db){
		String sql = "create table " + QUIZATTEMPTS_TABLE + " (" +
							QUIZATTEMPTS_C_ID + " integer primary key autoincrement, " + 
							QUIZATTEMPTS_C_DATETIME + " datetime default current_timestamp, " + 
							QUIZATTEMPTS_C_DATA + " text, " +  
							QUIZATTEMPTS_C_ACTIVITY_DIGEST + " text, " + 
							QUIZATTEMPTS_C_SENT + " integer default 0, "+
							QUIZATTEMPTS_C_COURSEID + " integer, " +
							QUIZATTEMPTS_C_USERID + " integer default 0, " +
							QUIZATTEMPTS_C_SCORE + " real default 0, " +
							QUIZATTEMPTS_C_MAXSCORE + " real default 0, " +
							QUIZATTEMPTS_C_PASSED + " integer default 0)";
		db.execSQL(sql);
	}

	//////////////////////////////////////////////////////////
	
	public void createSearchTable(SQLiteDatabase db){
		String sql = "CREATE VIRTUAL TABLE "+SEARCH_TABLE+" USING FTS3 (" +
                SEARCH_C_TEXT + " text, " +
                SEARCH_C_COURSETITLE + " text, " +
                SEARCH_C_SECTIONTITLE + " text, " +
                SEARCH_C_ACTIVITYTITLE + " text " +
            ")";
		db.execSQL(sql);
	}
	
	public void createCbccUserTable(SQLiteDatabase db){
		String sql = "CREATE TABLE ["+CBCC_USER_TABLE+"] (" +
                "["+USER_C_ID+"]" + " integer primary key autoincrement, " +
                "["+USER_C_USERNAME +"]" + " TEXT, "+
                "["+USER_C_FIRSTNAME +"] TEXT, " +
                "["+USER_C_LASTNAME+"] TEXT, " +
                "["+USER_C_PASSWORDENCRYPTED +"] TEXT, " +
                "["+USER_C_APIKEY +"] TEXT, " +
                "["+USER_C_LAST_LOGIN_DATE +"] datetime null, " +
                "["+USER_C_NO_LOGINS +"] integer default 0,  " +
                "["+USER_C_POINTS +"] integer default 0,  " +
                "["+USER_C_BADGES +"] integer default 0 " +
            ");";
		db.execSQL(sql);
	}

    public void createUserPrefsTable(SQLiteDatabase db){
        String m_sql = "create table " + USER_PREFS_TABLE + " ("
                + USER_PREFS_C_USERNAME + " text not null, "
                + USER_PREFS_C_PREFKEY + " text not null, "
                + USER_PREFS_C_PREFVALUE + " text, "
                + "primary key (" + USER_PREFS_C_USERNAME + ", " + USER_PREFS_C_PREFKEY + ") "
                +  ")";
        db.execSQL(m_sql);
    }

	public void createUsersTable(SQLiteDatabase db){
		String m_sql = "create table if not exists " + USERS_TABLE + " ("
				+ BaseColumns._ID + " integer primary key autoincrement, "
				+ UID + TEXT_TYPE + COMMA_SEP
				+ USER_FIRST_NAME + TEXT_TYPE + COMMA_SEP
				+ USER_LAST_NAME + TEXT_TYPE + COMMA_SEP
				+ USER_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP
				+ USER_DOB + TEXT_TYPE + COMMA_SEP
				+ USER_GENDER + TEXT_TYPE + COMMA_SEP
				+ USER_NATIONAL_ID + TEXT_TYPE + COMMA_SEP
				+ USER_CLIENT_TYPE + TEXT_TYPE + COMMA_SEP
				+ USER_AFYA_CHANNEL + TEXT_TYPE + COMMA_SEP
				+ USER_AFYA_STARTWEEK + TEXT_TYPE + COMMA_SEP
				+ USER_FACILITY_ID + TEXT_TYPE + COMMA_SEP
				+ USER_IS_INSURED + TEXT_TYPE + COMMA_SEP
				+ USER_LANGUAGE + TEXT_TYPE + COMMA_SEP
				+ USER_LOCATION + TEXT_TYPE + COMMA_SEP
				+ USER_EDUCATION + TEXT_TYPE + COMMA_SEP
				+ USER_RELATIVE_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP
				+ IS_AFYA + TEXT_TYPE + COMMA_SEP
				+ TIMESTAMP + TEXT_TYPE + COMMA_SEP
				+ SYNC_STATUS + " integer)";
		db.execSQL(m_sql);
	}

	public void createProjectsTable(SQLiteDatabase db){
		String m_sql = "create table if not exists " + PROJECTS_TABLE + " ("
				+ BaseColumns._ID + " integer primary key autoincrement, "
				+ PROJECT_ID + INT_TYPE + COMMA_SEP
				+ PROJECT_OWNER_ID + INT_TYPE + COMMA_SEP
				+ PROJECT_NAME + TEXT_TYPE + COMMA_SEP
				+ PROJECT_DATE_CREATED + TEXT_TYPE + COMMA_SEP
				+ PROJECT_DATE_UPDATED + " text)";
		db.execSQL(m_sql);
	}

	public void createSectionsTable(SQLiteDatabase db){
		String m_sql = "create table if not exists " + SECTIONS_TABLE + " ("
				+ BaseColumns._ID + " integer primary key autoincrement, "
				+ SECTION_ID + INT_TYPE + COMMA_SEP
				+ SECTION_TEAM_ID + INT_TYPE + COMMA_SEP
				+ SECTION_MODIFIER + INT_TYPE + COMMA_SEP
				+ SECTION_NAME + TEXT_TYPE + COMMA_SEP
				+ SECTION_IMAGE_URL + TEXT_TYPE + COMMA_SEP
				+ SECTION_DATE_CREATED + TEXT_TYPE + COMMA_SEP
				+ SECTION_DATE_UPDATED + " text)";
		db.execSQL(m_sql);
	}

	public void createSubSectionsTable(SQLiteDatabase db){
		String m_sql = "create table if not exists " + SUB_SECTIONS_TABLE + " ("
				+ BaseColumns._ID + " integer primary key autoincrement, "
				+ SUB_SECTION_ID + INT_TYPE + COMMA_SEP
				+ SUB_SECTION_TEAM_ID + INT_TYPE + COMMA_SEP
				+ SUB_SECTION_SID + INT_TYPE + COMMA_SEP
				+ SUB_SECTION_MODIFIER + INT_TYPE + COMMA_SEP
				+ SUB_SECTION_NAME + TEXT_TYPE + COMMA_SEP
				+ SUB_SECTION_S_NAME + TEXT_TYPE + COMMA_SEP
				+ SUB_SECTION_IMAGE_URL + TEXT_TYPE + COMMA_SEP
				+ SUB_SECTION_DATE_CREATED + TEXT_TYPE + COMMA_SEP
				+ SUB_SECTION_DATE_UPDATED + " text)";
		db.execSQL(m_sql);
	}

	public void createTopicsTable(SQLiteDatabase db){
		String m_sql = "create table if not exists " + TOPICS_TABLE + " ("
				+ BaseColumns._ID + " integer primary key autoincrement, "
				+ TOPIC_ID + INT_TYPE + COMMA_SEP
				+ TOPIC_TEAM_ID + INT_TYPE + COMMA_SEP
				+ TOPIC_SECTION_ID + INT_TYPE + COMMA_SEP
				+ TOPIC_SUB_SECTION_ID + INT_TYPE + COMMA_SEP
				+ TOPIC_MODIFIER + INT_TYPE + COMMA_SEP
				+ TOPIC_NAME + TEXT_TYPE + COMMA_SEP
				+ TOPIC_SHORT_NAME + TEXT_TYPE + COMMA_SEP
				+ TOPIC_DESCRIPTION + TEXT_TYPE + COMMA_SEP
				+ TOPIC_UPLOAD_STATUS + TEXT_TYPE + COMMA_SEP
				+ TOPIC_SECTION_NAME + TEXT_TYPE + COMMA_SEP
				+ TOPIC_SUB_SECTION_NAME + TEXT_TYPE + COMMA_SEP
				+ TOPIC_FILE_URL + TEXT_TYPE + COMMA_SEP
				+ TOPIC_DATE_CREATED + TEXT_TYPE + COMMA_SEP
				+ TOPIC_DATE_UPDATED + " text)";
		db.execSQL(m_sql);
	}

	public void createLearningReferencesTable(SQLiteDatabase db){
		String m_sql = "create table if not exists " + LEARNING_REFERENCES_TABLE + " ("
				+ BaseColumns._ID + " integer primary key autoincrement, "
				+ CCH_REFERENCE_DESC + TEXT_TYPE + COMMA_SEP
				+ CCH_SHORTNAME + TEXT_TYPE + COMMA_SEP
				+ CCH_REFERENCE_URL + TEXT_TYPE + COMMA_SEP
				+ CCH_REFERENCE_SIZE + " text)";
		db.execSQL(m_sql);

	}

	public void createCbccLogTable(SQLiteDatabase db){
		String l_sql = "create table " + LOG_TABLE + " (" +
				LOG_ID + " integer primary key autoincrement, " +
				LOG_TYPE_NAME + " text, " +
				LOG_DATA + " text)";

		db.execSQL(l_sql);

	}



	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


		db.execSQL("drop table if exists " + COURSE_TABLE);
		db.execSQL("drop table if exists " + ACTIVITY_TABLE);
		db.execSQL("drop table if exists " + TRACKER_LOG_TABLE);
		db.execSQL("drop table if exists " + QUIZATTEMPTS_TABLE);
		db.execSQL("drop table if exists " + USER_PREFS_TABLE);
		db.execSQL("drop table if exists " + CBCC_USER_TABLE);



		db.execSQL("drop table if exists " + LEARNING_REFERENCES_TABLE);
		db.execSQL("drop table if exists " + PROJECTS_TABLE);
		db.execSQL("drop table if exists " + USERS_TABLE);
		db.execSQL("drop table if exists " + SECTIONS_TABLE);
		db.execSQL("drop table if exists " + SUB_SECTIONS_TABLE);
		db.execSQL("drop table if exists " + TOPICS_TABLE);
		db.execSQL("drop table if exists " + LOG_TABLE);



		onCreate(db);
	  }



	public boolean addUser(boolean isAfyaUser,  User user){

		Log.i(TAG, "About to add new User to Users table");
		Log.i(TAG, "Is "+ user.getFirstName() + " registering for Afya?  " + isAfyaUser);


		try {

			beginTransaction();

 			ContentValues contentValues = new ContentValues();

			contentValues.put(UID, String.valueOf(System.currentTimeMillis()));
			contentValues.put(USER_FIRST_NAME, user.getFirstName());
			contentValues.put(USER_LAST_NAME, user.getLastName());
			contentValues.put(USER_PHONE_NUMBER, user.getPhoneNumber());
			contentValues.put(USER_DOB, user.getDOB());
			contentValues.put(USER_GENDER, user.getGender());
			contentValues.put(USER_NATIONAL_ID, user.getNationalId());
			contentValues.put(USER_CLIENT_TYPE, user.getClientType());

			contentValues.put(USER_FACILITY_ID, user.getFacilityId());
			contentValues.put(USER_IS_INSURED, user.getIsInsured());
			contentValues.put(USER_PHONE_NUMBER, user.getPhoneNumber());
			contentValues.put(USER_LANGUAGE, user.getLanguage());
			contentValues.put(USER_LOCATION, user.getLocation());
			contentValues.put(USER_EDUCATION, user.getEducation());
			contentValues.put(TIMESTAMP,   new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()));
			contentValues.put(IS_AFYA, String.valueOf(isAfyaUser));

			if(isAfyaUser) {
				contentValues.put(USER_RELATIVE_PHONE_NUMBER, user.getRelativePhoneNumber());
				contentValues.put(USER_AFYA_CHANNEL, user.getAfyaChannel());
				contentValues.put(USER_AFYA_STARTWEEK, user.getAfyaStartWeek());
			}



			db.insert(USERS_TABLE, null, contentValues);

			Log.i(TAG, "User with name " + user.getFirstName() + " has been added!");

			endTransaction(true);
			return true;



		}catch(Exception e){
			e.printStackTrace();

			Log.i(TAG, "User with name " + user.getFirstName() + " was not added!");

			return false;
		}

	}




	public List<User> getAllRegisteredUsers() {
		List<User> users = new ArrayList<User>();

		String query = "SELECT * FROM " + USERS_TABLE;

		Log.i("QUERY", query);
 		Cursor cursor = db.rawQuery(query, null);

		try {

			if (cursor.moveToFirst() && cursor.getCount() > 0) {

				do {
					User cbccUser = new User(
							cursor.getString(cursor.getColumnIndex(USER_FIRST_NAME)),
							cursor.getString(cursor.getColumnIndex(USER_LAST_NAME)),
							cursor.getString(cursor.getColumnIndex(USER_PHONE_NUMBER)),
							cursor.getString(cursor.getColumnIndex(USER_DOB)),
							cursor.getString(cursor.getColumnIndex(USER_GENDER)),
							cursor.getString(cursor.getColumnIndex(USER_NATIONAL_ID)),
							cursor.getString(cursor.getColumnIndex(USER_CLIENT_TYPE)),
							cursor.getString(cursor.getColumnIndex(USER_AFYA_CHANNEL)),
							cursor.getString(cursor.getColumnIndex(USER_AFYA_STARTWEEK)),
							cursor.getString(cursor.getColumnIndex(USER_FACILITY_ID)),
							cursor.getString(cursor.getColumnIndex(USER_IS_INSURED)),
							cursor.getString(cursor.getColumnIndex(USER_LANGUAGE)),
							cursor.getString(cursor.getColumnIndex(USER_LOCATION)),
							cursor.getString(cursor.getColumnIndex(USER_EDUCATION)),
							cursor.getString(cursor.getColumnIndex(USER_RELATIVE_PHONE_NUMBER)),
							cursor.getInt(cursor.getColumnIndex(SYNC_STATUS))
					);


					users.add(cbccUser);

					Log.i("User list  ", String.valueOf(users.size()) + "\t" +
							cursor.getString(cursor.getColumnIndex(USER_FIRST_NAME)));

				} while (cursor.moveToNext());

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}finally {
			cursor.close();
		}

		return users;

	}



	public List<User> getAllUnsyncedUsers() {
		List<User> users = new ArrayList<User>();

		String query = "SELECT * FROM " + USERS_TABLE + " WHERE " +
				SYNC_STATUS + " = '" + 0 + "'";

		Log.i("QUERY", query);
 		Cursor cursor = db.rawQuery(query, null);

		try {

			if (cursor.moveToFirst() && cursor.getCount() > 0) {

				do {
					 User user = new User(
							cursor.getString(cursor.getColumnIndex(USER_FIRST_NAME)),
							cursor.getString(cursor.getColumnIndex(USER_LAST_NAME)),
							cursor.getString(cursor.getColumnIndex(USER_PHONE_NUMBER)),
							cursor.getString(cursor.getColumnIndex(USER_DOB)),
							cursor.getString(cursor.getColumnIndex(USER_GENDER)),
							cursor.getString(cursor.getColumnIndex(USER_NATIONAL_ID)),
							cursor.getString(cursor.getColumnIndex(USER_CLIENT_TYPE)),
							cursor.getString(cursor.getColumnIndex(USER_AFYA_CHANNEL)),
							cursor.getString(cursor.getColumnIndex(USER_AFYA_STARTWEEK)),
							cursor.getString(cursor.getColumnIndex(USER_FACILITY_ID)),
							cursor.getString(cursor.getColumnIndex(USER_IS_INSURED)),
							cursor.getString(cursor.getColumnIndex(USER_LANGUAGE)),
							cursor.getString(cursor.getColumnIndex(USER_LOCATION)),
							cursor.getString(cursor.getColumnIndex(USER_EDUCATION)),
							cursor.getString(cursor.getColumnIndex(USER_RELATIVE_PHONE_NUMBER)),
							cursor.getInt(cursor.getColumnIndex(SYNC_STATUS))
					);


					users.add(user);

					Log.i("User list  ", String.valueOf(users.size()) + "\t" +
							cursor.getString(cursor.getColumnIndex(USER_FIRST_NAME)));

				} while (cursor.moveToNext());

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}finally {
			cursor.close();
		}

		return users;

	}


	public int markUserAsSynced(String uid){

 		ContentValues values = new ContentValues();
		values.put(SYNC_STATUS, 1);

		return db.update(USERS_TABLE, values, UID + "=" + uid, null);

	}

	public int deleteUser(String uid){
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(USERS_TABLE, UID + " = ? ", new String[]{uid});

	}


	private Boolean doesUserExist(String uid) {

		try {

 			return DatabaseUtils.queryNumEntries(db, USERS_TABLE,
					UID + " = ? ",
					new String[]{uid}) > 0;
		} catch (Exception e) {
			e.printStackTrace();

			Log.i(TAG, "No! user does not exists!");
			return null;
		}
	}

	private Boolean hasUserSynced(String uid) {

 		Cursor cursor = null;

		String selectQuery = "SELECT  * FROM " + USERS_TABLE + " WHERE " +
				UID + " ='" + uid + "'";
		Log.i("QUERY", selectQuery);

		try {
 			cursor = db.rawQuery(selectQuery, null);
			return cursor.getInt(cursor.getColumnIndex(SYNC_STATUS)) == 1;

		}catch(Exception e){
			e.printStackTrace();
			return null;

		}finally{
			cursor.close();
			db.close();

		}
	}






	public boolean addSection(POCSection section){
		try {

  			ContentValues contentValues = new ContentValues();
			contentValues.put(SECTION_ID, section.getSectionId());
			contentValues.put(SECTION_TEAM_ID, section.getTeamId());
			contentValues.put(SECTION_MODIFIER, section.getModdedBy());
			contentValues.put(SECTION_NAME, section.getSectionName());
			contentValues.put(SECTION_IMAGE_URL, section.getImageUrl());
			contentValues.put(SECTION_DATE_CREATED, section.getDateCreated());
			contentValues.put(SECTION_DATE_UPDATED, section.getDateUpdated());
			db.insert(SECTIONS_TABLE, null, contentValues);

			Log.i("Section\t", section.getSectionName() + "\tinserted");


		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean addSubSection(SubSection sub_section){

		try{

 			ContentValues contentValues = new ContentValues();
			contentValues.put(SUB_SECTION_ID, sub_section.getId());
			contentValues.put(SUB_SECTION_TEAM_ID, sub_section.getTeamId());
			contentValues.put(SUB_SECTION_SID, sub_section.getSectionId());
			contentValues.put(SUB_SECTION_MODIFIER, sub_section.getModdedBy());
			contentValues.put(SUB_SECTION_NAME, sub_section.getName());
			contentValues.put(SUB_SECTION_S_NAME, sub_section.getSectionName());
			contentValues.put(SUB_SECTION_IMAGE_URL, sub_section.getImageUrl());
			contentValues.put(SUB_SECTION_DATE_CREATED, sub_section.getDateCreated());
			contentValues.put(SUB_SECTION_DATE_UPDATED, sub_section.getDateUpdated());
			db.insert(SUB_SECTIONS_TABLE, null, contentValues);

			Log.i("Section\t", sub_section.getName() + "\tinserted");


 		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;

	}


	public boolean addTopic(Topic topic){


		try{

 			ContentValues contentValues = new ContentValues();
			contentValues.put(TOPIC_ID, topic.getTopicId());
			contentValues.put(TOPIC_TEAM_ID, topic.getTeamId());
			contentValues.put(TOPIC_SUB_SECTION_ID, topic.getSubSectionId());
			contentValues.put(TOPIC_MODIFIER, topic.getModdedBy());
			contentValues.put(TOPIC_NAME, topic.getTopicName());
			contentValues.put(TOPIC_SHORT_NAME, topic.getShortName());
			contentValues.put(TOPIC_DESCRIPTION, topic.getDescription());
			contentValues.put(TOPIC_UPLOAD_STATUS, topic.getUploadStatus());
			contentValues.put(TOPIC_SECTION_NAME, topic.getSectionName());
			contentValues.put(TOPIC_FILE_URL, topic.getFileUrl());
			contentValues.put(TOPIC_DATE_CREATED, topic.getDateCreated());
			contentValues.put(TOPIC_DATE_UPDATED, topic.getDateUpdated());
			contentValues.put(TOPIC_SUB_SECTION_NAME, topic.getSubSectionName());
			db.insert(TOPICS_TABLE, null, contentValues);

			Log.i("Section\t", topic.getTopicName() + "\tinserted");


 		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;

	}



	public List<POCSection> getAllSections() {
		List<POCSection> sections = new ArrayList<POCSection>();

		String query = "SELECT * FROM " + SECTIONS_TABLE;

		Log.i("QUERY", query);

		//beginTransaction();
 		Cursor cursor = db.rawQuery(query, null);

		try {

			if (cursor.moveToFirst() && cursor.getCount() > 0) {

				do {
					POCSection section = new POCSection(
							cursor.getInt(cursor.getColumnIndex(SECTION_ID)),
							cursor.getInt(cursor.getColumnIndex(SECTION_TEAM_ID)),
							cursor.getInt(cursor.getColumnIndex(SECTION_MODIFIER)),
							cursor.getString(cursor.getColumnIndex(SECTION_NAME)),
							cursor.getString(cursor.getColumnIndex(SECTION_IMAGE_URL)),
							cursor.getString(cursor.getColumnIndex(SECTION_DATE_CREATED)),
							cursor.getString(cursor.getColumnIndex(SECTION_DATE_UPDATED))
					);

					sections.add(section);

					Log.i("Sections List\t", String.valueOf(sections.size()) + "\t" +
							cursor.getString(cursor.getColumnIndex(SECTION_NAME)));

				} while (cursor.moveToNext());

				//endTransaction(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cursor.close();

		return sections;

	}




	public List<SubSection> getSubSections(Integer section_id){

		ArrayList<SubSection> subSections = new ArrayList<SubSection>();
		String query = "SELECT * FROM " + SUB_SECTIONS_TABLE + " WHERE " +
				SUB_SECTION_SID + " = '" + section_id + "'";

		Log.i("QUERY", query);
 		Cursor cursor = db.rawQuery(query, null);

		try {

			if (cursor.moveToFirst() && cursor.getCount() > 0) {

				do {
					SubSection sub_section = new SubSection(
							cursor.getInt(cursor.getColumnIndex(SUB_SECTION_ID)),
							cursor.getInt(cursor.getColumnIndex(SUB_SECTION_TEAM_ID)),
							cursor.getInt(cursor.getColumnIndex(SUB_SECTION_SID)),
							cursor.getInt(cursor.getColumnIndex(SUB_SECTION_MODIFIER)),
							cursor.getString(cursor.getColumnIndex(SUB_SECTION_NAME)),
							cursor.getString(cursor.getColumnIndex(SUB_SECTION_IMAGE_URL)),
							cursor.getString(cursor.getColumnIndex(SUB_SECTION_DATE_CREATED)),
							cursor.getString(cursor.getColumnIndex(SUB_SECTION_DATE_UPDATED)),
							cursor.getString(cursor.getColumnIndex(SUB_SECTION_S_NAME))
					);

					subSections.add(sub_section);

					Log.i("Sub Sections List\t", String.valueOf(subSections.size()) + "\t" +
							cursor.getString(cursor.getColumnIndex(SUB_SECTION_NAME)));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cursor.close();
		return subSections;

	}


	public List<Topic> getTopics(Integer sub_section_id){

		ArrayList<Topic> topics = new ArrayList<>();

		String query = "SELECT * FROM " + TOPICS_TABLE + " WHERE " + TOPIC_SUB_SECTION_ID + " = '"
				+ sub_section_id + "'";
		Log.i("QUERY", query);

 		Cursor cursor = db.rawQuery(query, null);


		try {

			if (cursor.moveToFirst() && cursor.getCount() > 0) {

				do {
					Topic topic = new Topic(
							cursor.getInt(cursor.getColumnIndex(TOPIC_ID)),
							cursor.getInt(cursor.getColumnIndex(TOPIC_TEAM_ID)),
							cursor.getInt(cursor.getColumnIndex(TOPIC_SECTION_ID)),
							cursor.getInt(cursor.getColumnIndex(TOPIC_SUB_SECTION_ID)),
							cursor.getInt(cursor.getColumnIndex(TOPIC_MODIFIER)),
							cursor.getString(cursor.getColumnIndex(TOPIC_NAME)),
							cursor.getString(cursor.getColumnIndex(TOPIC_SHORT_NAME)),
							cursor.getString(cursor.getColumnIndex(TOPIC_DESCRIPTION)),
							cursor.getString(cursor.getColumnIndex(TOPIC_UPLOAD_STATUS)),
							cursor.getString(cursor.getColumnIndex(TOPIC_FILE_URL)),
							cursor.getString(cursor.getColumnIndex(TOPIC_DATE_CREATED)),
							cursor.getString(cursor.getColumnIndex(TOPIC_DATE_UPDATED)),
							cursor.getString(cursor.getColumnIndex(TOPIC_SECTION_NAME)),
							cursor.getString(cursor.getColumnIndex(TOPIC_SUB_SECTION_NAME))
					);

					topics.add(topic);

					Log.i("Sub Sec Topic List\t", String.valueOf(topics.size()) + "\t" +
							cursor.getString(cursor.getColumnIndex(TOPIC_NAME)));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cursor.close();


		return topics;


	}


	public void deleteAllPOCTableRows(){


		try {
			if (doesTableExists(SECTIONS_TABLE))
				db.execSQL("DELETE FROM " + SECTIONS_TABLE); //delete all rows in a table

			if (doesTableExists(SUB_SECTIONS_TABLE))
				db.execSQL("DELETE FROM " + SUB_SECTIONS_TABLE);

			if (doesTableExists(TOPICS_TABLE))
				db.execSQL("DELETE FROM " + TOPICS_TABLE);
		}catch (Exception e){
			e.printStackTrace();

		}
		//db.close();


	}



	public boolean doesTableExists(String tableName) {
		try {
 			Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
			if(cursor!=null) {
				if(cursor.getCount()>0) {
					cursor.close();
					return true;
				}
				cursor.close();
			}
			return false;
		} catch (SQLiteException e) {
			Log.v(TAG,e.getMessage());
		}

		return false;
	}


	public boolean addLog(String log_type, String data){

		beginTransaction();
		try {
 			ContentValues cv = new ContentValues();
			cv.put(LOG_TYPE_NAME, log_type);
			cv.put(LOG_DATA, data);

			Log.i(TAG, "Log type =\t" + log_type + "\tdata =\t" + data );
			db.insert(LOG_TABLE, null, cv);


			endTransaction(true);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}



	public boolean deleteAllLogs(){

		try {

			if (doesTableExists(LOG_TABLE)) {
				db.execSQL("DELETE FROM " + LOG_TABLE);
				return true;
			}//delete all rows in a table

		}catch (Exception e){
			e.printStackTrace();
			return false;

		}
		db.close();

		return true;
	}


	public String getAllLogs(){
		JSONObject logJSONObject = new JSONObject();
		JSONArray logArray = new JSONArray();

		String query = "SELECT * FROM " + LOG_TABLE;

		Log.i("QUERY", query);
 		Cursor cursor = db.rawQuery(query, null);

		try {

			if (cursor.moveToFirst() && cursor.getCount() > 0) {

				do {
					int ID  =  cursor.getInt(cursor.getColumnIndex(LOG_ID));
					String log_type = cursor.getString(cursor.getColumnIndex(LOG_TYPE_NAME));
					String data =  cursor.getString(cursor.getColumnIndex(LOG_DATA));


					int index = ID - 1;




					JSONObject dataJSONObj = new JSONObject();

					dataJSONObj.put("log_type", log_type);

					dataJSONObj.put("data", data);

					logArray.put(index, dataJSONObj);


					Log.i(TAG, "JSON\t" + dataJSONObj.toString());



				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cursor.close();

		try {
			logJSONObject.put("log", logArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}


		Log.i(TAG, "JSONArray\t" + logJSONObject);

		return String.valueOf(logJSONObject);

	}






















	public void updateV43(long userId){
		// update existing trackers
		ContentValues values = new ContentValues();
		values.put(TRACKER_LOG_C_USERID, userId);
		
		db.update(TRACKER_LOG_TABLE, values, "1=1", null);
		
		// update existing trackers
		ContentValues values2 = new ContentValues();
		values2.put(QUIZATTEMPTS_C_USERID, userId);
		
		db.update(QUIZATTEMPTS_TABLE, values2, "1=1", null);
	}
	
	
	// returns id of the row
	public long addOrUpdateCourse(Course course) {

		ContentValues values = new ContentValues();
		values.put(COURSE_C_VERSIONID, course.getVersionId());
		values.put(COURSE_C_TITLE, course.getTitleJSONString());
		values.put(COURSE_C_SHORTNAME, course.getShortname());
		values.put(COURSE_C_LANGS, course.getLangsJSONString());
		values.put(COURSE_C_IMAGE, course.getImageFile());
		values.put(COURSE_C_DESC, course.getDescriptionJSONString());
		values.put(COURSE_C_ORDER_PRIORITY, course.getPriority());
        values.put(COURSE_C_SEQUENCING, course.getSequencingMode());
		
		if (!this.isInstalled(course.getShortname())) {
			Log.v(TAG, "Record added");
			return db.insertOrThrow(COURSE_TABLE, null, values);
		} else if(this.toUpdate(course.getShortname(), course.getVersionId())){
			long toUpdate = this.getCourseID(course.getShortname());
			
			// remove existing course info from search index
			this.searchIndexRemoveCourse(toUpdate);
			
			if (toUpdate != 0) {
				db.update(COURSE_TABLE, values, COURSE_C_ID + "=" + toUpdate, null);
				// remove all the old activities
				String s = ACTIVITY_C_COURSEID + "=?";
				String[] args = new String[] { String.valueOf(toUpdate) };
				db.delete(ACTIVITY_TABLE, s, args);
				return toUpdate;
			}
		} 
		return -1;
	}

	// returns id of the row
	public long addOrUpdateUser(CbccUser cbccUser) {
		
		if (cbccUser.getUsername().equals("") || cbccUser.getUsername() == null){
			return 0;
		}
		
		ContentValues values = new ContentValues();
		values.put(USER_C_USERNAME, cbccUser.getUsername());
		values.put(USER_C_FIRSTNAME, cbccUser.getFirstname());
		values.put(USER_C_LASTNAME, cbccUser.getLastname());
		values.put(USER_C_PASSWORDENCRYPTED, cbccUser.getPasswordEncrypted());
		values.put(USER_C_APIKEY, cbccUser.getApiKey());
		values.put(USER_C_POINTS, cbccUser.getPoints());
		values.put(USER_C_BADGES, cbccUser.getBadges());
		
		long userId = this.isUser(cbccUser.getUsername());
		if (userId == -1) {
			Log.v(TAG, "Record added");
			return db.insertOrThrow(CBCC_USER_TABLE, null, values);
		} else {
			String s = USER_C_ID + "=?";
			String[] args = new String[] { String.valueOf(userId) };
			db.update(CBCC_USER_TABLE, values, s, args);
			return userId;
		} 
	}
	
	public long isUser(String username){
		String s = USER_C_USERNAME + "=?";
		String[] args = new String[] { username };
		Cursor c = db.query(CBCC_USER_TABLE, null, s, args, null, null, null);
		if(c.getCount() == 0){
			c.close();
			return -1;
		} else {
			c.moveToFirst();
			int userId = c.getInt(c.getColumnIndex(USER_C_ID));
			c.close();
			return userId;
		}
	}
	
	
	public int getCourseID(String shortname){
		String s = COURSE_C_SHORTNAME + "=?";
		String[] args = new String[] { shortname };
		Cursor c = db.query(COURSE_TABLE, null, s, args, null, null, null);
		if(c.getCount() == 0){
			c.close();
			return 0;
		} else {
			c.moveToFirst();
			int courseId = c.getInt(c.getColumnIndex(COURSE_C_ID));
			c.close();
			return courseId;
		}
	}
	
	public void updateScheduleVersion(long courseId, long scheduleVersion){
		ContentValues values = new ContentValues();
		values.put(COURSE_C_SCHEDULE, scheduleVersion);
		db.update(COURSE_TABLE, values, COURSE_C_ID + "=" + courseId, null);
	}
	
	public void insertActivities(ArrayList<Activity> acts) {

        beginTransaction();
		for (Activity a : acts) {
			ContentValues values = new ContentValues();
			values.put(ACTIVITY_C_COURSEID, a.getCourseId());
			values.put(ACTIVITY_C_SECTIONID, a.getSectionId());
			values.put(ACTIVITY_C_ACTID, a.getActId());
			values.put(ACTIVITY_C_ACTTYPE, a.getActType());
			values.put(ACTIVITY_C_ACTIVITYDIGEST, a.getDigest());
			values.put(ACTIVITY_C_TITLE, a.getTitleJSONString());
			db.insertOrThrow(ACTIVITY_TABLE, null, values);
		}
        endTransaction(true);
	}

	public void  insertSchedule(ArrayList<ActivitySchedule> actsched) {

        beginTransaction();
		for (ActivitySchedule as : actsched) {
			ContentValues values = new ContentValues();
			values.put(ACTIVITY_C_STARTDATE, as.getStartTimeString());
			values.put(ACTIVITY_C_ENDDATE, as.getEndTimeString());
			db.update(ACTIVITY_TABLE, values, ACTIVITY_C_ACTIVITYDIGEST + "='" + as.getDigest() + "'", null);
		}
        endTransaction(true);
	}
	
	public void insertTrackers(ArrayList<TrackerLog> trackers) {
        beginTransaction();
		for (TrackerLog t : trackers) {
			ContentValues values = new ContentValues();
			values.put(TRACKER_LOG_C_DATETIME, t.getDateTimeString());
			values.put(TRACKER_LOG_C_ACTIVITYDIGEST, t.getDigest());
			values.put(TRACKER_LOG_C_SUBMITTED, t.isSubmitted());
			values.put(TRACKER_LOG_C_COURSEID, t.getCourseId());
			values.put(TRACKER_LOG_C_COMPLETED, t.isCompleted());
			values.put(TRACKER_LOG_C_USERID, t.getUserId());
			db.insertOrThrow(TRACKER_LOG_TABLE, null, values);
		}
        endTransaction(true);
	}
	
	public void resetSchedule(int courseId){
		ContentValues values = new ContentValues();
		values.put(ACTIVITY_C_STARTDATE,"");
		values.put(ACTIVITY_C_ENDDATE, "");
		db.update(ACTIVITY_TABLE, values, ACTIVITY_C_COURSEID + "=" + courseId, null);
	}
	
	public ArrayList<Course> getAllCourses() {
		ArrayList<Course> courses = new ArrayList<Course>();
		String order = COURSE_C_ORDER_PRIORITY + " DESC, " + COURSE_C_TITLE + " ASC";
		Cursor c = db.query(COURSE_TABLE, null, null, null, null, null, order);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			Course course = new Course(prefs.getString(PrefsActivity.PREF_STORAGE_LOCATION, ""));
			course.setCourseId(c.getInt(c.getColumnIndex(COURSE_C_ID)));
			course.setVersionId(c.getDouble(c.getColumnIndex(COURSE_C_VERSIONID)));
			course.setTitlesFromJSONString(c.getString(c.getColumnIndex(COURSE_C_TITLE)));
			course.setImageFile(c.getString(c.getColumnIndex(COURSE_C_IMAGE)));
			course.setLangsFromJSONString(c.getString(c.getColumnIndex(COURSE_C_LANGS)));
			course.setShortname(c.getString(c.getColumnIndex(COURSE_C_SHORTNAME)));
			course.setPriority(c.getInt(c.getColumnIndex(COURSE_C_ORDER_PRIORITY)));
            course.setSequencingMode(c.getString(c.getColumnIndex(COURSE_C_SEQUENCING)));
			courses.add(course);
			c.moveToNext();
		}
		c.close();
		return courses;
	}
	
	public ArrayList<QuizAttempt> getAllQuizAttempts() {
		ArrayList<QuizAttempt> quizAttempts = new ArrayList<QuizAttempt>();
		Cursor c = db.query(QUIZATTEMPTS_TABLE, null, null, null, null, null, null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			QuizAttempt qa = new QuizAttempt();
			qa.setId(c.getInt(c.getColumnIndex(QUIZATTEMPTS_C_ID)));
			qa.setActivityDigest(c.getString(c.getColumnIndex(QUIZATTEMPTS_C_ACTIVITY_DIGEST)));
			qa.setData(c.getString(c.getColumnIndex(QUIZATTEMPTS_C_DATA)));
			qa.setSent(Boolean.parseBoolean(c.getString(c.getColumnIndex(QUIZATTEMPTS_C_SENT))));
			qa.setCourseId(c.getLong(c.getColumnIndex(QUIZATTEMPTS_C_COURSEID)));
			qa.setUserId(c.getLong(c.getColumnIndex(QUIZATTEMPTS_C_USERID)));
			qa.setScore(c.getFloat(c.getColumnIndex(QUIZATTEMPTS_C_SCORE)));
			qa.setMaxscore(c.getFloat(c.getColumnIndex(QUIZATTEMPTS_C_MAXSCORE)));
			qa.setPassed(Boolean.parseBoolean(c.getString(c.getColumnIndex(QUIZATTEMPTS_C_PASSED))));
			quizAttempts.add(qa);
			c.moveToNext();
		}
		c.close();
		return quizAttempts;
	}
	
	public ArrayList<Course> getCourses(long userId) {
		ArrayList<Course> courses = new ArrayList<Course>();
		String order = COURSE_C_ORDER_PRIORITY + " DESC, " + COURSE_C_TITLE + " ASC";
		Cursor c = db.query(COURSE_TABLE, null, null, null, null, null, order);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			
			Course course = new Course(prefs.getString(PrefsActivity.PREF_STORAGE_LOCATION, ""));
			course.setCourseId(c.getInt(c.getColumnIndex(COURSE_C_ID)));
			course.setVersionId(c.getDouble(c.getColumnIndex(COURSE_C_VERSIONID)));
			course.setTitlesFromJSONString(c.getString(c.getColumnIndex(COURSE_C_TITLE)));
			course.setImageFile(c.getString(c.getColumnIndex(COURSE_C_IMAGE)));
			course.setLangsFromJSONString(c.getString(c.getColumnIndex(COURSE_C_LANGS)));
			course.setShortname(c.getString(c.getColumnIndex(COURSE_C_SHORTNAME)));
			course.setPriority(c.getInt(c.getColumnIndex(COURSE_C_ORDER_PRIORITY)));
			course.setDescriptionsFromJSONString(c.getString(c.getColumnIndex(COURSE_C_DESC)));
            course.setSequencingMode(c.getString(c.getColumnIndex(COURSE_C_SEQUENCING)));
            course = this.courseSetProgress(course, userId);
			courses.add(course);
			c.moveToNext();
		}
		c.close();
		return courses;
	}
	
	public Course getCourse(long courseId, long userId) {
		Course course = null;
		String s = COURSE_C_ID + "=?";
		String[] args = new String[] { String.valueOf(courseId) };
		Cursor c = db.query(COURSE_TABLE, null, s, args, null, null, null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			course = new Course(prefs.getString(PrefsActivity.PREF_STORAGE_LOCATION, ""));
			course.setCourseId(c.getInt(c.getColumnIndex(COURSE_C_ID)));
            course.setVersionId(c.getDouble(c.getColumnIndex(COURSE_C_VERSIONID)));
            course.setTitlesFromJSONString(c.getString(c.getColumnIndex(COURSE_C_TITLE)));
			course.setImageFile(c.getString(c.getColumnIndex(COURSE_C_IMAGE)));
            course.setLangsFromJSONString(c.getString(c.getColumnIndex(COURSE_C_LANGS)));
            course.setShortname(c.getString(c.getColumnIndex(COURSE_C_SHORTNAME)));
            course.setPriority(c.getInt(c.getColumnIndex(COURSE_C_ORDER_PRIORITY)));
            course.setDescriptionsFromJSONString(c.getString(c.getColumnIndex(COURSE_C_DESC)));
            course.setSequencingMode(c.getString(c.getColumnIndex(COURSE_C_SEQUENCING)));
			course = this.courseSetProgress(course, userId);
			c.moveToNext();
		}
		c.close();
		return course;
	}
	
	private Course courseSetProgress(Course course, long userId){
		// get no activities
		String s = ACTIVITY_C_COURSEID + "=?";
		String[] args = new String[] { String.valueOf(course.getCourseId()) };
		Cursor c = db.query(ACTIVITY_TABLE, null, s, args, null, null, null);
		course.setNoActivities(c.getCount());
		c.close();
		
		// get no completed
		String sqlCompleted = "SELECT DISTINCT " + TRACKER_LOG_C_ACTIVITYDIGEST + " FROM " + TRACKER_LOG_TABLE +
						" WHERE " + TRACKER_LOG_C_COURSEID + "=" + course.getCourseId() + 
						" AND " + TRACKER_LOG_C_USERID + "=" + userId +
						" AND " + TRACKER_LOG_C_COMPLETED + "=1" +
						" AND " + TRACKER_LOG_C_ACTIVITYDIGEST + " IN ( SELECT " + ACTIVITY_C_ACTIVITYDIGEST + " FROM " + ACTIVITY_TABLE + " WHERE " + ACTIVITY_C_COURSEID + "=" + course.getCourseId() + ")";
		c = db.rawQuery(sqlCompleted,null);
		course.setNoActivitiesCompleted(c.getCount());
		c.close();
		
		// get no started
		String sqlStarted = "SELECT DISTINCT " + TRACKER_LOG_C_ACTIVITYDIGEST + " FROM " + TRACKER_LOG_TABLE +
				" WHERE " + TRACKER_LOG_C_COURSEID + "=" + course.getCourseId() + 
				" AND " + TRACKER_LOG_C_USERID + "=" + userId +
				" AND " + TRACKER_LOG_C_COMPLETED + "=0" +
				" AND " + TRACKER_LOG_C_ACTIVITYDIGEST + " NOT IN (" + sqlCompleted + ")" +
				" AND " + TRACKER_LOG_C_ACTIVITYDIGEST + " IN ( SELECT " + ACTIVITY_C_ACTIVITYDIGEST + " FROM " + ACTIVITY_TABLE + " WHERE " + ACTIVITY_C_COURSEID + "=" + course.getCourseId() + ")";
		c = db.rawQuery(sqlStarted,null);
		course.setNoActivitiesStarted(c.getCount());
		c.close();
		
		return course;
	}
	
	public ArrayList<Activity> getCourseActivities(long courseId){
		ArrayList<Activity> activities = new ArrayList<Activity>();
		String s = ACTIVITY_C_COURSEID + "=?";
		String[] args = new String[] { String.valueOf(courseId) };
		Cursor c = db.query(ACTIVITY_TABLE, null, s, args, null, null, null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			Activity activity = new Activity();
			activity.setDbId(c.getInt(c.getColumnIndex(ACTIVITY_C_ID)));
			activity.setDigest(c.getString(c.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)));
			activity.setTitlesFromJSONString(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)));			
			activities.add(activity);
			c.moveToNext();
		}
		c.close();
		return activities;
	}
	
	public ArrayList<Activity> getCourseQuizzes(long courseId){
		ArrayList<Activity> quizzes = new ArrayList<Activity>();
		String s = ACTIVITY_C_COURSEID + "=? AND " + ACTIVITY_C_ACTTYPE +"=? AND " + ACTIVITY_C_SECTIONID+">0";
		String[] args = new String[] { String.valueOf(courseId), "quiz" };
		Cursor c = db.query(ACTIVITY_TABLE, null, s, args, null, null, null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			Activity quiz = new Activity();
			quiz.setDbId(c.getInt(c.getColumnIndex(ACTIVITY_C_ID)));
			quiz.setDigest(c.getString(c.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)));
			quiz.setTitlesFromJSONString(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)));			
			quizzes.add(quiz);
			c.moveToNext();
		}
		c.close();
		return quizzes;
	}
	
	public QuizStats getQuizAttempt(String digest, long userId){
		QuizStats qs = new QuizStats();
		qs.setDigest(digest);
		qs.setAttempted(false);
		qs.setPassed(false);
		
		// find if attempted
		String s1 = QUIZATTEMPTS_C_USERID + "=? AND " + QUIZATTEMPTS_C_ACTIVITY_DIGEST +"=?";
		String[] args1 = new String[] { String.valueOf(userId), digest };
		Cursor c1 = db.query(QUIZATTEMPTS_TABLE, null, s1, args1, null, null, null);
        qs.setNumAttempts(c1.getCount());
		if (c1.getCount() == 0){ return qs; }
		c1.moveToFirst();
		while (c1.isAfterLast() == false) {
			float userScore = c1.getFloat(c1.getColumnIndex(QUIZATTEMPTS_C_SCORE));
			if (userScore > qs.getUserScore()){
				qs.setUserScore(userScore);
			}
			if (c1.getInt(c1.getColumnIndex(QUIZATTEMPTS_C_PASSED)) != 0){
				qs.setPassed(true);
			}
			qs.setMaxScore(c1.getFloat(c1.getColumnIndex(QUIZATTEMPTS_C_MAXSCORE)));
			c1.moveToNext();
		}
		c1.close();
		qs.setAttempted(true);
		
		return qs;
	}
	public void insertTracker(int courseId, String digest, String data, String type, boolean completed){
		//get current user id
		long userId = this.getUserId(prefs.getString(PrefsActivity.PREF_USER_NAME, ""));
		
		ContentValues values = new ContentValues();
		values.put(TRACKER_LOG_C_COURSEID, courseId);
		values.put(TRACKER_LOG_C_ACTIVITYDIGEST, digest);
		values.put(TRACKER_LOG_C_DATA, data);
		values.put(TRACKER_LOG_C_COMPLETED, completed);
		values.put(TRACKER_LOG_C_USERID, userId);
		values.put(TRACKER_LOG_C_TYPE, type);
		db.insertOrThrow(TRACKER_LOG_TABLE, null, values);
	}
	
	public void resetCourse(long courseId, long userId){
		// delete quiz results
		this.deleteQuizAttempts(courseId, userId);
		this.deleteTrackers(courseId, userId);
	}
	
	public void deleteCourse(int courseId){
		// remove from search index
		this.searchIndexRemoveCourse(courseId);
		
		// delete activities
		String s = ACTIVITY_C_COURSEID + "=?";
		String[] args = new String[] { String.valueOf(courseId) };
		db.delete(ACTIVITY_TABLE, s, args);
		
		// delete course
		s = COURSE_C_ID + "=?";
		args = new String[] { String.valueOf(courseId) };
		db.delete(COURSE_TABLE, s, args);

	}
	
	public boolean isInstalled(String shortname){
		String s = COURSE_C_SHORTNAME + "=?";
		String[] args = new String[] { shortname };
		Cursor c = db.query(COURSE_TABLE, null, s, args, null, null, null);
		if(c.getCount() == 0){
			c.close();
			return false;
		} else {
			c.close();
			return true;
		}
	}
	
	public boolean toUpdate(String shortname, Double version){
		String s = COURSE_C_SHORTNAME + "=? AND "+ COURSE_C_VERSIONID + "< ?";
		String[] args = new String[] { shortname, String.format("%.0f", version) };
		Cursor c = db.query(COURSE_TABLE, null, s, args, null, null, null);
		if(c.getCount() == 0){
			c.close();
			return false;
		} else {
			c.close();
			return true;
		}
	}
	
	public boolean toUpdateSchedule(String shortname, Double scheduleVersion){
		String s = COURSE_C_SHORTNAME + "=? AND "+ COURSE_C_SCHEDULE + "< ?";
		String[] args = new String[] { shortname, String.format("%.0f", scheduleVersion) };
		Cursor c = db.query(COURSE_TABLE, null, s, args, null, null, null);
		if(c.getCount() == 0){
			c.close();
			return false;
		} else {
			c.close();
			return true;
		}
	}
	
	public long getUserId(String username){
		String s = USER_C_USERNAME + "=? ";
		String[] args = new String[] { username };
		Cursor c = db.query(CBCC_USER_TABLE, null, s, args, null, null, null);
		c.moveToFirst();
		long userId = -1;
		while (c.isAfterLast() == false) {
			userId = c.getLong(c.getColumnIndex(USER_C_ID));
			c.moveToNext();
		}
		c.close();
		return userId;
	}
	
	public CbccUser getUser(long userId) throws UserNotFoundException {
		String s = USER_C_ID + "=? ";
		String[] args = new String[] { String.valueOf(userId) };
		Cursor c = db.query(CBCC_USER_TABLE, null, s, args, null, null, null);
		c.moveToFirst();
		CbccUser u = null;
		while (c.isAfterLast() == false) {
			u = new CbccUser();
			u.setUserId(c.getLong(c.getColumnIndex(USER_C_ID)));
			u.setApiKey(c.getString(c.getColumnIndex(USER_C_APIKEY)));
			u.setUsername(c.getString(c.getColumnIndex(USER_C_USERNAME)));
			u.setFirstname(c.getString(c.getColumnIndex(USER_C_FIRSTNAME)));
			u.setLastname(c.getString(c.getColumnIndex(USER_C_LASTNAME)));
			u.setPoints(c.getInt(c.getColumnIndex(USER_C_POINTS)));
			u.setBadges(c.getInt(c.getColumnIndex(USER_C_BADGES)));
			u.setPasswordEncrypted(c.getString(c.getColumnIndex(USER_C_PASSWORDENCRYPTED)));
			c.moveToNext();
		}
		c.close();
		if (u == null){
			throw new UserNotFoundException();
		}
		return u;
	}
	
	public CbccUser getUser(String userName) throws UserNotFoundException {
		String s = USER_C_USERNAME + "=? ";
		String[] args = new String[] { userName };
		Cursor c = db.query(CBCC_USER_TABLE, null, s, args, null, null, null);
		c.moveToFirst();
		CbccUser u = null;
		while (c.isAfterLast() == false) {
			u = new CbccUser();
			u.setUserId(c.getLong(c.getColumnIndex(USER_C_ID)));
			u.setApiKey(c.getString(c.getColumnIndex(USER_C_APIKEY)));
			u.setUsername(c.getString(c.getColumnIndex(USER_C_USERNAME)));
			u.setFirstname(c.getString(c.getColumnIndex(USER_C_FIRSTNAME)));
			u.setLastname(c.getString(c.getColumnIndex(USER_C_LASTNAME)));
			u.setPoints(c.getInt(c.getColumnIndex(USER_C_POINTS)));
			u.setBadges(c.getInt(c.getColumnIndex(USER_C_BADGES)));
			u.setPasswordEncrypted(c.getString(c.getColumnIndex(USER_C_PASSWORDENCRYPTED)));
			c.moveToNext();
		}
		c.close();
		if (u == null){
			throw new UserNotFoundException();
		}
		return u;
	}
	
	public void updateUserPoints(String userName, int points){
		ContentValues values = new ContentValues();
		values.put(USER_C_POINTS, points);
		String s = USER_C_USERNAME + "=? ";
		String[] args = new String[] { userName };
		db.update(CBCC_USER_TABLE, values, s ,args);
	}
	
	public void updateUserBadges(String userName, int badges){
		ContentValues values = new ContentValues();
		values.put(USER_C_BADGES, badges);
		String s = USER_C_USERNAME + "=? ";
		String[] args = new String[] { userName };
		db.update(CBCC_USER_TABLE, values, s ,args);
	}
	
	public void updateUserPoints(long userId, int points){
		ContentValues values = new ContentValues();
		values.put(USER_C_POINTS, points);
		String s = USER_C_ID + "=? ";
		String[] args = new String[] { String.valueOf(userId) };
		db.update(CBCC_USER_TABLE, values, s ,args);
	}
	
	public void updateUserBadges(long userId, int badges){
		ContentValues values = new ContentValues();
		values.put(USER_C_BADGES, badges);
		String s = USER_C_ID + "=? ";
		String[] args = new String[] { String.valueOf(userId) };
		db.update(CBCC_USER_TABLE, values, s ,args);
	}
	
	public ArrayList<CbccUser> getAllUsers(){
		Cursor c = db.query(CBCC_USER_TABLE, null, null, null, null, null, null);
		c.moveToFirst();
		
		ArrayList<CbccUser> cbccUsers = new ArrayList<>();
		while (c.isAfterLast() == false) {
			CbccUser u = new CbccUser();
			u.setUserId(c.getInt(c.getColumnIndex(USER_C_ID)));
			u.setApiKey(c.getString(c.getColumnIndex(USER_C_APIKEY)));
			u.setUsername(c.getString(c.getColumnIndex(USER_C_USERNAME)));
			u.setFirstname(c.getString(c.getColumnIndex(USER_C_FIRSTNAME)));
			u.setLastname(c.getString(c.getColumnIndex(USER_C_LASTNAME)));
			u.setPoints(c.getInt(c.getColumnIndex(USER_C_POINTS)));
			u.setBadges(c.getInt(c.getColumnIndex(USER_C_BADGES)));
			u.setPasswordEncrypted(c.getString(c.getColumnIndex(USER_C_PASSWORDENCRYPTED)));
			cbccUsers.add(u);
			c.moveToNext();
		}
		c.close();
		return cbccUsers;
	}
	
	public int getSentTrackersCount(){
		String s = TRACKER_LOG_C_SUBMITTED + "=? ";
		String[] args = new String[] { "1"};
		Cursor c = db.query(TRACKER_LOG_TABLE, null, s, args, null, null, null);
		int count = c.getCount();
		c.close();
		return count;
	}
	
	public int getUnsentTrackersCount(){
		String s = TRACKER_LOG_C_SUBMITTED + "=? ";
		String[] args = new String[] { "0" };
		Cursor c = db.query(TRACKER_LOG_TABLE, null, s, args, null, null, null);
		int count = c.getCount();
		c.close();
		return count;
	}
	
	
	public Payload getUnsentTrackers(long userId){
		String s = TRACKER_LOG_C_SUBMITTED + "=? AND " + TRACKER_LOG_C_USERID + "=? ";
		String[] args = new String[] { "0", String.valueOf(userId) };
		Cursor c = db.query(TRACKER_LOG_TABLE, null, s, args, null, null, null);
		c.moveToFirst();

		ArrayList<Object> sl = new ArrayList<Object>();
		while (!c.isAfterLast()) {
			TrackerLog so = new TrackerLog();
			String digest = c.getString(c.getColumnIndex(TRACKER_LOG_C_ACTIVITYDIGEST));
			so.setId(c.getLong(c.getColumnIndex(TRACKER_LOG_C_ID)));
			so.setDigest(digest);
			String content = "";
			try {
				JSONObject json = new JSONObject();
				json.put("data", c.getString(c.getColumnIndex(TRACKER_LOG_C_DATA)));
				json.put("tracker_date", c.getString(c.getColumnIndex(TRACKER_LOG_C_DATETIME)));
				json.put("completed", c.getInt(c.getColumnIndex(TRACKER_LOG_C_COMPLETED)));
				json.put("digest", (digest!=null) ? digest : "");
				Course m = this.getCourse(c.getLong(c.getColumnIndex(TRACKER_LOG_C_COURSEID)), userId);
				if (m != null){
					json.put("course", m.getShortname());
				}
				String trackerType = c.getString(c.getColumnIndex(TRACKER_LOG_C_TYPE));
				if (trackerType != null){
					json.put("type",trackerType);
				}
				content = json.toString();
			} catch (JSONException e) {
 				e.printStackTrace();
			}
			
			so.setContent(content);
			sl.add(so);
			c.moveToNext();
		}
		Payload p = new Payload(sl);
		c.close();
		
		return p;
	}
	
	public int markLogSubmitted(long rowId){
		ContentValues values = new ContentValues();
		values.put(TRACKER_LOG_C_SUBMITTED, 1);
		
		return db.update(TRACKER_LOG_TABLE, values, TRACKER_LOG_C_ID + "=" + rowId, null);
	}
	
	public long insertQuizAttempt(QuizAttempt qa){
		ContentValues values = new ContentValues();
		values.put(QUIZATTEMPTS_C_DATA, qa.getData());
		values.put(QUIZATTEMPTS_C_COURSEID, qa.getCourseId());
		values.put(QUIZATTEMPTS_C_USERID, qa.getUserId());
		values.put(QUIZATTEMPTS_C_MAXSCORE, qa.getMaxscore());
		values.put(QUIZATTEMPTS_C_SCORE, qa.getScore());
		values.put(QUIZATTEMPTS_C_PASSED, qa.isPassed());
		values.put(QUIZATTEMPTS_C_ACTIVITY_DIGEST, qa.getActivityDigest());
		return db.insertOrThrow(QUIZATTEMPTS_TABLE, null, values);
	}
	
	public void updateQuizAttempt(QuizAttempt qa){
		ContentValues values = new ContentValues();
		values.put(QUIZATTEMPTS_C_DATA, qa.getData());
		values.put(QUIZATTEMPTS_C_COURSEID, qa.getCourseId());
		values.put(QUIZATTEMPTS_C_USERID, qa.getUserId());
		values.put(QUIZATTEMPTS_C_MAXSCORE, qa.getMaxscore());
		values.put(QUIZATTEMPTS_C_SCORE, qa.getScore());
		values.put(QUIZATTEMPTS_C_PASSED, qa.isPassed());
		values.put(QUIZATTEMPTS_C_ACTIVITY_DIGEST, qa.getActivityDigest());
		db.update(QUIZATTEMPTS_TABLE, values, QUIZATTEMPTS_C_ID + "=" + qa.getId(), null);
	}
	
	public void insertQuizAttempts(ArrayList<QuizAttempt> quizAttempts){
        beginTransaction();
        for (QuizAttempt qa : quizAttempts) {
            ContentValues values = new ContentValues();
            values.put(QUIZATTEMPTS_C_DATA, qa.getData());
            values.put(QUIZATTEMPTS_C_COURSEID, qa.getCourseId());
            values.put(QUIZATTEMPTS_C_USERID, qa.getUserId());
            values.put(QUIZATTEMPTS_C_MAXSCORE, qa.getMaxscore());
            values.put(QUIZATTEMPTS_C_SCORE, qa.getScore());
            values.put(QUIZATTEMPTS_C_PASSED, qa.isPassed());
            values.put(QUIZATTEMPTS_C_ACTIVITY_DIGEST, qa.getActivityDigest());
            values.put(QUIZATTEMPTS_C_SENT, qa.isSent());
            values.put(QUIZATTEMPTS_C_DATETIME, qa.getDateTimeString());
            db.insertOrThrow(QUIZATTEMPTS_TABLE, null, values);
        }
        endTransaction(true);
	}
	
	public ArrayList<QuizAttempt> getUnsentQuizAttempts(){
		String s = QUIZATTEMPTS_C_SENT + "=? ";
		String[] args = new String[] { "0" };
		Cursor c = db.query(QUIZATTEMPTS_TABLE, null, s, args, null, null, null);
		c.moveToFirst();
		ArrayList<QuizAttempt> quizAttempts = new ArrayList<QuizAttempt>();
		while (c.isAfterLast() == false) {
			try {
				QuizAttempt qa = new QuizAttempt();
				qa.setId(c.getLong(c.getColumnIndex(QUIZATTEMPTS_C_ID)));
				qa.setData(c.getString(c.getColumnIndex(QUIZATTEMPTS_C_DATA)));
				qa.setUserId(c.getLong(c.getColumnIndex(QUIZATTEMPTS_C_USERID)));
				CbccUser u = this.getUser(qa.getUserId());
				qa.setUser(u);
				quizAttempts.add(qa);
			} catch (UserNotFoundException unfe){
				// do nothing
			}
			c.moveToNext();
		}	
		c.close();
		return quizAttempts;
	}
	
	public int markQuizSubmitted(long rowId){
		ContentValues values = new ContentValues();
		values.put(QUIZATTEMPTS_C_SENT, 1);
		
		String s = QUIZATTEMPTS_C_ID + "=? ";
		String[] args = new String[] { String.valueOf(rowId) };
		return db.update(QUIZATTEMPTS_TABLE, values, s, args);
	}
	
	public void deleteQuizAttempts(long courseId, long userId){
		// delete any quiz attempts
		String s = QUIZATTEMPTS_C_COURSEID + "=? AND " + QUIZATTEMPTS_C_USERID +"=?";
		String[] args = new String[] { String.valueOf(courseId), String.valueOf(userId) };
		db.delete(QUIZATTEMPTS_TABLE, s, args);
	}
	
	public void deleteTrackers(long courseId, long userId){
		// delete any trackers
		String s = TRACKER_LOG_C_COURSEID + "=? AND " + TRACKER_LOG_C_USERID + "=? ";
		String[] args = new String[] { String.valueOf(courseId), String.valueOf(userId) };
		db.delete(TRACKER_LOG_TABLE, s, args);
	}
	
	public boolean activityAttempted(long courseId, String digest, long userId){
		String s = TRACKER_LOG_C_ACTIVITYDIGEST + "=? AND " +
					TRACKER_LOG_C_USERID + "=? AND " +
					TRACKER_LOG_C_COURSEID + "=?";
		String[] args = new String[] { digest, String.valueOf(userId), String.valueOf(courseId) };
		Cursor c = db.query(TRACKER_LOG_TABLE, null, s, args, null, null, null);
		if(c.getCount() == 0){
			c.close();
			return false;
		} else {
			c.close();
			return true;
		}
	}
	
	public boolean activityCompleted(int courseId, String digest, long userId){
		String s = TRACKER_LOG_C_ACTIVITYDIGEST + "=? AND " +
					TRACKER_LOG_C_COURSEID + "=? AND " + 
					TRACKER_LOG_C_USERID + "=? AND " +
					TRACKER_LOG_C_COMPLETED + "=1";
		String[] args = new String[] { digest, String.valueOf(courseId), String.valueOf(userId) };
		Cursor c = db.query(TRACKER_LOG_TABLE, null, s, args, null, null, null);
		if(c.getCount() == 0){
			c.close();
			return false;
		} else {
			c.close();
			return true;
		}
	}

    public void getCourseQuizResults(ArrayList<QuizStats> stats, int courseId, long userId){

        String quizResultsWhereClause = QUIZATTEMPTS_C_COURSEID+" =? AND " + QUIZATTEMPTS_C_USERID + "=?";
        String[] quizResultsArgs = new String[] { String.valueOf(courseId), String.valueOf(userId) };
        String[] quizResultsColumns = new String[]{ QUIZATTEMPTS_C_ACTIVITY_DIGEST, QUIZATTEMPTS_C_PASSED, QUIZATTEMPTS_C_MAXSCORE, QUIZATTEMPTS_C_SCORE};

        //We get the attempts made by the user for this course's quizzes
        Cursor c = db.query(QUIZATTEMPTS_TABLE, quizResultsColumns, quizResultsWhereClause, quizResultsArgs, null, null, null);
        if (c.getCount() <= 0) return; //we return the empty array

        if (stats == null) stats = new ArrayList<QuizStats>();

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String quizDigest = c.getString(c.getColumnIndex(QUIZATTEMPTS_C_ACTIVITY_DIGEST));
            int score = (int)(c.getFloat(c.getColumnIndex(QUIZATTEMPTS_C_SCORE)) * 100);
            int maxScore = (int)(c.getFloat(c.getColumnIndex(QUIZATTEMPTS_C_MAXSCORE)) * 100);
            boolean passed = c.getInt(c.getColumnIndex(QUIZATTEMPTS_C_PASSED))>0;

            boolean alreadyInserted = false;
            for (QuizStats quiz : stats){
                if (quiz.getDigest().equals(quizDigest)){
                    if (quiz.getUserScore() < score) quiz.setUserScore(score);
                    if (quiz.getMaxScore() < maxScore) quiz.setMaxScore(maxScore);
                    quiz.setAttempted(true);
                    quiz.setPassed(passed);
                    Log.d(TAG, "quiz score: " + quiz.getUserScore());
                    Log.d(TAG, "quiz passed: " + quiz.isPassed());
                    alreadyInserted = true;
                    break;
                }
            }
            if (!alreadyInserted){
                QuizStats quiz = new QuizStats();
                quiz.setAttempted(true);
                quiz.setDigest(quizDigest);
                quiz.setUserScore(score);
                quiz.setMaxScore(maxScore);
                quiz.setPassed(passed);
                stats.add(quiz);
            }

            c.moveToNext();
        }
        c.close();

    }

	
	public Activity getActivityByDigest(String digest){
		String sql = "SELECT * FROM  "+ ACTIVITY_TABLE + " a " +
					" WHERE " + ACTIVITY_C_ACTIVITYDIGEST + "='"+ digest + "'";
		Cursor c = db.rawQuery(sql,null);
		c.moveToFirst();
		Activity a = new Activity();
		while (c.isAfterLast() == false) {
			
			if(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)) != null){
				a.setDigest(c.getString(c.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)));
				a.setDbId(c.getInt(c.getColumnIndex(ACTIVITY_C_ID)));
				a.setTitlesFromJSONString(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)));
				a.setSectionId(c.getInt(c.getColumnIndex(ACTIVITY_C_SECTIONID)));
			}
			c.moveToNext();
		}
		c.close();
		return a;
	}
	
	public Activity getActivityByActId(int actId){
		String sql = "SELECT * FROM  "+ ACTIVITY_TABLE + " a " +
					" WHERE " + ACTIVITY_C_ACTID + "="+ actId;
		Cursor c = db.rawQuery(sql,null);
		c.moveToFirst();
		Activity a = new Activity();
		while (c.isAfterLast() == false) {
			
			if(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)) != null){
				a.setDigest(c.getString(c.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)));
				a.setDbId(c.getInt(c.getColumnIndex(ACTIVITY_C_ID)));
				a.setTitlesFromJSONString(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)));
				a.setSectionId(c.getInt(c.getColumnIndex(ACTIVITY_C_SECTIONID)));
			}
			c.moveToNext();
		}
		c.close();
		return a;
	}
	
	
	public ArrayList<Activity> getActivitiesDue(int max, long userId){
		
		ArrayList<Activity> activities = new ArrayList<Activity>();
		DateTime now = new DateTime();
		String nowDateString = MobileLearning.DATETIME_FORMAT.print(now);
		String sql = "SELECT a.* FROM "+ ACTIVITY_TABLE + " a " +
					" INNER JOIN " + COURSE_TABLE + " m ON a."+ ACTIVITY_C_COURSEID + " = m."+COURSE_C_ID +
					" LEFT OUTER JOIN (SELECT * FROM " + TRACKER_LOG_TABLE + " WHERE " + TRACKER_LOG_C_COMPLETED + "=1 AND "+ TRACKER_LOG_C_USERID +"="+ userId + ") tl ON a."+ ACTIVITY_C_ACTIVITYDIGEST + " = tl."+ TRACKER_LOG_C_ACTIVITYDIGEST +
					" WHERE tl." + TRACKER_LOG_C_ID + " IS NULL "+
					" AND a." + ACTIVITY_C_STARTDATE + "<='" + nowDateString + "'" +
					" AND a." + ACTIVITY_C_TITLE + " IS NOT NULL " +
					" ORDER BY a." + ACTIVITY_C_ENDDATE + " ASC" +
					" LIMIT " + max;
							
		
		Cursor c = db.rawQuery(sql,null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			Activity a = new Activity();
			if(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)) != null){
				a.setTitlesFromJSONString(c.getString(c.getColumnIndex(ACTIVITY_C_TITLE)));
				a.setCourseId(c.getLong(c.getColumnIndex(ACTIVITY_C_COURSEID)));
				a.setDigest(c.getString(c.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)));
				activities.add(a);
			}
			c.moveToNext();
		}
		
		if(c.getCount() < max){
			//just add in some extra suggested activities unrelated to the date/time
			String sql2 = "SELECT a.* FROM "+ ACTIVITY_TABLE + " a " +
					" INNER JOIN " + COURSE_TABLE + " m ON a."+ ACTIVITY_C_COURSEID + " = m."+COURSE_C_ID +
					" LEFT OUTER JOIN (SELECT * FROM " + TRACKER_LOG_TABLE + " WHERE " + TRACKER_LOG_C_COMPLETED + "=1) tl ON a."+ ACTIVITY_C_ACTIVITYDIGEST + " = tl."+ TRACKER_LOG_C_ACTIVITYDIGEST +
					" WHERE (tl." + TRACKER_LOG_C_ID + " IS NULL "+
					" OR tl." + TRACKER_LOG_C_COMPLETED + "=0)" +
					" AND a." + ACTIVITY_C_TITLE + " IS NOT NULL " +
					" AND a." + ACTIVITY_C_ID + " NOT IN (SELECT " + ACTIVITY_C_ID + " FROM (" + sql + ") b)" +
					" LIMIT " + (max-c.getCount());
			Cursor c2 = db.rawQuery(sql2,null);
			c2.moveToFirst();
			while (c2.isAfterLast() == false) {
				Activity a = new Activity();
				if(c2.getString(c.getColumnIndex(ACTIVITY_C_TITLE)) != null){
					a.setTitlesFromJSONString(c2.getString(c2.getColumnIndex(ACTIVITY_C_TITLE)));
					a.setCourseId(c2.getLong(c2.getColumnIndex(ACTIVITY_C_COURSEID)));
					a.setDigest(c2.getString(c2.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)));
					activities.add(a);
				}
				c2.moveToNext();
			}
			c2.close();
		}
		c.close();
		return activities;
	}
	
	/*
	 * SEARCH Functions
	 * 
	 */
	
	public void searchIndexRemoveCourse(long courseId){
		ArrayList<Activity> activities = this.getCourseActivities(courseId);
		Log.d(TAG, "deleting course from index: " + courseId);
		for(Activity a: activities){
			this.deleteSearchRow(a.getDbId());
		}
	}
	
	public void insertActivityIntoSearchTable(String courseTitle, String sectionTitle, String activityTitle, int activityDbId, String fullText){
		// strip out all html tags from string (not needed for search)
		String noHTMLString = fullText.replaceAll("\\<.*?\\>", " ");
		
		ContentValues values = new ContentValues();
		values.put("docid", activityDbId);
		values.put(SEARCH_C_TEXT, noHTMLString);
		values.put(SEARCH_C_COURSETITLE, courseTitle);
		values.put(SEARCH_C_SECTIONTITLE, sectionTitle);
		values.put(SEARCH_C_ACTIVITYTITLE, activityTitle);
		db.insertOrThrow(SEARCH_TABLE, null, values);
	}
	
	/*
	 * Perform a search
	 */
	public ArrayList<SearchResult> search(String searchText, int limit, long userId, Context ctx, DBListener listener){
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		String sqlSeachFullText = String.format("SELECT c.%s AS courseid, a.%s as activitydigest, a.%s as sectionid, 1 AS ranking FROM %s ft " +
									" INNER JOIN %s a ON a.%s = ft.docid" +
									" INNER JOIN %s c ON a.%s = c.%s " +
									" WHERE %s MATCH '%s' ",
										COURSE_C_ID, ACTIVITY_C_ACTIVITYDIGEST, ACTIVITY_C_SECTIONID, SEARCH_TABLE, 
										ACTIVITY_TABLE, ACTIVITY_C_ID, 
										COURSE_TABLE, ACTIVITY_C_COURSEID, COURSE_C_ID,
										SEARCH_C_TEXT, searchText);
		String sqlActivityTitle = String.format("SELECT c.%s AS courseid, a.%s as activitydigest, a.%s as sectionid, 5 AS ranking FROM %s ft " +
				" INNER JOIN %s a ON a.%s = ft.docid" +
				" INNER JOIN %s c ON a.%s = c.%s " +
				" WHERE %s MATCH '%s' ",
					COURSE_C_ID, ACTIVITY_C_ACTIVITYDIGEST, ACTIVITY_C_SECTIONID, SEARCH_TABLE, 
					ACTIVITY_TABLE, ACTIVITY_C_ID, 
					COURSE_TABLE, ACTIVITY_C_COURSEID, COURSE_C_ID,
					SEARCH_C_ACTIVITYTITLE, searchText);
		
		String sqlSectionTitle = String.format("SELECT c.%s AS courseid, a.%s as activitydigest, a.%s as sectionid, 10 AS ranking FROM %s ft " +
                        " INNER JOIN %s a ON a.%s = ft.docid" +
                        " INNER JOIN %s c ON a.%s = c.%s " +
                        " WHERE %s MATCH '%s' ",
                COURSE_C_ID, ACTIVITY_C_ACTIVITYDIGEST, ACTIVITY_C_SECTIONID, SEARCH_TABLE,
                ACTIVITY_TABLE, ACTIVITY_C_ID,
                COURSE_TABLE, ACTIVITY_C_COURSEID, COURSE_C_ID,
                SEARCH_C_SECTIONTITLE, searchText);
		String sqlCourseTitle = String.format("SELECT c.%s AS courseid, a.%s as activitydigest, a.%s as sectionid, 15 AS ranking FROM %s ft " +
				" INNER JOIN %s a ON a.%s = ft.docid" +
				" INNER JOIN %s c ON a.%s = c.%s " +
				" WHERE %s MATCH '%s' ",
					COURSE_C_ID, ACTIVITY_C_ACTIVITYDIGEST, ACTIVITY_C_SECTIONID, SEARCH_TABLE, 
					ACTIVITY_TABLE, ACTIVITY_C_ID, 
					COURSE_TABLE, ACTIVITY_C_COURSEID, COURSE_C_ID,
					SEARCH_C_COURSETITLE, searchText);
		
		String sql = String.format("SELECT DISTINCT courseid, activitydigest FROM ( SELECT * FROM (" +
				"%s UNION %s UNION %s UNION %s) ORDER BY ranking DESC LIMIT 0,%d)",
				sqlSeachFullText, sqlActivityTitle, sqlSectionTitle, sqlCourseTitle, limit);
		
		Cursor c = db.rawQuery(sql,null);
	    if(c !=null && c.getCount()>0){

            //We inform the AsyncTask that the query has been performed
            listener.onQueryPerformed();

            long startTime = System.currentTimeMillis();
            HashMap<Long, Course> fetchedCourses = new HashMap<Long, Course>();
            HashMap<Long, CourseXMLReader> fetchedXMLCourses = new HashMap<Long, CourseXMLReader>();

            c.moveToFirst();
            while (!c.isAfterLast()) {
                SearchResult result = new SearchResult();

                long courseId = c.getLong(c.getColumnIndex("courseid"));
                Course course = fetchedCourses.get(courseId);
                if (course == null){
                    course = this.getCourse(courseId, userId);
                    fetchedCourses.put(courseId, course);
                }
                result.setCourse(course);
	    		
	    		int digest = c.getColumnIndex("activitydigest");
	    		Activity activity = this.getActivityByDigest(c.getString(digest));
	    		result.setActivity(activity);
				
	    		int sectionOrderId = activity.getSectionId();
	    		CourseXMLReader cxr = fetchedXMLCourses.get(courseId);
				try {
                    if (cxr == null){
                        cxr = new CourseXMLReader(course.getCourseXMLLocation(), course.getCourseId(), ctx);
                        fetchedXMLCourses.put(courseId, cxr);
                    }
					result.setSection(cxr.getSection(sectionOrderId));
		    		results.add(result);
				} catch (InvalidXMLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		c.moveToNext();
			}
            long ellapsedTime = System.currentTimeMillis() - startTime;
            Log.d(TAG, "Performing search query and fetching. " + ellapsedTime + " ms ellapsed");
	    }
        if(c !=null) { c.close(); }
	    return results;

	}
	
	/*
	 * Delete the entire search index
	 */
	public void deleteSearchIndex(){
		db.execSQL("DELETE FROM " + SEARCH_TABLE);
		Log.d(TAG, "Deleted search index...");
	}
	
	/*
	 * Delete a particular activity from the search index
	 */
	public void deleteSearchRow(int activityDbId) {
		String s = "docid=?";
		String[] args = new String[] { String.valueOf(activityDbId) };
		db.delete(SEARCH_TABLE, s, args);
	}
	
	/*
	 * 
	 */
	public boolean isPreviousSectionActivitiesCompleted(Course course, Activity activity, long userId){
		// get this activity
		
		Log.d(TAG,"this digest = " + activity.getDigest());
		Log.d(TAG,"this actid = " + activity.getActId());
		Log.d(TAG,"this courseid = " + activity.getCourseId());
		Log.d(TAG,"this sectionid = " + activity.getSectionId());
		// get all the previous activities in this section
		String sql =  String.format("SELECT * FROM " + ACTIVITY_TABLE +
						" WHERE " + ACTIVITY_C_ACTID + " < %d " +
						" AND " + ACTIVITY_C_COURSEID + " = %d " +
						" AND " + ACTIVITY_C_SECTIONID + " = %d", activity.getActId(), activity.getCourseId(), activity.getSectionId());
		
		Log.d(TAG, "sql: " + sql);
		Cursor c = db.rawQuery(sql,null);
	    if(c !=null && c.getCount()>0){
	    	c.moveToFirst();
	    	boolean completed = true;
	    	// check if each activity has been completed or not
	    	while (c.isAfterLast() == false) {
	    		String sqlCheck = String.format("SELECT * FROM " + TRACKER_LOG_TABLE +
						" WHERE " + TRACKER_LOG_C_ACTIVITYDIGEST + " = '%s'" +
						" AND " + TRACKER_LOG_C_COMPLETED + " =1" +
						" AND " + TRACKER_LOG_C_USERID + " = %d",c.getString(c.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)), userId );
	    		Cursor c2 = db.rawQuery(sqlCheck,null);
	    		if (c2 == null || c2.getCount() == 0){
	    			completed = false;
	    			break;
	    		}
	    		c2.close();
	    		c.moveToNext();
	    	}
	    	c.close();
	    	return completed;
	    } else {
	    	c.close();
	    	return true;
	    }
	}
	
	/*
	 * 
	 */
	public boolean isPreviousCourseActivitiesCompleted(Course course, Activity activity, long userId){
		
		Log.d(TAG,"this digest = " + activity.getDigest());
		Log.d(TAG,"this actid = " + activity.getActId());
		Log.d(TAG,"this courseid = " + activity.getCourseId());
		Log.d(TAG,"this sectionid = " + activity.getSectionId());
		// get all the previous activities in this section
		String sql =  String.format("SELECT * FROM " + ACTIVITY_TABLE +
						" WHERE (" + ACTIVITY_C_COURSEID + " = %d " +
						" AND " + ACTIVITY_C_SECTIONID + " < %d )" +
						" OR (" + ACTIVITY_C_ACTID + " < %d " +
						" AND " + ACTIVITY_C_COURSEID + " = %d " +
						" AND " + ACTIVITY_C_SECTIONID + " = %d)", activity.getCourseId(), activity.getSectionId(), activity.getActId(), activity.getCourseId(), activity.getSectionId());
		
		Log.d(TAG,"sql: " + sql);
		Cursor c = db.rawQuery(sql,null);
	    if(c !=null && c.getCount()>0){
	    	c.moveToFirst();
	    	boolean completed = true;
	    	// check if each activity has been completed or not
	    	while (c.isAfterLast() == false) {
	    		String sqlCheck = String.format("SELECT * FROM " + TRACKER_LOG_TABLE +
	    										" WHERE " + TRACKER_LOG_C_ACTIVITYDIGEST + " = '%s'" +
	    										" AND " + TRACKER_LOG_C_COMPLETED + " =1" +
	    										" AND " + TRACKER_LOG_C_USERID + " = %d",c.getString(c.getColumnIndex(ACTIVITY_C_ACTIVITYDIGEST)), userId );
	    		Cursor c2 = db.rawQuery(sqlCheck,null);
	    		if (c2 == null || c2.getCount() == 0){
	    			completed = false;
	    			break;
	    		}
	    		c2.close();
	    		c.moveToNext();
	    	}
	    	c.close();
	    	return completed;
	    } else {
	    	c.close();
	    	return true;
	    }
	}

    public void insertUserPreferences(String username, List<Pair<String, String>> preferences){
        beginTransaction();
        for (Pair<String, String> prefence : preferences) {
            ContentValues values = new ContentValues();
            values.put(USER_PREFS_C_USERNAME, username);
            values.put(USER_PREFS_C_PREFKEY, prefence.first);
            values.put(USER_PREFS_C_PREFVALUE, prefence.second);
            db.insertWithOnConflict(USER_PREFS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        endTransaction(true);
    }

    public List<Pair<String, String>> getUserPreferences(String username){
        ArrayList<Pair<String, String>> prefs = new ArrayList<>();
        String whereClause = USER_PREFS_C_USERNAME + "=? ";
        String[] args = new String[] { username };

        Cursor c = db.query(USER_PREFS_TABLE, null, whereClause, args, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {

            String prefKey = c.getString(c.getColumnIndex(USER_PREFS_C_PREFKEY));
            String prefValue = c.getString(c.getColumnIndex(USER_PREFS_C_PREFVALUE));
            Pair<String, String> pref = new Pair<>(prefKey, prefValue);
            prefs.add(pref);

            c.moveToNext();
        }
        c.close();

        return prefs;
    }

    public String getUserPreference(String username, String preferenceKey){
        String whereClause = USER_PREFS_C_USERNAME + "=? AND " + USER_PREFS_C_PREFKEY + "=? ";
        String[] args = new String[] { username, preferenceKey };

        String prefValue = null;
        Cursor c = db.query(USER_PREFS_TABLE, null, whereClause, args, null, null, null);
        if (c.getCount() > 0){
            c.moveToFirst();
            prefValue = c.getString(c.getColumnIndex(USER_PREFS_C_PREFVALUE));
        }

        c.close();
        return prefValue;
    }
}
