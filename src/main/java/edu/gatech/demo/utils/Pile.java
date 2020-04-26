package edu.gatech.demo.utils;

import java.util.List;

public class Pile {
    private String name;
    private int remainingCards;
    private List<Card> Cards;

    public Pile(String name, int remainingCards) {
        this.name = name;
        this.remainingCards = remainingCards;
    }

    public Pile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemainingCards() {
        return remainingCards;
    }

    public void setRemainingCards(int remainingCards) {
        this.remainingCards = remainingCards;
    }

    public List<Card> getCards() {
        return Cards;
    }

    public void setCards(List<Card> cards) {
        Cards = cards;
    }
}
