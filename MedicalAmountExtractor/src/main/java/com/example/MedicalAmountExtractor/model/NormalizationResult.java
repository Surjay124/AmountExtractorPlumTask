package com.example.MedicalAmountExtractor.model;

import java.util.ArrayList;
import java.util.List;


public class NormalizationResult {
private List<Double> normalizedAmounts = new ArrayList<>();
private String currencyHint;
private double normalizationConfidence = 0.0;


public List<Double> getNormalizedAmounts() { return normalizedAmounts; }
public void setNormalizedAmounts(List<Double> normalizedAmounts) { this.normalizedAmounts = normalizedAmounts; }
public String getCurrencyHint() { return currencyHint; }
public void setCurrencyHint(String currencyHint) { this.currencyHint = currencyHint; }
public double getNormalizationConfidence() { return normalizationConfidence; }
public void setNormalizationConfidence(double normalizationConfidence) { this.normalizationConfidence = normalizationConfidence; }
}