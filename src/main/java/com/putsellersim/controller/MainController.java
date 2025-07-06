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
    private TableView<PutOption> journalTable;

    @FXML
    private TableColumn<PutOption, String> journalTickerColumn;

    @FXML
    private TableColumn<PutOption, LocalDate> journalExpiryColumn;

    @FXML
    private TableColumn<PutOption, Double> journalStrikePriceColumn;

    @FXML
    private TableColumn<PutOption, Double> journalPremiumColumn;

    @FXML
    private TableColumn<PutOption, Double> journalProfitColumn;

    private int pnlCounter = 0;
    private final XYChart.Series<Number, Number> pnlSeries = new XYChart.Series<>();

    private final OptionDataService dataService;
    private final TradeSimulatorService simulator;

    private ObservableList<PutOption> optionsList = FXCollections.observableArrayList();

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

            journalTickerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().ticker()));
            journalExpiryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().expiry()));
            journalStrikePriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().strikePrice()));
            journalPremiumColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().premium()));

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
        journalTable.getItems().setAll(simulator.getExpiredPuts());
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
}
