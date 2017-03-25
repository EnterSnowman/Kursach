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

    public static String operSign(String oper){
        if (oper.equals("AND"))
            return " \u2227 ";
        else if (oper.equals("OR"))
            return " \u2228 ";
        else return null;
    }

    public void restruct(boolean inverse){
        System.out.println("Inv " +inverse+" "+ getOutputName()+" "+type);
        boolean isCorrected = false;
        if (!inverse){
        if (type.equals("NAND")){
            isCorrected = true;
            type = "OR";
            if (inputSignals.size()>0){
                for (InputSignal s: inputSignals)
                    s.setValue(!s.isValue());
            }
            if (inputElements.size()>0){
                for (LogicElement l: inputElements)
                    l.restruct(true);
            }
        }
        if (type.equals("NOR")&&!isCorrected){
            type = "AND";
            isCorrected = true;
            if (inputSignals.size()>0){
                for (InputSignal s: inputSignals)
                    s.setValue(!s.isValue());
            }
            if (inputElements.size()>0){
                for (LogicElement l: inputElements)
                    l.restruct(true);
            }
        }
        if ((type.equals("OR")||type.equals("AND"))&&!isCorrected){
            isCorrected = true;
            if (inputElements.size()>0){
                for (LogicElement l: inputElements)
                    l.restruct(false);
            }
        }

        }
        else
        {
            if (type.equals("NAND")&&!isCorrected){
                isCorrected = true;
                type = "AND";
                if (inputElements.size()>0){
                    for (LogicElement l: inputElements)
                        l.restruct(false);
                }
            }
            if (type.equals("NOR")&&!isCorrected){
                isCorrected = true;
                type = "OR";
                if (inputElements.size()>0){
                    for (LogicElement l: inputElements)
                        l.restruct(false);
                }
            }
            if (type.equals("AND")&&!isCorrected){
                isCorrected = true;
                type = "OR";
                if (inputSignals.size()>0){
                    for (InputSignal s: inputSignals)
                        s.setValue(!s.isValue());
                }
                if (inputElements.size()>0){
                    for (LogicElement l: inputElements)
                        l.restruct(true);
                }
            }
            if (type.equals("OR")&&!isCorrected){
                isCorrected = true;
                type = "AND";
                if (inputSignals.size()>0){
                    for (InputSignal s: inputSignals)
                        s.setValue(!s.isValue());
                }
                if (inputElements.size()>0){
                    for (LogicElement l: inputElements)
                        l.restruct(true);
                }
            }
        }

        System.out.println("Res "+getOutputName()+" "+type);
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

    public void printStructure(){
        System.out.println("Log element: "+getOutputName()+" "+type);
        for(InputSignal i: inputSignals)
            System.out.println(i);
        for (LogicElement l: inputElements)
            System.out.println(l.getOutputName());
        System.out.println("===========================");
        for (LogicElement l: inputElements){
            l.printStructure();
        }
    }

    public StringBuilder convertElementsToString(boolean isRoot){
        StringBuilder res = new StringBuilder();
        if (!isRoot)
        res.append("(");
        for (int i = 0; i< inputSignals.size();i++){
            res.append(inputSignals.get(i).getOutputName());
            if (i<inputSignals.size()-1||inputElements.size()>0)
                res.append(operSign(type));
        }
        for (int i =0; i < inputElements.size();i++){
            res.append(inputElements.get(i).convertElementsToString(false));
            if (i<inputElements.size()-1)
                res.append(operSign(type));
        }
        if (!isRoot)
        res.append(")");
        else res.append(" ");
        return res;
    }

    public ArrayList<LogicElement> toENF(String t){
        ArrayList<LogicElement> res = new ArrayList<LogicElement>();
        if (inputElements.size()>0){




        }





        return  res;
    }

}
