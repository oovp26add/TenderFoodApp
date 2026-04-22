import json
import re

# Mock Database of Seller Products
MOCK_PRODUCTS_DB = [
    {"id": "p1", "name": "Fruit Tea", "tags": ["sweet", "fruit", "cold", "something"], "base_price": 9000, "is_ai_priced": True},
    {"id": "p2", "name": "Hot Tea", "tags": ["hot", "warm", "classic"], "base_price": 6000, "is_ai_priced": True},
    {"id": "p3", "name": "Spicy Noodles", "tags": ["spicy", "hot", "food"], "base_price": 15000, "is_ai_priced": True},
    {"id": "p4", "name": "Sweet Cake", "tags": ["cake", "dessert"], "base_price": 12000, "is_ai_priced": True}
]

class DynamicPricingExtractor:
    """ AI-driven extractor for finding the actual price at this moment """
    @staticmethod
    def get_price(product):
        if product["is_ai_priced"]:
            # Insert Agentic / AI pricing logic here (demand-based)
            # Returning a mock dynamic price with random variation to simulate marketplace competition
            import random
            fluctuation = random.choice([-1000, 0, 1000, 2000])
            return product["base_price"] + fluctuation
        return product["base_price"]

class NLPAgentParser:
    """
    Simulates an LLM Information Extractor.
    In a real system, this would use a framework like LangChain/Instructor 
    with a model like GPT-4 to map natural language to structured JSON output.
    """
    def __init__(self, catalog):
        self.catalog = catalog

    def query_llm(self, text):
        """
        MOCK function representing an LLM call.
        User input: "something sweet 2 hot 3"
        Expected LLM output: [{ "query": "something sweet", "qty": 2 }, { "query": "hot", "qty": 3 }]
        """
        # A simple regex fallback mock to simulate extraction of context + quantity
        # E.g., matches "something sweet 2" -> context: "something sweet", qty: 2
        pattern = r'([a-zA-Z\s]+)\s+(\d+)'
        matches = re.findall(pattern, text)
        
        parsed_intent = []
        for match in matches:
            parsed_intent.append({
                "query": match[0].strip(),
                "qty": int(match[1])
            })
        return parsed_intent

    def find_best_match(self, query):
        """
        Agentic Vector Search or Semantic Search placeholder. 
        For now, matches user query to product tags.
        """
        query_words = set(query.lower().split())
        best_match = None
        highest_score = 0

        for product in self.catalog:
            tags = set(product["tags"])
            score = len(query_words.intersection(tags))
            
            # Add bonus for name match
            for word in query_words:
                if word in product["name"].lower():
                    score += 2

            if score > highest_score:
                highest_score = score
                best_match = product

        return best_match

    def parse_order(self, text):
        intents = self.query_llm(text)
        
        order_items = []
        total_price = 0
        
        for intent in intents:
            product = self.find_best_match(intent["query"])
            if product:
                actual_price = DynamicPricingExtractor.get_price(product)
                subtotal = actual_price * intent["qty"]
                total_price += subtotal
                
                order_items.append({
                    "product_name": product["name"],
                    "qty": intent["qty"],
                    "unit_price": actual_price,
                    "subtotal": subtotal
                })
            else:
                print(f"[Warning] AI Agent could not confidently match item for: '{intent['query']}'")

        return {
            "raw_input": text,
            "structured_items": order_items,
            "total_price": total_price
        }

import sys

def main():
    parser = NLPAgentParser(MOCK_PRODUCTS_DB)
    
    # Test input provided by user or command line
    user_input = sys.argv[1] if len(sys.argv) > 1 else "something sweet 2 hot 3"
    print(f"--- Natural Language Order Parsing Demo ---")
    print(f"Buyer says: '{user_input}'\n")
    
    # Process
    order = parser.parse_order(user_input)
    
    # Formatted Output
    items_str = " + ".join([f"{item['product_name']} {item['qty']}x{item['unit_price']//1000}k" for item in order['structured_items']])
    total_str = f"total {order['total_price']//1000}k"
    
    print("Agentic AI parsing result:")
    print(f"-> {items_str} = {total_str}")
    print("\nDetailed JSON Structure (Feeds into Payment Module):")
    print(json.dumps(order, indent=2))

if __name__ == "__main__":
    main()
