package Controllers.Admin.GS;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ServiceDetailsController {

    @FXML
    private Label serviceNameLabel;
    @FXML
    private Label serviceDescriptionLabel;
    @FXML
    private ImageView serviceImageView;

    public void setDetails(String name, String description, String imagePath) {
        serviceNameLabel.setText(name);
        serviceDescriptionLabel.setText(description);
        serviceImageView.setImage(new Image(imagePath));
    }

}
