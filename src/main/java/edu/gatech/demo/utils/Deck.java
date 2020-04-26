package edu.gatech.demo.utils;

import java.util.List;

public class Deck {
    private String DeckID;
    private boolean isShuffled;
    private int remainingCards;
    private List<Card> Cards;
    private List<Pile> Piles;
    private String Error;

    public Deck(String deckID, boolean isShuffled, int remainingCards) {
        DeckID = deckID;
        this.isShuffled = isShuffled;
        this.remainingCards = remainingCards;
    }

    public Deck() {
    }

    public String getDeckID() {
        return DeckID;
    }

    public void setDeckID(String deckID) {
        DeckID = deckID;
    }

    public boolean isShuffled() {
        return isShuffled;
    }

    public void setShuffled(boolean shuffled) {
        isShuffled = shuffled;
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

    public List<Pile> getPiles() {
        return Piles;
    }

    public void setPiles(List<Pile> piles) {
        Piles = piles;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }
}
