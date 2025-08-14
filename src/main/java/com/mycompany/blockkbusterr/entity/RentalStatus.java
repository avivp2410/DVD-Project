package com.mycompany.blockkbusterr.entity;

public enum RentalStatus {
    ACTIVE("Active"),
    RETURNED("Returned"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    RentalStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}