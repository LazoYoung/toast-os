package toast.ui.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HomeController extends PageController {

    @FXML
    private Hyperlink notionLink;

    @FXML
    private Hyperlink githubLink;


    @Override
    void init() {

    }

    @Override
    void exit() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notionLink.setGraphic(makeImageView("toast/images/Home/Notion.png"));
        githubLink.setGraphic(makeImageView("toast/images/Home/GitHub.png"));

    }

    @FXML
    void openGithub() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/LazoYoung/toast-os"));
    }


    @FXML
    void openNotion() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://difficult-algebra-4c9.notion.site/TOAST-OS-5dd0e91e94e045468c53bce80821a2ba"));
    }

    private static ImageView makeImageView(String url) {
        ImageView notionImage = new ImageView();
        notionImage.setFitHeight(55);
        notionImage.setFitWidth(120);
        notionImage.setImage(new Image(url));
        return notionImage;
    }
}
