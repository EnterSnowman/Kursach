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
    ArrayList<Link> links;
    boolean finded,deleteMode, isInPinFinded,isOutPinFinded, canSkip;
    int findedElement,inPin,outPin, inPinElement;
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
        for (Link l: links)
            l.draw(canvas);
    }

    public void init(){
        elements = new ArrayList<Element>();
        links = new ArrayList<Link>();
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        testPaint = new Paint();
        testPaint.setColor(Color.GREEN);
        finded = false;
        deleteMode = false;
        isInPinFinded = false;
        isOutPinFinded = false;
        canSkip = true;
    }

    public void addElement(Element element){
        elements.add(element);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("Finded in "+isInPinFinded+" out "+isOutPinFinded);
        //return super.onTouchEvent(event);
        float evX = event.getX();
        float evY = event.getY();
        canSkip = true;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!deleteMode){
                    //click on input pin
                    if (!isInPinFinded){
                for (int i = 0; i<elements.size()&&!isInPinFinded;i++){
                    if (elements.get(i).getPin(evX,evY)!=-1){
                        if (elements.get(i).inputPins.get(elements.get(i).getPin(evX,evY)).getLink()==null){
                        isInPinFinded = true;
                        inPinElement = i;
                        inPin = elements.get(i).getPin(evX,evY);
                        elements.get(i).inputPins.get(inPin).paint.setColor(Color.RED);
                        System.out.println("In find. El = "+findedElement+" pin = "+inPin);
                        invalidate();
                        canSkip = false;
                        }
                    }
                }
                    if (isOutPinFinded&&isInPinFinded){
                        canSkip = true;
                        addLink();
                    }
                    }
                //click on output pin
                    if (!isOutPinFinded){
                    for (int i = 0; i<elements.size()&&!isOutPinFinded;i++){
                        if (elements.get(i).isClickOnOutPin(evX,evY)){
                            isOutPinFinded = true;
                            outPin = i;
                            elements.get(i).outputPin.paint.setColor(Color.RED);
                            System.out.println("Out find. El = "+outPin);
                            invalidate();
                            canSkip = false;
                        }
                    }
                        if (isInPinFinded&&isOutPinFinded){
                            canSkip = true;
                            addLink();
                        }
                    }
                    //click on element
                for (int i = 0; i<elements.size()&&!finded&&!isInPinFinded&&!isOutPinFinded; i++){
                    if (elements.get(i).isClickOnBorders(evX,evY)){
                        elements.get(i).isMove = false;
                        finded = true;
                        findedElement = i;
                        dragX = evX - elements.get(findedElement).x;
                        dragY = evY - elements.get(findedElement).y;
                    }
                }
                if (canSkip){
                    skipPins();
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
                    elements.get(findedElement).setNewXY(evX - dragX, evY - dragY);
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

    public void addLink(){
        System.out.println("Link added");
        isInPinFinded = false;
        isOutPinFinded = false;
        Link link =  new Link(getContext());
        link.setInputPin(elements.get(inPinElement).inputPins.get(inPin));
        link.setOutputPin(elements.get(outPin).getOutputPin());
        links.add(link);
        elements.get(inPinElement).inputPins.get(inPin).paint.setColor(Color.BLACK);
        elements.get(outPin).getOutputPin().paint.setColor(Color.BLACK);
        elements.get(inPinElement).inputPins.get(inPin).setLink(link);
        elements.get(outPin).getOutputPin().getLinks().add(link);
        invalidate();
    }

    public void skipPins(){
        if (isInPinFinded)
        elements.get(inPinElement).inputPins.get(inPin).paint.setColor(Color.BLACK);
        if (isOutPinFinded)
        elements.get(outPin).getOutputPin().paint.setColor(Color.BLACK);
        isInPinFinded = false;
        isOutPinFinded = false;
        invalidate();
    }
}
