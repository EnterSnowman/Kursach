package com.entersnowman.kursach;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.entersnowman.kursach.logic.InputSignal;
import com.entersnowman.kursach.logic.LogicElement;
import com.entersnowman.kursach.normalform.FormalConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Valentin on 14.03.2017.
 */

public class Scheme extends View{
    Paint paint,testPaint;
    ArrayList<Element> elements;
    ArrayList<Link> links;
    ArrayList<String> signals;
    ArrayList<String> alphabet;
    ArrayList<LogicElement> logicElements;
    ArrayList<Character> posChars;
    ArrayList<String> result_test;
    Activity activity;
    HashMap<String,String> char_path;
    boolean finded,deleteMode, inputMode, isInPinFinded,isOutPinFinded, canSkip;
    int findedElement,inPin,outPin, inPinElement;
    float dragX;
    float dragY;
    EditText name_et;
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
        logicElements = new ArrayList<LogicElement>();
        elements = new ArrayList<Element>();
        links = new ArrayList<Link>();
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        testPaint = new Paint();
        testPaint.setColor(Color.GREEN);
        finded = false;
        deleteMode = false;
        inputMode=  false;
        isInPinFinded = false;
        isOutPinFinded = false;
        result_test = new ArrayList<String>();
        canSkip = true;
        posChars = new ArrayList<Character>();
        char_path = new HashMap<String, String>();
        //digits
        for (int i = 48;i<58;i++)
            posChars.add((char) i);
        //low case
        for (int i = 64;i<91;i++)
            posChars.add((char) i);
        //up case
        for (int i = 97;i<123;i++)
            posChars.add((char) i);
        signals = new ArrayList<String>();
        alphabet = new ArrayList<String>();
        for (int i =0 ; i < "abcdefghijklmnopqrstuvwxyz".length();i++)
            alphabet.add("abcdefghijklmnopqrstuvwxyz".substring(i,i+1));
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
                    if (inputMode){//добавление сигналов
                        boolean isUnnamedPinFinded = false;
                        for (int i = 0; i < elements.size() && !isUnnamedPinFinded; i++) {
                            if (elements.get(i).getPin(evX, evY) != -1) {
                                if (elements.get(i).inputPins.get(elements.get(i).getPin(evX, evY)).getLink() == null) {
                                    isUnnamedPinFinded = true;
                                    elements.get(i).inputPins.get(elements.get(i).getPin(evX, evY)).setTerm(name_et.getText().toString());
                                    if (!signals.contains(name_et.getText().toString()))
                                        signals.add(name_et.getText().toString());
                                    invalidate();
                                }
                            }
                        }
                    }
                    else {
                        //click on input pin
                        if (!isInPinFinded) {
                            for (int i = 0; i < elements.size() && !isInPinFinded; i++) {
                                if (elements.get(i).getPin(evX, evY) != -1) {
                                    if (elements.get(i).inputPins.get(elements.get(i).getPin(evX, evY)).getLink() == null && elements.get(i).inputPins.get(elements.get(i).getPin(evX, evY)).getTerm()==null) {
                                        isInPinFinded = true;
                                        inPinElement = i;
                                        inPin = elements.get(i).getPin(evX, evY);
                                        elements.get(i).inputPins.get(inPin).paint.setColor(Color.RED);
                                        System.out.println("In find. El = " + findedElement + " pin = " + inPin);
                                        invalidate();
                                        canSkip = false;
                                    }
                                }
                            }
                            if (isOutPinFinded && isInPinFinded) {
                                canSkip = true;
                                addLink();
                            }
                        }
                        //click on output pin
                        if (!isOutPinFinded) {
                            for (int i = 0; i < elements.size() && !isOutPinFinded; i++) {
                                if (elements.get(i).isClickOnOutPin(evX, evY)) {
                                    isOutPinFinded = true;
                                    outPin = i;
                                    elements.get(i).outputPin.paint.setColor(Color.RED);
                                    System.out.println("Out find. El = " + outPin);
                                    invalidate();
                                    canSkip = false;
                                }
                            }
                            if (isInPinFinded && isOutPinFinded) {
                                canSkip = true;
                                addLink();
                            }
                        }
                        //click on element
                        for (int i = 0; i < elements.size() && !finded && !isInPinFinded && !isOutPinFinded; i++) {
                            if (elements.get(i).isClickOnBorders(evX, evY)) {
                                elements.get(i).isMove = false;
                                finded = true;
                                findedElement = i;
                                dragX = evX - elements.get(findedElement).x;
                                dragY = evY - elements.get(findedElement).y;
                            }
                        }
                        if (canSkip) {
                            skipPins();
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

    public void nameOutputSignals(){
        int n = 0;
        int currentLetter = 0;
        ArrayList<Integer> notCheckedElements = new ArrayList<Integer>();
        ArrayList<Integer> readyToCheckElements = new ArrayList<Integer>();
        for (Element e : elements) {
            boolean flag = false;
            e.passingSignals.add(n);
            e.setNumber(Integer.toString(n));
            n++;
            for (int i = currentLetter; i < alphabet.size() && !flag; i++) {
                if (!signals.contains(alphabet.get(i))) {
                    e.outputPin.setTerm(alphabet.get(i));
                    signals.add(alphabet.get(i));
                    if (e.outputPin.getLinks().size() > 0)
                        for (int j = 0; j < e.outputPin.getLinks().size(); j++)
                            e.outputPin.getLinks().get(j).inputPin.setTerm(alphabet.get(i));
                    flag = true;
                    currentLetter = i + 1;
                }
            }
        }
        //delete back links
        for (Element e: elements){
            e.passingSignals.add(Integer.valueOf(e.getNumber()));
            e.removeBackLinks();
        }

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

    public EditText getName_et() {
        return name_et;
    }

    public void setName_et(EditText name_et) {
        this.name_et = name_et;
    }


    public void synthesis_test(){

        int root = -1;
        String rootName ="";


        for (int i = 0;i<elements.size();i++) {
            if (elements.get(i).getOutputPin().getLinks().size() == 0) {
                root = i;
                rootName = elements.get(i).getOutputPin().getTerm();
            }
        }
        for(String letter: signals) {
            boolean flag = false;
            boolean isFinded = false;
            if (!letter.equals(rootName)) {
                int whichWrong = -1;
                for (int i = 0; i < elements.size() && !isFinded; i++) {
                    if (elements.get(i).getOutputPin().getTerm().equals(letter)) {
                        isFinded = true;
                        whichWrong = i;

                    }
                }
                if (whichWrong != -1) {//output
                    for (int i = 0; i < elements.size() && !flag; i++) {
                        if (elements.get(i).getOutputPin().getLinks().size() == 0) {
                            root = i;
                            flag = true;
                            elements.get(i).createLogicElement(letter);
                            elements.get(i).getLogicElement().restruct(false);
                            elements.get(i).getLogicElement().nameInputSignalsAsUniqChars(posChars, char_path);
                            FormalConverter formalConverter = new FormalConverter(elements.get(i).getLogicElement().convertElementsToString(true).toString().replaceAll("\\s", ""));
                            String enf = formalConverter.convertToDNF();
                            elements.get(i).getLogicElement().setDNF(enf);
                            for (Map.Entry<String, String> entry : char_path.entrySet())
                                System.out.println(entry.getKey() + " : " + entry.getValue());
                            elements.get(i).getLogicElement().replaceCharsToPath(char_path);
                            result_test.add("ЭНФ:");
                            result_test.add(elements.get(i).getLogicElement().getDNFInOneString());
                        }
                    }
                    elements.get(whichWrong).createLogicElement();
                    elements.get(whichWrong).getLogicElement().restruct(false);
                    FormalConverter formalConverter = new FormalConverter(elements.get(whichWrong).getLogicElement().convertElementsToString(true).toString().replaceAll("\\s", ""));
                    elements.get(whichWrong).getLogicElement().setDNF(formalConverter.convertToDNF());
                    System.out.println("Wrong DNF " + elements.get(whichWrong).getLogicElement().getDNFInOneString());
                    boolean isFindedZero = false, isFindedOne = false;
                    //неисправность хі = 0
                    //for each term in DNF of wrong signal
                    result_test.add("Неисправность для " + letter + " = 0");
                    for (int i = 0; i < elements.get(whichWrong).getLogicElement().getDNF().size() && !isFindedZero; i++) {
                        HashMap<String, Boolean> values = new HashMap<String, Boolean>();
                        values.put(letter, true);
                        ArrayList<String> otherLetters = elements.get(whichWrong).getLogicElement().getListOfAllLetter();
                        ArrayList<String> oneTerm = elements.get(whichWrong).getLogicElement().getDNF().get(i);
                        boolean isGoodTermInWrongSignal = true;
                        //checking term in wrong signal and set values
                        for (int j = 0; j < oneTerm.size() && isGoodTermInWrongSignal; j++) {
                            if (oneTerm.get(j).substring(0, 1).equals("¬")) {
                                if (!values.containsKey(oneTerm.get(j).substring(1, 2))) {
                                    values.put(oneTerm.get(j).substring(1, 2), false);
                                    otherLetters.remove(oneTerm.get(j).substring(1, 2));
                                } else if (values.get(oneTerm.get(j).substring(1, 2)))
                                    isGoodTermInWrongSignal = false;
                            } else {
                                if (!values.containsKey(oneTerm.get(j).substring(0, 1))) {
                                    values.put(oneTerm.get(j).substring(0, 1), true);
                                    otherLetters.remove(oneTerm.get(j).substring(0, 1));
                                } else if (!values.get(oneTerm.get(j).substring(0, 1)))
                                    isGoodTermInWrongSignal = false;
                            }
                        }
                        if (isGoodTermInWrongSignal) {
                            //finded good term in all ENF
                            for (int j = 0; j < elements.get(root).getLogicElement().getDNF().size() && !isFindedZero; j++) {
                                ArrayList<String> otherVariables = elements.get(root).getLogicElement().getListOfAllLetter();
                                otherVariables.remove(letter);
                                otherVariables.removeAll(oneTerm);
                                boolean isGoodTerm = false;
                                int goodTerm = j;
                                ArrayList<String> goodOneTerm = elements.get(root).getLogicElement().getDNF().get(j);
                                boolean f = false;
                                for (int k = 0; k < goodOneTerm.size() && !isGoodTerm && !f; k++) {
                                    boolean inverseOfLetter = false;
                                    if (goodOneTerm.get(k).substring(0, 1).equals(letter)) {//finded good term
                                        isGoodTerm = true;
                                    } else if (goodOneTerm.get(k).substring(0, 2).equals("¬" + letter)) {
                                        inverseOfLetter = true;
                                        isGoodTerm = true;
                                    }
                                    for (int m = 0; m < goodOneTerm.size() && isGoodTerm; m++) {//set values of other letters in good term
                                        if (!goodOneTerm.get(m).substring(0, 1).equals(letter) && !goodOneTerm.get(m).substring(1, 2).equals(letter)) {
                                            if (goodOneTerm.get(m).substring(0, 1).equals("¬")) {
                                                if (!values.containsKey(goodOneTerm.get(m).substring(1, 2))) {
                                                    values.put(goodOneTerm.get(m).substring(1, 2), false);
                                                    otherVariables.remove(goodOneTerm.get(m).substring(1, 2));
                                                } else if (values.get(goodOneTerm.get(m).substring(1, 2)))
                                                    isGoodTerm = false;
                                            } else {
                                                if (!values.containsKey(goodOneTerm.get(m).substring(0, 1))) {
                                                    values.put(goodOneTerm.get(m).substring(0, 1), true);
                                                    otherVariables.remove(goodOneTerm.get(m).substring(0, 1));
                                                } else if (!values.get(goodOneTerm.get(m).substring(0, 1)))
                                                    isGoodTerm = false;
                                            }


                                        } else {
                                            System.out.println("Smth wrong with " + goodOneTerm.get(m));
                                            if (inverseOfLetter && goodOneTerm.get(m).substring(0, 1).equals(letter))
                                                isGoodTerm = false;
                                            else if (!inverseOfLetter && goodOneTerm.get(m).substring(0, 2).equals("¬" + letter))
                                                isGoodTerm = false;

                                        }
                                    }
                                    if (isGoodTerm)
                                        f = true;
                                }
                                if (isGoodTerm) {
                                    //var which could be changed
                                    for (int k = 0; k < Math.pow(2, otherVariables.size()) && !isFindedZero; k++) {
                                        //checking all variants
                                        boolean isBadVariant = false;
                                        HashMap<String, Boolean> var = new HashMap<String, Boolean>();
                                        for (int l = 0; l < otherVariables.size(); l++) {
                                            if (getBit(k, l) == 1)
                                                var.put(otherVariables.get(l), true);
                                            else
                                                var.put(otherVariables.get(l), false);
                                        }
                                        for (Map.Entry<String, Boolean> e : values.entrySet())
                                            var.put(e.getKey(), e.getValue());
                                        for (int l = 0; l < elements.get(root).getLogicElement().getDNF().size() && !isBadVariant; l++) {
                                            boolean res = true;
                                            if (l != goodTerm) {
                                                for (String s : elements.get(root).getLogicElement().getDNF().get(l)) {
                                                    if (s.substring(0, 1).equals("¬"))
                                                        res = res && !var.get(s.substring(1, 2));
                                                    else
                                                        res = res && var.get(s.substring(0, 1));

                                                }
                                                if (res)
                                                    isBadVariant = true;
                                            }
                                        }
                                        if (!isBadVariant) {
                                            isFindedZero = true;
                                            StringBuilder res = new StringBuilder();
                                            System.out.println("Finded combination for 0");
                                            for (Map.Entry<String, Boolean> e : var.entrySet()) {
                                                System.out.print(e.getKey() + " " + e.getValue() + " ");
                                                res.append(e.getKey() + " = " + getBit(e.getValue()) + " ");
                                            }
                                            System.out.println();
                                            result_test.add(res.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //неисправность хі = 1
                    //set all letters in wrong signal, that wrong signal is 0
                    result_test.add("Неисправность для " + letter + " = 1");
                    ArrayList<String> lettersOfWrongSignal = elements.get(whichWrong).getLogicElement().getListOfAllLetter();
                    for (int j = 0; j < Math.pow(2, lettersOfWrongSignal.size()) && !isFindedOne; j++) {
                        HashMap<String, Boolean> values = new HashMap<String, Boolean>();
                        values.put(letter, false);
                        for (int k = 0; k < lettersOfWrongSignal.size(); k++) {
                            if (getBit(j, k) == 1)
                                values.put(lettersOfWrongSignal.get(k), true);
                            else
                                values.put(lettersOfWrongSignal.get(k), false);
                        }
                        boolean isLetterZero = true;
                        System.out.println("Find that " + letter + " can be 0");
                        for (int m = 0; m < elements.get(whichWrong).getLogicElement().getDNF().size() && isLetterZero; m++) {
                            boolean res = true;
                            System.out.println("Term " + m);
                            for (String s : elements.get(whichWrong).getLogicElement().getDNF().get(m)) {
                                System.out.println(s);
                                if (s.substring(0, 1).equals("¬"))
                                    res = res && !values.get(s.substring(1, 2));
                                else
                                    res = res && values.get(s.substring(0, 1));

                            }
                            if (res)
                                isLetterZero = false;

                        }
                        //find good term with wrong letter
                        if (isLetterZero)
                            for (int k = 0; k < elements.get(root).getLogicElement().getDNF().size() && !isFindedOne; k++) {
                                int goodTerm = k;
                                ArrayList<String> otherLetters = elements.get(root).getLogicElement().getListOfAllLetter();
                                otherLetters.removeAll(lettersOfWrongSignal);
                                otherLetters.remove(letter);
                                ArrayList<String> oneTerm = elements.get(root).getLogicElement().getDNF().get(k);
                                boolean isGoodTerm = false;
                                boolean f = false;
                                for (int i = 0; i < oneTerm.size() && !isGoodTerm && !f; i++) {
                                    boolean inverseOfLetter = false;
                                    if (oneTerm.get(i).substring(0, 1).equals(letter)) {//finded good term
                                        isGoodTerm = true;
                                    } else if (oneTerm.get(i).substring(0, 2).equals("¬" + letter)) {
                                        inverseOfLetter = true;
                                        isGoodTerm = true;
                                    }
                                    for (int m = 0; m < oneTerm.size() && isGoodTerm; m++) {//set values of other letters in good term
                                        if (!oneTerm.get(m).substring(0, 1).equals(letter) && !oneTerm.get(m).substring(1, 2).equals(letter)) {
                                            if (oneTerm.get(m).substring(0, 1).equals("¬")) {
                                                if (!values.containsKey(oneTerm.get(m).substring(1, 2))) {
                                                    values.put(oneTerm.get(m).substring(1, 2), false);
                                                    otherLetters.remove(oneTerm.get(m).substring(1, 2));
                                                } else if (values.get(oneTerm.get(m).substring(1, 2)))
                                                    isGoodTerm = false;
                                            } else {
                                                if (!values.containsKey(oneTerm.get(m).substring(0, 1))) {
                                                    values.put(oneTerm.get(m).substring(0, 1), true);
                                                    otherLetters.remove(oneTerm.get(m).substring(0, 1));
                                                } else if (!values.get(oneTerm.get(m).substring(0, 1)))
                                                    isGoodTerm = false;
                                            }


                                        } else {
                                            if (inverseOfLetter && oneTerm.get(m).substring(0, 1).equals(letter))
                                                isGoodTerm = false;
                                            else if (!inverseOfLetter && oneTerm.get(m).substring(0, 2).equals("¬" + letter))
                                                isGoodTerm = false;

                                        }
                                    }
                                    if (isGoodTerm)
                                        f = true;
                                    if (isGoodTerm)
                                        for (int l = 0; l < Math.pow(2, otherLetters.size()) && !isFindedOne; l++) {
                                            boolean isBadVariant = false;
                                            HashMap<String, Boolean> var = new HashMap<String, Boolean>();
                                            for (int m = 0; m < otherLetters.size(); m++) {
                                                if (getBit(l, m) == 1)
                                                    var.put(otherLetters.get(m), true);
                                                else
                                                    var.put(otherLetters.get(m), false);
                                            }
                                            for (Map.Entry<String, Boolean> e : values.entrySet())
                                                var.put(e.getKey(), e.getValue());
                                            for (Map.Entry<String, Boolean> e : var.entrySet()) {
                                                System.out.print(e.getKey() + " " + e.getValue() + " ");
                                            }
                                            System.out.println();
                                            for (int m = 0; m < elements.get(root).getLogicElement().getDNF().size() && !isBadVariant; m++) {
                                                boolean res = true;
                                                if (m != goodTerm) {
                                                    System.out.println("Term " + m);
                                                    for (String s : elements.get(root).getLogicElement().getDNF().get(m)) {
                                                        System.out.println(s);
                                                        if (s.substring(0, 1).equals("¬"))
                                                            res = res && !var.get(s.substring(1, 2));
                                                        else
                                                            res = res && var.get(s.substring(0, 1));

                                                    }
                                                    if (res)
                                                        isBadVariant = true;
                                                }
                                            }
                                            if (!isBadVariant) {
                                                isFindedOne = true;
                                                StringBuilder res = new StringBuilder();
                                                System.out.println("Finded combination for 1");
                                                for (Map.Entry<String, Boolean> e : var.entrySet()) {
                                                    System.out.print(e.getKey() + " " + e.getValue() + " ");
                                                    res.append(e.getKey() + " = " + getBit(e.getValue()) + " ");
                                                }
                                                result_test.add(res.toString());
                                            }
                                        }
                                }


                            }
                    }


                } else//input signal
                {
                    for (int i = 0; i < elements.size() && !flag; i++) {
                        if (elements.get(i).getOutputPin().getLinks().size() == 0) {//create logic scheme
                            root = i;
                            flag = true;
                            elements.get(i).createLogicElement();
                            elements.get(i).getLogicElement().restruct(false);
                            elements.get(i).getLogicElement().nameInputSignalsAsUniqChars(posChars, char_path);
                            FormalConverter formalConverter = new FormalConverter(elements.get(i).getLogicElement().convertElementsToString(true).toString().replaceAll("\\s", ""));
                            String enf = formalConverter.convertToDNF();

                            elements.get(i).getLogicElement().setDNF(enf);
                            for (Map.Entry<String, String> entry : char_path.entrySet())
                                System.out.println(entry.getKey() + " : " + entry.getValue());
                            elements.get(i).getLogicElement().replaceCharsToPath(char_path);
                            result_test.add(elements.get(i).getLogicElement().getDNFInOneString());
                        }
                    }
                    if (root != -1) {
                        result_test.add("Неисправность для " +letter+ " = 0");
                        boolean isFindedZero = false, isFindedOne = false;
                        //неисправность хі = 0

                        for (int i = 0; i < elements.get(root).getLogicElement().getDNF().size() && !isFindedZero; i++) {
                            HashMap<String, Boolean> values = new HashMap<String, Boolean>();
                            values.put(letter, true);
                            ArrayList<String> otherLetters = elements.get(root).getLogicElement().getListOfAllLetter();
                            otherLetters.remove(letter);
                            boolean isGoodTerm = false;
                            int goodTerm = i;
                            ArrayList<String> oneTerm = elements.get(root).getLogicElement().getDNF().get(i);
                            boolean f = false;
                            for (int j = 0; j < oneTerm.size() && !isGoodTerm && !f; j++) {
                                boolean inverseOfLetter = false;
                                if (oneTerm.get(j).substring(0, 1).equals(letter)) {//finded good term
                                    isGoodTerm = true;
                                } else if (oneTerm.get(j).substring(0, 2).equals("¬" + letter)) {
                                    inverseOfLetter = true;
                                    isGoodTerm = true;
                                }
                                for (int k = 0; k < oneTerm.size() && isGoodTerm; k++) {//set values of other letters in good term
                                    if (!oneTerm.get(k).substring(0, 1).equals(letter) && !oneTerm.get(k).substring(1, 2).equals(letter)) {
                                        if (oneTerm.get(k).substring(0, 1).equals("¬")) {
                                            if (!values.containsKey(oneTerm.get(k).substring(1, 2))) {
                                                values.put(oneTerm.get(k).substring(1, 2), false);
                                                otherLetters.remove(oneTerm.get(k).substring(1, 2));
                                            } else if (values.get(oneTerm.get(k).substring(1, 2)))
                                                isGoodTerm = false;
                                        } else {
                                            if (!values.containsKey(oneTerm.get(k).substring(0, 1))) {
                                                values.put(oneTerm.get(k).substring(0, 1), true);
                                                otherLetters.remove(oneTerm.get(k).substring(0, 1));
                                            } else if (!values.get(oneTerm.get(k).substring(0, 1)))
                                                isGoodTerm = false;
                                        }


                                    } else {
                                        if (inverseOfLetter && oneTerm.get(k).substring(0, 1).equals(letter))
                                            isGoodTerm = false;
                                        else if (!inverseOfLetter && oneTerm.get(k).substring(0, 2).equals("¬" + letter))
                                            isGoodTerm = false;

                                    }
                                }
                                if (isGoodTerm)
                                    f = true;
                            }
                            if (isGoodTerm) {
                                //forming list of possible values
                                ArrayList<HashMap<String, Boolean>> variants = new ArrayList<HashMap<String, Boolean>>();
                                boolean isBadVariant = false;
                                for (int j = 0; j < Math.pow(2, otherLetters.size()) && !isFindedZero; j++) {
                                    isBadVariant = false;
                                    HashMap<String, Boolean> var = new HashMap<String, Boolean>();
                                    for (int k = 0; k < otherLetters.size(); k++) {
                                        if (getBit(j, k) == 1)
                                            var.put(otherLetters.get(k), true);
                                        else
                                            var.put(otherLetters.get(k), false);
                                    }
                                    for (Map.Entry<String, Boolean> e : values.entrySet())
                                        var.put(e.getKey(), e.getValue());
                                    //checking current variant
                                    for (int k = 0; k < elements.get(root).getLogicElement().getDNF().size() && !isBadVariant; k++) {
                                        boolean res = true;
                                        if (k != i) {
                                            for (String s : elements.get(root).getLogicElement().getDNF().get(k)) {
                                                if (s.substring(0, 1).equals("¬"))
                                                    res = res && !var.get(s.substring(1, 2));
                                                else
                                                    res = res && var.get(s.substring(0, 1));

                                            }
                                            if (res)
                                                isBadVariant = true;
                                        }
                                    }
                                    if (!isBadVariant) {
                                        isFindedZero = true;
                                        StringBuilder res = new StringBuilder();
                                        System.out.println("Finded combination for 0");
                                        for (Map.Entry<String, Boolean> e : var.entrySet()) {
                                            System.out.print(e.getKey() + " " + e.getValue() + " ");
                                            res.append(e.getKey() + " = " + getBit(e.getValue()) + " ");
                                        }
                                        result_test.add(res.toString());
                                    }


                                    variants.add(var);

                                }
                                System.out.println("Print variants");
                                for (HashMap<String, Boolean> v : variants) {
                                    for (Map.Entry<String, Boolean> e : v.entrySet())
                                        System.out.print(e.getKey() + " " + e.getValue() + " ");
                                    System.out.println();

                                }
                            }
                        }
                        //неисправность xi = 1
                        result_test.add("Неисправность для " +letter+ " = 1");
                        for (int i = 0; i < elements.get(root).getLogicElement().getDNF().size() && !isFindedOne; i++) {
                            HashMap<String, Boolean> values = new HashMap<String, Boolean>();
                            values.put(letter, false);
                            ArrayList<String> otherLetters = elements.get(root).getLogicElement().getListOfAllLetter();
                            otherLetters.remove(letter);
                            boolean isGoodTerm = false;
                            int goodTerm = i;
                            ArrayList<String> oneTerm = elements.get(root).getLogicElement().getDNF().get(i);
                            boolean f = false;
                            for (int j = 0; j < oneTerm.size() && !isGoodTerm && !f; j++) {
                                boolean inverseOfLetter = false;
                                if (oneTerm.get(j).substring(0, 1).equals(letter)) {//finded good term
                                    isGoodTerm = true;
                                } else if (oneTerm.get(j).substring(0, 2).equals("¬" + letter)) {
                                    inverseOfLetter = true;
                                    isGoodTerm = true;
                                }
                                for (int k = 0; k < oneTerm.size() && isGoodTerm; k++) {//set values of other letters in good term
                                    if (!oneTerm.get(k).substring(0, 1).equals(letter) && !oneTerm.get(k).substring(1, 2).equals(letter)) {
                                        if (oneTerm.get(k).substring(0, 1).equals("¬")) {
                                            if (!values.containsKey(oneTerm.get(k).substring(1, 2))) {
                                                values.put(oneTerm.get(k).substring(1, 2), false);
                                                otherLetters.remove(oneTerm.get(k).substring(1, 2));
                                            } else if (values.get(oneTerm.get(k).substring(1, 2)))
                                                isGoodTerm = false;
                                        } else {
                                            if (!values.containsKey(oneTerm.get(k).substring(0, 1))) {
                                                values.put(oneTerm.get(k).substring(0, 1), true);
                                                otherLetters.remove(oneTerm.get(k).substring(0, 1));
                                            } else if (!values.get(oneTerm.get(k).substring(0, 1)))
                                                isGoodTerm = false;
                                        }


                                    } else {
                                        if (inverseOfLetter && oneTerm.get(k).substring(0, 1).equals(letter))
                                            isGoodTerm = false;
                                        else if (!inverseOfLetter && oneTerm.get(k).substring(0, 2).equals("¬" + letter))
                                            isGoodTerm = false;

                                    }
                                }
                                if (isGoodTerm)
                                    f = false;
                            }
                            if (isGoodTerm) {
                                //forming list of possible values
                                ArrayList<HashMap<String, Boolean>> variants = new ArrayList<HashMap<String, Boolean>>();
                                boolean isBadVariant = false;
                                for (int j = 0; j < Math.pow(2, otherLetters.size()) && !isFindedOne; j++) {
                                    isBadVariant = false;
                                    HashMap<String, Boolean> var = new HashMap<String, Boolean>();
                                    for (int k = 0; k < otherLetters.size(); k++) {
                                        if (getBit(j, k) == 1)
                                            var.put(otherLetters.get(k), true);
                                        else
                                            var.put(otherLetters.get(k), false);
                                    }
                                    for (Map.Entry<String, Boolean> e : values.entrySet())
                                        var.put(e.getKey(), e.getValue());
                                    //checking current variant
                                    for (int k = 0; k < elements.get(root).getLogicElement().getDNF().size() && !isBadVariant; k++) {
                                        boolean res = true;
                                        if (k != i) {
                                            for (String s : elements.get(root).getLogicElement().getDNF().get(k)) {
                                                if (s.substring(0, 1).equals("¬"))
                                                    res = res && !var.get(s.substring(1, 2));
                                                else
                                                    res = res && var.get(s.substring(0, 1));

                                            }
                                            if (res)
                                                isBadVariant = true;
                                        }
                                    }
                                    if (!isBadVariant) {
                                        isFindedOne = true;
                                        System.out.println("Finded combination for 1");
                                        StringBuilder res = new StringBuilder();
                                        for (Map.Entry<String, Boolean> e : var.entrySet()) {
                                            System.out.print(e.getKey() + " " + e.getValue() + " ");
                                            res.append(e.getKey() + " = " + getBit(e.getValue()) + " ");
                                        }
                                        result_test.add(res.toString());
                                        System.out.println();
                                    }


                                    variants.add(var);

                                }
                                System.out.println("Print variants");
                                for (HashMap<String, Boolean> v : variants) {

                                    for (Map.Entry<String, Boolean> e : v.entrySet()) {
                                        System.out.print(e.getKey() + " " + e.getValue() + " ");

                                        System.out.println();

                                    }
                                }
                            }


                        }
                    }

                }
            }
        }
        activity.showDialog(MainActivity.RESULT_DIALOG);
    //end of cycle
    }

    public void setActivity(Activity activity){this.activity = activity;}
    public int getBit(int value,int position)
    {
        return (value >> position) & 1;
    }
    public String getBit(boolean value){
        if (value)
            return "1";
        else
            return "0";
    }
}
