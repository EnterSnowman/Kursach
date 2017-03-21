package com.entersnowman.kursach.logic;

import java.util.ArrayList;

/**
 * Created by Valentin on 21.03.2017.
 */

public class LogicElement extends AbstractElement{
    ArrayList<LogicElement> inputElements;
    ArrayList<InputSignal> inputSignals;
    String type;
    public LogicElement(String outputName,String type) {
        inputElements = new ArrayList<LogicElement>();
        inputSignals = new ArrayList<InputSignal>();
        this.outputName = outputName;
        this.type = type;
    }


    public ArrayList<LogicElement> getInputElements() {
        return inputElements;
    }

    public void setInputElements(ArrayList<LogicElement> inputElements) {
        this.inputElements = inputElements;
    }

    public ArrayList<InputSignal> getInputSignals() {
        return inputSignals;
    }

    public void setInputSignals(ArrayList<InputSignal> inputSignals) {
        this.inputSignals = inputSignals;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
