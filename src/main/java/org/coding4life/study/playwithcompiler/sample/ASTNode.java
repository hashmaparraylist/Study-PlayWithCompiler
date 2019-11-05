package org.coding4life.study.playwithcompiler.sample;

import java.util.List;

public interface ASTNode {
    ASTNode getParent();
    void setParent(ASTNode node);
    List<ASTNode> getChildren();
    void setChildren(List<ASTNode> node);
    ASTNodeType getType();
    void setType(ASTNodeType type);
    String getText();
    void setText(String text);
}
