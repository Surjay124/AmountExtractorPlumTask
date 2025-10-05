package com.example.MedicalAmountExtractor.model;

import java.util.ArrayList;
import java.util.List;


public class FinalResponse {
private String currency;
private List<AmountEntry> amounts = new ArrayList<>();
private String status;
private String reason;


public String getCurrency() { return currency; }
public void setCurrency(String currency) { this.currency = currency; }
public List<AmountEntry> getAmounts() { return amounts; }
public void setAmounts(List<AmountEntry> amounts) { this.amounts = amounts; }
public String getStatus() { return status; }
public void setStatus(String status) { this.status = status; }
public String getReason() { return reason; }
public void setReason(String reason) { this.reason = reason; }
}