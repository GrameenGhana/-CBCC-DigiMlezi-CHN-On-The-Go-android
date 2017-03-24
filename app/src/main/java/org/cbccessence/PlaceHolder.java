package org.cbccessence;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.digitalcampus.mobile.learningGF.R;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by aangjnr on 08/02/2017.
 */

public class PlaceHolder {
    ProgressDialog progress;
    SharedPreferences prefs;
    String TAG = "Place Holder";
    final AppCompatActivity context;
    Intent intent;




    public PlaceHolder(AppCompatActivity context, @Nullable Intent intent){
        this.context = context;
        this.intent = intent;

    }




    public void attemptLogin(){

         prefs = PreferenceManager.getDefaultSharedPreferences(context);


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);

        final TextInputLayout emailLayout = (TextInputLayout) mView.findViewById(R.id.email_layout);
        final TextInputLayout passwordLayout = (TextInputLayout) mView.findViewById(R.id.password_layout);

        emailLayout.getEditText().setText(prefs.getString("email", ""));


        alertDialogBuilderUserInput
                .setCancelable(false);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        mView.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailLayout.getEditText().getText().toString();
                String password = passwordLayout.getEditText().getText().toString();
                alertDialogAndroid.dismiss();
                //Attempt login here
                progress = new ProgressDialog(context);
                progress.setTitle("Logging in");
                progress.show();

                new GetNewTokenTask().execute(email, password);


            }
        });


        mView.findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
                //inflate empty view
                inflateEmptyView();
            }
        });

    }





    public  void refreshActivity(){
            if ( context != null) {
                context.finish();
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                context.startActivity(intent);




            }

    }



    public void inflateEmptyView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.empty_view, null);
//        if (view !=  null)((ViewGroup) view.getParent()).removeView(view);


        context.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });


    }


    private class GetNewTokenTask extends AsyncTask<String, Void, String> {
        String url = "http://188.166.30.140/gfcare/api/users/login";
        String module_name = "gfcare-module-3";
        String email, password;
        String results = null;




        JSONObject json = new JSONObject();




        @Override
        protected String doInBackground(String... creds) {
            email = creds[0];
            password = creds[1];

            Log.i(TAG, email + "\t" + password);


            try {

                json.put("email", email);
                json.put("password", password);
                json.put("module", module_name);


                HttpHandler post = new HttpHandler(context);

                String responseJsonString = post.makePostRequest(url, json.toString());

                Log.i(TAG, responseJsonString);



                switch (responseJsonString) {
                    case "no access": // unauthorised
                       results = "no access";
                        break;
                    case "invalid": // user not found
                        results = "invalid";
                        break;
                    case "null":
                        results = "connection error";

                        break;
                    default: // logged in

                        results = "OK";

                        JSONObject jsonResp = new JSONObject(responseJsonString);
                        String token = jsonResp.getString("token");
                        JSONObject jsonUserDetails = jsonResp.getJSONObject("user");
                        Log.d(TAG, token);
                        //User has access to This module, Save name in shared prefs/database and Sign him in
                        SharedPreferences.Editor prefsEditor = prefs.edit();
                        prefsEditor.putString("token", token);
                        prefsEditor.apply();

                }


            } catch (JSONException e) {

                e.printStackTrace();

            }
            return results;


        }

    @Override
    protected void onPostExecute(String result) {

        if(progress != null){
        progress.cancel();
        progress.dismiss();
        }


        switch(result)
        {
            case "no access": // unauthorised
                inflateEmptyView();
                Toast.makeText(context, context.getString(R.string.no_access), Toast.LENGTH_SHORT).show();

                break;
            case "invalid": // user not found
                inflateEmptyView();
                Toast.makeText(context, context.getString(R.string.error_login), Toast.LENGTH_SHORT).show();

                break;
            case "connection error":
                inflateEmptyView();
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                break;
            case "OK": // logged in
                refreshActivity();
                break;


        }

    }
}


    public static void inflateNoContentEmptyView(final AppCompatActivity _context){
        LayoutInflater inflater = (LayoutInflater)_context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.empty_view_no_content, null);
        if (view !=  null)((ViewGroup) view.getParent()).removeView(view);


        _context.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _context.finish();
            }
        });


    }


}
