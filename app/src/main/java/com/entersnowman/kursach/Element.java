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

import com.entersnowman.kursach.logic.LogicElement;

import java.sql.SQLOutput;
import java.util.ArrayList;

/**
 * Created by Valentin on 14.03.2017.
 */

public class Element extends View {
    final static float SMALL_H = 40;
    final static float LENGTH_IN = 30;
    final static float WIDTH = 120;
    final static float PIN_RADIUS = 12;
    Paint paint,backgroundPaint,textPaint;
    float w,h,x,y;
    int numberOfIns;
    String type;
    boolean isMove;
    ArrayList<InputPin> inputPins;
    OutputPin outputPin;
    LogicElement logicElement;

    public LogicElement getLogicElement() {
        return logicElement;
    }

    public void setLogicElement(LogicElement logicElement) {
        this.logicElement = logicElement;
    }

    public Element(Context context, String t, int number) {
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
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(35);
        textPaint.setStrokeWidth(3);
        w = WIDTH;
        h = (numberOfIns+1)*SMALL_H;
        x = 50;
        y = 50;
        inputPins = new ArrayList<InputPin>();
        for (int i = 0; i < numberOfIns;i++){
            InputPin inputPin = new InputPin(getContext(),y+SMALL_H*(i+1),x-LENGTH_IN,x);
            inputPin.setElement(this);
            inputPins.add(inputPin);
        }
        outputPin = new OutputPin(getContext(),y+h/2,x+w,x+w+LENGTH_IN);
        outputPin.setElement(this);
        isMove = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(x,y,x+w,y+h,backgroundPaint);
        canvas.drawRect(x,y,x+w,y+h,paint);
        for (int i = 0;i<inputPins.size();i++){
            inputPins.get(i).draw(canvas);
        }
        outputPin.draw(canvas);
        canvas.drawText(type,x+w/2,(y+h/2) - ((textPaint.descent() + textPaint.ascent()) / 2),textPaint);
        if (type.equals("NAND")||type.equals("NOR")){
            canvas.drawCircle(x+w,y+h/2,PIN_RADIUS,backgroundPaint);
            canvas.drawCircle(x+w,y+h/2,PIN_RADIUS,paint);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean isClickOnBorders(float evX,float evY){
        if (evX >=x &&evX <=x+w &&evY>=y&&evY <=y+h)
            return true;
        else
            return false;
    }

    public  boolean isClickOnOutPin(float evX,float evY){
        if (evX>=outputPin.x1-10 &&evX<=outputPin.x2+10&&evY>=outputPin.y-10 &&evY<=outputPin.y+10)
            return true;
        else
            return false;
    }


    public OutputPin getOutputPin() {
        return outputPin;
    }

    public void setOutputPin(OutputPin outputPin) {
        this.outputPin = outputPin;
    }

    public void setNewXY(float evX, float evY){
        x = evX;
        y = evY;
        for (int i = 0; i < numberOfIns;i++){
            inputPins.get(i).y = evY+SMALL_H*(i+1);
            inputPins.get(i).x2 = evX;
            inputPins.get(i).x1 = evX-LENGTH_IN;
        }
        outputPin.y = evY+h/2;
        outputPin.x1 = evX+w;
        outputPin.x2 = evX+w+LENGTH_IN;
    }

    public int getPin(float evX,float evY){
        boolean finded = false;
        int r = -1;
        for (int i = 0;i < inputPins.size()&&!finded;i++){
            if (evX>=inputPins.get(i).x1-10 &&evX<=inputPins.get(i).x2+10&&evY>=inputPins.get(i).y-10 &&evY<=inputPins.get(i).y+10){
                r = i;
                finded = true;
            }
        }
        System.out.println("Pin "+r);
        return  r;
    }
}
