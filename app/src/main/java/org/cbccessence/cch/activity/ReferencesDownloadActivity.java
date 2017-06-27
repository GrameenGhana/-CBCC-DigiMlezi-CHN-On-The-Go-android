package org.cbccessence.cch.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import org.cbccessence.R;
import org.cbccessence.activity.MainScreenActivity;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.cbccessence.models.LcReference;
import org.cbccessence.utilities.HttpHandler;
import org.cbccessence.utilities.PlaceHolder;
import org.cbccessence.adapters.LcReferencesAdapter;
import org.cbccessence.poc.BaseActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReferencesDownloadActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener {

    String TAG = ReferencesDownloadActivity.class.getSimpleName();
    ProgressDialog pDialog;
    private File myDirectory;
    private SharedPreferences prefs;
    PDFView pdfView;
    static LcReferencesAdapter adapter;
    private String token;
    private List<LcReference> references;
    private RecyclerView mRecycler;
    HttpHandler sh;
    Integer pageNumber = 0;
    String pdfFileName;
    DbHelper databaseHelper;
    long pdf_start_time;
    long pdf_stop_time;
    String pdf_fileName = "";
    int position = 0;
    int size = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_references_download);
        getSupportActionBar().setTitle("Learning Center");
        getSupportActionBar().setSubtitle("References");
        databaseHelper =   DbHelper.getInstance(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(ReferencesDownloadActivity.this);


        token = prefs.getString("token", null);
        references = new ArrayList<LcReference>();

        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecycler = (RecyclerView) findViewById(R.id.lc_references_recyclerView);
        pdfView = (PDFView) findViewById(R.id.ref_pdfView);


        mRecycler.setLayoutManager(mStaggeredLayoutManager);
        mRecycler.setHasFixedSize(true);
        GridSpacesItemDecoration decoration = new GridSpacesItemDecoration(24);
        mRecycler.addItemDecoration(decoration);

        sh = new HttpHandler(ReferencesDownloadActivity.this);

        new GetContent().execute();

    }



    public void onBackPressed() {


        if(pdfView.getVisibility() == View.VISIBLE) {
            pdfView.setVisibility(View.GONE);
            pdf_stop_time = System.currentTimeMillis();


            try {

                JSONObject log = new JSONObject();
                log.put("section", "Learning Center");
                log.put("sub_section", "References" );
                log.put("topic", pdf_fileName);
                log.put("time_taken", pdf_stop_time - pdf_start_time );

                if(databaseHelper.addLog(log_type, log.toString()))
                    Log.i(TAG, "Log added with data\t" + log);


            }catch(Exception e){
                e.printStackTrace();
            }


        }
        else
        finish();
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
    public void onStop() {
        super.onStop();
        end_time = System.currentTimeMillis();
        try {

            JSONObject log = new JSONObject();
            log.put("name", "References");
            log.put("start_time", start_time);
            log.put("end_time", end_time);
            log.put("time_taken", end_time - start_time);

            if (databaseHelper.addLog(log_type, log.toString()))
                Log.i(TAG, "Log added with data\t" + log);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



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
         /*   case R.id.action_home:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.setClass(ReferencesDownloadActivity.this, MainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
                return true;*/

            case R.id.download_all:

                showAlertDialog(true, "Download All", "Do you want to download all reference materials?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToDo Download all PDF's method


                        new MassDownloadReferencesTask(ReferencesDownloadActivity.this).execute();




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


    ////////////////////////////////////////////////////////////////////////////////////////////////



    public class GridSpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public GridSpacesItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


            //outRect.bottom = mSpace/2;

            // Add top margin only for the first item to avoid double space between items

            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = mSpace;
                outRect.top = mSpace;
                outRect.right = mSpace;


            }else{

                outRect.top = mSpace;
                outRect.right = mSpace;

            }


        }

    }












    private class GetContent extends AsyncTask<String, String, String> {

        Intent intent = new Intent(ReferencesDownloadActivity.this, ReferencesDownloadActivity.class);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ReferencesDownloadActivity.this);
            pDialog.setTitle("Retrieving content");
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            if (token != null) {

                String url = "http://188.166.30.140/gfcare/api/chn-on-the-go/content/references";
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
                            JSONArray contentArray = new JSONArray(jsonStringFromServer);
                            Log.i(TAG, "Content Array is " + contentArray);

                            for (int i =0; i < contentArray.length(); i++){
                                JSONObject reference = contentArray.getJSONObject(i);

                                Integer id = reference.getInt("id");
                                Integer team_id = reference.getInt("team_id");
                                Integer modifier = reference.getInt("modified_by");
                                String  name = reference.getString("reference_desc");
                                String short_name = reference.getString("shortname");
                                String file_url = reference.getString("file_url");
                                String file_size = reference.getString("size");
                                String date_created = reference.getString("created_at");
                                String date_updated = reference.getString("updated_at");

                                LcReference _reference = new LcReference(id, team_id, modifier, name,
                                        short_name, file_url, file_size, date_created, date_updated);

                                    references.add(_reference);


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

            } else {
                //Login
                Log.i(TAG, "token is empty");
                result = "false";

            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                final PlaceHolder holder = new PlaceHolder(ReferencesDownloadActivity.this, intent);
                Log.i(TAG, "" + result);

                // Dismiss the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();
                if (result.equals("true")) {


                    if (references != null) {

                        Log.i(TAG, "sectionsList Array is not null with size   " + references.size());

                        adapter = new LcReferencesAdapter(ReferencesDownloadActivity.this, references);
                        mRecycler.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
                        adapter.setOnItemClickListener(onItemClickListener);


                    }


                } else if (result.equals("false")) {

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ReferencesDownloadActivity.this, R.style.AppAlertDialog);
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
        catch (Exception e){

            e.printStackTrace();

        }
        }

    }


    LcReferencesAdapter.OnItemClickListener onItemClickListener = new LcReferencesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {


            //get section Id, start subsections activity with section id, get sub sections from db which has section_id = section_id you clicked :)
            LcReference reference = references.get(position);

            String file_name = reference.getReferenceName();
            String file_size = reference.getFileSize();
            String file_short_name = reference.getShortName();

            pdfFileName = file_name;

            String file_url = "http://188.166.30.140/gfcare" + reference.getFileUrl();

           // Toast.makeText(ReferencesDownloadActivity.this, file_short_name + "\t" + file_size +
             //       "\t" +   file_url, Toast.LENGTH_SHORT).show();



            Log.i(TAG, file_name + "\t" + file_size + "\t" +   file_url );

            //openPdfInWebView(file_url);
           // startWebView("http://188.166.30.140/gfcare/" + file_url);

           // new DownloadReferencesTask(ReferencesDownloadActivity.this, file_url, file_short_name).execute();

            if(MobileLearning.doesFileExist(file_short_name)) {

                Intent intent = new Intent(ReferencesDownloadActivity.this, VisualAidsPdfView.class);
                intent.putExtra("pdf_location", file_short_name);
                startActivity(intent);

               // displayFromAsset(file_short_name);

                Log.i(TAG, "File exists!");

            }

            else {
                Log.i(TAG, "File doesn't exist! Initializing download");
                new DownloadReferencesTask(ReferencesDownloadActivity.this, file_url, file_name, file_short_name).execute();
            }

        }
    };



    public void displayFromAsset(String assetFileName) {

         pdf_start_time = System.currentTimeMillis();


        pdfView.setVisibility(View.VISIBLE);

        File file = new File(MobileLearning.REFERENCES_ROOT + assetFileName + ".pdf");
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();










    }



    public void notifyAdapter(){
        if (adapter != null)
        adapter.notifyDataSetChanged();


    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }







//Download task/////////////////////////////////////////////////////////////////////////////////////


    class DownloadReferencesTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private DbHelper db;
        private String file_url;
        private String file_short_name;
        String file_name;
        String TAG = "Download Ref Task";
        ProgressDialog mProgressDialog;


        private int i;

        DownloadReferencesTask(Context context, String file_url, String file_name, String file_short_name) {
            this.context = context;
            this.file_url = file_url;
            this.file_short_name = file_short_name;
            this.file_name = file_name;


            //instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle("Downloading document, Please wait... ");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
        protected String doInBackground(String... reqUrl) {


            File downloadDirectory = new File(MobileLearning.REFERENCES_ROOT);

            Log.i(TAG, "Storage directory is  " + downloadDirectory);
            i = 0;

            if (!downloadDirectory.exists()) {
                downloadDirectory.mkdirs();
            }


            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(file_url);

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
                output = new FileOutputStream(downloadDirectory + "/" + file_short_name + ".pdf");


                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage("Downloading: " + file_name + ".pdf  ");

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


            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();

                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            mWakeLock.release();
            mProgressDialog.dismiss();

            if (result != null) {
                System.out.println(result);
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                notifyAdapter();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(ReferencesDownloadActivity.this, VisualAidsPdfView.class);
                        intent.putExtra("pdf_location", file_short_name);
                        startActivity(intent);

                    }
                }, 1000);
            }


        }
    }





    //Mass Download task/////////////////////////////////////////////////////////////////////////////////////


    class MassDownloadReferencesTask extends AsyncTask<String, Integer, String> {

        private  Context context;
        private PowerManager.WakeLock mWakeLock;
        private DbHelper db;
        String TAG = "Download Ref Task";
        ProgressDialog mProgressDialog;
        File downloadDirectory;
        String file_name  ;
        String file_size ;
        String file_short_name ;

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        LcReference reference;



        MassDownloadReferencesTask(Context context) {
            this.context = context;



//instantiate it within the onCreate method

            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle("Downloading Content, Please wait... " );
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
            downloadDirectory = new File(MobileLearning.REFERENCES_ROOT);
            if(!downloadDirectory.exists()){
                downloadDirectory.mkdirs();
            }


        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download


            size = references.size();



                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
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
        protected String doInBackground(String... reqUrl) {

            if(references != null && size != 0){

                for(int i = 0; i < size; i++){
                    position = i;

                    reference = references.get(i);

                     file_name = reference.getReferenceName();
                     file_size = reference.getFileSize();
                     file_short_name = reference.getShortName();

                    String file_url = "http://188.166.30.140/gfcare" + reference.getFileUrl();

                    Log.i(TAG, "Downloading file at " + position  + " " + file_name + " " + file_size + " " +   file_url );
                    if(!MobileLearning.doesFileExist(file_short_name)) {
                        Log.i(TAG, "File doesn't exist! Initializing download");
                    Log.i(TAG, "Storage directory is  " + downloadDirectory);



            try {
                URL url = new URL(file_url);

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
                output = new FileOutputStream(downloadDirectory + "/" + file_short_name + ".pdf");


                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setTitle("Downloading "  + (position + 1) + " of " + size);
                        mProgressDialog.setMessage("Downloading: " + file_name + ".pdf  ");

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
                return null;

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();

                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }



                    }
                }
                return "OK";
            }
            else
                return null;

        }

        @Override
        protected void onPostExecute(String result) {

                mWakeLock.release();
                mProgressDialog.dismiss();

                if (result == null){
                    System.out.println(result);
                    Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
                    notifyAdapter();

                } else {
                    if (result.equalsIgnoreCase("OK")) {
                        notifyAdapter();
                        Toast.makeText(context, "Mass download complete", Toast.LENGTH_LONG).show();


                    }else {



                    }



                }


        }



    }

}
