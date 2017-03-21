package com.entersnowman.kursach;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Valentin on 15.03.2017.
 */

public class OutputPin extends View {
    float y,x1,x2;
    Paint paint, textPaint;
    ArrayList<Link> links;
    String term;
    Element element;

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }

    public OutputPin(Context context, float y, float x1, float x2) {
        super(context);
        links = new ArrayList<Link>();
        this.y = y;
        this.x1 = x1;
        this.x2 = x2;
        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(35);
        textPaint.setStrokeWidth(3);

    }

    public OutputPin(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(x1,y,x2,y,paint);
        if (term!=null)
        canvas.drawText(term,x2,y-2,textPaint);
    }
}
