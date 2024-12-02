package solitaire;

import java.util.ArrayList;
import java.util.Stack;

public class GameState {
    private Stack<Card> deck;
    private Stack<Card>[] gamePiles;
    private Stack<Card> visibleCards;
    private Stack<Card> discardedCards;
    private Stack<Card>[] foundationPiles; // Add foundation piles

    @SuppressWarnings("unchecked")
    public GameState() {
        deck = new Stack<>();
        gamePiles = new Stack[7];
        visibleCards = new Stack<>();
        discardedCards = new Stack<>();
        foundationPiles = new Stack[4]; // Initialize foundation piles

        for (int i = 0; i < gamePiles.length; i++) {
            gamePiles[i] = new Stack<>();
        }

        for (int i = 0; i < foundationPiles.length; i++) {
            foundationPiles[i] = new Stack<>();
        }

        initializeDeck();
        shuffleDeck();
        dealInitialCards();
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.push(new Card(suit, rank));
            }
        }
    }

    private void shuffleDeck() {
        java.util.Collections.shuffle(deck);
    }

    private void dealInitialCards() {
        for (int i = 0; i < gamePiles.length; i++) {
            for (int j = 0; j <= i; j++) {
                Card card = deck.pop();
                if (j == i) {
                    card.flip();
                }
                gamePiles[i].push(card);
            }
        }
    }

    public void drawFromDeck() {
        if (deck.isEmpty() && !visibleCards.isEmpty()) {
            while (!visibleCards.isEmpty()) {
                Card card = visibleCards.pop();
                if (card.isFaceUp()) {
                    card.flip();
                }
                deck.push(card);
            }
        }
        for (int i = 0; i < 3 && !deck.isEmpty(); i++) {
            Card card = deck.pop();
            if (!card.isFaceUp()) {
                card.flip();
            }
            visibleCards.push(card);
        }
    }
    



    public void discardCards() {
        while (!visibleCards.isEmpty()) {
            discardedCards.push(visibleCards.pop());
        }
    }

    // new methods from part 3
    public boolean canCardMove(Card card, int toPile) {
        /*a card can be moved from the visible cards to a pile if 
            A) The card is the opposite color and its rank is ONE smaller than the card it will be placed on
            B) The pile is empty and the card is a King
        */
         // attempts to move top card from visible card stack to the toPileIndex
        // returns true if successful and false if unsuccessful
        if (gamePiles[toPile].isEmpty()) {
            return card.getRank() == Rank.KING;
        } else {
            Card topCard = gamePiles[toPile].peek();
            
            // Check for opposite color directly
            if ((card.getSuit() == Suit.HEARTS || card.getSuit() == Suit.DIAMONDS) && 
                (topCard.getSuit() == Suit.CLUBS || topCard.getSuit() == Suit.SPADES)) {
                return card.getRank().ordinal() == topCard.getRank().ordinal() - 1;
            } else if ((card.getSuit() == Suit.CLUBS || card.getSuit() == Suit.SPADES) && 
                       (topCard.getSuit() == Suit.HEARTS || topCard.getSuit() == Suit.DIAMONDS)) {
                return card.getRank().ordinal() == topCard.getRank().ordinal() - 1;
            }
        }
        return false;
    }

    public boolean moveCardFromVisibleCardsToPile(int toPileIndex) {
        /* 
            If a card can be moved, it should be popped from the visible cards pile and pushed to the pile it is added to
            hints: use peek() and ordinal() to determine whether or not a card can be moved. 
            USE the method you just made, canCardMove
        */
        if (!visibleCards.isEmpty() && canCardMove(visibleCards.peek(), toPileIndex)) {
            gamePiles[toPileIndex].push(visibleCards.pop());
            return true;
        }
        return false;
    }
     // Move a card from one pile to another
    public boolean moveCards(int fromPileIndex, int cardIndex, int toPileIndex) {
        Stack<Card> fromPile = gamePiles[fromPileIndex];
        Stack<Card> toPile = gamePiles[toPileIndex];
           // Create a sub-stack of cards to move
        ArrayList<Card> cardsToMove = new ArrayList<>(fromPile.subList(cardIndex, fromPile.size()));
         // Check if bottomCard can be moved to the toPile
        // if we can move the cards, add cardsToMove to the toPile and remove them from the fromPile
        // Then, flip the next card in the fromPile stack
        if (!cardsToMove.isEmpty() && canCardMove(cardsToMove.get(0), toPileIndex)) {
            toPile.addAll(cardsToMove);
            fromPile.subList(cardIndex, fromPile.size()).clear();

            if (!fromPile.isEmpty() && !fromPile.peek().isFaceUp()) {
                fromPile.peek().flip();
            }
            return true;
        }
        return false;
    }

    private boolean canMoveToFoundation(Card card, int foundationIndex) {
        //The foundation piles are the 4 piles that you have to build to win the game. 
        //In order for a card to be added to the pile, it needs to be one larger than the 
        //current top card of the foundation pile. It needs to be the same suit. 
        Stack<Card> foundation = foundationPiles[foundationIndex];
        if (foundation.isEmpty()) {
            return card.getRank() == Rank.ACE;
        } else {
            Card topCard = foundation.peek();
            return card.getSuit() == topCard.getSuit() &&
                   card.getRank().ordinal() == topCard.getRank().ordinal() + 1;
        }
    }

    public boolean moveToFoundation(int fromPileIndex, int foundationIndex) {
        //check if we can move the top card of the fromPile to the foundation at foundationIndex
        Stack<Card> fromPile = gamePiles[fromPileIndex];
        if (!fromPile.isEmpty() && canMoveToFoundation(fromPile.peek(), foundationIndex)) {
            foundationPiles[foundationIndex].push(fromPile.pop());
            if (!fromPile.isEmpty() && !fromPile.peek().isFaceUp()) {
                fromPile.peek().flip();
            }
            return true;
        }
        return false;
    }

    public boolean moveToFoundationFromVisibleCards(int foundationIndex) {
        //similar to the above method, 
        //move the top card from the visible cards to the foundation pile with index foundationIndex if possible
        if (!visibleCards.isEmpty() && canMoveToFoundation(visibleCards.peek(), foundationIndex)) {
            foundationPiles[foundationIndex].push(visibleCards.pop());
            return true;
        }
        return false;
    }
     // Don't change this, used for testing
    public void printState() {
        System.out.println("Deck size: " + deck.size());

        System.out.print("Visible cards: ");
        if (visibleCards.isEmpty()) {
            System.out.println("None");
        } else {
            for (Card card : visibleCards) {
                System.out.print(card + " ");
            }
            System.out.println();
        }

        System.out.println("Discarded cards: " + discardedCards.size());

        System.out.println("Game piles:");
        for (int i = 0; i < gamePiles.length; i++) {
            System.out.print("Pile " + (i + 1) + ": ");
            if (gamePiles[i].isEmpty()) {
                System.out.println("Empty");
            } else {
                for (Card card : gamePiles[i]) {
                    System.out.print(card + " ");
                }
                System.out.println();
            }
        }
    }

    public Stack<Card> getGamePile(int index) {
        return gamePiles[index];
    }

    public Stack<Card> getFoundationPile(int index) {
        return foundationPiles[index];
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Stack<Card> getVisibleCards() {
        return visibleCards;
    }

   
}