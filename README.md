# 🍔 Tender Food App
### "Buyer is King" - Real-time Food Auction System

**Tender Food** is a Java Swing application designed to simulate a real-time auction marketplace. Built on the philosophy of **"Buyer is King"**, the system allows a buyer to broadcast a meal request to multiple sellers, who then compete by offering their best prices. The application automatically sorts offers to ensure the buyer always sees the cheapest option first.

This project was developed as a final assignment for **Object-Oriented Programming (OOP)**, utilizing the **MVC Architecture** and the **Mediator Design Pattern**.

---

## 🚀 Key Features

### 1. 👑 "Buyer is King" Logic
- The system automatically sorts all incoming bids from **Lowest Price to Highest Price**.
- The buyer does not need to search manually; the best option is always at the top (Index 0).

### 2. ⚡ Real-Time Bidding System
- **Broadcast Mechanism:** When the Buyer requests an item, all Sellers receive the notification instantly.
- **Live Updates:** As sellers submit their prices, the Buyer's list updates in real-time.

### 3. 🏷️ Quick Preference Tags
- Includes "Quick Add" buttons based on user psychology:
    - `+ Something Sweet`
    - `+ A Lot`
    - `+ Simple`
    - `+ Last Longer`

### 4. ⚡ "GET BEST OFFER NOW" Button
- A specialized feature for speed.
- With one click, the Buyer can automatically select and accept the cheapest bid available without scrolling through the list.

### 5. 🤝 Deal & Transaction Locking
- Once a winner is selected (Deal), the tender is **closed**.
- The winning Seller gets a "Winner" notification (Green screen).
- Losing Sellers get a "Closed" notification (Gray screen).
- Inputs are locked to prevent further bidding.

---

## 🛠️ Tech Stack & Architecture

* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing (javax.swing)
* **Architecture:** MVC (Model-View-Controller)
* **Design Pattern:** Mediator Pattern (To handle communication between Buyer and multiple Sellers).

### Object-Oriented Principles Applied:
1.  **Encapsulation:** Data protection within the `Bid` class.
2.  **Inheritance:** `BuyerPanel` and `SellerPanel` extending `JPanel`.
3.  **Polymorphism:** Implementation of the `AuctionMediator` interface.
4.  **Abstraction:** Hiding complex logic inside the Controller.

---

## 💻 How to Run

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/TenderFoodApp.git](https://github.com/YOUR_USERNAME/TenderFoodApp.git)
    ```

2.  **Navigate to the Directory**
    ```bash
    cd TenderFoodApp
    ```

3.  **Compile the Code**
    Make sure you have Java installed.
    ```bash
    javac TenderSystemApp.java
    ```

4.  **Run the Application**
    ```bash
    java TenderSystemApp
    ```

---

## 👥 Group ADD (Ade, Dave, Diego)

---

**Note:** This project demonstrates the implementation of a *Tender/Auction Mechanism* where the priority is strictly given to the lowest price and speed of transaction.
