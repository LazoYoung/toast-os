package toast.enums;

public enum Page {
    HOME("/toast/fxml/home.fxml"),
    SETTINGS("/toast/fxml/settings.fxml"),
    SIMULATION("/toast/fxml/simulation.fxml");

    private final String location;

    Page(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
