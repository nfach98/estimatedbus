package com.example.eta.util;

public class SplitComma {

    String string;

    public SplitComma(String string) {
        this.string = string;
    }

    public String[] getSplitted(){
        return string.split(", ");
    }
}
