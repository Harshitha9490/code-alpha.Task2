import java.io.*;
import java.util.*;

class Stock {
    private String symbol;
    private double price;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
    }
}

class Transaction {
    private String stockSymbol;
    private int quantity;
    private double price;
    private boolean isBuy;

    public Transaction(String stockSymbol, int quantity, double price, boolean isBuy) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.isBuy = isBuy;
    }

    public String toString() {
        return (isBuy ? "BUY" : "SELL") + " " + quantity + " of " + stockSymbol + " at $" + price;
    }
}

class User {
    private String name;
    private Map<String, Integer> portfolio = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    public void buyStock(Stock stock, int quantity) {
        portfolio.put(stock.getSymbol(), portfolio.getOrDefault(stock.getSymbol(), 0) + quantity);
        transactions.add(new Transaction(stock.getSymbol(), quantity, stock.getPrice(), true));
        System.out.println("Bought " + quantity + " of " + stock.getSymbol() + " at $" + stock.getPrice());
    }

    public void sellStock(Stock stock, int quantity) {
        String symbol = stock.getSymbol();
        int owned = portfolio.getOrDefault(symbol, 0);

        if (owned >= quantity) {
            portfolio.put(symbol, owned - quantity);
            transactions.add(new Transaction(symbol, quantity, stock.getPrice(), false));
            System.out.println("Sold " + quantity + " of " + symbol + " at $" + stock.getPrice());
        } else {
            System.out.println("Not enough shares to sell.");
        }
    }

    public void viewPortfolio(Map<String, Stock> market) {
        System.out.println("\n--- Portfolio for " + name + " ---");
        double totalValue = 0;
        for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Stock stock = market.get(symbol);
            double value = quantity * stock.getPrice();
            totalValue += value;
            System.out.println(symbol + ": " + quantity + " shares @ $" + stock.getPrice() + " = $" + value);
        }
        System.out.println("Total Portfolio Value: $" + totalValue);
    }

    public void viewTransactions() {
        System.out.println("\n--- Transaction History ---");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    public void savePortfolio(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        }
    }

    public void loadPortfolio(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            portfolio.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                portfolio.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
    }
}

public class StockTradingPlatform {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Stock> market = new HashMap<>();
        market.put("AAPL", new Stock("AAPL", 150.00));
        market.put("GOOG", new Stock("GOOG", 2700.00));
        market.put("TSLA", new Stock("TSLA", 700.00));

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        User user = new User(name);
        try {
            user.loadPortfolio("portfolio.txt");
        } catch (IOException e) {
            System.out.println("No previous portfolio found.");
        }

        while (true) {
            System.out.println("\n1. View Market");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. View Transactions");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Market Data ---");
                    for (Stock stock : market.values()) {
                        System.out.println(stock.getSymbol() + ": $" + stock.getPrice());
                    }
                    break;

                case 2:
                    System.out.print("Enter stock symbol to buy: ");
                    String buySymbol = scanner.next().toUpperCase();
                    Stock buyStock = market.get(buySymbol);
                    if (buyStock == null) {
                        System.out.println("Invalid symbol.");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int buyQty = scanner.nextInt();
                    user.buyStock(buyStock, buyQty);
                    break;

                case 3:
                    System.out.print("Enter stock symbol to sell: ");
                    String sellSymbol = scanner.next().toUpperCase();
                    Stock sellStock = market.get(sellSymbol);
                    if (sellStock == null) {
                        System.out.println("Invalid symbol.");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int sellQty = scanner.nextInt();
                    user.sellStock(sellStock, sellQty);
                    break;

                case 4:
                    user.viewPortfolio(market);
                    break;

                case 5:
                    user.viewTransactions();
                    break;

                case 6:
                    try {
                        user.savePortfolio("portfolio.txt");
                        System.out.println("Portfolio saved. Goodbye!");
                    } catch (IOException e) {
                        System.out.println("Error saving portfolio.");
                    }
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}