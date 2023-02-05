package com.service;

import com.dto.currency.ExchangeRateDTO;
import com.dto.dashboard.FinancialSummaryDTO;

import java.math.BigDecimal;

public interface DashboardService {
    FinancialSummaryDTO financialSummaryForCurrentCompany();

    ExchangeRateDTO getExchangeRates();

    BigDecimal[] profitPerExchangeMap();
}
