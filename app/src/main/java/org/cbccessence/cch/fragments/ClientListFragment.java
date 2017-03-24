package org.cbccessence.cch.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.json.JSONObject;

/**
 * Created by aangjnr on 17/02/2017.
 */

public class ClientListFragment extends Fragment {


    public View rootView;
    private DbHelper databaseHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DbHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.client_list_layout, container, false);






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
    }

    @Override
    public void onResume() {
        super.onResume();



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
