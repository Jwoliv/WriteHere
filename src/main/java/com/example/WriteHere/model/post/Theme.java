package com.example.WriteHere.model.post;

public enum Theme {
    it("IT"),
    movie("Movie"),
    photo("Photo"),
    anime("Anime"),
    games("Games"),
    war("War"),
    programing("Programing");

    private final String displayName;
    Theme(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
