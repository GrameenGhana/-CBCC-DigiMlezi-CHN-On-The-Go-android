package org.cbccessence.cch.utils;

import org.cbccessence.R;



import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.text.Layout;

public class MaterialSpinner extends Spinner implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = MaterialSpinner.class.getSimpleName();


    //Paint objects
    private Paint paint;
    private TextPaint textPaint;
    private StaticLayout staticLayout;


    private Path selectorPath;
    private Point[] selectorPoints;

    //Inner padding = "Normal" android padding
    private int innerPaddingLeft;
    private int innerPaddingRight;
    private int innerPaddingTop;
    private int innerPaddingBottom;

    //Private padding to add space for FloatingLabel and Underline
    private int extraPaddingTop;
    private int extraPaddingBottom;

    //@see dimens.xml
    private int underlineTopSpacing;
    private int underlineBottomSpacing;
    private int errorLabelSpacing;
    private int floatingLabelTopSpacing;
    private int floatingLabelBottomSpacing;
    private int floatingLabelInsideSpacing;
    private int rightLeftSpinnerPadding;

    //Properties about Error Label
    private int lastPosition;
    private ObjectAnimator errorLabelAnimator;
    private int errorLabelPosX;
    private boolean errorAnimationReverse;
    private int minNbErrorLines;
    private float currentNbErrorLines;


    //Properties about Floating Label (
    private float floatingLabelPercent;
    private ObjectAnimator floatingLabelAnimator;
    private boolean isSelected;
    private boolean floatingLabelVisible;
    private int baseAlpha ;


    //AttributeSet
    private int baseColor;
    private int highlightColor;
    private int errorColor;
    private CharSequence error;
    private CharSequence hint;
    private CharSequence floatingLabelText;
    private boolean multiline;
    private Typeface typeface;
    private boolean alignLabels ;
    private int thickness ;


    /*
    * **********************************************************************************
    * CONSTRUCTORS
    * **********************************************************************************
    */

    public MaterialSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public MaterialSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /*
    * **********************************************************************************
    * INITIALISATION METHODS
    * **********************************************************************************
    */

    private void init(Context context, AttributeSet attrs) {


        initAttributes(context, attrs);
        initPaintObjects();
        initDimensions();
        initPadding();
        initFloatingLabelAnimator();
        initOnItemSelectedListener();

        //Erase the drawable selector not to be affected by new size (extra paddings)
        setBackgroundResource(R.drawable.my_background);


    }

    private void initAttributes(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.colorControlNormal, R.attr.colorAccent});
        int defaultBaseColor = a.getColor(0, 0);
        int defaultHighlightColor = a.getColor(1, 0);
        int defaultErrorColor = Color.parseColor("#E7492E");
        a.recycle();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaterialSpinner);
        baseColor = array.getColor(R.styleable.MaterialSpinner_ms_baseColor, defaultBaseColor);
        highlightColor = array.getColor(R.styleable.MaterialSpinner_ms_highlightColor, defaultHighlightColor);
        errorColor = array.getColor(R.styleable.MaterialSpinner_ms_errorColor, defaultErrorColor);
        error = array.getString(R.styleable.MaterialSpinner_ms_error);
        hint = array.getString(R.styleable.MaterialSpinner_ms_hint);
        floatingLabelText = array.getString(R.styleable.MaterialSpinner_ms_floatingLabelText);
        multiline = array.getBoolean(R.styleable.MaterialSpinner_ms_multiline, true);
        minNbErrorLines = array.getInt(R.styleable.MaterialSpinner_ms_nbErrorLines, 1);
        alignLabels = array.getBoolean(R.styleable.MaterialSpinner_ms_alignLabels,true);
        thickness = array.getInteger(R.styleable.MaterialSpinner_ms_thickness,1);

        String typefacePath = array.getString(R.styleable.MaterialSpinner_ms_typeface);
        if (typefacePath != null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), typefacePath);
        }

        array.recycle();

        floatingLabelPercent = 0f;
        errorLabelPosX = 0;
        isSelected = false;
        floatingLabelVisible = false;
        lastPosition = -1;
        currentNbErrorLines = minNbErrorLines;

    }

    private void initPaintObjects() {

        int labelTextSize = getResources().getDimensionPixelSize(R.dimen.label_text_size);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(labelTextSize);
        if (typeface != null) {
            textPaint.setTypeface(typeface);
        }
        textPaint.setColor(baseColor);
        baseAlpha = textPaint.getAlpha();

        selectorPath = new Path();
        selectorPath.setFillType(Path.FillType.EVEN_ODD);

        selectorPoints = new Point[3];
        for (int i = 0; i < 3; i++) {
            selectorPoints[i] = new Point();
        }
    }

    private void initPadding() {

        innerPaddingTop = getPaddingTop();
        innerPaddingLeft = getPaddingLeft();
        innerPaddingRight = getPaddingRight();
        innerPaddingBottom = getPaddingBottom();

        extraPaddingTop = floatingLabelTopSpacing + floatingLabelInsideSpacing + floatingLabelBottomSpacing;
        updateBottomPadding();

    }

    private void updateBottomPadding() {
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
        extraPaddingBottom = (int) ((textMetrics.descent - textMetrics.ascent) * currentNbErrorLines) + underlineTopSpacing + underlineBottomSpacing;
        setPadding();
    }

    private void initDimensions() {
        underlineTopSpacing = getResources().getDimensionPixelSize(R.dimen.underline_top_spacing);
        underlineBottomSpacing = getResources().getDimensionPixelSize(R.dimen.underline_bottom_spacing);
        floatingLabelTopSpacing = getResources().getDimensionPixelSize(R.dimen.floating_label_top_spacing);
        floatingLabelBottomSpacing = getResources().getDimensionPixelSize(R.dimen.floating_label_bottom_spacing);
        rightLeftSpinnerPadding = alignLabels? getResources().getDimensionPixelSize(R.dimen.right_left_spinner_padding) : 0 ;
        floatingLabelInsideSpacing = getResources().getDimensionPixelSize(R.dimen.floating_label_inside_spacing);
        errorLabelSpacing = getResources().getDimensionPixelSize(R.dimen.error_label_spacing);

    }

    private void initOnItemSelectedListener() {
        setOnItemSelectedListener(null);
    }

    /*
    * **********************************************************************************
    * ANIMATION METHODS
    * **********************************************************************************
    */

    private void initFloatingLabelAnimator() {
        if (floatingLabelAnimator == null) {
            floatingLabelAnimator = ObjectAnimator.ofFloat(this, "floatingLabelPercent", 0f, 1f);
            floatingLabelAnimator.addUpdateListener(this);
        }
    }

    private void showFloatingLabel() {
        if (floatingLabelAnimator != null) {
            floatingLabelVisible = true;
            if (floatingLabelAnimator.isRunning()) {
                floatingLabelAnimator.reverse();
            } else {
                floatingLabelAnimator.start();
            }
        }
    }

    private void hideFloatingLabel() {
        if (floatingLabelAnimator != null) {
            floatingLabelVisible = false ;
            floatingLabelAnimator.reverse();
        }
    }

    private void startErrorScrollingAnimator() {

        int textWidth = Math.round(textPaint.measureText(error.toString()));
        if (errorLabelAnimator == null) {
            errorLabelAnimator = ObjectAnimator.ofInt(this, "errorLabelPosX", 0, textWidth + getWidth() / 2);
            errorLabelAnimator.setStartDelay(1000);
            errorLabelAnimator.setInterpolator(new LinearInterpolator());
            errorLabelAnimator.setDuration(150 * error.length());
            errorLabelAnimator.addUpdateListener(this);
            errorLabelAnimator.setRepeatCount(ValueAnimator.INFINITE);
        } else {
            errorLabelAnimator.setIntValues(0, textWidth + getWidth() / 2);
        }
        errorLabelAnimator.start();
    }


    private void startErrorMultilineAnimator(float destLines) {
        if (errorLabelAnimator == null) {
            errorLabelAnimator = ObjectAnimator.ofFloat(this, "currentNbErrorLines", destLines);

        } else {
            errorLabelAnimator.setFloatValues(destLines);
        }
        errorLabelAnimator.start();
    }


    /*
     * **********************************************************************************
     * UTILITY METHODS
     * **********************************************************************************
    */

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(px);
    }

    private void setPadding() {
        super.setPadding(innerPaddingLeft, innerPaddingTop + extraPaddingTop, innerPaddingRight, innerPaddingBottom + extraPaddingBottom);
    }

    private boolean needScrollingAnimation() {
        if (error != null) {
            float screenWidth = getWidth() - rightLeftSpinnerPadding;
            float errorTextWidth = textPaint.measureText(error.toString(), 0, error.length());
            return errorTextWidth > screenWidth ? true : false;
        }
        return false;
    }

    private int prepareBottomPadding() {

        int targetNbLines = minNbErrorLines;
        if (error != null) {
            staticLayout = new StaticLayout(error, textPaint, getWidth() - getPaddingRight() - getPaddingLeft(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            int nbErrorLines = staticLayout.getLineCount();
            targetNbLines = Math.max(minNbErrorLines, nbErrorLines);
        }
        return targetNbLines;
    }


    /*
     * **********************************************************************************
     * DRAWING METHODS
     * **********************************************************************************
    */


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int startX = 0;
        int endX = getWidth();
        int lineHeight = dpToPx(thickness);

        int startYLine = getHeight() - getPaddingBottom() + underlineTopSpacing;
        int startYErrorLabel = startYLine + errorLabelSpacing + lineHeight ;
        int startYFloatingLabel = (int) (getPaddingTop() - floatingLabelPercent * floatingLabelBottomSpacing);

        if (error != null) {
            paint.setColor(errorColor);
            textPaint.setColor(errorColor);
            //Error Label Drawing
            if (multiline) {
                canvas.save();
                canvas.translate(startX+rightLeftSpinnerPadding, startYErrorLabel - errorLabelSpacing);
                staticLayout.draw(canvas);
                canvas.restore();

            } else {
                //scrolling
                canvas.drawText(error.toString(), startX + rightLeftSpinnerPadding - errorLabelPosX, startYErrorLabel, textPaint);
                canvas.save();
                canvas.translate(textPaint.measureText(error.toString()) + getWidth() / 2, 0);
                canvas.drawText(error.toString(), startX + rightLeftSpinnerPadding - errorLabelPosX, startYErrorLabel, textPaint);
                canvas.restore();
            }

        } else {
            if (isSelected) {
                paint.setColor(highlightColor);
            } else {
                paint.setColor(baseColor);
            }
        }

        // Underline Drawing
        canvas.drawRect(startX, startYLine, endX, startYLine + lineHeight, paint);

        //Floating Label Drawing
        if (hint != null || floatingLabelText != null) {
            if (isSelected) {
                textPaint.setColor(highlightColor);
            } else {
                textPaint.setColor(baseColor);
            }
            if(floatingLabelAnimator.isRunning() || !floatingLabelVisible) {
                textPaint.setAlpha((int) ((0.8 * floatingLabelPercent + 0.2) * baseAlpha * floatingLabelPercent));
            }
            String textToDraw = floatingLabelText != null ? floatingLabelText.toString() : hint.toString();
            canvas.drawText(textToDraw, startX + rightLeftSpinnerPadding, startYFloatingLabel, textPaint);
        }

        drawSelector(canvas, getWidth() - rightLeftSpinnerPadding, getPaddingTop() + dpToPx(8));


    }

    private void drawSelector(Canvas canvas, int posX, int posY) {

        if (isSelected) {
            paint.setColor(highlightColor);
        } else {
            paint.setColor(baseColor);
        }

        Point point1 = selectorPoints[0];
        Point point2 = selectorPoints[1];
        Point point3 = selectorPoints[2];

        point1.set(posX, posY);
        point2.set(posX - 18, posY);
        point3.set(posX - 9, posY + 9);

        selectorPath.reset();
        selectorPath.moveTo(point1.x, point1.y);
        selectorPath.lineTo(point2.x, point2.y);
        selectorPath.lineTo(point3.x, point3.y);
        selectorPath.close();
        canvas.drawPath(selectorPath, paint);

    }



    /*
     * **********************************************************************************
     * LISTENER METHODS
     * **********************************************************************************
    */


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                isSelected = true;
                break;
            }
            case MotionEvent.ACTION_UP: {
                isSelected = false;
                break;
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }


    @Override
    public void setOnItemSelectedListener(final OnItemSelectedListener listener) {

        OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (hint != null || floatingLabelText != null) {
                    if (!floatingLabelVisible && position != 0 ) {
                        hideFloatingLabel();
                    } else if (floatingLabelVisible && position == 0) {
                        hideFloatingLabel();
                    }
                }

                if (position != lastPosition) {
                    setError(null);
                }
                lastPosition = position ;

                if (listener != null) {
                	if(hint!=null){
                		position=position -1;
                	}else{
                		position=position;
                	}
                    position = hint != null ? position -1 : position ;
                    listener.onItemSelected(parent, view, position, id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (listener != null) {
                    listener.onNothingSelected(parent);
                }
            }
        };

        super.setOnItemSelectedListener(onItemSelectedListener);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }



   /*
    * **********************************************************************************
    * GETTERS AND SETTERS
    * **********************************************************************************
    */

    public int getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
        textPaint.setColor(baseColor);
        baseAlpha = textPaint.getAlpha() ;
        invalidate();
    }

    public int getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
        invalidate();
    }

    public int getErrorColor() {
        return errorColor;
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
        invalidate();
    }

    public void setHint(CharSequence hint) {
        this.hint = hint;
        invalidate();
    }

    public void setHint(int resid) {
        CharSequence hint = getResources().getString(resid);
        setHint(hint);
    }

    public CharSequence getHint() {
        return hint;
    }

    public void setFloatingLabelText(CharSequence floatingLabelText) {
        this.floatingLabelText = floatingLabelText;
        invalidate();
    }

    public void setFloatingLabelText(int resid) {
        String floatingLabelText = getResources().getString(resid);
        setFloatingLabelText(floatingLabelText);
    }

    public CharSequence getFloatingLabelText() {
        return this.floatingLabelText;
    }

    public void setError(CharSequence error) {
        this.error = error;
        if (errorLabelAnimator != null) {
            errorLabelAnimator.end();
        }

        if (multiline) {
            startErrorMultilineAnimator(prepareBottomPadding());
        } else if (needScrollingAnimation()) {
            startErrorScrollingAnimator();
        }
        requestLayout();
    }

    public void setError(int resid) {
        CharSequence error = getResources().getString(resid);
        setError(error);
    }

    public CharSequence getError() {
        return this.error;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {

        innerPaddingRight = right;
        innerPaddingLeft = left;
        innerPaddingTop = top;
        innerPaddingBottom = bottom;

        setPadding();
    }

    @Override
    public int getPaddingBottom() {
        return super.getPaddingBottom();
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(new HintAdapter(adapter, getContext()));


    }

    private float getFloatingLabelPercent() {
        return floatingLabelPercent;
    }

    private void setFloatingLabelPercent(float floatingLabelPercent) {
        this.floatingLabelPercent = floatingLabelPercent;
    }

    private int getErrorLabelPosX() {
        return errorLabelPosX;
    }

    private void setErrorLabelPosX(int errorLabelPosX) {
        this.errorLabelPosX = errorLabelPosX;
    }

    private float getCurrentNbErrorLines() {
        return currentNbErrorLines;
    }

    private void setCurrentNbErrorLines(float currentNbErrorLines) {
        this.currentNbErrorLines = currentNbErrorLines;
        updateBottomPadding();
    }




    /*
     * **********************************************************************************
     * INNER CLASS
     * **********************************************************************************
     */

    private class HintAdapter extends BaseAdapter {

        private static final int HINT_TYPE = -1;

        private SpinnerAdapter mSpinnerAdapter;
        private Context mContext;

        public HintAdapter(SpinnerAdapter spinnerAdapter, Context context) {
            mSpinnerAdapter = spinnerAdapter;
            mContext = context;
        }

        @Override
        public int getViewTypeCount() {
            int viewTypeCount = mSpinnerAdapter.getViewTypeCount();
            return hint != null ? viewTypeCount + 1 : viewTypeCount;
        }

        @Override
        public int getItemViewType(int position) {
            return (hint != null && position == 0) ? HINT_TYPE : mSpinnerAdapter.getItemViewType(position);
        }

        @Override
        public int getCount() {
            int count = mSpinnerAdapter.getCount();
            return hint != null ? count + 1 : count;
        }

        @Override
        public Object getItem(int position) {
            return (hint != null && position == 0) ? hint : mSpinnerAdapter.getItem(position-1);
        }

        @Override
        public long getItemId(int position) {
            return (hint != null && position == 0) ? 0 : mSpinnerAdapter.getItemId(position-1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return buildView(position, convertView, parent, false);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return buildView(position, convertView, parent, true);
        }

        private View buildView(int position, View convertView, ViewGroup parent, boolean isDropDownView) {
            if (getItemViewType(position) == HINT_TYPE) {
                return getHintView(parent, isDropDownView);
            }

            //workaround to have multiple types in spinner
            if(convertView != null) {
                convertView = (convertView.getTag() != null && convertView.getTag() instanceof Integer && (Integer) convertView.getTag() != HINT_TYPE) ? convertView : null;
            }
            position = hint != null ? position - 1 : position;
            return isDropDownView ? mSpinnerAdapter.getDropDownView(position, convertView, parent) :
                    mSpinnerAdapter.getView(position, convertView, parent);

        }

        private View getHintView(ViewGroup parent, boolean isDropDownView) {

            TextView textView;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final int resid = isDropDownView ? android.R.layout.simple_spinner_dropdown_item : android.R.layout.simple_spinner_item;
            textView = (TextView) inflater.inflate(resid, parent, false);

            textView.setText(hint);
            textView.setTextColor(baseColor);
            textView.setTag(HINT_TYPE);

            return textView;
        }


    }
}