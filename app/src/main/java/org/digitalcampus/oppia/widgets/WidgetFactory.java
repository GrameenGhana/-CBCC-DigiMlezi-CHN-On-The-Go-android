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

package org.digitalcampus.oppia.widgets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.cbccessence.R;
import org.digitalcampus.oppia.model.Activity;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.utils.mediaplayer.VideoPlayerActivity;
import org.digitalcampus.oppia.utils.storage.FileUtils;
import org.digitalcampus.oppia.utils.storage.Storage;

import java.util.HashMap;

public abstract class WidgetFactory extends Fragment {
	
	public final static String TAG = WidgetFactory.class.getSimpleName();
	protected Activity activity = null;
	protected Course course = null;
	protected SharedPreferences prefs;
	protected boolean isBaseline = false;
    protected boolean readAloud = false;

	protected long startTime = 0;
    protected long spentTime = 0;
	protected boolean currentTimeAccounted = false;
	
	public abstract boolean getActivityCompleted();
	public abstract void saveTracker();
	
	public abstract String getContentToRead();
	public abstract HashMap<String,Object> getWidgetConfig();
	public abstract void setWidgetConfig(HashMap<String,Object> config);
	
	public void setReadAloud(boolean readAloud){
		this.readAloud = readAloud;
	}
	
	protected String getDigest() {
		return activity.getDigest();
	}

	public void setIsBaseline(boolean isBaseline) {
		this.isBaseline = isBaseline;
	}
	
	protected void setStartTime(long startTime){
		this.startTime = (startTime != 0) ? startTime : (System.currentTimeMillis()/1000);
        currentTimeAccounted = false;
	}
	
	public long getStartTime(){
		return (startTime != 0) ? startTime : (System.currentTimeMillis()/1000);
	}

    private void addSpentTime(){
        long start = getStartTime();
        long now = System.currentTimeMillis()/1000;

        long spent = now - start;
        spentTime += spent;
        currentTimeAccounted = true;
    }

    public void resetTimeTracking(){
        spentTime = 0;
        startTime = System.currentTimeMillis() / 1000;
        currentTimeAccounted = false;
    }

    public void resumeTimeTracking(){
        startTime = System.currentTimeMillis() / 1000;
        currentTimeAccounted = false;
    }

    public void pauseTimeTracking(){
        addSpentTime();
    }

    public long getSpentTime(){
        if (!currentTimeAccounted){
            addSpentTime();
        }
        return this.spentTime;
    }

    protected void startMediaPlayerWithFile(String mediaFileName){
        // check media file exists
        boolean exists = Storage.mediaFileExists(getActivity(), mediaFileName);
        if (!exists) {
            Toast.makeText(getActivity(),
                    getActivity().getString(R.string.error_media_not_found, mediaFileName),
                    Toast.LENGTH_LONG).show();
            return;
        }

        String mimeType = FileUtils.getMimeType(Storage.getMediaPath(getActivity()) + mediaFileName);
        if (!FileUtils.isSupportedMediafileType(mimeType)) {
            Toast.makeText(getActivity(),
                    getActivity().getString(R.string.error_media_unsupported, mediaFileName),
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        Bundle tb = new Bundle();
        tb.putSerializable(VideoPlayerActivity.MEDIA_TAG, mediaFileName);
        tb.putSerializable(Activity.TAG, activity);
        tb.putSerializable(Course.TAG, course);
        intent.putExtras(tb);
        getActivity().startActivity(intent);
    }
}
