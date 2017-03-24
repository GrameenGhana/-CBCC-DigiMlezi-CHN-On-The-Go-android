package org.cbccessence.cch.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.cbccessence.cch.utils.Utils;
import org.json.JSONObject;

/**
 * Created by aangjnr on 17/02/2017.
 */

public class ClientRegistrationFragment extends Fragment {

    public View rootView;
    LinearLayout extraLayout;
    int translation;
    DbHelper databaseHelper;

    Integer year, month, day, presentYear;
    RelativeLayout selectDate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DbHelper(getActivity());
        translation = Utils.getScreenHeight(getActivity())/3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.client_registration, container, false);



        TextInputLayout name = (TextInputLayout) rootView.findViewById(R.id.user_name_layout);
         TextInputLayout phone = (TextInputLayout) rootView.findViewById(R.id.user_phone_layout);

        final CheckBox male = (CheckBox) rootView.findViewById(R.id.male);
        final CheckBox female = (CheckBox) rootView.findViewById(R.id.female);

        Switch rmm = (Switch) rootView.findViewById(R.id.rmm);
        Button ok = (Button) rootView.findViewById(R.id.ok);

        extraLayout = (LinearLayout) rootView.findViewById(R.id.extraLayout);
        extraLayout.setTranslationY(translation);


        Spinner client_type_spinner = (Spinner) rootView.findViewById(R.id.client_type_spinner);
        Spinner channel_spinner = (Spinner) rootView.findViewById(R.id.channel_spinner);
        Spinner program_spinner = (Spinner) rootView.findViewById(R.id.program_spinner);





        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(male.isChecked()) female.setChecked(false);
                else female.setChecked(true);


            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(female.isChecked()) male.setChecked(false);
                else male.setChecked(true);


            }
        });

        rmm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (compoundButton.isChecked()) startContentAnimation();
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

    }

    @Override
    public void onResume() {
        super.onResume();



    }


    public void showDatePickerDialog() {

        DatePickerDialog date_picker_dialog = new DatePickerDialog(getActivity(), datePickerListener, 1970, 01, 01);
        date_picker_dialog.show();


    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

        }
    };



    private void resetContentAnimation() {

        extraLayout.animate()
                .translationY(translation)
                .setInterpolator(new LinearInterpolator())
                .alpha(0)
                .setDuration(500)
                .start();

    }
    private void startContentAnimation() {
        extraLayout.animate()
                .translationY(0)
                .alpha(1)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(200)
                .setDuration(700)
                .start();

    }

    private Long start_time;
    private Long end_time;
    String log_type = "module_usage";


    @Override
    public void onStart(){
        super.onStart();

        start_time = System.currentTimeMillis();



    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        end_time = System.currentTimeMillis();
        try {

            JSONObject log = new JSONObject();
            log.put("name", "Client Registration");
            log.put("start_time", start_time);
            log.put("end_time", end_time );
            log.put("time_taken", end_time - start_time );

            if(databaseHelper.addLog(log_type, log.toString()))
                Log.i("Client Registration", "Log added with data\t" + log);


        }catch(Exception e){
            e.printStackTrace();
        }


    }



}
