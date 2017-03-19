package com.entersnowman.kursach;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Valentin on 15.03.2017.
 */

public class InputPin extends View {
    float y,x1,x2;
    Paint paint, textPaint;
    Link link;
    String term;

    public InputPin(Context context, float y, float x1, float x2) {
        super(context);
        this.y = y;
        this.x1 = x1;
        this.x2 = x2;
        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(35);
        textPaint.setStrokeWidth(3);
    }

    public InputPin(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(x1,y,x2,y,paint);
        if (term!=null)
        canvas.drawText(term,x1,y-2,textPaint);
    }


    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }


    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }


}
