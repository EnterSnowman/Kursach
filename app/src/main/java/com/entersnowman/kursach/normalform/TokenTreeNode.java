package com.entersnowman.kursach.normalform;

/**
 *
 * @author chavez
 */
public class TokenTreeNode {
    public char value;
    public TokenTreeNode leftNode;
    public TokenTreeNode rightNode;
    
    public TokenTreeNode negatedExpression;
    
    public TokenTreeNode(TokenTreeNode leftNode, char type, TokenTreeNode rightNode) {
        this.value = type;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }
    
    public TokenTreeNode(char type, TokenTreeNode negatedExpression) {
        this.value = type;
        this.negatedExpression = negatedExpression;
    }
    
    public TokenTreeNode(char type) {
        this.value = type;
    }
}
