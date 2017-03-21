package com.entersnowman.kursach.logic;

/**
 * Created by Valentin on 21.03.2017.
 */

public class InputSignal extends  AbstractElement{
    boolean inversion;

    public InputSignal(String outputName,boolean inversion) {
        this.inversion = inversion;
        this.outputName = outputName;
    }
}
