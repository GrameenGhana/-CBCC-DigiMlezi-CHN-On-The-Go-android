package org.cbccessence.poc;

/**
 * Created by aangjnr on 14/02/2017.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.model.SubSection;
import org.cbccessence.RecyclerItemViewAnimator;
import org.cbccessence.SpacesItemDecoration;
import org.cbccessence.adapters.PocSubSectionsAdapter;
import org.cbccessence.cch.utils.Utils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PocSubSectionActivity extends BaseActivity {

    private ListView listView_ancMenu;
    //	private Context mContext;
    private DbHelper databaseHelper;
    private Long start_time;
    private Long end_time;
    private JSONObject json;
    private Button button_load;
    private Button button_refresh;
    private List<SubSection> subSectionsList;
    private RecyclerView recyclerView;
    SpacesItemDecoration itemDecoration;
    String TAG = PocSubSectionActivity.class.getSimpleName();
    private PocSubSectionsAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
        Intent intent = getIntent();
        String sectionName = intent.getStringExtra("sectionName");
        Integer sectionId = intent.getIntExtra("sectionId", -1);

        setContentView(R.layout.poc_sub_section_activity);
        subSectionsList = new ArrayList<>();

        databaseHelper = new DbHelper(this.getApplicationContext());

        if(sectionId != -1)
            subSectionsList = databaseHelper.getSubSections(sectionId);


        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Point of Care");

            if (sectionName != null)
                getSupportActionBar().setSubtitle(sectionName);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.poc_sub_sec_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        int spaces = 32;
        if (Utils.isTabletDevice(this)) spaces = spaces / 2;

        itemDecoration = new SpacesItemDecoration(spaces);

        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setHasFixedSize(true);


        if(subSectionsList != null){

            Log.i(TAG, "subSectionsList Array is not null with size   " + subSectionsList.size());

            adapter = new PocSubSectionsAdapter(PocSubSectionActivity.this, subSectionsList);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new RecyclerItemViewAnimator());
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(onItemClickListener);





        }





    }


    PocSubSectionsAdapter.OnItemClickListener onItemClickListener = new PocSubSectionsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            //get section Id, start subsections activity with section id, get sub sections from db which has section_id = section_id you clicked :)
            SubSection subSection = subSectionsList.get(position);



                Intent intent = new Intent(PocSubSectionActivity.this, PocTopicsActivity.class);
                intent.putExtra("sectionName", subSection.getSectionName());
                intent.putExtra("subSectionName", subSection.getName());
                intent.putExtra("subSectionId", subSection.getId());
                startActivity(intent);



        }
    };



    public void onBackPressed() {

        finish();
    }



    String log_type = "module_usage";

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
