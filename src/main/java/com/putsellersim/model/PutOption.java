package com.putsellersim.model;

import java.time.LocalDate;

public record PutOption(String ticker, LocalDate expiry, double strikePrice, double premium) {}
