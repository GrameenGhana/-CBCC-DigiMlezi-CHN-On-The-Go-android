package org.cbccessence.cch.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cbccessence.adapters.UsersRecyclerAdapter;
import org.cbccessence.cch.model.User;
import org.cbccessence.R;
import org.cbccessence.utilities.TaskCompleteListener;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.listener.SubmitListener;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aangjnr on 17/02/2017.
 */

public class ClientListFragment extends Fragment implements UsersRecyclerAdapter.OnItemClickListener, UsersRecyclerAdapter.OnItemLongClickListener, TaskCompleteListener {

    public static List<User> users;
    TextView place_holder_text;
    UsersRecyclerAdapter mAdapter;
    String TAG = ClientListFragment.class.getSimpleName();
    RecyclerView mRecycler;

    public View rootView;
    private DbHelper databaseHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        databaseHelper =   DbHelper.getInstance(getActivity());
        users = databaseHelper.getAllRegisteredUsers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.client_list_layout, container, false);


        mRecycler = (RecyclerView) rootView.findViewById(R.id.users_recycler);

        place_holder_text = (TextView) rootView.findViewById(R.id.place_holder_text);






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

        if(users != null && users.size() > 0){
            if(place_holder_text.getVisibility() == View.VISIBLE) place_holder_text.setVisibility(View.GONE);

            mAdapter = new UsersRecyclerAdapter(getActivity(), users);
            LinearLayoutManager lll = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            mRecycler.setLayoutManager(lll);
            mRecycler.setAdapter(mAdapter);
            mRecycler.hasFixedSize();
            mAdapter.setOnItemCickListener(this);
            mAdapter.setOnItemLongClickListener(this);





        }else{

            place_holder_text.setVisibility(View.VISIBLE);

        }





    }


    @Override
    public void onResume() {
        super.onResume();

        if(mAdapter != null)
        mAdapter.notifyDataSetChanged();



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

/*

    UsersRecyclerAdapter.OnItemClickListener onItemClickListener = new UsersRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.i(TAG, "On item click listener");

        }
    };

    UsersRecyclerAdapter.OnItemLongClickListener onItemLongClickListener = new UsersRecyclerAdapter.OnItemLongClickListener() {
        @Override
        public boolean onLongClick(View view, int position) {


            return false;
        }
    };*/


    @Override
    public void onItemClick(View view, int position) {
        Log.i(TAG, "On item click listener");

    }

    @Override
    public boolean onLongClick(View view, int position) {
        Log.i(TAG, "On item long click listener");

        return false;
    }

    @Override
    public void submitTaskComplete(User user) {


        if(user != null)
        {

            users.add(user);
            mAdapter.notifyDataSetChanged();


        }

    }
}
