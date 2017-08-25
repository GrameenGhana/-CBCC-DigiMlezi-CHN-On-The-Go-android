package org.cbccessence.utilities;/*
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


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.cbccessence.activity.LoginActivity;
import org.cbccessence.models.Projects;
import org.cbccessence.models.User;
import org.cbccessence.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.listener.SubmitListener;
import org.cbccessence.adapters.ProjectsListAdapter;
import org.digitalcampus.oppia.task.Payload;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class LoginTask extends AsyncTask<Payload, Object, Payload> {

	public static final String TAG = LoginTask.class.getSimpleName();

	private Context ctx;
	private SharedPreferences prefs;
	private SubmitListener mStateListener;
	private String module_name = "gfcare-module-3";
	List<Projects> projects;
	private DbHelper dbh;
    ProgressDialog progress;
    AlertDialog dialog;
    Payload _response = null;
    HttpHandler post;
    Integer uid;


	private String name;
	
	public LoginTask(AppCompatActivity c) {
		this.ctx = c;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		dbh =   DbHelper.getInstance(ctx);


	}

    @Override
    protected void onPreExecute(){


        post = new HttpHandler((AppCompatActivity) ctx);



    }



	@Override
	protected Payload doInBackground(Payload... params) {
		Payload payload = params[0];
		User u = (User) payload.getData().get(0);



		//String url = prefs.getString("prefServer", ctx.getString(R.string.prefServerDefault)) + MobileLearning.LOGIN_PATH;
		projects = new ArrayList<Projects>();
		String url = "http://188.166.30.140/gfcare/api/users/login";


		Log.i("LoginTask url is ", url.toString());
		Log.i("************** ", url.toString());
		JSONObject json = new JSONObject();


		try {
			Log.d(TAG, "logging in user *** " + u.getUsername());


			json.put("email", u.getUsername());
			json.put("password", u.getPassword());
			json.put("module", module_name);



			String responseJsonString = post.makePostRequest(url, json.toString());






			Log.i("Login Task", responseJsonString);



			switch (responseJsonString) {
				case "no access": // unauthorised
					payload.setResult(false);
					payload.setResultResponse(ctx.getString(R.string.no_access));
					break;
				case "invalid": // user not found
					payload.setResult(false);
					payload.setResultResponse(ctx.getString(R.string.error_login));
					break;
				case "null":

					Log.d(TAG, responseJsonString);
					payload.setResult(false);
					payload.setResultResponse(ctx.getString(R.string.error_connection));
					break;
				default: // logged in


					JSONObject jsonResp = new JSONObject(responseJsonString);
					String token = jsonResp.getString("token");
					JSONObject jsonUserDetails = jsonResp.getJSONObject("user");
					JSONArray projectsArray = jsonUserDetails.getJSONArray("projects");


                    uid = jsonUserDetails.getInt("id");








					Log.d(TAG, token);
					Log.d(TAG, jsonUserDetails.toString());

					for (int i = 0; i < projectsArray.length(); i++){
						JSONObject project = projectsArray.getJSONObject(i);
                        JSONObject pivotObject = project.getJSONObject("pivot");




						Integer id = project.getInt("id");
						Integer owner_id = project.getInt("owner_id");
						String name = project.getString("name");
						String date_created = project.getString("created_at");
						String date_updated = project.getString("updated_at");

                        int tid = pivotObject.getInt("team_id");




						Projects _project = new Projects();
						_project.setProjectId(id);
						_project.setProjectOwnerId(owner_id);
						_project.setProjectName(name);
						_project.setDateUpdated(date_updated);
						_project.setgetDateCreated(date_created);
                        _project.setTeamId(tid);

						projects.add(_project);

						Log.d(TAG, i + "\t" + _project.getProjectName());


					}

                    //User's parameters
					String name[] = jsonUserDetails.getString("name").split("\\s+");

					//Get current users Projects, Display in a custom dialog with a list of users projects, User makes a selection then app starts
					//with the current projects data! after making a post with the project id


					String firstName = name[0].trim();
					String lastName = name[1].trim();


					//User has access to This module, Save name in shared prefs/database and Sign him in
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putString("token", token);
                    prefsEditor.putInt("uid", uid);
					prefsEditor.putString("email", u.getUsername());
					prefsEditor.putString("first_name", firstName);
					prefsEditor.putString("last_name", lastName);

					prefsEditor.apply();

					Log.d(TAG, token);
					Log.d(TAG, firstName);
					Log.d(TAG, lastName);


					payload.setResult(true);
			}


		} catch (JSONException e) {

			e.printStackTrace();

			payload.setResult(false);
			payload.setResultResponse(ctx.getString(R.string.error_processing_response));
		}
		return payload;




	}

	@Override
	protected void onPostExecute(final Payload response) {
		synchronized (this) {
            if (mStateListener != null) {

                if(response.isResult()) {



                    if (projects != null) {
                        Log.i(TAG, "There are " + projects.size() + " project(s) ");




                        if (projects.size() == 0) {
                            response.setResult(false);
                            response.setResultResponse("No Projects!. You have no current projects");

                            mStateListener.submitComplete(response);

                            //If no projects, display a dialog. Close app since there are no projects



                        } else if (projects.size() == 1) { //Else log the user in.
                            response.setResult(true);
                            mStateListener.submitComplete(response);

                            _response = response;


                            //sendPostLoginRequest(uid, projects.get(0).getProjectId(), projects.get(0).getTeamId());

                        } else if (projects.size() > 1) {
                            _response = response;

                            LayoutInflater inflater = LayoutInflater.from(ctx);
                            View view = inflater.inflate(R.layout.select_project_dialog, null, false);
                            RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
                            recycler.setLayoutManager(layoutManager);
                            recycler.hasFixedSize();


                            ProjectsListAdapter adapter = new ProjectsListAdapter(ctx, projects);
                            recycler.setAdapter(adapter);
                            adapter.setOnItemClickListener(onItemClickListener);



                            //Show a dialog with a list of current projects if the projects obtained are more than one.

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx, R.style.AppDialog);
                            alertDialog.setCancelable(false);
                            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    response.setResult(false);
                                    response.setResultResponse("You did not make any selection");
                                    mStateListener.submitComplete(response);
                                }
                            }).setView(view);
                            dialog = alertDialog.create();
                            dialog.show();
                        }

                    } else {
                        response.setResult(false);
                        response.setResultResponse("Couldn't load app projects! Something went wrong :(");
                        mStateListener.submitComplete(response);
                    }




                    createNoMediaFile();


                }else mStateListener.submitComplete(response);



            }
        }
	}

    private void createNoMediaFile() {

        try{
            FileOutputStream out;

            File file = new File(MobileLearning.FOLDER_NAME + File.separator, ".nomedia");
            if (!file.exists()) {
                out = new FileOutputStream(file);
                out.write(0);
                out.close();


                Log.i(TAG, "No media file created!  " + file);
            } else {
                Log.i(TAG, "No media already exists!!!!!!  " + file);

            }


        }catch(Exception e){e.printStackTrace();}





    }

    public void setLoginListener(SubmitListener srl) {
        synchronized (this) {
            mStateListener = srl;
        }
    }


    ProjectsListAdapter.OnItemClickListener onItemClickListener = new ProjectsListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {



            Projects _project = projects.get(position);

            int mid = _project.getProjectId();
            int tid = _project.getTeamId();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("project_name", _project.getProjectName());
            editor.putInt("project_id", mid);
            editor.putInt("project_owner_id", _project.getProjectOwnerId());
            editor.putString("project_date_created", _project.getDateCreated());
            editor.putString("project_date_updated", _project.getDateUpdated());
            editor.apply();

            Log.i(TAG, _project.getProjectName() + " was clicked!");

            if(dialog != null) dialog.dismiss();

            if(progress != null) progress.dismiss();


            //Just had to to do this. It happens just once anyways
            sendPostLoginRequest(uid, tid, mid);


        }
    };



    public void sendPostLoginRequest(int uid, int tid, int mid){

        LoginActivity.pDialog.setTitle("Please wait");
        LoginActivity.pDialog.setMessage("Doing more stuff...");

        String postProjectSelectedUrl = "http://188.166.30.140/gfcare/api/users/context/" + uid + "/" + tid + "/"+ mid;
        String responsePostJsonString = post.sendPostProjectSelection(postProjectSelectedUrl);


        Log.i(TAG, "Post url is " + postProjectSelectedUrl);
        Log.i(TAG, "Post url response is " + responsePostJsonString);

        //test
        responsePostJsonString = "OK";

        switch (responsePostJsonString){

            case "OK" :
                //log the user in
                _response.setResult(true);
                break;

            default:
                //Something happened, check if the user has signed in before

                if(prefs.getBoolean("isFirstSignIn", true))
                {//first time ever user signed in, let user try again later
                    _response.setResult(false);
                    _response.setResultResponse("Couldn't get modules. Please try again later");

                }else{ //user has signed in before, just sign in anyway :)

                    _response.setResult(true);
                }
                break;




        }

        if (mStateListener != null) mStateListener.submitComplete(_response);
    }

}