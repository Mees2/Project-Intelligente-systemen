package framework.controllers;

public enum GameMode {
    PVP("PVP"),
    PVA("PVA"),
    SERVER("SERVER"),
    TOURNAMENT("TOURNAMENT"),
    AI_VS_AI("AI_VS_AI");

    private final String code;
    GameMode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    public boolean isServerMode() {
        return this == SERVER || this == TOURNAMENT;
    }
    public boolean hasAI() {
        return this == PVA || this == TOURNAMENT || this == AI_VS_AI;
    }

    public static GameMode fromCode(String code) {
        for (GameMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        return null;
    }
}
