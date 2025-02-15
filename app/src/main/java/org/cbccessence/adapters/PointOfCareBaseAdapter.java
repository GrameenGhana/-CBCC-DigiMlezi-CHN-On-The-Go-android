package org.cbccessence.adapters;

import org.cbccessence.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PointOfCareBaseAdapter extends BaseAdapter{
	Context mContext;
	int[] imageIds;
	String[] category;
	public PointOfCareBaseAdapter(Context mContext, int[] imageIds, String[] category){
		this.mContext=mContext;
		this.imageIds=imageIds;
		this.category=category;
		
	}

	@Override
	public int getCount() {
		return category.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View list = null;
		if(convertView==null){
			  LayoutInflater inflater = (LayoutInflater) mContext
      		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      	  list = new View(mContext);
      	  list = inflater.inflate(R.layout.point_of_care_listview_single, null);
     
        } else {
      	  list = (View) convertView;
        }
		TextView category_text=(TextView) list.findViewById(R.id.textView_pocCategory);
		category_text.setText(category[position]);
		ImageView icon=(ImageView) list.findViewById(R.id.imageView_pocIcon);
		icon.setImageResource(imageIds[position]);
		
 		return list;
	}

}
