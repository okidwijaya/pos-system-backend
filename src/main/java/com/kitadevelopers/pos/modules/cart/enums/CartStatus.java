package com.kitadevelopers.pos.modules.cart.enums;

public enum CartStatus {
    ACTIVE("active"),
    ABANDONED("abandoned"),
    CHECKED_OUT("checked_out");

    private final String label;

    CartStatus(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
