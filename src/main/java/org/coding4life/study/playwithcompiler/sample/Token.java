package org.coding4life.study.playwithcompiler.sample;

/**
 * 一个简单的Token。
 * 只有类型和文本值两个属性。
 */
public interface Token {
    /**
     * Token的类型
     * @return
     */
    TokenType getType();

    /**
     * Token的文本值
     * @return
     */
    String getText();

    /**
     *
     * @param type
     */
    void setType(TokenType type);

    /**
     *
     * @param text
     */
    void setText(String text);
}
