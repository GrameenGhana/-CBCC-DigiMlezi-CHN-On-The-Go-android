package org.cbccessence.poc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.cbccessence.R;
import org.cbccessence.activity.MainScreenActivity;
import org.cbccessence.utilities.HttpHandler;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.cbccessence.models.Topic;
import org.cbccessence.utilities.RecyclerItemViewAnimator;
import org.cbccessence.utilities.SpacesItemDecoration;
import org.cbccessence.adapters.PocTopicsAdapter;
import org.cbccessence.cch.utils.Utils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by aangjnr on 14/02/2017.
 */

public class PocTopicsActivity extends BaseActivity {

    private ListView listView_ancMenu;
    //	private Context mContext;
    private DbHelper databaseHelper;
    private Long start_time;
    private Long end_time;
    private JSONObject json;
    private Button button_load;
    private Button button_refresh;
    private List<Topic> topicsList;
    private RecyclerView recyclerView;
    SpacesItemDecoration itemDecoration;
    String TAG = PocTopicsActivity.class.getSimpleName();
    private PocTopicsAdapter adapter;
    private int size;
    private int position;
    HttpHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
        Intent intent = getIntent();
        String sectionName = intent.getStringExtra("sectionName");
        String subSectionName = intent.getStringExtra("subSectionName");
        Integer subSectionId = intent.getIntExtra("subSectionId", -1);

        setContentView(R.layout.poc_topics_activity);
        topicsList = new ArrayList<>();
        handler = new HttpHandler(this);

        databaseHelper =   DbHelper.getInstance(this.getApplicationContext());

        if (subSectionId != -1)
            topicsList = databaseHelper.getTopics(subSectionId);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Point of Care");

            if (subSectionName != null)
                getSupportActionBar().setSubtitle(sectionName + " -> " + subSectionName);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.topics_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        int spaces = 20;
        if (Utils.isTabletDevice(this)) spaces = spaces / 2;

        itemDecoration = new SpacesItemDecoration(spaces);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setHasFixedSize(true);


        if (topicsList != null) {

            Log.i(TAG, "topicsList Array is not null with size   " + topicsList.size());

            adapter = new PocTopicsAdapter(PocTopicsActivity.this, topicsList);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new RecyclerItemViewAnimator());
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(onItemClickListener);


        }


    }








    PocTopicsAdapter.OnItemClickListener onItemClickListener = new PocTopicsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            //get section Id, start Topics activity with section id, get sub sections from db which has section_id = section_id you clicked :)
            Topic topic = topicsList.get(position);

            String TopicName = topic.getTopicName();
            String topicSectionName = topic.getSectionName();
            String topicSubSectionName = topic.getSubSectionName();
            String shortName = topic.getShortName();

            Integer TopicId = topic.getTopicId();
            Integer subSectionId = topic.getSubSectionId();

            Toast.makeText(PocTopicsActivity.this, TopicName + "\t \t" + TopicId, Toast.LENGTH_SHORT).show();




            String index = MobileLearning.doesTopicsFileExist(MobileLearning.POC_ROOT + shortName + File.separator);

            Log.i(TAG, "Index is " + index);
            if(index != null){//file found, open it

                Log.i(TAG, "File is in location!!!!!");
                startDynamicActivity(shortName + "/" + index, shortName, topicSectionName, topicSubSectionName);

            }else {//file not found, download and extract into directory then open it :)

                if(handler.checkInternetConnection())

                new DownloadZipFileTask(topicSectionName.trim(), topicSubSectionName.trim(), shortName).execute();

                else handler.showAlertDialog(PocTopicsActivity.this, "No internet connection", "You need an active internet to download this file");


            }

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.references_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.setClass(PocTopicsActivity.this, MainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
                return true;

            case R.id.download_all:

                showAlertDialog(true, "Download All", "Do you want to download all reference materials?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToDo Download all ZIP's method


                    new MassDownloadZipFileTask().execute();



                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        dialog.dismiss();

                    }
                });

                return true;
            case R.id.action_logout:
                logout();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }




    public void onBackPressed() {

        finish();
    }



    @Override

    public void onResume(){
        super.onResume();



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // do stuff

                    if(adapter != null) adapter.notifyDataSetChanged();

                }
            }, 2000);

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


      class DownloadZipFileTask extends AsyncTask<String, Integer, String> {

        private PowerManager.WakeLock mWakeLock;
        String TAG = "Download Zip Task";
        ProgressDialog mProgressDialog;

        String secName;
        String subSecName;
        String fileName;
        File zipFile;


        public DownloadZipFileTask(String sec, String subSec, String file_name) {
            this.secName = sec;
            this.subSecName = subSec;
            this.fileName = file_name;

            mProgressDialog = new ProgressDialog(PocTopicsActivity.this);
            mProgressDialog.setMessage("Downloading file, Please wait...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) PocTopicsActivity.this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @SuppressWarnings("resource")
        @Override
        protected String doInBackground(final String... reqUrl) {


            File downloadDirectory = new File(MobileLearning.POC_ROOT + File.separator + fileName + File.separator);
            zipFile = new File(downloadDirectory, fileName  + ".zip");

            Log.i(TAG, "Storage directory is  " + downloadDirectory);


            if (!downloadDirectory.exists()) {
                downloadDirectory.mkdirs();
            }

            if(zipFile.exists()) {
                Log.i(TAG, "File  " + zipFile + " already exists! Skipping download");

                return null;
            }




            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL("http://188.166.30.140/gfcare/storage/uploads/" + fileName + ".zip");

                Log.i(TAG, "The url is " + url.toString());

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                Log.i(TAG, "The file length is " + fileLength);
                // download the file

                input = connection.getInputStream();
                output = new FileOutputStream(zipFile);


                (PocTopicsActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage("Downloading: " + fileName + ".zip");
                    }
                });
                //
                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }


            } catch (Exception e) {
                 e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();

                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();


            final ProgressDialog thread_Progress = new ProgressDialog(PocTopicsActivity.this);
            thread_Progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            thread_Progress.setTitle("Please wait...");
            thread_Progress.setMessage("Unzipping " + fileName + ".zip");
            thread_Progress.setIndeterminate(true);
            thread_Progress.setCancelable(false);

            if (result != null){
                System.out.println(result);
                Toast.makeText(PocTopicsActivity.this,"Download error: "+result, Toast.LENGTH_LONG).show();
            } else{
                Log.i(TAG, "Unzipping " + fileName + ".zip");

                thread_Progress.show();

                Toast.makeText(PocTopicsActivity.this,"File downloaded", Toast.LENGTH_SHORT).show();

                final String file = MobileLearning.POC_ROOT + fileName + File.separator + fileName + ".zip";
                final String unzipLocation = MobileLearning.POC_ROOT + fileName + File.separator ;
                //unzip here and open file

                Log.i(TAG, "Zip file is " + file);
                Log.i(TAG, "Location of unzipped files will be at  " + unzipLocation);


                final Thread thread = new Thread() {
                    @Override
                    public void run() {

                        try

                        {
                            FileInputStream fin = new FileInputStream(file);
                            ZipInputStream zin = new ZipInputStream(fin);
                            ZipEntry ze = null;
                            while ((ze = zin.getNextEntry()) != null) {
                                Log.v("Decompress", "Unzipping " + ze.getName());


                                if (ze.isDirectory()) {
                                    _dirChecker(ze.getName(), unzipLocation);
                                } else {
                                    FileOutputStream fout = new FileOutputStream(unzipLocation + ze.getName());
                                    for (int c = zin.read(); c != -1; c = zin.read()) {
                                        fout.write(c);
                                    }

                                    zin.closeEntry();
                                    fout.close();
                                }

                            }
                            thread_Progress.dismiss();
                            zin.close();
                            //Open file via intent
                            Log.v("Unzipping", "COMPLETED SUCCESSFULLY!");


                            try {
                                if (zipFile.exists()) {
                                    zipFile.delete();

                                    Log.v("Redundant Zip file", "DELETED!");
                                }
                            }catch(Exception e){
                                e.printStackTrace();

                            }

                            String index = MobileLearning.doesTopicsFileExist(unzipLocation);

                            Log.i(TAG, "Index is " + index);
                            if(index != null){//file found, open it

                                Log.i(TAG, "After zipping, File is found in location!");
                                startDynamicActivity(fileName + "/" + index, fileName, secName, subSecName);



                            }else
                                Toast.makeText(PocTopicsActivity.this, "Oops! something terrible happened", Toast.LENGTH_SHORT).show();


                        } catch (
                                Exception e
                                )

                        {
                            thread_Progress.dismiss();
                            Log.e("Decompress", "unzip", e);
                           // Toast.makeText(PocTopicsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    };

                    thread.start();


            }

        }



    }



    class MassDownloadZipFileTask extends AsyncTask<String, Integer, String> {

        private PowerManager.WakeLock mWakeLock;
        String TAG = "Mass Download Zip Task";
        ProgressDialog mProgressDialog;

        String secName;
        String subSecName;
        String fileName;
        File zipFile;
        File downloadDirectory;
        Topic topic;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;


        MassDownloadZipFileTask( ) {

            mProgressDialog = new ProgressDialog(PocTopicsActivity.this);
            mProgressDialog.setTitle("Downloading Content, Please wait... " );
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            downloadDirectory = new File(MobileLearning.POC_ROOT);
            if(!downloadDirectory.exists()){
                downloadDirectory.mkdirs();
            }

        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            size = topicsList.size();

            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) PocTopicsActivity.this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            if(!mProgressDialog.isShowing())
                mProgressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);

        }

        @SuppressWarnings("resource")
        @Override
        protected String doInBackground(final String... reqUrl) {

            if(topicsList != null && size != 0){

                for(int i = 0; i < size; i++){
                    position = i;

                    topic = topicsList.get(i);

                    fileName = topic.getShortName();
                    secName = topic.getSectionName();
                    subSecName = topic.getSubSectionName();


            zipFile = new File(downloadDirectory, fileName + File.separator + fileName + ".zip");

            Log.i(TAG, "Storage directory is  " + downloadDirectory);


            if (!zipFile.exists()) {
                Log.i(TAG, "File  " + zipFile + " doesn't exists!  Downloading...");


                try {
                    URL url = new URL("http://188.166.30.140/gfcare/storage/uploads/" + fileName + ".zip");

                    Log.i(TAG, "The url is " + url.toString());

                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {

                        Log.i(TAG, "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage());

                        return null;
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    Log.i(TAG, "The file length is " + fileLength);
                    // download the file

                    input = connection.getInputStream();
                    output = new FileOutputStream(zipFile);


                    (PocTopicsActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setMessage("Downloading: " + fileName + ".zip");
                        }
                    });
                    //
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }

                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();


                    return "OK";

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;

                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();

                    } catch (IOException ignored) {
                        ignored.printStackTrace();
                    }

                    if (connection != null)
                        connection.disconnect();
                }

            }
        }
        return "OK";


        }else
            return null;


 }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();


            final ProgressDialog thread_Progress = new ProgressDialog(PocTopicsActivity.this);
            thread_Progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            thread_Progress.setTitle("Please wait...");
            thread_Progress.setMessage("Unzipping " + fileName + ".zip");
            thread_Progress.setIndeterminate(true);
            thread_Progress.setCancelable(false);

            if (result == null) {
                System.out.println(result);
                Toast.makeText(PocTopicsActivity.this, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else {

                if (result.equalsIgnoreCase("OK")) {

                Log.i(TAG, "Unzipping " + fileName + ".zip");

                thread_Progress.show();

                Toast.makeText(PocTopicsActivity.this, "File downloaded", Toast.LENGTH_SHORT).show();

                final String file = MobileLearning.POC_ROOT + fileName + File.separator + fileName + ".zip";
                final String unzipLocation = MobileLearning.POC_ROOT + fileName + File.separator;
                //unzip here and open file

                Log.i(TAG, "Zip file is " + file);
                Log.i(TAG, "Location of unzipped files will be at  " + unzipLocation);


                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try
                        {
                            FileInputStream fin = new FileInputStream(file);
                            ZipInputStream zin = new ZipInputStream(fin);
                            ZipEntry ze = null;
                            while ((ze = zin.getNextEntry()) != null) {
                                Log.v("Decompress", "Unzipping " + ze.getName());


                                if (ze.isDirectory()) {
                                    _dirChecker(ze.getName(), unzipLocation);
                                } else {
                                    FileOutputStream fout = new FileOutputStream(unzipLocation + ze.getName());
                                    for (int c = zin.read(); c != -1; c = zin.read()) {
                                        fout.write(c);
                                    }

                                    zin.closeEntry();
                                    fout.close();
                                }

                            }
                            thread_Progress.dismiss();
                            zin.close();
                            //Open file via intent
                            Log.v("Unzipping", "COMPLETED SUCCESSFULLY!");


                            try {
                                if (zipFile.exists()) {
                                    zipFile.delete();

                                    Log.v("Redundant Zip file", "DELETED!");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }


                        } catch (Exception e) {
                            thread_Progress.dismiss();
                            Log.e("Decompress", "unzip", e);
                            // Toast.makeText(PocTopicsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                };

                thread.start();


            }
        }

        }



    }



    private void _dirChecker(String dir, String _location) {
        File f = new File(_location + dir);

        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public void startDynamicActivity(String index, String shortName, String secName, String subSecName){

        Intent intent;
        intent = new Intent(PocTopicsActivity.this, POCDynamicActivity.class);
        intent.putExtra("sectionName", secName);
        intent.putExtra("subSectionName", subSecName);
        intent.putExtra("shortname", shortName);
        intent.putExtra("link",  index);

        Log.i(TAG, "Starting Dynamic Activity!");
        Log.i(TAG, "Name is " + shortName + "  Link is " + index);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);


    }


}
