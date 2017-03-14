package com.entersnowman.kursach;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Valentin on 14.03.2017.
 */

public class Element extends View {
    final static float SMALL_H = 30;
    final static float LENGTH_IN = 30;
    final static float WIDTH = 200;
    Paint paint,backgroundPaint;
    float w,h,x,y;
    int numberOfIns;
    String type;
    boolean isMove;
    public Element(Context context,String t,int number) {
        super(context);
        type = t;
        numberOfIns = number;
        init();
    }

    public Element(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    public void init(){
        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(35);
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.WHITE);
        w = WIDTH;
        h = (numberOfIns+1)*SMALL_H;
        x = 50;
        y = 50;
        isMove = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("OnDraw sq");
        canvas.drawRect(x,y,x+w,y+h,backgroundPaint);
        canvas.drawRect(x,y,x+w,y+h,paint);
        for (int i = 0;i<numberOfIns;i++){
            canvas.drawLine(x-LENGTH_IN,y+SMALL_H*(i+1),x,y+SMALL_H*(i+1),paint);
        }
        canvas.drawLine(x+w,y+h/2,x+w+LENGTH_IN,y+h/2,paint);
        canvas.drawText(type,x+w/2,y+h/2,paint);
        System.out.println("w = "+getWidth()+", h = "+getHeight());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println(widthMeasureSpec+" : "+ heightMeasureSpec);
    }

    public boolean isClickOnBorders(float evX,float evY){
        if (evX >=x &&evX <=x+w &&evY>=y&&evY <=y+h)
            return true;
        else
            return false;
    }
}
