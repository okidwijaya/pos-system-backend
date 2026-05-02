package com.kitadevelopers.pos.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxCalculator {

    private TaxCalculator () {}

    public static BigDecimal Calculatetax(BigDecimal price, int quantity, BigDecimal taxRate){
        return price
                .multiply(BigDecimal.valueOf(quantity))
                .multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcualteSubtotalWithMax(BigDecimal price, int quantity, BigDecimal taxRate){
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal tax = subTotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        return subTotal.add(tax);
    }
}
