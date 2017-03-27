package com.entersnowman.kursach.logic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Valentin on 21.03.2017.
 */

public class LogicElement extends AbstractElement{
    ArrayList<LogicElement> inputElements;
    ArrayList<InputSignal> inputSignals;
    String type;
    ArrayList<ArrayList<String>> DNF;
    String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LogicElement(String outputName, String type) {
        inputElements = new ArrayList<LogicElement>();
        inputSignals = new ArrayList<InputSignal>();
        this.outputName = outputName;
        this.type = type;
        DNF = new ArrayList<ArrayList<String>>();
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

    public void nameInputSignalsAsUniqChars(ArrayList<Character> possibleChars, HashMap<String,String > uniqChar_nameWithPath){
        for (int i = 0;i<inputSignals.size();i++){
            inputSignals.get(i).setOutputName(String.valueOf(possibleChars.get(0)));
            String inv = "";
            if (!inputSignals.get(i).isValue())
                inv  ="¬";
            uniqChar_nameWithPath.put(inv+String.valueOf(possibleChars.remove(0)),inputSignals.get(i).getNameWithPath());
        }
        for (int i = 0;i<inputElements.size();i++)
            inputElements.get(i).nameInputSignalsAsUniqChars(possibleChars,uniqChar_nameWithPath);
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
            if(!inputSignals.get(i).isValue())
                res.append("\u00AC");
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

    public void setDNF(String nf){
        setOutputName(nf);
        String[] ands = nf.split("∨");
        for (String s: ands){
            s = s.replaceAll("\\(","");
            s = s.replaceAll("\\)","");
            s = s.replaceAll("\\s","");
            String[] and = s.split("∧");
            ArrayList<String> newAnd = new ArrayList<>();
            for (String a: and)
                newAnd.add(a);
            DNF.add(newAnd);
        }

        for (ArrayList<String> f:DNF){
            for (String s: f)
                System.out.print(s+" ");
            System.out.println();
        }
        System.out.println("=================================");
    }

    public void replaceCharsToPath(HashMap<String,String> uniqChar_nameWithPath){
        for (int i = 0; i<DNF.size();i++)
            for (int j = 0;j<DNF.get(i).size();j++){
                if (DNF.get(i).get(j).substring(0,1).equals("¬"))
                DNF.get(i).set(j,"¬"+uniqChar_nameWithPath.get(DNF.get(i).get(j)));
                else
                 DNF.get(i).set(j,uniqChar_nameWithPath.get(DNF.get(i).get(j)));
        }
        System.out.println("After replacing");
        for (ArrayList<String> f:DNF){
            for (String s: f)
                System.out.print(s+" ");
            System.out.println();
        }
        System.out.println("=================================");
    }

    public ArrayList<String> getAllInputSignals(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0;i<inputSignals.size();i++)
            res.add(inputSignals.get(i).getOutputName());
        for (int i = 0;i<inputElements.size();i++)
            res.addAll(inputElements.get(i).getAllInputSignals());
        return res;
    }


}
