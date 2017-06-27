package org.cbccessence.cch.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.cbccessence.utilities.HttpHandler;
import org.cbccessence.cch.model.User;
import org.cbccessence.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.cbccessence.cch.utils.Utils;
import org.cbccessence.utilities.TaskCompleteListener;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by aangjnr on 17/02/2017.
 */

public class ClientRegistrationFragment extends Fragment   {

    public View rootView;
    LinearLayout extraLayout;
    int translation;
    DbHelper databaseHelper;
    String TAG = ClientRegistrationFragment.class.getSimpleName();

    Integer year, month, day ;

    int responseCode = 0;
    String responseMessage = null;
    Switch rmm;

    TextInputLayout firstNameEditTextLayout;
    TextInputLayout lastNameEditTextLayout;
    TextInputLayout phoneEdittextLayout;
    Button ok;
    String firstName = "";
    String lastName = "";
    String DOB = "";
    String gender = "";
    String nationalId = "";
    String clientType = "";
    String phoneNumber = "";
    String afyaChannel = "";
    String afyaStartWeek = "";
    String program = "";
    String facilityId = "";
    Boolean insured;
    String language = "";
    String location = "";
    String education = "";
    String relativePhoneNumber = "";
    HttpHandler handler;
    String uid = "";
    TextInputLayout nationalIdEdittextLayout;
    TextInputLayout locationIdEdittextLayout;
    TextInputLayout facilityIdEdittextLayout;
    TextInputLayout languageIdEdittextLayout;
    TextInputLayout relativeIdEdittextLayout;
    Spinner client_type_spinner;
    Spinner channel_spinner;
    Spinner program_spinner;
    Spinner start_week_spinner;
    CheckBox male;
    CheckBox female;
    CheckBox insured_yes;
    CheckBox insured_no;
    static TaskCompleteListener completeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper =   DbHelper.getInstance(getActivity());
        translation = Utils.getScreenHeight(getActivity()) / 3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.client_registration, container, false);


        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(4, 6));
        day = Integer.parseInt(date.substring(6, 8));

        Log.i(TAG, "Year = " + year + " month = " + month + " day = " + day);
        handler = new HttpHandler((AppCompatActivity) getContext());


        male = (CheckBox) rootView.findViewById(R.id.male);
        female = (CheckBox) rootView.findViewById(R.id.female);
        insured_yes = (CheckBox) rootView.findViewById(R.id.yes);
        insured_no = (CheckBox) rootView.findViewById(R.id.no);

        male.setChecked(true);
        insured_no.setChecked(true);

        rmm = (Switch) rootView.findViewById(R.id.rmm);
        ok = (Button) rootView.findViewById(R.id.ok);

        extraLayout = (LinearLayout) rootView.findViewById(R.id.extraLayout);
        extraLayout.setTranslationY(translation);


         firstNameEditTextLayout = (TextInputLayout) rootView.findViewById(R.id.first_name_layout);
         lastNameEditTextLayout = (TextInputLayout) rootView.findViewById(R.id.last_name_layout);
         phoneEdittextLayout = (TextInputLayout) rootView.findViewById(R.id.user_phone_layout);
         nationalIdEdittextLayout = (TextInputLayout) rootView.findViewById(R.id.national_id_no_layout);
         locationIdEdittextLayout = (TextInputLayout) rootView.findViewById(R.id.location_layout);
         facilityIdEdittextLayout = (TextInputLayout) rootView.findViewById(R.id.facility_id_no_layout);
         languageIdEdittextLayout = (TextInputLayout) rootView.findViewById(R.id.language_layout);
         relativeIdEdittextLayout = (TextInputLayout) rootView.findViewById(R.id.relative_phone_layout);


        client_type_spinner = (Spinner) rootView.findViewById(R.id.client_type_spinner);
        channel_spinner = (Spinner) rootView.findViewById(R.id.channel_spinner);
        program_spinner = (Spinner) rootView.findViewById(R.id.program_spinner);
        start_week_spinner = (Spinner) rootView.findViewById(R.id.start_week);





        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _month = month.toString();
                String _day = day.toString();

                firstName = firstNameEditTextLayout.getEditText().getText().toString();
                lastName = lastNameEditTextLayout.getEditText().getText().toString();

                if(month < 10) _month = "0" + month;
                if(day < 10) _day = "0" + day;

                DOB = year.toString() + "-" + _month + "-" + _day;

                if(male.isChecked())
                    gender = "male";
                else gender = "female";
                nationalId = nationalIdEdittextLayout.getEditText().getText().toString();
                clientType = client_type_spinner.getSelectedItem().toString();
                phoneNumber = phoneEdittextLayout.getEditText().getText().toString();

                if(rmm.isChecked()) {
                    afyaChannel = channel_spinner.getSelectedItem().toString();
                    afyaStartWeek = start_week_spinner.getSelectedItem().toString();
                }else {
                    afyaChannel = "";
                    afyaStartWeek = "";

                }

                program = program_spinner.getSelectedItem().toString();
                facilityId = facilityIdEdittextLayout.getEditText().getText().toString();
                insured = insured_yes.isChecked();
                language = languageIdEdittextLayout.getEditText().getText().toString();
                location = locationIdEdittextLayout.getEditText().getText().toString();
                education = "";
                relativePhoneNumber = relativeIdEdittextLayout.getEditText().getText().toString();
                uid = String.valueOf(System.currentTimeMillis());



                if(handler.checkInternetConnection()) {

                    RegisterUserTask task = new RegisterUserTask();
                    task.execute();


                }else{// No internet, Save into the database and mark sync_status as 0;
                    Log.i(TAG, "No internet, save locally and mark as unSynced");
                    User user = new User(firstName, lastName, phoneNumber, DOB, gender, nationalId,
                            clientType, afyaChannel, afyaStartWeek, facilityId,
                            insured.toString(), language, location, education, relativePhoneNumber, 0 );


                    if (databaseHelper.addUser(rmm.isChecked(), user)){
                        handler.showAlertDialog((AppCompatActivity) getActivity(), "Registration not successful", "User details will be saved locally and synced later");


                    //scroll vp

                         //ClientListFragment.users.add(user);
                        if (ClientRegistrationFragment.completeListener != null) {
                            ClientRegistrationFragment.completeListener.submitTaskComplete(user);
                            Log.i(TAG, "Listener has been passed");

                        }


                    }
                    else {
                        Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_LONG).show();

                    }



                }





            }
        });




        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (male.isChecked()) female.setChecked(false);
                else female.setChecked(true);


            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (female.isChecked()) male.setChecked(false);
                else male.setChecked(true);


            }
        });

        insured_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (insured_yes.isChecked()) insured_no.setChecked(false);
                else insured_no.setChecked(true);


            }
        });

        insured_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (insured_no.isChecked()) insured_yes.setChecked(false);
                else insured_yes.setChecked(true);


            }
        });



        rmm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                 if (compoundButton.isChecked())
                     startContentAnimation();
                else resetContentAnimation();

            }
        });


        return rootView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onActivityCreated(savedInstanceState);

        /*float offsetPx = getResources().getDimension(R.dimen.recycler_bottom_space);
        BottomOffsetDecoration bottomOffsetDecoration = new BottomOffsetDecoration((int) offsetPx);
        grid_recycler.addItemDecoration(bottomOffsetDecoration);*/




        rootView.findViewById(R.id.select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showDatePickerDialog();


            }
        });


        firstNameEditTextLayout.getEditText().addTextChangedListener(nameEditorWatcher);
        lastNameEditTextLayout.getEditText().addTextChangedListener(nameEditorWatcher);
        phoneEdittextLayout.getEditText().addTextChangedListener(phoneNumberEditorWatcher);


    }


    public void showDatePickerDialog() {



        DatePickerDialog date_picker_dialog = new DatePickerDialog(getActivity(), datePickerListener,
                year,
                month,
                day);

        date_picker_dialog.show();


    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {


            if(selectedYear >= year) {
                Toast.makeText(getContext(), "You can't be born in the future. Please select correct date.", Toast.LENGTH_SHORT).show();
                ok.setVisibility(View.GONE);
            }else if ((year - selectedYear) <=10 ){

                Toast.makeText(getContext(), "Please select correct date.", Toast.LENGTH_SHORT).show();
                ok.setVisibility(View.GONE);

            }else {

                if(ok.getVisibility() == View.GONE)
                    ok.setVisibility(View.VISIBLE);

                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;

            }

        }
    };






    private final TextWatcher nameEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0){
                firstNameEditTextLayout.setError("Please enter name");
            ok.setEnabled(false);
        }
            else {
                ok.setEnabled(true);
                firstNameEditTextLayout.setError(null);
            }

        }
    };

    private final TextWatcher phoneNumberEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() < 10) {
                phoneEdittextLayout.setError("Please enter a valid phone number");

                ok.setEnabled(false);
            }else{
                ok.setEnabled(true);
                phoneEdittextLayout.setError(null);
            }
        }
    };






    private void resetContentAnimation() {

        extraLayout.animate()
                .translationY(translation)
                .setInterpolator(new LinearInterpolator())
                .alpha(0)
                .setDuration(300)
                .start();

    }

    private void startContentAnimation() {
        extraLayout.animate()
                .translationY(0)
                .alpha(1)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(200)
                .setDuration(300)
                .start();

    }

    private Long start_time;
    private Long end_time;
    String log_type = "module_usage";


    @Override
    public void onStart() {
        super.onStart();

        start_time = System.currentTimeMillis();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        end_time = System.currentTimeMillis();
        try {

            JSONObject log = new JSONObject();
            log.put("name", "Client Registration");
            log.put("start_time", start_time);
            log.put("end_time", end_time);
            log.put("time_taken", end_time - start_time);

            if (databaseHelper.addLog(log_type, log.toString()))
                Log.i("Client Registration", "Log added with data\t" + log);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private class RegisterUserTask extends AsyncTask<String, Integer, String> {

        private Context context = getContext();
        private PowerManager.WakeLock mWakeLock;
        String TAG = "registration Task";
        ProgressDialog mProgressDialog;

        String url = "http://188.166.30.140/gfcare/api/users/register/appuser";


        private int i;

        RegisterUserTask() {

            //instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();


            if (!mProgressDialog.isShowing())
                mProgressDialog.show();


        }


        @SuppressWarnings("resource")
        @Override
        protected String doInBackground(String... reqUrl) {


            Map<String, String> contentValues = new HashMap<String, String>();
            contentValues.put("app_data", "ml");
            contentValues.put("uuid", uid);

            contentValues.put("firstname", firstName);
            contentValues.put("lastname", lastName);
            contentValues.put("dob", DOB);
            contentValues.put("gender", gender);
            contentValues.put("client_type", clientType);
            contentValues.put("phonenumber", phoneNumber);
            contentValues.put("afya_channel", afyaChannel);
            contentValues.put("start_week", afyaStartWeek);
            contentValues.put("program", program);
            contentValues.put("facility_id", facilityId);
            contentValues.put("insured", insured.toString());
            contentValues.put("national_id", nationalId);
            contentValues.put("language", language);
            contentValues.put("location", location);
            contentValues.put("education", education);
            contentValues.put("relative_phonenumber", relativePhoneNumber);


            HttpURLConnection conn = handler.sendRegistrationDetailsToServer(url, contentValues);

            try {

                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();

                Log.i(TAG, "Response code is  " + conn.getResponseCode());
                Log.i(TAG, "Response message is  " + conn.getResponseMessage());



            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            mWakeLock.release();


            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();


           if(responseCode == HttpURLConnection.HTTP_OK || responseMessage.equals("OK")){
                //sent to server, save locally and mark as synced

               User user = new User(firstName, lastName, phoneNumber, DOB, gender, nationalId,
                       clientType, afyaChannel, afyaStartWeek, facilityId,
                       insured.toString(), language, location, education, relativePhoneNumber, 1 );

               if (databaseHelper.addUser(rmm.isChecked(), user)){

                   Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show();
                   //ClientListFragment.users.add(user);

                   if (ClientRegistrationFragment.completeListener != null) {
                       ClientRegistrationFragment.completeListener.submitTaskComplete(user);
                       Log.i(TAG, "Listener has been passed");

                   }


               }else {
                   Toast.makeText(context, "Registration failed", Toast.LENGTH_LONG).show();

               }


            }else  {

               User user = new User(firstName, lastName, phoneNumber, DOB, gender, nationalId,
                       clientType, afyaChannel, afyaStartWeek, facilityId,
                       insured.toString(), language, location, education, relativePhoneNumber, 0 );

               //no connection or connection timed out or error. Show dialog Save locally and flag as unSynced.
               if (databaseHelper.addUser(rmm.isChecked(), user)){
                   //ClientListFragment.users.add(0, user);


                   if (ClientRegistrationFragment.completeListener != null) {
                       ClientRegistrationFragment.completeListener.submitTaskComplete(user);
                       Log.i(TAG, "Listener has been passed");

                   }


               }else {
                   Toast.makeText(context, "Registration failed", Toast.LENGTH_LONG).show();

               }

           }
        }



    }


    public static void setTaskCompleteListener(TaskCompleteListener eventListener) {

        completeListener = eventListener;

    }


}
