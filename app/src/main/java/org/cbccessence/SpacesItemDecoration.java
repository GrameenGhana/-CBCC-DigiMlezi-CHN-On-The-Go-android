package org.cbccessence;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by aangjnr on 14/02/2017.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;

    public SpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = mSpace;

    outRect.bottom = mSpace;
    outRect.right = mSpace;
    outRect.left = mSpace;
    // Add top margin only for the first item to avoid double space between items

}
}