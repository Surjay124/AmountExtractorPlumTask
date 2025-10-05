package com.example.MedicalAmountExtractor.model;

public class AmountEntry {
private String type;
private double value;
private String source;


public String getType() { return type; }
public void setType(String type) { this.type = type; }
public double getValue() { return value; }
public void setValue(double value) { this.value = value; }
public String getSource() { return source; }
public void setSource(String source) { this.source = source; }
}