/*
  Author: Kyle Pensiton
  Date: February 8, 2024
  
  Project Title: Random Card Selection Program
  
  Description:
  This program generates a random playing card from a standard deck of 52 cards. 
  It utilizes the random module to select a random suit (hearts, diamonds, clubs, spades)
  and a random rank (Ace through King). The selected cards are then displayed to the user. 
  The program also includes a GUI interface where users can deal cards and view the dealt cards.
  Dealt cards are recorded in a text file, CardsDealt.txt, along with the timestamp of the deal.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Main {
    private static List<String> deck = new ArrayList<>(); // List to hold the cards in the deck
    public static List<String> dealtCards = new ArrayList<>(); // List to hold the dealt cards
    public static Map<String, ImageIcon> cardImages = new HashMap<>(); // Map to hold card images

    // Static flag to track if timestamp has been written
    private static boolean timestampWritten = false;

    public static void main(String[] args) {
        System.out.println("Welcome to the Card Dealing Program!");

        initializeDeck(); // Initialize the deck of cards
        loadCardImages(); // Load images for the cards

        // Create and display the GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CardGameGUI().setVisible(true);
            }
        });
    }

    // Method to deal cards from the deck
    public static List<String> dealCards() {
        List<String> dealtCards = new ArrayList<>(); // List to hold the newly dealt cards
        if (deck.isEmpty()) { // Check if the deck is empty
            System.out.println("Sorry, the deck is empty");
            return new ArrayList<>(); // Return an empty list if the deck is empty
        }

        Collections.shuffle(deck); // Shuffle the deck to randomize the order of the cards

        // Clear the previous dealt cards
        dealtCards.clear();

        // Deal 4 cards
        for (int i = 0; i < 4; i++) {
            String card = deck.remove(0); // Remove and get the first card from the deck
            System.out.println("Card " + (i + 1) + ": " + card);
            dealtCards.add(card); // Add the dealt card to the list
        }

        // Add the dealt cards back to the deck
        deck.addAll(dealtCards);

        // Record the dealt cards
        recordDealtCards(dealtCards);

        return dealtCards; // Return the list of dealt cards
    }

    // Method to record the dealt cards
    public static void recordDealtCards(List<String> dealtCards) {
        if (dealtCards.isEmpty()) { // Check if there are any dealt cards
            return;
        }

        try (PrintWriter outFile = new PrintWriter(new FileWriter("CardsDealt.txt", true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date currentTime = new Date();
            
            if (!timestampWritten) {
                outFile.println(dateFormat.format(currentTime));
                timestampWritten = true; // Set flag to true after writing the timestamp
            }

            // Write each dealt card to the file
            for (int i = 0; i < dealtCards.size(); i++) {
                outFile.print(dealtCards.get(i));
                if (i < dealtCards.size() - 1) {
                    outFile.print(","); // Add a comma and space if it's not the last card
                }
            }

            outFile.println();
        } catch (IOException e) {
            System.err.println("Error opening file"); // Handle file writing errors
        }
    }

    // Method to initialize the deck of cards
    public static void initializeDeck() {
        String[] suits = {"S", "H", "D", "C"}; // Array of suits
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"}; // Array of ranks

        // Generate all possible combinations of suits and ranks to create the deck
        for (String suit : suits) {
            for (String rank : ranks) {
                String card = rank + suit;
                deck.add(card); // Add the card to the deck
            }
        }
    }

    // Method to load images for all the cards
    private static void loadCardImages() {
        try {
            String[] suits = {"S", "H", "D", "C"}; // Array of suits
            String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"}; // Array of ranks

            // Iterate through all combinations of suits and ranks to load the images
            for (String suit : suits) {
                for (String rank : ranks) {
                    String cardName = rank + suit; // Generate the card name
                    String imagePath = "/images/" + cardName + ".png"; // Construct the path to the image
                    cardImages.put(cardName, new ImageIcon(Main.class.getResource(imagePath))); // Load the image and add it to the map
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle any errors that occur during image loading
        }
    }
}

// GUI class for the card game
class CardGameGUI extends JFrame {
    private JPanel cardPanel;
    private JButton dealButton;
    private JButton quitButton;
    private JScrollPane scrollPane; // ScrollPane to hold the cardPanel
    private JLabel welcomeLabel; // JLabel for the welcome message

    private List<String> dealtCards = new ArrayList<>(); // Initialize dealtCards to an empty list

    // Constructor to initialize the GUI components
    public CardGameGUI() {
        setTitle("Card Dealing Program");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents(); // Initialize the GUI components
        layoutComponents(); // Layout the GUI components
        setLocationRelativeTo(null); // Center the window on the screen
    }

    // Method to initialize the GUI components
    private void initComponents() {
        cardPanel = new JPanel();
        cardPanel.setLayout(new FlowLayout());
        scrollPane = new JScrollPane(cardPanel); // Wrap the cardPanel in a JScrollPane

        dealButton = new JButton("Deal Cards");
        quitButton = new JButton("Quit");
        welcomeLabel = new JLabel("Welcome to the Card Dealing Program! Press 'Deal Cards' to start or 'Quit' to exit."); // Initialize the welcome message label

        // ActionListener for the dealButton
        dealButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dealtCards = Main.dealCards(); // Deal cards
                displayDealtCards(dealtCards); // Display the dealt cards
            }
        });

        // ActionListener for the quitButton
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitGame(); // Quit the game
            }
        });
    }

    // Method to layout the GUI components
    private void layoutComponents() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dealButton);
        buttonPanel.add(quitButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(welcomeLabel, BorderLayout.NORTH);
        getContentPane().add(cardPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    // Method to display the dealt cards
    private void displayDealtCards(List<String> cards) {
        // Clear existing cards
        cardPanel.removeAll();
        cardPanel.revalidate();
        cardPanel.repaint();

        // Display dealt cards
        for (String card : cards) {
            ImageIcon cardImage = Main.cardImages.get(card); // Get the image for the card
            if (cardImage != null) {
                JLabel label = new JLabel(cardImage); // Create a JLabel with the image
                cardPanel.add(label); // Add the JLabel to the cardPanel
            }
        }
        pack(); // Adjust layout
    }

    // Method to quit the game
    private void quitGame() {
        System.out.println("Thanks for playing! Goodbye!"); // Print a message to the console
        System.exit(0); // Exit the program
    }
}
