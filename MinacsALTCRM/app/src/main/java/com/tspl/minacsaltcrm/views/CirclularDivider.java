package com.tspl.minacsaltcrm.views;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tspl.minacsaltcrm.ClassObject.Items;
import com.tspl.minacsaltcrm.Pr;
import com.tspl.minacsaltcrm.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by t0396 on 5/8/2015.
 */
public class CirclularDivider extends View {
    private List<Items> data = new ArrayList<Items>();
    private Paint circlePaint;
    private Paint outerCirclePaint;
    private Paint sectorPaint;
    private Paint selectedPaint;
    private Paint textPaint;
    private Paint centerTextPaint;
    int selectedPos = -1;
    int innerCircleRadius = 60;
    int innerPadding = 3;
    String centerText = "";
    String SPLIT_CHAR = "_";
    int pushPaddingLeft = 0;
    int radius;
    int centerCircleColor = android.R.color.holo_green_light;
    int textSize = 15;
    int centerTextSize = 17;
    int outerPaddingCircle = 2;
    public CirclularDivider(Context context) {
        super(context);
        init();
    }

    public void setCenterCircleColor(int centerCircleColor) {
        this.centerCircleColor = centerCircleColor;
        if(circlePaint != null)
        circlePaint.setColor(getResources().getColor(centerCircleColor));
    }

    public CirclularDivider(Context context, AttributeSet attrs) {
        super(context, attrs);

        // attrs contains the raw values for the XML attributes
        // This call uses R.styleable.CirclularDivider, which is an array of
        // the custom attributes that were declared in attrs.xml.
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CirclularDivider,
                0, 0
        );

        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.CirclularDivider_* constants represent the index for
            // each custom attribute in the R.styleable.PieChart array.
            innerCircleRadius = a.getInteger(R.styleable.CirclularDivider_innerRadius, 0);
            innerPadding = a.getInteger(R.styleable.CirclularDivider_innerPadding, 0);
            centerText = a.getString(R.styleable.CirclularDivider_centerText);
            textSize = a.getInteger(R.styleable.CirclularDivider_textSize, 15);
            centerTextSize = a.getInteger(R.styleable.CirclularDivider_centerTextSize, 17);
            pushPaddingLeft = a.getInteger(R.styleable.CirclularDivider_pushPaddingLeft, 0);
            outerPaddingCircle = a.getInteger(R.styleable.CirclularDivider_outerPaddingCircle, 2);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        init();
    }

    public void setData(List<Items> data) {
        this.data = new ArrayList<Items>(data);
        bindAngles();
        invalidate();
    }

    private void bindAngles() {
        int j = 0;
        for (Items item : data) {
            int cnt = data.size();
            if (cnt > 0) {
                int angle = 360 / cnt;
                item.startAngle = j * angle;
                j++;
            }
        }
    }

    private void init() {
        outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerCirclePaint.setStyle(Paint.Style.FILL);
        outerCirclePaint.setColor(getResources().getColor(R.color.minacs_greenishYello));

        sectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sectorPaint.setStyle(Paint.Style.FILL);
//        sectorPaint.setColor(getResources().getColor());

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(getResources().getColor(centerCircleColor));

        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setStyle(Paint.Style.FILL);
        selectedPaint.setColor(getResources().getColor(R.color.minacs_green));

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(android.R.color.white));
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        centerTextPaint = new Paint();
        centerTextPaint.setColor(getResources().getColor(android.R.color.white));
        centerTextPaint.setTextSize(centerTextSize);
        centerTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Items item = new Items();
        item.label = "1st";
        item.color = getResources().getColor(R.color.seafoam);
        data.add(item);
        Items item2 = new Items();
        item2.label = "2nd";
        item2.color = getResources().getColor(R.color.bluegrass);
        data.add(item2);
    }


    RectF mBounds;

    private float getRadius() {
        float rad = mBounds.height() < mBounds.width() ? mBounds.height() / 2 : mBounds.width() / 2;
        return rad ;
    }

    public Shader shader;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new RectF(outerPaddingCircle, outerPaddingCircle, w-outerPaddingCircle, h-outerPaddingCircle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = (int) getRadius();
        int i = 0;
        float temp = 0;
        int cnt = data.size();
        float center_x = (getWidth() / 2) ;
        float center_y = (getHeight() / 2);

//        sectorPaint.setShader(new SweepGradient(center_x, center_y, R.color.black, R.color.white));
        sectorPaint.setStyle(Paint.Style.STROKE);
        sectorPaint.setStrokeWidth(5);
        if (cnt > 0) {
            float angle = (float) 360 / cnt;
            canvas.drawCircle(center_x, center_y, radius, outerCirclePaint);
            for (Items it : data) {
                if (i > 0) {
                    temp += angle;
                }
                sectorPaint.setColor(getResources().getColor(R.color.white));
//                DRAWING ARC
                float startangle = (float) (360.0 - ((i + 1) * angle));
//                Pr.log("startangle "+startangle);
                if ((selectedPos != -1) && (selectedPos == i)) {
                    canvas.drawArc(mBounds,
                            startangle,
                            angle,
                            true, selectedPaint);
                } else {
                    canvas.drawArc(mBounds,
                            startangle,
                            angle,
                            true, sectorPaint);

                }
//              FINDING THE MEDIAN ANGLE
                float medianAngleRad = (temp + (angle / 2f)) * (float) Math.PI / 180f; // this angle will place the text in the center of the arc.
                float medianAngle = (float) (Math.toDegrees(medianAngleRad));


                float rad_inner = innerCircleRadius + innerPadding;
                float rad_outer = radius - innerPadding;

                int distance = (int) (rad_outer - rad_inner);
                String rotatedTxt = it.label;
//LEFT SIDE OF CENTER VERTICAL
                if (medianAngle < 270 && medianAngle > 90) {
//                    text draw towards centre
/**
 * Custom adjustment with pushPaddingLeft
 */
                    rad_outer -= pushPaddingLeft;
 /**
 * Custom adjustment with pushPaddingLeft
 */
                    float x2 = (float) (center_x + ((rad_outer) * Math.cos(Math.toRadians(medianAngle))));
                    float y2 = (float) ((getHeight() / 2) - ((rad_outer) * Math.sin(Math.toRadians(medianAngle))));
                    Pr.ln("x2 : " + x2 + ", y2 :" + y2);
                    Rect rect = new Rect();

                    canvas.save();
                    textPaint.getTextBounds(rotatedTxt, 0, 1, rect);
                    canvas.rotate(180 - medianAngle, x2, y2);
                    float wordLen = textPaint.measureText(rotatedTxt);
                    int txtLen = textPaint.breakText(rotatedTxt, true, wordLen, null);

                    if (rotatedTxt.contains(SPLIT_CHAR)) {
                        String[] items = rotatedTxt.split(SPLIT_CHAR);
                        canvas.drawText(items[0], x2, y2, textPaint);
                        textPaint.getTextBounds(items[0], 0, items[0].length(), rect);
                        canvas.drawText(items[1], x2 , y2 + (rect.height() + 5), textPaint);
                    } else {

                        canvas.drawText(rotatedTxt, x2, y2, textPaint);
                    }

                    canvas.restore();
                } else {
//                    RIGHT SIDE OF CENTER VERTICAL
//                    text draw from end to centre
                    float x2 = (float) (center_x + ((rad_inner) * Math.cos(Math.toRadians(medianAngle))));
                    float y2 = (float) ((getHeight() / 2) - ((rad_inner) * Math.sin(Math.toRadians(medianAngle))));
                    Pr.ln("x2 : " + x2 + ", y2 :" + y2);
                    Rect rect = new Rect();
                    canvas.save();

                    canvas.rotate(-medianAngle, x2, y2);
                    float wordLen = textPaint.measureText(rotatedTxt);
                    int txtLen = textPaint.breakText(rotatedTxt, true, wordLen, null);
                    if (rotatedTxt.contains(SPLIT_CHAR)) {

                        String[] items = rotatedTxt.split("_");
                        canvas.drawText(items[0], x2, y2, textPaint);
                        try {
                            textPaint.getTextBounds(items[0], 0, items[0].length(), rect);
                            canvas.drawText(items[1], x2 , y2 + (rect.height() + 5), textPaint);
                        } catch (Exception e) {

                        }
                    } else {
                        canvas.drawText(rotatedTxt, x2, y2, textPaint);
                    }

                    canvas.restore();


                }
                i++;
            }

            /**
             * Drawing center circle
             */
            canvas.drawCircle(center_x, center_y, innerCircleRadius+2, sectorPaint);
            canvas.drawCircle(center_x, center_y, innerCircleRadius, circlePaint);
            canvas.save();
            float centrewd = centerTextPaint.measureText(centerText);
            canvas.drawText(centerText, center_x - (centrewd / 2), center_y, centerTextPaint);
            /*
//            direction line to clicked position
            if (selectedx1 != -1)
                canvas.drawLine(getWidth() / 2, getWidth() / 2, selectedx1, selectedy1, selectedPaint);*/

        }
    }

    float selectedx1 = -1, selectedy1 = -1;

    /**
     * Finding the angle between two lines
     * @param l1x1
     * @param l1x2
     * @param l1y1
     * @param l1y2
     * @param l2x1
     * @param l2x2
     * @param l2y1
     * @param l2y2
     * @return
     */
    public static double angleBetween2LinesPoints(float l1x1, float l1x2, float l1y1, float l1y2, float l2x1, float l2x2, float l2y1, float l2y2) {
        double angle1 = Math.atan2(l1y1 - l1y2,
                l1x1 - l1x2);
        double angle2 = Math.atan2(l2y1 - l2y2,
                l2x1 - l2x2);
        return (Math.toDegrees(angle1 - angle2));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            float w = getWidth() / 2;
            float h = getHeight() / 2;
            selectedx1 = x;
            selectedy1 = y;
            int clickedRadius = getDistance(w, h, x, y);
            if (clickedRadius < innerCircleRadius || clickedRadius > getRadius()) {
                /**
                 * Clicked outside the region
                 */
                return super.onTouchEvent(event);
            } else {
                double selAngle = angleBetween2LinesPoints(w, w + 20, h, h, w, x, h, y);
                Pr.ln("selAngle : " + selAngle);
                int cnt = data.size();
//                FINDING THE CLICKED SECTION AND REDRAWING
                int angle = 360 / cnt;
                for (int j = 0; j < cnt; j++) {

                    int minAngle = j * angle;
                    if (minAngle < selAngle && selAngle < minAngle + angle) {
                        selectedPos = j;
                        invalidate();
                        return true;
                    }
                }
                selectedPos = -1;
                return super.onTouchEvent(event);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            if (selectedPos != -1) {
//                UPDATE CLICK TO LISTENERS

                float x = event.getX();
                float y = event.getY();
                float w = getWidth() / 2;
                float h = getHeight() / 2;
                selectedx1 = x;
                selectedy1 = y;
                int clickedRadius = getDistance(w, h, x, y);
                if (clickedRadius < innerCircleRadius || clickedRadius > getRadius()) {
                    /**
                     * Clicked outside the region
                     */
                    selectedPos = -1;
                    invalidate();
                    return super.onTouchEvent(event);
                }


                if (click != null) {
                    click.onClick(selectedPos);
//                    RELOADING CLICK AFTER TOUCH UP
                    selectedPos = -1;
                    invalidate();
                }
            }
            return super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public int getDistance(float x1, float y1, float x2, float y2) {
        double d = Math.sqrt((x2 -= x1) * x2 + (y2 -= y1) * y2);
        return (int) d;
    }

    /**
     * Handling click events
     */
    private Click click = null;

    public interface Click {
        void onClick(int position);
    }

    public void setClickListener(Click click) {
        this.click = click;
    }
    /**
     * Handling click events ends
     */
}
