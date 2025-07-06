package com.putsellersim.service;

import com.putsellersim.model.PutOption;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TradeSimulatorService {

    private final List<PutOption> soldPuts = new ArrayList<>();
    private final List<PutOption> expiredPuts = new ArrayList<>();
    private double cash = 100000.0;

    public void sellPut(PutOption option) {
        double marginRequired = option.strikePrice() * 100;
        if (cash >= marginRequired) {
            soldPuts.add(option);
            cash -= marginRequired;
            System.out.println("sellPut: Sold " + option.ticker() + " exp " + option.expiry() + ", soldPuts size=" + soldPuts.size());
        } else {
            throw new IllegalStateException("Insufficient margin to sell this put.");
        }
    }

    public void checkExpirations(LocalDate currentDate, double currentPrice) {
        Iterator<PutOption> iterator = soldPuts.iterator();
        while (iterator.hasNext()) {
            PutOption option = iterator.next();
            if (!option.expiry().isAfter(currentDate)) {
                // Option expired
                double intrinsic = Math.max(0, option.strikePrice() - currentPrice);
                double pnl = (option.premium() - intrinsic) * 100;
                cash += option.strikePrice() * 100 + pnl;
                expiredPuts.add(option);
                iterator.remove();
            }
        }
    }

    public void closePut(PutOption option, double currentPrice) {
        if (soldPuts.remove(option)) {
            double intrinsic = Math.max(0, option.strikePrice() - currentPrice);
            double pnl = (option.premium() - intrinsic) * 100;
            cash += option.strikePrice() * 100 + pnl;
            expiredPuts.add(option);
            System.out.println("closePut: Closed " + option.ticker() + " exp " + option.expiry() + ", pnl=" + pnl);
        }
    }

    public double getCashBalance() {
        return cash;
    }

    public void addCash(double amount) {
        cash += amount;
    }

    public List<PutOption> getSoldPuts() {
        return List.copyOf(soldPuts);
    }

    public List<PutOption> getExpiredPuts() {
        return List.copyOf(expiredPuts);
    }

    public double calculateUnrealizedPnL(double currentPrice) {
        return soldPuts.stream()
            .mapToDouble(p -> {
                double intrinsic = Math.max(0, p.strikePrice() - currentPrice);
                return (p.premium() - intrinsic) * 100;
            }).sum();
    }

    public double getTotalMarginUsed() {
        return soldPuts.stream()
            .mapToDouble(p -> p.strikePrice() * 100)
            .sum();
    }

    public double calculateROI(double currentPrice) {
        double unrealized = calculateUnrealizedPnL(currentPrice);
        double totalEquity = cash + getTotalMarginUsed() + unrealized;
        return (totalEquity - 10000.0) / 10000.0;
    }

    public double calculateWinRate(double currentPrice) {
        int total = soldPuts.size() + expiredPuts.size();
        if (total == 0) return 0.0;
        long wins = expiredPuts.stream()
                .filter(p -> currentPrice >= p.strikePrice())
                .count();
        return (double) wins / total;
    }
}
