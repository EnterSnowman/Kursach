package com.entersnowman.kursach.logic;

/**
 * Created by Valentin on 21.03.2017.
 */

public class InputSignal extends  AbstractElement{
    boolean value;
    String nameWithPath;

    public String getNameWithPath() {
        return nameWithPath;
    }

    public void setNameWithPath(String nameWithPath) {
        this.nameWithPath = nameWithPath;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public InputSignal(String outputName, boolean value) {
        this.value = value;
        this.outputName = outputName;
    }

    @Override
    public String toString() {
        return value +" "+ getOutputName();

    }
}
