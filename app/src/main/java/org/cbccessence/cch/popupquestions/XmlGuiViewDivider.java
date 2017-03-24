/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package org.cbccessence.cch.popupquestions;

import org.digitalcampus.mobile.learningGF.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class XmlGuiViewDivider extends LinearLayout {
	View label;
	
	public XmlGuiViewDivider(final Context context) {
		super(context);
		  LinearLayout ll = new LinearLayout(context);
          ll.setOrientation(LinearLayout.VERTICAL);
          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        		     LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT );
          ll.setLayoutParams(layoutParams);
		
			 label = new View(context);
			 label.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,1));
			label.setBackgroundColor(getResources().getColor(R.color.background_dark));
		    
		     ll.addView(label,layoutParams);
	      
		 this.addView(ll);
		
	}

	public XmlGuiViewDivider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

}
