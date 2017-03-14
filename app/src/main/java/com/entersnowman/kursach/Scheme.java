package com.entersnowman.kursach;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Valentin on 14.03.2017.
 */

public class Scheme extends View{
    Paint paint,testPaint;
    ArrayList<Element> elements;
    boolean finded,deleteMode;
    int findedElement;
    float dragX;
    float dragY;
    public Scheme(Context context) {
        super(context);
        init();
    }

    public Scheme(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Element e: elements)
            e.draw(canvas);
        System.out.println("Scheme "+getHeight()+" "+getWidth());
    }

    public void init(){
        elements = new ArrayList<Element>();
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        testPaint = new Paint();
        testPaint.setColor(Color.GREEN);
        finded = false;
        deleteMode = false;
    }

    public void addElement(Element element){
        elements.add(element);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        float evX = event.getX();
        float evY = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                System.out.println("evX = "+evX+" , evY = "+evY);
                if (!deleteMode){
                for (int i = 0; i<elements.size()&&!finded;i++){
                    if (elements.get(i).isClickOnBorders(evX,evY)){
                        elements.get(i).isMove = false;
                        finded = true;
                        findedElement = i;
                        dragX = evX - elements.get(findedElement).x;
                        dragY = evY - elements.get(findedElement).y;
                        System.out.println("Drags "+dragX+" "+dragY);
                    }
                }
                }
                else{
                    for (int i = elements.size()-1; i>=0&&!finded;i--){
                        if (elements.get(i).isClickOnBorders(evX,evY)){
                            elements.remove(i);
                            finded = true;
                        }
                    }
                    invalidate();
                    finded = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (finded&&!deleteMode){
                    System.out.println("Drags "+dragX+" "+dragY);
                    elements.get(findedElement).x = evX - dragX;
                    elements.get(findedElement).y = evY - dragY;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!deleteMode)
                finded = false;
                break;
        }

        return true;
    }
}
