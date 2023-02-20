package com.example.WriteHere.model.report;

public enum ComplaintType {
    SPAM("Spam"),
    HATE_SPEECH("Hate speech"),
    BULLYING("Bullying"),
    INAPPROPRIATE_CONTENT("Inappropriate content"),
    OFFENSIVE_LANGUAGE("Offensive language"),
    PERSONAL_ATTACK("Personal attack"),
    ADVERTISING("Advertising"),
    TROLLING("Trolling");

    private final String displayName;

    ComplaintType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}