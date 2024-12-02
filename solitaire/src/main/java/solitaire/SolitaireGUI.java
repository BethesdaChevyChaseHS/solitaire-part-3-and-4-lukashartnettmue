package solitaire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class SolitaireGUI {
    private JFrame frame;
    private JPanel topPanel;
    private JPanel playArea;
    private GameState gameState;

    // Variable to keep track of the selected pile index
    private int selectedPileIndex = -1;
    private int selectedCardIndex = -1;

    public SolitaireGUI(GameState gameState) {
        this.gameState = gameState;

        // Create the main frame
        frame = new JFrame("Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        // Initialize and add panels
        setupTopPanel();
        setupPlayArea();

        // Make the frame visible
        frame.setVisible(true);
    }

    private void setupTopPanel() {
        // Create the top panel with a BorderLayout
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.LIGHT_GRAY);

        // Title label in the center
        JLabel titleLabel = new JLabel("Luke's Solitaire", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Left panel for deck and visible cards
        JPanel leftTopPanel = new JPanel();
        leftTopPanel.setOpaque(false);
        leftTopPanel.add(createDeckComponent());
        leftTopPanel.add(createVisibleCardsComponent());
        topPanel.add(leftTopPanel, BorderLayout.WEST);

        // Right panel for foundation piles
        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            int foundationIndex = i;
            JLabel foundationPile = createFoundationComponent(foundationIndex);
            rightTopPanel.add(foundationPile);
        }
        topPanel.add(rightTopPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);
    }

    private void setupPlayArea() {
        // Create the center panel for the main play area
        playArea = new JPanel();
        playArea.setLayout(new GridLayout(1, 7, 5, 5)); // One row, seven columns with spacing

        // Add seven piles to the play area
        for (int i = 0; i < 7; i++) {
            int pileIndex = i;
            JPanel pilePanel = createPileComponent(pileIndex);
            playArea.add(pilePanel);
        }

        // Add the play area to the CENTER of the frame
        frame.add(playArea, BorderLayout.CENTER);
    }

    // Method to create a card component (as a JLabel)
    private JLabel createCardComponent(Card card) {
        ImageIcon icon = getCardIcon(card);
        JLabel cardLabel = new JLabel(icon);
        cardLabel.setPreferredSize(new Dimension(73, 97));
        return cardLabel;
    }

    // Method to create a pile component (as a JPanel)
    private JPanel createPileComponent(int pileIndex) {
        JPanel pilePanel = new JPanel();
        pilePanel.setLayout(null); // Absolute positioning for overlapping cards
        pilePanel.setBackground(new Color(0, 128, 0)); // Green background
        pilePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        pilePanel.setPreferredSize(new Dimension(100, 600));
        updatePileComponent(pilePanel, pileIndex);

        pilePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handlePileClick(pileIndex, e);
            }
        });

        return pilePanel;
    }

    // Method to update a pile component with the current cards
    private void updatePileComponent(JPanel pilePanel, int pileIndex) {
        pilePanel.removeAll();
        Stack<Card> pile = gameState.getGamePile(pileIndex);
    
        int yOffset = 20; // Vertical offset between cards
        int y = 0;
    
        for (int i = 0; i < pile.size(); i++) {
            Card card = pile.get(i);
            JLabel cardLabel = createCardComponent(card);
            cardLabel.setBounds(0, y, 73, 97); // Position within the pile panel
            pilePanel.add(cardLabel, 0); // Add at index 0 to adjust stacking order
            y += yOffset;
        }
    
       
        pilePanel.revalidate();
        pilePanel.repaint();
    }

    // Method to handle pile clicks
    private void handlePileClick(int pileIndex, MouseEvent e) {
        JPanel pilePanel = (JPanel) playArea.getComponent(pileIndex);
        if (selectedPileIndex == 7) {
            // Moving from visible cards to pile
            boolean success = gameState.moveCardFromVisibleCardsToPile(pileIndex);
            if (success) {
                updateVisibleCardsComponent();
                updatePileComponent(pilePanel, pileIndex);
                selectedPileIndex = -1;
                selectedCardIndex = -1;
            }
            updateVisibleCardsComponentSelection(false);
            return;
        } 
        int clickY = e.getY();
    
        Stack<Card> pile = gameState.getGamePile(pileIndex);
        int yOffset = 20;
        int cardIndex = clickY / yOffset; // Estimate the card index based on click position
    
       
        if (cardIndex >= pile.size()) {
            cardIndex = pile.size() - 1;
        }
        
        
    
        if (selectedPileIndex == -1) {
            // Check if the selected card is face up
            if (pile.size() == 0 || !pile.get(cardIndex).isFaceUp()) {
                return; // Do nothing if the card is face down
            }
            // No pile selected; select this one
            selectedPileIndex = pileIndex;
            selectedCardIndex = cardIndex; // Keep track of the card index
            updatePileComponentSelection(pileIndex, cardIndex, true);
        } else if (selectedPileIndex == pileIndex && selectedCardIndex == cardIndex) {
            // Deselect the pile
            updatePileComponentSelection(pileIndex, cardIndex, false);
            selectedPileIndex = -1;
            selectedCardIndex = -1;
        } else {
            // Attempt to move the sequence of cards
            boolean success = gameState.moveCards(selectedPileIndex, selectedCardIndex, pileIndex);
            if (success) {
                // Update the source and destination piles
                JPanel fromPilePanel = (JPanel) playArea.getComponent(selectedPileIndex);
                JPanel toPilePanel = (JPanel) playArea.getComponent(pileIndex);
                updatePileComponent(fromPilePanel, selectedPileIndex);
                updatePileComponent(toPilePanel, pileIndex);
            }
            // Deselect after attempting to move
            updatePileComponentSelection(selectedPileIndex, selectedCardIndex, false);
            selectedPileIndex = -1;
            selectedCardIndex = -1;
        }
    }
    
    // Method to update pile selection visuals
    private void updatePileComponentSelection(int pileIndex, int cardIndex, boolean isSelected) {
        JPanel pilePanel = (JPanel) playArea.getComponent(pileIndex);
        Component[] components = pilePanel.getComponents();
    
        if (cardIndex >= 0 && cardIndex < components.length) {
            JLabel cardLabel = (JLabel) components[components.length - 1 - cardIndex];
            if (isSelected) {
                // Enlarge the card by 15%
                int newWidth = (int) (73 * 1.15);
                int newHeight = (int) (97 * 1.15);
                ImageIcon icon = (ImageIcon) cardLabel.getIcon();
                Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                cardLabel.setIcon(new ImageIcon(scaledImage));
                cardLabel.setBounds(cardLabel.getX(), cardLabel.getY(), newWidth, newHeight);
            } else {
                // Reset to original size
                ImageIcon icon = getCardIcon(gameState.getGamePile(pileIndex).get(cardIndex));
                cardLabel.setIcon(icon);
                cardLabel.setBounds(cardLabel.getX(), cardLabel.getY(), 73, 97);
            }
        }
    }
    
    // Method to create foundation pile components
    private JLabel createFoundationComponent(int foundationIndex) {
        JLabel foundationLabel = new JLabel();
        foundationLabel.setPreferredSize(new Dimension(73, 97));
        foundationLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        foundationLabel.setBackground(new Color(0, 128, 0)); // Green background
        foundationLabel.setOpaque(true);
        updateFoundationComponent(foundationLabel, foundationIndex);

        foundationLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleFoundationClick(foundationIndex);
            }
        });

        return foundationLabel;
    }

    // Method to update foundation components
    private void updateFoundationComponent(JLabel foundationLabel, int foundationIndex) {
        Stack<Card> foundationPile = gameState.getFoundationPile(foundationIndex);
        if (!foundationPile.isEmpty()) {
            Card topCard = foundationPile.peek();
            ImageIcon icon = getCardIcon(topCard);
            foundationLabel.setIcon(icon);
        } else {
            foundationLabel.setIcon(null);
        }
    }

    // Method to handle foundation pile clicks
    private void handleFoundationClick(int foundationIndex) {
        if (selectedPileIndex != -1) {
            // Try to move card from selected pile to foundation
            boolean success = false;
            if (selectedPileIndex == 7) {
                // Moving from visible cards to foundation
                success = gameState.moveToFoundationFromVisibleCards(foundationIndex);
                if (success) {
                    updateVisibleCardsComponent();
                    JLabel foundationLabel = (JLabel) ((JPanel) topPanel.getComponent(2)).getComponent(foundationIndex);
                    updateFoundationComponent(foundationLabel, foundationIndex);
                }
                updateVisibleCardsComponentSelection(false);
            } else {
                success = gameState.moveToFoundation(selectedPileIndex, foundationIndex);
                if (success) {
                    JPanel pilePanel = (JPanel) playArea.getComponent(selectedPileIndex);
                    updatePileComponent(pilePanel, selectedPileIndex);
                    JLabel foundationLabel = (JLabel) ((JPanel) topPanel.getComponent(2)).getComponent(foundationIndex);
                    updateFoundationComponent(foundationLabel, foundationIndex);
                }
                updatePileComponentSelection(selectedPileIndex, selectedCardIndex, false);
            }
            selectedPileIndex = -1;
            // Update pile selections
            for (int i = 0; i < 7; i++) {
                JPanel pilePanel = (JPanel) playArea.getComponent(i);
                updatePileComponent(pilePanel, i);
            }
        }
    }

    // Helper method to get scaled card icon
    private ImageIcon getCardIcon(Card card) {
        String imageName;
        if (card.isFaceUp()) {
            imageName = card.getRank().getRankString().toLowerCase() + "_of_" + card.getSuit().toString().toLowerCase() + ".png";
        } else {
            imageName = "card_back.png";
        }
        System.out.println("Attempting to load image: " + imageName);
        ImageIcon icon = new ImageIcon("./solitaire/src/main/cardImages/" + imageName);
        if (icon.getIconWidth() == -1) {
            System.out.println("Failed to load image: " + imageName);
        } else {
            System.out.println("Image loaded successfully: " + imageName);
        }
        System.out.println("Current Working Directory: " + System.getProperty("user.dir"));
        Image scaledImage = icon.getImage().getScaledInstance(73, 97, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Method to create the deck component
    private JLabel createDeckComponent() {
        JLabel deckLabel = new JLabel();
        deckLabel.setPreferredSize(new Dimension(73, 97));
        deckLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        updateDeckComponent(deckLabel);

        deckLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleDeckClick();
            }
        });

        return deckLabel;
    }

    // Method to update the deck component
    private void updateDeckComponent(JLabel deckLabel) {
        if (!gameState.getDeck().isEmpty()) {
            ImageIcon icon = new ImageIcon("./solitaire/src/main/cardImages/card_back.png");
            Image scaledImage = icon.getImage().getScaledInstance(73, 97, Image.SCALE_SMOOTH);
            deckLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            deckLabel.setIcon(null);
        }
    }

    // Method to handle deck clicks
    private void handleDeckClick() {
        gameState.drawFromDeck();
        updateDeckAndVisibleCardsComponents();
    }

    // Method to create the visible cards component
    private JPanel createVisibleCardsComponent() {
        JPanel visibleCardsPanel = new JPanel();
        visibleCardsPanel.setPreferredSize(new Dimension(73 + 40, 97)); // Adjust size for overlapping cards
        visibleCardsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        visibleCardsPanel.setLayout(null); // Absolute positioning

        updateVisibleCardsComponent(visibleCardsPanel);

        visibleCardsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleVisibleCardsClick();
            }
        });

        return visibleCardsPanel;
    }

    // Method to update the visible cards component
    private void updateVisibleCardsComponent(JPanel visibleCardsPanel) {
        visibleCardsPanel.removeAll();
        Stack<Card> visibleCards = gameState.getVisibleCards();
        int numCardsToDisplay = Math.min(visibleCards.size(), 3); // Show up to 3 cards
        int xOffset = 20; // Horizontal offset between overlapping cards
        int x = 0;

        // Loop through the cards from bottom to top
        for (int i = visibleCards.size() - numCardsToDisplay; i < visibleCards.size(); i++) {
            Card card = visibleCards.get(i);
            JLabel cardLabel = createCardComponent(card);
            cardLabel.setBounds(x, 0, 73, 97);
            visibleCardsPanel.add(cardLabel, 0 ); // Add cards in order
            x += xOffset;
        }

        // Ensure no border adjustment is needed
        visibleCardsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        visibleCardsPanel.revalidate();
        visibleCardsPanel.repaint();
    }

    // Overloaded method to update the existing visible cards component
    private void updateVisibleCardsComponent() {
        JPanel leftTopPanel = (JPanel) topPanel.getComponent(1);
        JPanel visibleCardsPanel = (JPanel) leftTopPanel.getComponent(1);
        updateVisibleCardsComponent(visibleCardsPanel);
    }

    // Method to update visible cards selection visuals
    private void updateVisibleCardsComponentSelection(boolean isSelected) {
        JPanel leftTopPanel = (JPanel) topPanel.getComponent(1);
        JPanel visibleCardsPanel = (JPanel) leftTopPanel.getComponent(1);

        // Get the top card label
        if (visibleCardsPanel.getComponentCount() > 0) {
            JLabel topCardLabel = (JLabel) visibleCardsPanel.getComponent(0);

            if (isSelected) {
                // Enlarge the top card by 15%
                int newWidth = (int) (73 * 1.15);
                int newHeight = (int) (97 * 1.15);
                ImageIcon icon = (ImageIcon) topCardLabel.getIcon();
                Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                topCardLabel.setIcon(new ImageIcon(scaledImage));
                topCardLabel.setBounds(topCardLabel.getX(), topCardLabel.getY(), newWidth, newHeight);
            } else {
                // Reset to original size
                Stack<Card> visibleCards = gameState.getVisibleCards();
                if (!visibleCards.isEmpty()) {
                    Card topCard = visibleCards.peek();
                    ImageIcon icon = getCardIcon(topCard);
                    topCardLabel.setIcon(icon);
                    topCardLabel.setBounds(topCardLabel.getX(), topCardLabel.getY(), 73, 97);
                }
            }

            visibleCardsPanel.revalidate();
            visibleCardsPanel.repaint();
        }
    }

    // Method to handle visible cards clicks
    private void handleVisibleCardsClick() {
        if (selectedPileIndex == -1 && !gameState.getVisibleCards().isEmpty()) {
            // Select the visible cards stack
            selectedPileIndex = 7;
            updateVisibleCardsComponentSelection(true);
        } else if (selectedPileIndex == 7) {
            // Deselect the visible cards stack
            selectedPileIndex = -1;
            updateVisibleCardsComponentSelection(false);
        }
    }

    // Method to update both deck and visible cards components
    private void updateDeckAndVisibleCardsComponents() {
        JPanel leftTopPanel = (JPanel) topPanel.getComponent(1);
        JLabel deckLabel = (JLabel) leftTopPanel.getComponent(0);
        JPanel visibleCardsPanel = (JPanel) leftTopPanel.getComponent(1);

        updateDeckComponent(deckLabel);
        updateVisibleCardsComponent(visibleCardsPanel);
    }
}
