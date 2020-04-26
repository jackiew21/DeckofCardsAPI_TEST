package edu.gatech.demo.domain;

import edu.gatech.demo.utils.Deck;
import edu.gatech.demo.utils.SUIT;
import edu.gatech.demo.utils.Card;
import edu.gatech.demo.utils.Pile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class DeckofCards {
    public static Map<Character, SUIT> suitMap = new HashMap<Character, SUIT>() {
        {
            put('H', SUIT.HEART);
            put('S', SUIT.SPADE);
            put('C', SUIT.CLUB);
            put('D', SUIT.DIAMOND);
        }
    };
    public static Map<Character, Integer> codeMap = new HashMap<Character, Integer>() {
        {
            put('A', 1);
            put('2', 2);
            put('3', 3);
            put('4', 4);
            put('5', 5);
            put('6', 6);
            put('7', 7);
            put('8', 8);
            put('9', 9);
            put('0', 10);
            put('J', 11);
            put('Q', 12);
            put('K', 13);
        }
    };

    private static String ShuffleCardsAPI = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count={0}";
    private static String DrawACardAPI = "https://deckofcardsapi.com/api/deck/{0}/draw/?count={1}";
    private static String ReshuffleCardsAPI = "https://deckofcardsapi.com/api/deck/{0}/shuffle/";
    private static String BrandNewDeckAPI = "https://deckofcardsapi.com/api/deck/new?jokers_enabled={0}";
    private static String PartialDeckAPI = "https://deckofcardsapi.com/api/deck/new/shuffle/?cards={0}";
    private static String AddingToPilesAPI = "https://deckofcardsapi.com/api/deck/{0}/pile/{1}/add/?cards={2}";
    private static String ShufflePilesAPI = "https://deckofcardsapi.com/api/deck/{0}/pile/{1}/shuffle/";
    private static String ListingPileCardsAPI = "https://deckofcardsapi.com/api/deck/{0}/pile/{1}/list/";
    private static String DrawingFromPilesAPI = "https://deckofcardsapi.com/api/deck/{0}/pile/{1}/draw/";
    private static String DrawingFromPilesAPI_Cards = "https://deckofcardsapi.com/api/deck/{0}/pile/{1}/draw/?cards={2}";
    private static String DrawingFromPilesAPI_Count = "https://deckofcardsapi.com/api/deck/{0}/pile/{1}/draw/?count={2}";
    private static String DrawingFromPilesAPI_Bottom = "https://deckofcardsapi.com/api/deck/{0}/pile/{1}/draw/bottom/";

    //region helper
    private static JSONObject sendURL(String urlStr, String requestMethod) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);//"GET","POST"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();
            int responsecode = conn.getResponseCode();

            if (responsecode != 200)
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            else {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //System.out.println(response);
                JSONParser parse = new JSONParser();
                JSONObject jsonObject = (JSONObject) parse.parse(response.toString());
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Card> retrieveCards(JSONArray array) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            String code = object.get("code").toString();
            int value = codeMap.get(code.charAt(0));

            Card card = new Card(suitMap.get(code.charAt(1)), value, code);
            cards.add(card);
        }
        return cards;
    }

    private static List<Pile> retrievePiles(Deck deck, JSONObject array) {
        List<Pile> piles = new ArrayList<>();

        for (Object pileName : array.keySet()) {
            JSONObject object = (JSONObject) array.get(pileName);
            int remainingValue = Integer.valueOf(object.get("remaining").toString());

            Pile pile = new Pile((String) pileName, remainingValue);
            if (object.containsKey("cards")) {
                List<Card> cards = retrieveCards((JSONArray) object.get("cards"));
                pile.setCards(cards);

                //Move card from deck add to pile
                for (Card _card : cards)
                    deck.getCards().remove(_card);
            }

            piles.add(pile);
        }
        return piles;
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean verifyAllCards(List<Card> cards) {
        List<String> sample = new ArrayList<String>();
        for (Character suit : DeckofCards.suitMap.keySet()) {
            for (Character code : DeckofCards.codeMap.keySet())
                sample.add(String.valueOf(code) + String.valueOf(suit));
        }

        for (Card _card : cards) {
            if (!sample.contains(_card.getCodeValue()))
                return false;
        }

        return true;
    }

    public static String generateOneNotPresentInList(List<String> codes) {
        for (Character suit : DeckofCards.suitMap.keySet()) {
            for (Character code : DeckofCards.codeMap.keySet()) {
                String tmp = String.valueOf(code) + String.valueOf(suit);
                if (!codes.contains(tmp))
                    return tmp;
            }
        }

        return "";
    }

    public static boolean verifyCardsMatch(List<Card> card1, List<Card> card2) {
        if (card1 == null || card2 == null || card1.size() != card2.size()) return false;
        for (Card card : card1) {
            if (!card2.contains(card))
                return false;
        }
        return true;
    }

    public static boolean verifyCardsMatchWithCodes(List<String> codes, List<Card> cards) {
        if (codes == null || cards == null || codes.size() != cards.size()) return false;
        for (Card _card : cards) {
            if (!codes.contains(_card.getCodeValue()))
                return false;
        }
        return true;
    }

    public static boolean verifyIsShuffled(List<Card> card1, List<Card> card2) {
        if (card1 == null || card2 == null || card1.size() != card2.size()) return false;
        for (int i = 0; i < card1.size(); i++) {
            Card _card = card1.get(i);
            int idx = card2.indexOf(_card);
            if (idx >= 0 && idx != i)
                return true;
        }
        return false;
    }

    public static List<Card> deepCopyCards(List<Card> cards) {
        List<Card> copied = new ArrayList<>();
        if (cards == null || cards.size() == 0) return copied;
        for (Card _card : cards)
            copied.add(new Card(_card.getSuit(), _card.getFaceValue(), _card.getCodeValue()));
        return copied;
    }
    //endregion

    //region API
    public static Deck shuffleCards(int count) {
        String url = MessageFormat.format(ShuffleCardsAPI, count);

        JSONObject jsonObject = sendURL(url, "GET");
        Deck deck = new Deck();
        if (jsonObject != null) {
            /*Sample
            {
                "success": true,
                "deck_id": "3p40paa87x90",
                "shuffled": true,
                "remaining": 52
            }*/
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setShuffled((boolean) jsonObject.get("shuffled"));
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));
        }
        return deck;
    }

    public static void drawACard(Deck deck, int cardCount) {
        String url = MessageFormat.format(DrawACardAPI, deck.getDeckID(), cardCount);

        JSONObject jsonObject = sendURL(url, "GET");
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));

            JSONArray cards = (JSONArray) jsonObject.get("cards");
            deck.setCards(retrieveCards(cards));

            if (jsonObject.containsKey("error"))
                deck.setError(jsonObject.get("error").toString());
        }
    }

    public static void reshuffleCards(Deck deck) {
        String url = MessageFormat.format(ReshuffleCardsAPI, deck.getDeckID());

        JSONObject jsonObject = sendURL(url, "GET");
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));
            deck.setShuffled((boolean) jsonObject.get("shuffled"));
        }
    }

    public static Deck brandNewDeck(boolean addJokers) {
        String url = MessageFormat.format(BrandNewDeckAPI, addJokers ? "true" : "false");

        JSONObject jsonObject = sendURL(url, "GET");
        Deck deck = new Deck();
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setShuffled((boolean) jsonObject.get("shuffled"));
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));
        }

        return deck;
    }

    public static Deck partialDeck(String cardsValue) {
        String url = MessageFormat.format(PartialDeckAPI, cardsValue);

        JSONObject jsonObject = sendURL(url, "GET");
        Deck deck = new Deck();
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setShuffled((boolean) jsonObject.get("shuffled"));
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));
        }

        return deck;
    }

    public static void addingToPiles(Deck deck, String pileName, String cardsValue) {
        String url = MessageFormat.format(AddingToPilesAPI, deck.getDeckID(), pileName, cardsValue);

        JSONObject jsonObject = sendURL(url, "GET");
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));

            deck.setPiles(retrievePiles(deck, (JSONObject) jsonObject.get("piles")));
        }
    }

    public static void shufflePiles(Deck deck, Pile pile) {
        String url = MessageFormat.format(ShufflePilesAPI, deck.getDeckID(), pile.getName());

        JSONObject jsonObject = sendURL(url, "GET");
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));

            deck.setPiles(retrievePiles(deck, (JSONObject) jsonObject.get("piles")));
        }
    }

    public static void listingPileCards(Deck deck, Pile pile) {
        String url = MessageFormat.format(ListingPileCardsAPI, deck.getDeckID(), pile.getName());

        JSONObject jsonObject = sendURL(url, "GET");
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());
            deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));

            deck.setPiles(retrievePiles(deck, (JSONObject) jsonObject.get("piles")));
        }
    }

    public static void drawingFromPiles(Deck deck, Pile pile, String value, boolean isBottom) {
        String url = "";
        if (value.equals("")) {
            if (isBottom)
                url = MessageFormat.format(DrawingFromPilesAPI_Bottom, deck.getDeckID(), pile.getName());
            else
                url = MessageFormat.format(DrawingFromPilesAPI, deck.getDeckID(), pile.getName());
        } else if (isNumeric(value))
            url = MessageFormat.format(DrawingFromPilesAPI_Count, deck.getDeckID(), pile.getName(), value);
        else
            url = MessageFormat.format(DrawingFromPilesAPI_Cards, deck.getDeckID(), pile.getName(), value);

        JSONObject jsonObject = sendURL(url, "GET");
        if (jsonObject != null) {
            deck.setDeckID(jsonObject.get("deck_id").toString());

            //Error Found: No remaining found from the return
            //deck.setRemainingCards(Integer.valueOf(jsonObject.get("remaining").toString()));

            JSONArray cards = (JSONArray) jsonObject.get("cards");
            deck.setPiles(retrievePiles(deck, (JSONObject) jsonObject.get("piles")));
            deck.setCards(retrieveCards(cards));
        }
    }
    //endregion
}
