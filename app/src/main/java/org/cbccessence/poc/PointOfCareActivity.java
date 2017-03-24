package org.cbccessence.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.model.POCSection;
import org.digitalcampus.oppia.model.SubSection;
import org.digitalcampus.oppia.model.Topic;
import org.cbccessence.HttpHandler;
import org.cbccessence.PlaceHolder;
import org.cbccessence.RecyclerItemViewAnimator;
import org.cbccessence.SpacesItemDecoration;
import org.cbccessence.adapters.PocSectionsAdapter;
import org.cbccessence.cch.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PointOfCareActivity extends BaseActivity implements OnItemClickListener {

	private String token;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    DbHelper databaseHelper;
    PocSectionsAdapter adapter;
    SpacesItemDecoration itemDecoration;


    private Long start_time;
    private Long end_time;
    String log_type = "module_usage";






	private String TAG = PointOfCareActivity.class.getSimpleName();

	private ProgressDialog pDialog;
	private RecyclerView recyclerView;

	private String url = "http://188.166.30.140/gfcare/api/chn-on-the-go/content/poc/sections";

    List<POCSection> sectionList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_point_of_care);

        databaseHelper = new DbHelper(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


		token = prefs.getString("token", null);
	    mContext = PointOfCareActivity.this;
        getSupportActionBar().setTitle("Point of Care");


		sectionList = new ArrayList<>();


		recyclerView = (RecyclerView) findViewById(R.id.recyclerView_pocMenu);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        int spaces = 32;
        if (Utils.isTabletDevice(this)) spaces = spaces / 2;

        itemDecoration = new SpacesItemDecoration(spaces);

        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setHasFixedSize(true);



		new GetContent().execute();


		//PointOfCareBaseAdapter adapter=new PointOfCareBaseAdapter(mContext,imageIds,category);

		//listView_menu.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(getApplicationContext(), position, Toast.LENGTH_SHORT).show();

	}

	private class GetContent extends AsyncTask<String, String, String> {


		Intent intent = new Intent(PointOfCareActivity.this, PointOfCareActivity.class);
        HttpHandler sh = new HttpHandler(PointOfCareActivity.this);


		@Override
		protected void onPreExecute() {
			super.onPreExecute();



            // Showing progress dialog
			pDialog = new ProgressDialog(PointOfCareActivity.this);
            pDialog.setTitle("Updating content");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;



			// Making a request to url and getting response

			//token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlzcyI6Imh0dHA6XC9cLzE4OC4xNjYuMzAuMTQwXC9nZmNhcmVcL2FwaVwvdXNlcnNcL2xvZ2luIiwiaWF0IjoxNDg2NDA2MTQzLCJleHAiOjE0ODY0Mjc3NDMsIm5iZiI6MTQ4NjQwNjE0MywianRpIjoiYWM3ZDhiNjNjMTM4YWVmN2RiOWFhM2ExY2RiY2I1OTUifQ.JaOoXgNkqPLqyI5JTEegk-2nZq6Tqof4vKwRuT6rcr0";

			if (token != null) {
				String jsonStringFromServer = sh.makeServiceCall(url, token);


				Log.i(TAG, "Response from url: " + jsonStringFromServer);

				if (jsonStringFromServer != null) {

					if (jsonStringFromServer.contains("no access")) {
						Log.i(TAG, "Token has expired " + jsonStringFromServer);
						result = "false";


					} else {
						result = "true";

						Log.i(TAG, jsonStringFromServer);

						try {

							JSONObject jsonString = new JSONObject(jsonStringFromServer);

                            //Get last time content was updated, save in sharedPrefs
                            String last_update_date = jsonString.getString("last_update");

                            Log.i(TAG, "Last content update was " + last_update_date);

                            String last_saved_date = prefs.getString("lastContentUpdate", "null");

                            Log.i(TAG, "Last saved date was " + last_saved_date);

                            if(!last_update_date.equalsIgnoreCase(last_saved_date)) {


                                //Dates don't match, content has been updated.
                                //delete all data from db and populate new content into db

                                databaseHelper.deleteAllPOCTableRows();

                                //It will also be good to just delete and update changed elements/data but better safe than sorry right?
                                //Than might take more steps and processes depending on how you do it.

                                // Getting JSON Array node
                                JSONArray contentArray = jsonString.getJSONArray("content");

                                Log.i(TAG, "Content array is " + contentArray);


                                // looping through All
                                for (int i = 0; i < contentArray.length(); i++) { //Add all available sections
                                    JSONObject section = contentArray.getJSONObject(i);

                                    Integer id = section.getInt("id");
                                    Integer team_id = section.getInt("team_id");
                                    String name = section.getString("name");
                                    String icon_url = section.getString("icon_url");
                                    Integer modifier = section.getInt("modified_by");
                                    String date_created = section.getString("created_at");
                                    String date_updated = section.getString("updated_at");


                                    POCSection _section = new POCSection(id, team_id, modifier, name, icon_url, date_created, date_updated);

                                    databaseHelper.addSection(_section);

                                    Log.i(TAG, "Section added with name\t" + name);

                                    JSONArray sub_sections = section.getJSONArray("subsections");

                                    for(int j = 0; j < sub_sections.length(); j++){ //for each section, add all subsections

                                        JSONObject sub_section = sub_sections.getJSONObject(j);

                                        Integer sub_section_id = sub_section.getInt("id");
                                        Integer sub_section_team_id = sub_section.getInt("team_id");
                                        Integer sub_section_sid = sub_section.getInt("section_id");
                                        String sub_section_name = sub_section.getString("name");
                                        String sub_section_icon_url = sub_section.getString("icon_url");
                                        Integer sub_section_modifier = sub_section.getInt("modified_by");
                                        String sub_section_date_created = sub_section.getString("created_at");
                                        String sub_section_date_updated = sub_section.getString("updated_at");
                                        String sub_section_section_name = sub_section.getString("section");

                                        SubSection _sub_section = new SubSection(sub_section_id, sub_section_team_id, sub_section_sid, sub_section_modifier, sub_section_name,
                                                sub_section_icon_url, sub_section_date_created, sub_section_date_updated, sub_section_section_name);
                                        databaseHelper.addSubSection(_sub_section);

                                        Log.i(TAG, "Sub Section added with name\t" + sub_section_name);



                                        JSONArray topics = sub_section.getJSONArray("topics");

                                        for(int k = 0; k < topics.length(); k++){ //for each subsection, add all topics

                                            JSONObject topic = topics.getJSONObject(k);

                                            Integer topic_id = topic.getInt("id");
                                            Integer topic_team_id = topic.getInt("team_id");
                                            Integer topic_subSection_id = topic.getInt("sub_section_id");
                                            Integer topic_modifier = topic.getInt("modified_by");
                                            Integer topic_section_id = topic.getInt("section_id");
                                            String topic_name = topic.getString("name");
                                            String topic_short_name = topic.getString("shortname");
                                            String topic_desc = topic.getString("description");
                                            String topic_upload_status = topic.getString("upload_status");
                                            String topic_file_url = topic.getString("file_url");
                                            String topic_date_created = topic.getString("created_at");
                                            String topic_date_updated = topic.getString("updated_at");
                                            String topic_section_name = topic.getString("section");
                                            String topic_subSection_name = topic.getString("sub_section");

                                            Topic _topic = new Topic(topic_id, topic_team_id, topic_section_id, topic_subSection_id, topic_modifier, topic_name, topic_short_name, topic_desc, topic_upload_status,
                                                    topic_file_url, topic_date_created, topic_date_updated, topic_section_name, topic_subSection_name);

                                            databaseHelper.addTopic(_topic);
                                            Log.i(TAG, "Topic added with name\t" + topic_name);


                                        }


                                    }


                                }

                                editor = prefs.edit();
                                editor.putString("lastContentUpdate", last_update_date);
                                editor.apply();

                                sectionList = databaseHelper.getAllSections();

                            }else { //same content as last updated, just load content from db and populate into view


                                sectionList = databaseHelper.getAllSections();

                            }

						} catch (final JSONException e) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(),
											"Json parsing error: " + e.getMessage(),
											Toast.LENGTH_LONG)
											.show();
								}
							});
						}
					}
				} else {
					Log.e(TAG, "Couldn't get json from server.");
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Couldn't get json from server. Check LogCat for possible errors!",
									Toast.LENGTH_LONG)
									.show();
						}
					});

				}
			} else { //Token is empty
				//Login
				Log.i(TAG, "token is empty");
				result = "false";
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Log.i(TAG, "" + result);

            if(result != null) {

                if (result.equals("true")) {

                    // Dismiss the progress dialog
                    if (pDialog.isShowing())
                        pDialog.dismiss();


                    if (sectionList != null) {

                        Log.i(TAG, "sectionsList Array is not null with size   " + sectionList.size());

                        adapter = new PocSectionsAdapter(PointOfCareActivity.this, sectionList);
                        recyclerView.setAdapter(adapter);


                        recyclerView.setItemAnimator(new RecyclerItemViewAnimator());

                        startContentAnimation();
                        adapter.notifyDataSetChanged();
                        adapter.setOnItemClickListener(onItemClickListener);


                    }


                } else if (result.equals("false")) {

                    // Dismiss the progress dialog
                    if (pDialog.isShowing())
                        pDialog.dismiss();

                    final PlaceHolder holder = new PlaceHolder(PointOfCareActivity.this, intent);

                    AlertDialog.Builder builder = new AlertDialog.Builder(PointOfCareActivity.this, R.style.AppAlertDialog);
                    builder.setCancelable(false);
                    builder.setTitle("Session has expired!");
                    builder.setMessage("Please login again");
                    builder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Show edit text for password and attepmt login to generate new token


                            holder.attemptLogin();
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Dismiss dialog and inflate empty view, if user clicks on login, show edittext and attempt login again
                            dialog.cancel();
                            dialog.dismiss();

                            holder.inflateEmptyView();
                        }
                    });
                    builder.show();


                }
            }

		}


	}


    PocSectionsAdapter.OnItemClickListener onItemClickListener = new PocSectionsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {


            //get section Id, start subsections activity with section id, get sub sections from db which has section_id = section_id you clicked :)
            POCSection section = sectionList.get(position);

            String sectionName = section.getSectionName();

            Integer sectionId = section.getSectionId();

            Toast.makeText(mContext, sectionName + "\t" + sectionId, Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(PointOfCareActivity.this, PocSubSectionActivity.class);
            intent.putExtra("sectionName", sectionName);
            intent.putExtra("sectionId", sectionId);
            startActivity(intent);






        }
    };



    private void startContentAnimation() {
        recyclerView.setAlpha(0f);
        recyclerView.animate()
                .setStartDelay(500)
                .setDuration(700)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
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
