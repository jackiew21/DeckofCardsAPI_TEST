Deck of Cards API
http://deckofcardsapi.com/

Objects:
Card, Deck, Pile, SUIT

API and helper function: DeckofCards
All Test Cases: DeckofCardsTest.java 

How to Run?
Run DeckofCardsTest directly

Test Report: 
In total 39 Test Cases, 38 Test Cases Passed, 1 Failed (Time Spent: 8s)

Errors found:
1. In drawing from piles, there is no remaining number of deck returned from the json,
which is different from the sample given.

2. (Test Case: testDrawingFromPiles6) In drawing from piles, if set drawing count to 0, it should not draw any cards from the pile,
but the result received is drawn all cards from that pile.