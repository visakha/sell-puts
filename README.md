# Put Seller Simulator

## Beginner User Guide

### What is this?
This is a JavaFX desktop application that simulates selling put options. It helps you learn about options trading, track trades, and see your profit/loss (PnL) over time.

### How to Run the Application
1. **Install Java 21** (required for this app).
2. **Install Maven** (https://maven.apache.org/download.cgi).
3. Open a terminal in the project folder.
4. Run the app with:
   ```
   mvn javafx:run
   ```

### Main Features
- **Options Table:** Shows available put options from `options.csv`.
- **Sell a Put:** Double-click any row in the options table to sell a put.
- **Trading Journal:** Shows all your trades (open and expired), their status, and profit/loss.
- **Close Trade:** Click the "Close" button in the journal to close an open trade at the current price.
- **Update PnL:** Enter a price and click "Update PnL" to expire trades and update profit/loss.
- **Add Cash:** Click "Add 10K" to add $10,000 to your cash balance.
- **Reload:** Click "Reload" to reload options from the CSV file.

### Step-by-Step Usage
1. **Start the app** (see above).
2. **Sell a put:**
   - Double-click a row in the top table. The trade appears in the Trading Journal below as "Open".
3. **Close a trade manually:**
   - Enter a price in the "Current Price" field.
   - Click the "Close" button for the trade you want to close. The trade moves to "Expired" and shows your PnL.
4. **Expire trades automatically:**
   - Enter a price and click "Update PnL". Any trades with expiry date on or before today will be closed.
5. **Add cash:**
   - Click "Add 10K" to increase your cash balance.
6. **Reload options:**
   - Click "Reload" to reload the options list from the CSV file.

### Tips
- You can edit `options.csv` in the `src/main/resources` folder to add or change available options.
- The Trading Journal shows both open and expired trades, with their status and profit.
- The app is for learning and simulation only. No real trades are made.

### Troubleshooting
- If the app does not start, make sure you have Java 21 and Maven installed.
- If you see errors about missing JavaFX, always run with `mvn javafx:run`.
- If you edit the CSV, click "Reload" in the app to see changes.

---

# Ack
code written by ChatGPT

# Disclaimer
Code is all experimental and may not work as expected. Use at your own risk.
