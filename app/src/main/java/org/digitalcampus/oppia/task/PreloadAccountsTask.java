/*
 * This file is part of OppiaMobile - https://digital-campus.org/
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

package org.digitalcampus.oppia.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import org.cbccessence.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.listener.PreloadAccountsListener;
import org.digitalcampus.oppia.model.CbccUser;
import org.digitalcampus.oppia.model.DownloadProgress;
import org.digitalcampus.oppia.utils.storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PreloadAccountsTask extends AsyncTask<Payload, DownloadProgress, Payload> {

    public final static String TAG = PreloadAccountsTask.class.getSimpleName();
    private Context ctx;
    private PreloadAccountsListener mListener;

    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_USERNAME_COLUMN = "username";
    private static final String CSV_PASSWORD_COLUMN = "password";
    private static final String CSV_APIKEY_COLUMN = "apikey";
    private static final String CSV_EMAIL_COLUMN = "email";
    private static final String CSV_FIRSTNAME_COLUMN = "first_name";
    private static final String CSV_LASTNAME_COLUMN = "last_name";

    public PreloadAccountsTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Payload doInBackground(Payload... params) {
        Payload payload = new Payload();
        payload.setResult(false);
        String csvPath = Storage.getStorageLocationRoot(ctx) + File.separator + SessionManager.ACCOUNTS_CSV_FILENAME;
        File csvAccounts = new File(csvPath);

        if (csvAccounts.exists()){
            BufferedReader reader = null;
            try {
                String line;
                reader = new BufferedReader(new FileReader(csvAccounts));
                DbHelper db = DbHelper.getInstance(ctx);
                int usernameColumn = -1, apikeyColumn = -1, passwordColumn = -1;
                int emailColumn = -1, firstNameColumn = -1, lastNameColumn = -1;
                int usersAdded = 0;

                line = reader.readLine(); //We read first line to get headers
                if (line != null){
                    String[] headerData = line.split(CSV_SEPARATOR);
                    for (int i=0; i<headerData.length; i++){
                        if (CSV_USERNAME_COLUMN.equals(headerData[i]))
                            usernameColumn = i;
                        else if (CSV_APIKEY_COLUMN.equals(headerData[i]))
                            apikeyColumn = i;
                        else if (CSV_PASSWORD_COLUMN.equals(headerData[i]))
                            passwordColumn = i;
                        else if (CSV_EMAIL_COLUMN.equals(headerData[i]))
                            emailColumn = i;
                        else if (CSV_FIRSTNAME_COLUMN.equals(headerData[i]))
                            firstNameColumn = i;
                        else if (CSV_LASTNAME_COLUMN.equals(headerData[i]))
                            lastNameColumn = i;
                    }
                }

                if (usernameColumn == -1 | apikeyColumn == -1 | passwordColumn == -1){
                    payload.setResult(true);
                    payload.setResultResponse(ctx.getString(R.string.error_preloading_accounts));
                }

                //Each line has to be at least the max column of any of the mandatory fields
                int minCSVColumns = Math.max(Math.max(usernameColumn, apikeyColumn), passwordColumn) + 1;
                Log.d(TAG, "Min columns:" + minCSVColumns);
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(CSV_SEPARATOR);

                    if (rowData.length < minCSVColumns){
                        Log.d(TAG, "Bad csv line, ignoring: " + line);
                        continue;
                    }
                    CbccUser csvCbccUser = new CbccUser();
                    csvCbccUser.setUsername(rowData[usernameColumn]);
                    csvCbccUser.setPassword(rowData[passwordColumn]);
                    csvCbccUser.setApiKey(rowData[apikeyColumn]);

                    //Then we set each optional field
                    if ((emailColumn != -1) && (rowData.length > emailColumn))
                        csvCbccUser.setEmail(rowData[emailColumn]);
                    if ((firstNameColumn != -1) && (rowData.length > firstNameColumn))
                        csvCbccUser.setFirstname(rowData[firstNameColumn]);
                    if ((lastNameColumn != -1) && (rowData.length > lastNameColumn))
                        csvCbccUser.setLastname(rowData[lastNameColumn]);

                    db.addOrUpdateUser(csvCbccUser);
                    usersAdded++;
                }

                if (usersAdded>0){
                    payload.setResult(true);
                    payload.setResultResponse(ctx.getString(R.string.info_startup_preloaded_accounts, usersAdded));
                }
                Log.d(TAG, usersAdded + " users added");
            }
            catch (IOException ex) {
                 ex.printStackTrace();
                payload.setResult(true);
                payload.setResultResponse(ctx.getString(R.string.error_preloading_accounts));
            }
            finally {
                try {
                    if (reader!=null) reader.close();
                    boolean deleted = csvAccounts.delete();
                    Log.d(TAG, "CSV file " + (deleted?"":"not ") + "deleted");
                }
                catch (IOException e) {
                 }
            }
        }

        return payload;
    }

    @Override
    protected void onPostExecute(Payload p) {
        synchronized (this) {
            if (mListener != null) {
                mListener.onPreloadAccountsComplete(p);
            }
        }
    }

    public void setPreloadAccountsListener(PreloadAccountsListener listener) {
        synchronized (this) {
            mListener = listener;
        }
    }
}
