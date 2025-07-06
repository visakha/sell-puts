package com.putsellersim.controller;

import com.putsellersim.model.PutOption;
import com.putsellersim.service.OptionDataService;
import com.putsellersim.service.TradeSimulatorService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
public class MainController {

    @FXML
    private TableView<PutOption> tableView;

    @FXML
    private TableColumn<PutOption, String> tickerColumn;

    @FXML
    private TableColumn<PutOption, LocalDate> expiryColumn;

    @FXML
    private TableColumn<PutOption, Double> strikePriceColumn;

    @FXML
    private TableColumn<PutOption, Double> premiumColumn;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label marginLabel;

    @FXML
    private Label pnlLabel;

    @FXML
    private Label roiLabel;

    @FXML
    private Label winRateLabel;

    @FXML
    private TextField currentPriceField;

    @FXML
    private TextField filterTickerField;

    @FXML
    private LineChart<Number, Number> pnlChart;

    @FXML
    private TableView<JournalRow> journalTable;

    @FXML
    private TableColumn<JournalRow, String> journalTickerColumn;

    @FXML
    private TableColumn<JournalRow, LocalDate> journalExpiryColumn;

    @FXML
    private TableColumn<JournalRow, Double> journalStrikePriceColumn;

    @FXML
    private TableColumn<JournalRow, Double> journalPremiumColumn;

    @FXML
    private TableColumn<JournalRow, Double> journalProfitColumn;

    @FXML
    private TableColumn<JournalRow, String> journalStatusColumn;

    @FXML
    private TableColumn<JournalRow, Void> journalActionColumn;

    private int pnlCounter = 0;
    private final XYChart.Series<Number, Number> pnlSeries = new XYChart.Series<>();

    private final OptionDataService dataService;
    private final TradeSimulatorService simulator;

    private ObservableList<PutOption> optionsList = FXCollections.observableArrayList();
    private ObservableList<JournalRow> journalRows = FXCollections.observableArrayList();

    private static class JournalRow {
        final PutOption option;
        final String status;
        final double profit;
        JournalRow(PutOption option, String status, double profit) {
            this.option = option;
            this.status = status;
            this.profit = profit;
        }
        public String getTicker() { return option.ticker(); }
        public LocalDate getExpiry() { return option.expiry(); }
        public Double getStrikePrice() { return option.strikePrice(); }
        public Double getPremium() { return option.premium(); }
        public Double getProfit() { return profit; }
        public String getStatus() { return status; }
    }

    public MainController(OptionDataService dataService, TradeSimulatorService simulator) {
        this.dataService = dataService;
        this.simulator = simulator;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            System.out.println("TableView: " + tableView);
            System.out.println("tickerColumn: " + tickerColumn);
            System.out.println("expiryColumn: " + expiryColumn);
            System.out.println("strikePriceColumn: " + strikePriceColumn);
            System.out.println("premiumColumn: " + premiumColumn);

            tickerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().ticker()));
            expiryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().expiry()));
            strikePriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().strikePrice()));
            premiumColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().premium()));

            journalTickerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTicker()));
            journalExpiryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getExpiry()));
            journalStrikePriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStrikePrice()));
            journalPremiumColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPremium()));
            journalProfitColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProfit()));
            journalStatusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

            journalActionColumn.setCellFactory(col -> new TableCell<>() {
                private final Button closeButton = new Button("Close");
                {
                    closeButton.setOnAction(e -> {
                        JournalRow row = getTableView().getItems().get(getIndex());
                        if (row.status.equals("Open")) {
                            closeTrade(row.option);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || !"Open".equals(getTableView().getItems().get(getIndex()).status)) {
                        setGraphic(null);
                    } else {
                        setGraphic(closeButton);
                    }
                }
            });

            optionsList.setAll(dataService.loadOptions());
            tableView.setItems(optionsList);
            System.out.println("Loaded options (Platform.runLater): " + optionsList);
            tableView.refresh();
            updateLabels(); // <-- Moved here so it runs on startup
        });

        pnlSeries.setName("PnL");
        pnlChart.getData().add(pnlSeries);

        updateJournal();

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                PutOption selected = tableView.getSelectionModel().getSelectedItem();
                try {
                    simulator.sellPut(selected);
                    updateLabels();
                    updateJournal();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sold 1 Put for " + selected.ticker());
                    alert.show();
                } catch (IllegalStateException ex) {
                    Alert error = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    error.show();
                }
            }
        });
    }

    @FXML
    private void applyFilter(ActionEvent event) {
        // TODO: Implement filter logic
    }

    @FXML
    private void exportJournal(ActionEvent event) {
        // TODO: Implement export logic
    }

    @FXML
    private void updatePnL(ActionEvent event) {
        // TODO: Implement update PnL logic
    }

    @FXML
    public void updatePnL() {
        try {
            double currentPrice = Double.parseDouble(currentPriceField.getText());

            simulator.checkExpirations(LocalDate.now(), currentPrice);
            updateJournal();

            double pnl = simulator.calculateUnrealizedPnL(currentPrice);
            pnlLabel.setText(String.format("Unrealized PnL: $%.2f", pnl));

            pnlCounter++;
            pnlSeries.getData().add(new XYChart.Data<>(pnlCounter, pnl));

            double roi = simulator.calculateROI(currentPrice);
            double winRate = simulator.calculateWinRate(currentPrice);

            roiLabel.setText(String.format("ROI: %.2f%%", roi * 100));
            winRateLabel.setText(String.format("Win Rate: %.1f%%", winRate * 100));

            updateLabels();

        } catch (NumberFormatException e) {
            pnlLabel.setText("Unrealized PnL: Invalid input");
        }
    }

    private void updateLabels() {
        balanceLabel.setText(String.format("Cash Balance: $%.2f", simulator.getCashBalance()));
        marginLabel.setText(String.format("Margin Used: $%.2f", simulator.getTotalMarginUsed()));
    }

    private void updateJournal() {
        journalRows.clear();
        for (PutOption o : simulator.getSoldPuts()) {
            journalRows.add(new JournalRow(o, "Open", 0.0));
        }
        for (PutOption o : simulator.getExpiredPuts()) {
            double profit = (o.premium()) * 100; // You can improve this logic if needed
            journalRows.add(new JournalRow(o, "Expired", profit));
        }
        System.out.println("updateJournal: journalRows size = " + journalRows.size());
        for (JournalRow row : journalRows) {
            System.out.println("JournalRow: " + row.getTicker() + ", status=" + row.getStatus());
        }
        journalTable.setItems(journalRows);
    }

    @FXML
    private void reloadOptions(ActionEvent event) {
        optionsList.setAll(dataService.loadOptions());
        tableView.refresh();
    }

    @FXML
    private void addCash(ActionEvent event) {
        simulator.addCash(10000.0);
        updateLabels();
    }

    private void closeTrade(PutOption option) {
        simulator.closePut(option, getCurrentPrice());
        updateLabels();
        updateJournal();
    }
    private double getCurrentPrice() {
        try {
            return Double.parseDouble(currentPriceField.getText());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
