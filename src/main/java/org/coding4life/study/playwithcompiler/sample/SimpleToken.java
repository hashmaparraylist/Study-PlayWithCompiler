package org.coding4life.study.playwithcompiler.sample;

public class SimpleToken implements Token{

    private TokenType type = null;
    private String text = null;

    public TokenType getType() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }
}
