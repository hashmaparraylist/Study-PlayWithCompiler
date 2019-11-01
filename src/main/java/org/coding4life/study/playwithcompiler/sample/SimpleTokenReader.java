package org.coding4life.study.playwithcompiler.sample;

import java.util.List;

/**
 * 一个简单的Token流。是把一个Token列表进行了封装。
 */
public class SimpleTokenReader implements TokenReader{
    private List<Token> tokens = null;
    private int pos = 0;

    public SimpleTokenReader(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token read() {
        if (this.pos < tokens.size()) {
            return tokens.get(this.pos++);
        }

        return null;
    }

    public Token peek() {
        if (this.pos < tokens.size()) {
            return tokens.get(this.pos);
        }
        return null;
    }

    public void unread() {
        if (this.pos > 0) {
            this.pos--;
        }
    }

    public int getPosition() {
        return this.pos;
    }

    public void setPosition(int position) {
        if (position >= 0 && position < tokens.size()) {
            this.pos = position;
        }
    }
}
