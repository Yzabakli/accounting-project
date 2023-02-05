package com.service.implementation;

import com.client.ExchangeClient;
import com.dto.currency.ExchangeRateDTO;
import com.dto.dashboard.FinancialSummaryDTO;
import com.service.DashboardService;
import com.service.InvoiceProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ExchangeClient exchangeClient;
    private final InvoiceProductService invoiceProductService;

    public DashboardServiceImpl(InvoiceProductService invoiceProductService, ExchangeClient exchangeClient) {
        this.invoiceProductService = invoiceProductService;
        this.exchangeClient = exchangeClient;
    }

    @Override
    public FinancialSummaryDTO financialSummaryForCurrentCompany() {

        return FinancialSummaryDTO.builder()
                .totalSales(invoiceProductService.getTotalSalesForLoggedCompany())
                .totalCost(invoiceProductService.getTotalCostForLoggedCompany())
                .profitLoss(invoiceProductService.getTotalProfitLossForLoggedCompany()).build();
    }

    @Override
    public ExchangeRateDTO getExchangeRates() {
        return exchangeClient.getExchangeRates();
    }

    @Override
    public BigDecimal[] profitPerExchangeMap() {

        BigDecimal[] profits = new BigDecimal[5];

        BigDecimal profitLoss = financialSummaryForCurrentCompany().getProfitLoss();
        ExchangeRateDTO rates = exchangeClient.getExchangeRates();
        profits[0] = profitLoss.multiply(rates.getEuro()).setScale(2, RoundingMode.HALF_EVEN);
        profits[1] = profitLoss.multiply(rates.getBritishPound()).setScale(2, RoundingMode.HALF_EVEN);
        profits[2] = profitLoss.multiply(rates.getSwissFranc()).setScale(2, RoundingMode.HALF_EVEN);
        profits[3] = profitLoss.multiply(rates.getJapaneseYen()).setScale(2, RoundingMode.HALF_EVEN);
        profits[4] = profitLoss.multiply(rates.getTurkishLira()).setScale(2, RoundingMode.HALF_EVEN);

        return profits;
    }
}
