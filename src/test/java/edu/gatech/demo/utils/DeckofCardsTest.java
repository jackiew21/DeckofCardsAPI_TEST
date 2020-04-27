package edu.gatech.demo.utils;

import com.sun.deploy.util.StringUtils;
import edu.gatech.demo.domain.DeckofCards;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class DeckofCardsTest {

    Deck defaultDeck = new Deck();
    int deckIDLength = 12;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
    }

    //region 1. Shuffle the Cards
    //https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1

    /**
     * Test Case 1: deck count=0,
     * Test Case 2: deck count=1,
     * Test Case 3: deck count=6
     */
    @Test
    public void testShuffleCards1() {
        //Test Case 1: deck count=0
        Deck tc1 = DeckofCards.shuffleCards(0);
        assertEquals(tc1.getDeckID().length(), deckIDLength);
        assertEquals(tc1.getRemainingCards(), 0);
        assertEquals(tc1.isShuffled(), true);
    }

    @Test
    public void testShuffleCards2() {
        //Test Case 2: deck count=1,
        Deck tc2 = DeckofCards.shuffleCards(1);
        assertEquals(tc2.getDeckID().length(), deckIDLength);
        assertEquals(tc2.getRemainingCards(), 52);
        assertEquals(tc2.isShuffled(), true);
    }

    @Test
    public void testShuffleCards3() {
        //Test Case 3: deck count=6,
        Deck tc3 = DeckofCards.shuffleCards(6);
        assertEquals(tc3.getDeckID().length(), deckIDLength);
        assertEquals(tc3.getRemainingCards(), 312);
        assertEquals(tc3.isShuffled(), true);
    }
    //endregion

    //region 2. Draw a Card
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/draw/?count=2

    /*Test Case 1: invalid deck id
     * Test Case 2: valid deck id, count=2
     * Test Case 3: valid deck id, count=0
     * Test Case 4: valid deck id, count=10
     * Test Case 5: valid deck id, count=52
     * Test Case 6: valid deck id, count=54*/
    @Test(expected = AssertionError.class)
    public void testDrawACard1() {
        //Test Case 1: invalid deck id
        //exceptionRule.expect(RuntimeException.class);
        //exceptionRule.expectMessage("HttpResponseCode: 500");
        try {
            Deck deck = new Deck();
            deck.setDeckID("123");
            DeckofCards.drawACard(deck, 1);
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 500";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Invalid Deck ID!");
    }

    @Test
    public void testDrawACard2() {
        //Test Case 2: valid deck id, count=2
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        DeckofCards.drawACard(deck, 2);
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 50);
        assertEquals(deck.getCards().size(), 2);
    }

    @Test
    public void testDrawACard3() {
        //Test Case 3: valid deck id, count=0
        //{"success": true, "deck_id": "eatvov5bb6i0", "cards": [], "remaining": 52}
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        DeckofCards.drawACard(deck, 0);
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 52);
    }

    @Test
    public void testDrawACard4() {
        //Test Case 4: valid deck id, count=10
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        DeckofCards.drawACard(deck, 10);
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 42);
    }

    @Test
    public void testDrawACard5() {
        //Test Case 5: valid deck id, count=52
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        DeckofCards.drawACard(deck, 52);
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 0);

        //Verify all 52 cards in the cards list
        assertEquals(DeckofCards.verifyAllCards(deck.getCards()), true);
    }

    @Test
    public void testDrawACard6() {
        //Test Case 6: valid deck id, count=54
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        DeckofCards.drawACard(deck, 54);
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 0);

        //Verify all 52 cards in the cards list
        assertEquals(DeckofCards.verifyAllCards(deck.getCards()), true);

        //Verify error message received
        assertEquals(deck.getError(), "Not enough cards remaining to draw 54 additional");
    }
    //endregion

    //region 3. Reshuffle the Cards
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/shuffle/

    /*Test Case 1: normal shuffle with valid deck id
     * Test Case 2: invalid deck id*/
    @Test
    public void testReshuffleCards1() {
        //Test Case 1: normal shuffle with valid deck id
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        DeckofCards.reshuffleCards(deck);
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 52);
        assertEquals(deck.isShuffled(), true);
    }

    @Test(expected = AssertionError.class)
    public void testReshuffleCards2() {
        //Test Case 2: invalid deck id
        //exceptionRule.expect(RuntimeException.class);
        //exceptionRule.expectMessage("HttpResponseCode: 500");
        try {
            Deck deck = new Deck();
            deck.setDeckID("123");
            DeckofCards.reshuffleCards(deck);
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 500";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Invalid Deck ID!");
    }
    //endregion

    //region 4. A Brand New Deck
    //https://deckofcardsapi.com/api/deck/new?jokers_enabled=true

    /*Test Case 1: jokers_enabled false
     * Test Case 2: jokers_enabled true */

    @Test
    public void testBrandNewDeck1() {
        //Test Case 1: jokers_enabled false
        Deck tc1 = DeckofCards.brandNewDeck(false);
        assertEquals(tc1.getDeckID().length(), deckIDLength);
        assertEquals(tc1.getRemainingCards(), 52);
        assertEquals(tc1.isShuffled(), false);
    }

    @Test
    public void testBrandNewDeck2() {
        //Test Case 2: jokers_enabled true
        Deck tc2 = DeckofCards.brandNewDeck(true);
        assertEquals(tc2.getDeckID().length(), deckIDLength);
        assertEquals(tc2.getRemainingCards(), 54);
        assertEquals(tc2.isShuffled(), false);
    }
    //endregion

    //region 5. A Partial Deck
    //https://deckofcardsapi.com/api/deck/new/shuffle/?cards=AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH
    /*If you want to use a partial deck, then you can pass the card codes you want to use using the cards parameter.
    Separate the card codes with commas, and each card code is a just a two character case-insensitive string:

    The value, one of A (for an ace), 2, 3, 4, 5, 6, 7, 8, 9, 0 (for a ten), J (jack), Q (queen), or K (king);
    The suit, one of S (Spades), D (Diamonds), C (Clubs), or H (Hearts).*/

    /*Test Case 1: empty value to cards
     * Test Case 2: invalid value to cards
     * Test Case 3: 12 valid value to cards*/

    @Test
    public void testPartialDeck1() {
        //Test Case 1: empty value to cards
        Deck tc1 = DeckofCards.partialDeck("");
        assertEquals(tc1.getDeckID().length(), deckIDLength);
        assertEquals(tc1.getRemainingCards(), 0);
        assertEquals(tc1.isShuffled(), true);
    }

    @Test
    public void testPartialDeck2() {
        //Test Case 2: invalid value to cards
        Deck tc1 = DeckofCards.partialDeck("2S,2D,11J,asdf");
        assertEquals(tc1.getDeckID().length(), deckIDLength);
        assertEquals(tc1.getRemainingCards(), 2);
        assertEquals(tc1.isShuffled(), true);

        DeckofCards.drawACard(tc1, 2);
        List<Card> cards = tc1.getCards();
        assertEquals(DeckofCards.verifyCardsMatchWithCodes((Arrays.asList("2S,2D".split(","))), cards), true);
    }

    @Test
    public void testPartialDeck3() {
        //Test Case 3: 12 valid value to cards
        Deck tc1 = DeckofCards.partialDeck("AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH");
        assertEquals(tc1.getDeckID().length(), deckIDLength);
        assertEquals(tc1.getRemainingCards(), 12);
        assertEquals(tc1.isShuffled(), true);

        DeckofCards.drawACard(tc1, 12);
        List<Card> cards = tc1.getCards();
        assertEquals(DeckofCards.verifyCardsMatchWithCodes((Arrays.asList("AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH".split(","))), cards), true);
    }
    //endregion

    //region 6. Adding to Piles
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/pile/<<pile_name>>/add/?cards=AS,2S

    /*Piles can be used for discarding, players hands, or whatever else. Piles are created on the fly,
    just give a pile a name and add a drawn card to the pile. If the pile didn't exist before, it does now.
    After a card has been drawn from the deck it can be moved from pile to pile.*/

    /*Test Case 1: invalid deck id
     * Test Case 2: all Digit pile name
     * Test Case 3: normal pile name with valid cards
     * Test Case 4: pile with adding multiple times valid cards
     * Test Case 5: two normal piles with valid cards
     * Test Case 6: pile with invalid cards
     * Test Case 7: add to pile without draw cards
     * Test Case 8: drawn card from pile to pile*/

    @Test(expected = AssertionError.class)
    public void testAddingToPiles1() {
        //Test Case 1: invalid deck id
        try {
            Deck deck = new Deck();
            deck.setDeckID("123");
            DeckofCards.addingToPiles(deck, "discard", "2S,3S");
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 500";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Invalid Deck ID!");
    }

    @Test
    public void testAddingToPiles2() {
        //Test Case 2: all Digit pile name
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "12345";

        //Draw cards
        DeckofCards.drawACard(deck, 2);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        //Add to pile
        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 50);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 2);
    }

    @Test
    public void testAddingToPiles3() {
        //Test Case 3: normal pile name with valid cards
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);
    }

    @Test
    public void testAddingToPiles4() {
        //Test Case 4: pile with adding multiple times valid cards
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        DeckofCards.drawACard(deck, 5);
        codes = DeckofCards.generateCodesFromCards(deck.getCards());
        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes.subList(0,3), ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 44);

        assertEquals(deck.getPiles().size(), 1);
        pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 6);
    }

    @Test
    public void testAddingToPiles5() {
        //Test Case 4: two normal piles with valid cards
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        //Player1
        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        //Player2
        pileName = "Player2";
        DeckofCards.drawACard(deck, 2);
        codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 47);

        assertEquals(deck.getPiles().size(), 2);
        for (Pile _pile : deck.getPiles()) {
            if (_pile.getName().equals(pileName)) {
                //assertEquals(_pile.getName(), pileName);
                assertEquals(_pile.getRemainingCards(), 2);
                break;
            }
        }
    }

    @Test
    public void testAddingToPiles6() {
        //Test Case 5: pile with invalid cards
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.addingToPiles(deck, pileName, "65S,34H");
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 52);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 0);
    }

    @Test
    public void testAddingToPiles7() {
        //Test Case 6: add to pile without draw cards
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.addingToPiles(deck, pileName, "6S,3H");
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 52);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 0);
    }

    @Test
    public void testAddingToPiles8() {
        //Test Case 7: drawn card from pile to pile
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        //Player1
        String pileName1 = "Player1";

        DeckofCards.drawACard(deck, 5);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName1, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 47);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName1);
        assertEquals(pile.getRemainingCards(), 5);

        //Player2
        String pileName2 = "Player2";

        DeckofCards.addingToPiles(deck, pileName2, StringUtils.join(codes.subList(0, 2), ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 47);

        assertEquals(deck.getPiles().size(), 2);
        for (Pile _pile : deck.getPiles()) {
            if (_pile.getName().equals(pileName1)) {
                //assertEquals(_pile.getName(), pileName);
                assertEquals(_pile.getRemainingCards(), 3);
            } else if (_pile.getName().equals(pileName2)) {
                assertEquals(_pile.getRemainingCards(), 2);
            }
        }
    }
    //endregion

    //region 7. Shuffle Piles
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/pile/<<pile_name>>/shuffle/

    /*Test Case 1: invalid deck id
     * Test Case 2: invalid pile name
     * Test Case 3: normal case with valid deck id and pile name*/

    @Test(expected = AssertionError.class)
    public void testShufflePiles1() {
        //Test Case 1: invalid deck id
        try {
            Deck deck = new Deck();
            deck.setDeckID("123");
            DeckofCards.shufflePiles(deck, new Pile("Player1", 2));
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 500";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Invalid Deck ID!");
    }

    @Test(expected = AssertionError.class)
    public void testShufflePiles2() {
        //Test Case 2: invalid pile name
        try {
            Deck deck = new Deck();
            deck = DeckofCards.shuffleCards(1);
            DeckofCards.shufflePiles(deck, new Pile("Player1", 2));
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 500";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Invalid Pile Name!");
    }

    @Test
    public void testShufflePiles3() {
        //Test Case 3: normal case with valid deck id and pile name
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 7);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 45);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 7);
        DeckofCards.listingPileCards(deck, pile);
        List<Card> beforeShuffle = DeckofCards.deepCopyCards(deck.getPiles().get(0).getCards());

        DeckofCards.shufflePiles(deck, pile);
        assertEquals(deck.getPiles().size(), 1);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 7);
        DeckofCards.listingPileCards(deck, pile);
        List<Card> afterShuffle = DeckofCards.deepCopyCards(deck.getPiles().get(0).getCards());

        assertEquals(DeckofCards.verifyIsShuffled(beforeShuffle, afterShuffle), true);
    }
    //endregion

    //region 8. Listing Cards in Piles
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/pile/<<pile_name>>/list/

    /*Test Case 1: invalid deck id
     * Test Case 2: invalid pile name
     * Test Case 3: list all cards for one pile
     * Test Case 4: list all cards for two piles*/

    @Test(expected = AssertionError.class)
    public void testListingPileCards1() {
        //Test Case 1: invalid deck id
        try {
            Deck deck = new Deck();
            deck.setDeckID("123");
            DeckofCards.listingPileCards(deck, new Pile("Player1", 2));
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 500";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Invalid Deck ID!");
    }

    @Test(expected = AssertionError.class)
    public void testListingPileCards2() {
        //Test Case 2: invalid pile name
        try {
            Deck deck = new Deck();
            deck = DeckofCards.shuffleCards(1);
            DeckofCards.listingPileCards(deck, new Pile("Player1", 2));
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 500";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Invalid Pile Name!");
    }

    @Test
    public void testListingPileCards3() {
        //Test Case 3: list all cards for one pile
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<Card> cards = DeckofCards.deepCopyCards(deck.getCards());
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        //list all cards and compare
        DeckofCards.listingPileCards(deck, pile);
        assertEquals(DeckofCards.verifyCardsMatch(deck.getPiles().get(0).getCards(), cards), true);
    }

    @Test
    public void testListingPileCards4() {
        //Test Case 4: list all cards for two piles
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<Card> cards = DeckofCards.deepCopyCards(deck.getCards());
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        //list all cards and compare
        DeckofCards.listingPileCards(deck, pile);
        assertEquals(DeckofCards.verifyCardsMatch(deck.getPiles().get(0).getCards(), cards), true);

        //Player 2
        String pileName2 = "Player2";

        DeckofCards.drawACard(deck, 3);
        cards = DeckofCards.deepCopyCards(deck.getCards());
        codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName2, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 46);

        assertEquals(deck.getPiles().size(), 2);
        pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName2);
        assertEquals(pile.getRemainingCards(), 3);

        //list all cards and compare
        DeckofCards.listingPileCards(deck, pile);
        assertEquals(DeckofCards.verifyCardsMatch(deck.getPiles().get(0).getCards(), cards), true);
    }
    //endregion

    //region 9. Drawing from Piles
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/pile/<<pile_name>>/draw/?cards=AS
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/pile/<<pile_name>>/draw/?count=2
    //https://deckofcardsapi.com/api/deck/<<deck_id>>/draw/bottom/

    /*Test Case 1: drawing special cards from one pile
     * Test Case 2: drawing special cards from one pile which is not present
     * Test Case 3: drawing invalid cards from one pile
     * Test Case 4: drawing count 2 cards from pile
     * Test Case 5: drawing special count cards larger than the counts of pile cards
     * Test Case 6: drawing 0 count cards from pile
     * Test Case 7: normal drawing from top
     * Test Case 8: special drawing from bottom*/

    @Test
    public void testDrawingFromPiles1() {
        //Test Case 1: drawing special cards from one pile
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        DeckofCards.drawingFromPiles(deck, pile, codes.get(0), false);
        pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 2);
        assertEquals(deck.getCards().get(0).getCodeValue(), codes.get(0));
    }

    @Test(expected = AssertionError.class)
    public void testDrawingFromPiles2() {
        //Test Case 2: drawing special cards from one pile which is not present
        try {
            Deck deck = DeckofCards.shuffleCards(1);
            String _deckID = deck.getDeckID();

            String pileName = "Player1";

            DeckofCards.drawACard(deck, 3);
            List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

            DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
            assertEquals(deck.getDeckID(), _deckID);
            assertEquals(deck.getRemainingCards(), 49);

            assertEquals(deck.getPiles().size(), 1);
            Pile pile = deck.getPiles().get(0);
            assertEquals(pile.getName(), pileName);
            assertEquals(pile.getRemainingCards(), 3);

            String notPresentCode = DeckofCards.generateOneNotPresentInList(codes);
            DeckofCards.drawingFromPiles(deck, pile, notPresentCode, false);
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 404";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Not Found!");
    }

    @Test(expected = AssertionError.class)
    public void testDrawingFromPiles3() {
        //Test Case 3: drawing invalid cards from one pile
        try {
            Deck deck = DeckofCards.shuffleCards(1);
            String _deckID = deck.getDeckID();

            String pileName = "Player1";

            DeckofCards.drawACard(deck, 3);
            List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

            DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
            assertEquals(deck.getDeckID(), _deckID);
            assertEquals(deck.getRemainingCards(), 49);

            assertEquals(deck.getPiles().size(), 1);
            Pile pile = deck.getPiles().get(0);
            assertEquals(pile.getName(), pileName);
            assertEquals(pile.getRemainingCards(), 3);

            DeckofCards.drawingFromPiles(deck, pile, "57S,19H", false);
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 404";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Not Found!");
    }

    @Test
    public void testDrawingFromPiles4() {
        //Test Case 4: drawing count 2 cards from pile
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        DeckofCards.drawingFromPiles(deck, pile, "2", false);
        pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 1);
        assertEquals(deck.getCards().size(), 2);
    }

    @Test(expected = AssertionError.class)
    public void testDrawingFromPiles5() {
        //Test Case 5: drawing special count cards larger than the counts of pile cards
        try {
            Deck deck = DeckofCards.shuffleCards(1);
            String _deckID = deck.getDeckID();

            String pileName = "Player1";

            DeckofCards.drawACard(deck, 3);
            List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

            DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
            assertEquals(deck.getDeckID(), _deckID);
            assertEquals(deck.getRemainingCards(), 49);

            assertEquals(deck.getPiles().size(), 1);
            Pile pile = deck.getPiles().get(0);
            assertEquals(pile.getName(), pileName);
            assertEquals(pile.getRemainingCards(), 3);

            DeckofCards.drawingFromPiles(deck, pile, "7", false);
        } catch (AssertionError re) {
            String error = "HttpResponseCode: 404";
            assertEquals(error, re.getMessage());
            throw re;
        }
        fail("Not Found!");
    }

    @Test
    public void testDrawingFromPiles6() {
        //Test Case 6: drawing 0 count cards from pile
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        //Error Found: if drawing count 0 from pile, no cards should be drawn, but actual drawn all cards
        DeckofCards.drawingFromPiles(deck, pile, "0", false);
        pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);
        assertEquals(deck.getCards().size(), 0);
    }

    @Test
    public void testDrawingFromPiles7() {
        //Test Case 7: normal drawing from top
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        DeckofCards.drawingFromPiles(deck, pile, "", false);
        pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 2);
        assertEquals(deck.getCards().size(), 1);
        assertEquals(deck.getCards().get(0).getCodeValue(), codes.get(codes.size() - 1));
    }

    @Test
    public void testDrawingFromPiles8() {
        //Test Case 8: special drawing from bottom
        Deck deck = DeckofCards.shuffleCards(1);
        String _deckID = deck.getDeckID();

        String pileName = "Player1";

        DeckofCards.drawACard(deck, 3);
        List<String> codes = DeckofCards.generateCodesFromCards(deck.getCards());

        DeckofCards.addingToPiles(deck, pileName, StringUtils.join(codes, ","));
        assertEquals(deck.getDeckID(), _deckID);
        assertEquals(deck.getRemainingCards(), 49);

        assertEquals(deck.getPiles().size(), 1);
        Pile pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 3);

        DeckofCards.drawingFromPiles(deck, pile, "", true);
        pile = deck.getPiles().get(0);
        assertEquals(pile.getName(), pileName);
        assertEquals(pile.getRemainingCards(), 2);
        assertEquals(deck.getCards().size(), 1);
        assertEquals(deck.getCards().get(0).getCodeValue(), codes.get(0));
    }
    //endregion
}