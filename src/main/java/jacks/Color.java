package jacks;

public enum Color {
    GRAY("\u001b[90m"),

    RED("\u001b[31m"),
    YELLOW("\u001b[33m"),
    GREEN("\u001b[32m"),
    BLUE("\u001b[34m"),
    PURPLE("\u001b[35m"),
    CYAN("\u001b[36m"),

    ITALIC("\u001b[3m"),
    BOLD("\u001b[1m"),
    UNDERLINE("\u001b[4m"),

    RESET("\u001b[0m");

    public final String color;
    Color(String s) {
        this.color = s;
    }

    public String getColor() {
        return color;
    }
}
