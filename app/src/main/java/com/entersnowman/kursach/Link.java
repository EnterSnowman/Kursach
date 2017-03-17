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

public class Link extends View {
    InputPin inputPin;
    OutputPin outputPin;
    Paint paint;
    public InputPin getInputPin() {
        return inputPin;
    }

    public void setInputPin(InputPin inputPin) {
        this.inputPin = inputPin;
    }

    public OutputPin getOutputPin() {
        return outputPin;
    }

    public void setOutputPin(OutputPin outputPin) {
        this.outputPin = outputPin;
    }

    public Link(Context context) {
        super(context);
        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }

    public Link(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(inputPin.x1,inputPin.y,outputPin.x2,outputPin.y,paint);
    }
}
