package org.coding4life.study.playwithcompiler.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一个简单的AST节点的实现。
 * 属性包括：类型、文本值、父节点、子节点。
 * @author QuSheng
 */
public class SimpleASTNode implements ASTNode {

    private SimpleASTNode parent;
    private List<ASTNode> children = new ArrayList<>();
    private List<ASTNode> readonlyChildren = Collections.unmodifiableList(children);
    private ASTNodeType nodeType = null;
    private String text = null;

    public SimpleASTNode(ASTNodeType nodeType, String text) {
        this.nodeType = nodeType;
        this.text = text;
    }

    @Override
    public ASTNode getParent() {
        return this.parent;
    }

    @Override
    public void setParent(ASTNode node) {

    }

    @Override
    public List<ASTNode> getChildren() {
        return this.readonlyChildren;
    }

    @Override
    public void setChildren(List<ASTNode> node) {
    }

    @Override
    public ASTNodeType getType() {
        return this.nodeType;
    }

    @Override
    public void setType(ASTNodeType type) {
        this.nodeType = type;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    public void addChild(SimpleASTNode child) {
        this.children.add(child);
        child.parent = this;
    }
}
