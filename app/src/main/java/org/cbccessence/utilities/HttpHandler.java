package org.cbccessence.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.cbccessence.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;


public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();
    final AppCompatActivity context;

    public HttpHandler(AppCompatActivity context) {
        this.context = context;

    }

    public String makeServiceCall(String reqUrl, String token) {
        String response = null;

        if (!checkInternetConnection()){


            context.runOnUiThread(new Runnable() {
                public void run() {
                    showAlertDialog(context, "No internet connection",  "Please connect to the internet and try again!");
                }
            });


        }else


            try {
                URL url = new URL(reqUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestMethod("GET");
                conn.connect();

                // read the response

                InputStream is = null;
                response = convertStreamToString_2(conn, is);







            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        return response;
    }



    public String makePostRequest(String reqUrl, String json) {
        String response = null;

        if (!checkInternetConnection()){
            showAlertDialog(context, "No internet connection",  "Please connect to the internet and try again!");


        }else

            try {
                URL url = new URL(reqUrl);



                //Connect
                HttpURLConnection httpcon = (HttpURLConnection) (url.openConnection());
                httpcon.setDoOutput(true);
                httpcon.setRequestProperty("Content-Type", "application/json");
                httpcon.setRequestProperty("Accept", "application/json");
                httpcon.setRequestMethod("POST");
                httpcon.setConnectTimeout(30000);
                httpcon.connect();

                //Write
                OutputStream os = httpcon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json.toString());
                writer.close();
                os.close();


                InputStream is = null;
                response = convertStreamToString_2(httpcon, is);




            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        return response;
    }


    public HttpURLConnection sendRegistrationDetailsToServer(String registrationUrl,  Map<String, String> params) {
        String response = null;
        HttpURLConnection httpcon = null;

        if (!checkInternetConnection()){
            showAlertDialog(context, "No internet connection",  "Please connect to the internet and try again!");


        }else

            try {
                URL url = new URL(registrationUrl);

                //Connect
                httpcon = (HttpURLConnection) (url.openConnection());
                httpcon.setDoOutput(true);
                httpcon.setDoInput(true);
                httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpcon.setRequestMethod("POST");
                httpcon.setConnectTimeout(20000);


                Uri builder = buildURI(registrationUrl, params);

                String query = builder.getEncodedQuery();

                httpcon.setFixedLengthStreamingMode(query.getBytes().length);


                httpcon.connect();

                //Write
                OutputStream os = httpcon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.close();
                os.close();


                InputStream is = null;
                response = convertStreamToString_2(httpcon, is);




            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        return httpcon;
    }



    private Uri buildURI(String url, Map<String, String> params) {

        // build url with parameters.
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }


    public String sendPostProjectSelection(String reqUrl) {
        String response = null;

        if (!checkInternetConnection()){
            showAlertDialog(context, "No internet connection",  "Please connect to the internet and try again!");


        }else

            try {
                URL url = new URL(reqUrl);
                //Connect
                HttpURLConnection httpcon = (HttpURLConnection) (url.openConnection());
                /*httpcon.setRequestProperty("Accept", "application/json");
                httpcon.setRequestMethod("POST");*/


                httpcon.connect();


                InputStream is = null;
                response = convertStreamToString_2(httpcon, is);

            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        return response;
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    private String convertStreamToString_2(HttpURLConnection httpcon, InputStream is) {
        String response = null;
        try{

            try {
                is = httpcon.getInputStream();
            } catch (IOException ioe) {
                if (httpcon instanceof HttpURLConnection) {
                    HttpURLConnection httpConn = (HttpURLConnection) httpcon;
                    int statusCode = httpConn.getResponseCode();
                    if (statusCode != 200) {
                        is = httpConn.getErrorStream();
                    } else is = httpcon.getInputStream();
                }
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));


            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            int responseCode = httpcon.getResponseCode();
            String responseMessage = String.valueOf(httpcon.getResponseMessage());

            Log.i(TAG, String.valueOf(responseCode) + "\t" + responseMessage);



            switch (responseCode) {
                case 401: // unauthorised
                    response = "no access";

                    break;
                case 422: // user not found
                    response = "invalid";


                    break;
                case 400: // logged in

                    response = "invalid_token";
                    break;
                case 200: // logged in

                    response = sb.toString();
                    break;

                default:
                    response = "null";
            }

            try {
                br.close();
            } catch (Exception e){


            }
            Log.i(TAG, response);




        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }



    public boolean checkInternetConnection() {


        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;

        } else {
            return false;
        }

    }


    public void showAlertDialog(final AppCompatActivity context, String title, String message){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppDialog);
        alertDialogBuilder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        dialogInterface.dismiss();
                        context.finish();


                    }
                }).create();


        alertDialogBuilder.show();
    }

    public void showAlertDialog_2(final AppCompatActivity context, String title, String message){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppDialog);
        alertDialogBuilder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        dialogInterface.dismiss();


                    }
                }).create();


        alertDialogBuilder.show();
    }


    public void showTokenExpiredAlert(final AppCompatActivity ctx){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx.getApplicationContext(), R.style.AppAlertDialog);
        builder.setCancelable(false);
        builder.setTitle("Session has expired!");
        builder.setMessage("Please login again");
        builder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Show edit text for password and attempt login to generate new token


            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog and inflate empty view, if user clicks on login, show edittext and attempt login again
                dialog.cancel();
                dialog.dismiss();

            }
        });
        builder.show();




    }





}