# Project Report: TenderFoodApp - Agentic Marketplace Update

## 1. Project Description
TenderFoodApp is a food auction application built with Java GUI (Swing) using an Object-Oriented architecture (Mediator Pattern) that dynamically connects Buyers and Sellers ("Sellers are Kings"). The latest update transforms the application into an **Agentic Marketplace**, a smart ecosystem where AI Agents are integrated to streamline the auction, communication, and transaction processes.

## 2. New Features Upgrade (Session 9 - Agentic Upgrades)

Here is a summary of the key feature enhancements implemented in this update:

*   **Price & Product Separation (Manual & Dynamic via AI):**
    The seller's system now separates the input for the product name and the price. With the *Agentic Mode*, prices are calculated dynamically using a Python AI script (`nlp_parser.py`), adjusting to market conditions ("Dynamic Pricing").
*   **Order Quantity Parsing (Natural Language NLP):**
    The system can now receive natural language text input from customers (e.g., `"something sweet 2 hot 3"`), and the AI agent automatically extracts it into structured orders with quantities and prices (e.g., `"fruit tea 2x9k + hot tea 3x6k = total 36k"`).
*   **Payment Simulation (Payment Flow):**
    Closing a deal no longer simply updates the status; it now features a payment interface simulation before the order and delivery data are finalized and saved into the database.
*   **Map Integration:**
    A `MapPanel` feature has been added to provide a visual simulation of the delivery area map for the Buyer.
*   **Instant Messaging Integration (Chat Panel):**
    Sellers and Buyers can now negotiate further or interact directly in real-time through the instant messaging feature via the `AuctionMediator`.
*   **Video/Identity-Based Seller Profile:**
    The system includes a visual verification module for the seller's profile, which can load placeholders for short videos or store identities.
*   **Buyer Address & Order History:**
    Buyers can specify a delivery location. Every completed transaction (*Deal Closed*) is recorded into an SQLite database and can be reviewed through the *Order History* interface.

## 3. Technical Architecture
*   **Frontend:** Java Swing (UI components, `JPanel`, `JFrame`).
*   **Backend Logic:** Java (Design Pattern: *Mediator* via `AuctionMediator`).
*   **AI Backend Engine:** Python (`nlp_parser.py`) integrated cross-platform via Java `ProcessBuilder`. Includes a sandbox timeout (*Safety Protocol*) to prevent excessive execution times for the AI Agent.
*   **Database:** SQLite (`tenderfood.db` via `DatabaseManager`) for permanent record keeping of order history.

## 4. How to Run
1. Ensure you have JRE/JDK (Java) and Python installed on your system.
2. Compile the project:
   `javac -cp ".;sqlite-jdbc.jar;slf4j-api.jar" *.java`
3. Run the application:
   `java -cp ".;sqlite-jdbc.jar;slf4j-api.jar" TenderSystemApp`
