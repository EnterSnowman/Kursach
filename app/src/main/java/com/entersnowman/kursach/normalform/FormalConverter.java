package com.entersnowman.kursach.normalform;

import com.entersnowman.kursach.logic.core.Constants;

/**
 *
 * @author chavez
 */
public class FormalConverter {
    private char token;
    private String resultExpression;
    private final String theExpression;
    private int i = 0;
    
    public FormalConverter(String theExpression) {
        this.theExpression = theExpression;
        resultExpression = "";
        System.out.println(this.theExpression);
    }
    
    private void nextToken() {
        if (i < theExpression.length()) {
            char ch = theExpression.charAt(i);
            if (Constants.isVoid(ch)) {
                return;
            } else if ( Constants.isVariable(ch) ) {
                token = ch;
            } else if ( Constants.isAND(ch) ) {
                token = Constants.AND;
            } else if ( Constants.isOR(ch) ) {
                token = Constants.OR;
            } else if ( Constants.isNOT(ch) ) {
                token = Constants.NOT;
            } else if ( Constants.isLEFT_PAR(ch) ) {
                token = Constants.LEFT_PAR;
            } else if ( Constants.isRIGHT_PAR(ch) ) {
                token = Constants.RIGHT_PAR;
            } else if ( Constants.isIMPLIES(ch) ) {
                token = Constants.IMPLIES;
            }
            i++;
        }
    }
    
    private TokenTreeNode procesaOperando() {
	TokenTreeNode t = null;
        if ( Constants.isVariable(token) ) {
            t = new TokenTreeNode(null,token, null);
            nextToken();
        } else if(token == Constants.NOT) {
            nextToken();
            t = new TokenTreeNode(Constants.NOT, procesaOperando());
        } else if (token == Constants.LEFT_PAR) {
            nextToken();
            t = startWFF();
            if (token == Constants.RIGHT_PAR) {
                nextToken();
            }
        }
        return t;
    }
    
    private TokenTreeNode startWFF() {
        return procesaOperador(Constants.IMPLIES);
    }
    
    /* Parse a sequence of one or more nonterminal(s) separated by value(s) */
    private TokenTreeNode procesaOperador(char operator) {
        TokenTreeNode t;
        if (operator == Constants.AND) {
            t = procesaOperando();
        } else if (operator == Constants.OR) {
            t = procesaOperador(Constants.AND);
        } else {
            t = procesaOperador(Constants.OR);
        }
        while ( token == operator ) {
            char tempActual = token;
            nextToken();
            if (operator == Constants.AND) {
                t = new TokenTreeNode(t, tempActual, procesaOperando());
            } else if (operator == Constants.OR) {
                t = new TokenTreeNode(t, tempActual, procesaOperador(Constants.AND));
            } else {
                t = new TokenTreeNode(t, tempActual, procesaOperador(Constants.OR));
            }
        }
        return t;
    }
    
    private TokenTreeNode removeImplies(TokenTreeNode t) {
        if (t.value == Constants.IMPLIES) {
            t = new TokenTreeNode(new TokenTreeNode(Constants.NOT, removeImplies(t.leftNode)),
                                Constants.OR,
                                removeImplies(t.rightNode));
        } else if (t.value == Constants.AND || t.value == Constants.OR) {
            t.leftNode  = removeImplies(t.leftNode);
            t.rightNode = removeImplies(t.rightNode);
        } else if (t.value == Constants.NOT) {
            t.negatedExpression = removeImplies(t.negatedExpression);
        }
        return t;
    }
    
    private TokenTreeNode pushNots(TokenTreeNode t) {
        if (t.value == Constants.NOT) {
            if (t.negatedExpression.value == Constants.AND) {
                t = new TokenTreeNode(pushNots(new TokenTreeNode(Constants.NOT, t.negatedExpression.leftNode)),
                                    Constants.OR, 
                                    pushNots(new TokenTreeNode(Constants.NOT, t.negatedExpression.rightNode)));
            } else if (t.negatedExpression.value == Constants.OR) {
                t = new TokenTreeNode(pushNots(new TokenTreeNode(Constants.NOT, t.negatedExpression.leftNode)),
                                    Constants.AND,
                                    pushNots(new TokenTreeNode(Constants.NOT, t.negatedExpression.rightNode)));
            } else if (t.negatedExpression.value == Constants.NOT) {
                t = pushNots(t.negatedExpression.negatedExpression);
            }
        } else if (t.value == Constants.AND || t.value == Constants.OR) {
            t.leftNode  = pushNots(t.leftNode);
            t.rightNode = pushNots(t.rightNode);
        }
        return t;
    }
    
    private TokenTreeNode distribute(char op1, char op2, TokenTreeNode t) {
        if (t.value == op2) {
            t.leftNode = distribute(op1, op2, t.leftNode);
            t.rightNode= distribute(op1, op2, t.rightNode);
        } else if (t.value == op1) {
            TokenTreeNode p = distribute(op1, op2, t.leftNode);
            TokenTreeNode q = distribute(op1, op2, t.rightNode);
            if (p.value == op2) {
                t = new TokenTreeNode(distribute(op1, op2, new TokenTreeNode(p.leftNode, op1, q)),
                                    op2,
                                    distribute(op1, op2, new TokenTreeNode(p.rightNode,op1, q)));
            } else if(q.value == op2) {
                t = new TokenTreeNode(distribute(op1, op2, new TokenTreeNode(p, op1, q.leftNode)),
                                    op2,
                                    distribute(op1, op2, new TokenTreeNode(p, op1, q.rightNode)));
            } else {
                t.leftNode = p;
                t.rightNode = q;
            }
        }
        return t;
    }
    
    private TokenTreeNode convertToDNF(TokenTreeNode t) {
        return distribute(Constants.AND, Constants.OR, pushNots(removeImplies(t)));
    }
    
    private TokenTreeNode convertToCNF(TokenTreeNode t) {
        return distribute(Constants.OR, Constants.AND, pushNots(removeImplies(t)));
    }
    
    public void convert() {
        nextToken();
        TokenTreeNode wff = startWFF();
        //Constants.println();
        
        //Constants.println("Well formed formula: ");
        processConvert(wff);
        resultExpression = resultExpression.substring(1, resultExpression.length()-1);
        //Constants.println(resultExpression);
        
        /* CNF */
       // Constants.println();
        resultExpression = "";
        //Constants.println("Conjunctive Normal Form: ");
        TokenTreeNode cnf = convertToCNF(wff);
        processConvert(cnf);
        resultExpression = resultExpression.substring(1, resultExpression.length()-1);
        //Constants.println(resultExpression);
        
        /* DNF */
        //Constants.println();
        resultExpression = "";
        //Constants.println("Disjunctive Normal Form: ");
        TokenTreeNode dnf = convertToDNF(wff);
        processConvert(dnf);
        resultExpression = resultExpression.substring(1, resultExpression.length()-1);
        //Constants.print(resultExpression);
        
        //System.err.println("maxLevel:"+BinaryTreePrinter.maxLevel(wff));
    }
    
    public String convertToCNF() {
        nextToken();
        TokenTreeNode wff = startWFF();
        processConvert(wff);
        resultExpression = resultExpression.substring(1, resultExpression.length()-1);
        
        /* CNF */
        resultExpression = "";
        TokenTreeNode cnf = convertToCNF(wff);
        processConvert(cnf);
        resultExpression = resultExpression.substring(1, resultExpression.length()-1);
        
        return resultExpression;
    }
    
    public String convertToDNF() {
        nextToken();
        TokenTreeNode wff = startWFF();
        processConvert(wff);
        resultExpression = resultExpression.substring(1, resultExpression.length()-1);
        
        /* DNF */
        resultExpression = "";
        TokenTreeNode dnf = convertToDNF(wff);
        processConvert(dnf);
        resultExpression = resultExpression.substring(1, resultExpression.length()-1);
        
        return resultExpression;
    }
    
    private void processConvert(TokenTreeNode t) {
        if (t != null) {
            if ( Constants.isVariable(t.value) ) {
                resultExpression += t.value;
            } else if ( Constants.isAND(t.value) || Constants.isOR(t.value) || Constants.isIMPLIES(t.value) ) {
                resultExpression += "(";
                processConvert(t.leftNode);
                String s;
                if ( Constants.isAND(t.value) ) {
                    s = " " + Constants.AND + " ";
                } else if ( Constants.isOR(t.value) ) {
                    s = " " + Constants.OR + " ";
                } else {
                    s = " " + Constants.IMPLIES + " ";
                }
                resultExpression += s;
                processConvert(t.rightNode);
                resultExpression += ")";
            } else if ( Constants.isNOT(t.value) ) {
                resultExpression += "" + Constants.NOT;
                processConvert(t.negatedExpression);
            }
        }
    }
    
}
