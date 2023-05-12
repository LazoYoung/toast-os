package toast.enums;

public enum Metric {
    RUN_TIME("elapsed-time"),
    AVERAGE_TT("average-tt"),
    POWER("power"),
    INVALID("invalid");

    private final String identifier;

    Metric(String identifier) {
        this.identifier = identifier;
    }

    public static Metric get(String identifier) {
        for (Metric m : values()) {
            if (m.getIdentifier().equals(identifier)) {
                return m;
            }
        }
        return INVALID;
    }

    public String getIdentifier() {
        return identifier;
    }
}
