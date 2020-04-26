package edu.gatech.demo.utils;

import java.util.Objects;

public class Card {
    private SUIT suit;
    private int faceValue;
    private String codeValue;

    public Card(SUIT suit, int faceValue, String codeValue) {
        this.suit = suit;
        this.faceValue = faceValue;
        this.codeValue = codeValue;
    }

    public Card() {
    }

    public SUIT getSuit() {
        return suit;
    }

    public void setSuit(SUIT suit) {
        this.suit = suit;
    }

    public int getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(int faceValue) {
        this.faceValue = faceValue;
    }

    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return faceValue == card.faceValue &&
                suit == card.suit &&
                Objects.equals(codeValue, card.codeValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, faceValue, codeValue);
    }
}
