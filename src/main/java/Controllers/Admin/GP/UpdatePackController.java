package Controllers.Admin.GP;

import Models.Locaux;
import Models.Pack;
import Models.Service;
import Services.ServiceGP;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UpdatePackController {

    @FXML private TextField tfNom;
    @FXML private TextArea taDescription;
    @FXML private TextField tfPrix;
    @FXML private TextField tfNbreInvitesMax;
    @FXML private TextField tfBudgetPrevu;
    @FXML private DatePicker dpDateEvenement;
    @FXML private ComboBox<String> cbType; // Exemple de types prédéfinis
    @FXML private ComboBox<Locaux> cbLieu;
    @FXML private VBox servicesContainer;
    @FXML private Button btnEnregistrer;
    @FXML private ComboBox<String> cbStatut;


    private Pack currentPack;
    private ServiceGP serviceGP = new ServiceGP();
    private List<Service> allServices;


    public void initData(Pack pack) {
        this.currentPack = pack;
        tfNom.setText(pack.getNom());
        taDescription.setText(pack.getDescription());
        tfPrix.setText(pack.getPrix().toString());
        tfNbreInvitesMax.setText(String.valueOf(pack.getNbreInvitesMax()));
        tfBudgetPrevu.setText(pack.getBudgetPrevu().toString());
        dpDateEvenement.setValue(pack.getDateEvenement());
        cbType.setValue(pack.getType());
        // Initialisation du statut
        if(cbStatut != null) {
            cbStatut.getItems().addAll("actif", "inactif","archivé");
            cbStatut.setValue(pack.getStatut());
        } else {
            System.err.println("Erreur: cbStatut n'est pas initialisé !");
        }

        List<Locaux> allLocaux = serviceGP.getAllLocaux();
        cbLieu.getItems().setAll(allLocaux);
        for (Locaux local : allLocaux) {
            if (local.getAdresse().equalsIgnoreCase(pack.getLieu())) {
                cbLieu.setValue(local);
                break;
            }
        }

        allServices = serviceGP.getAllServices();
        List<Integer> associatedServiceIds = serviceGP.getServicesByPackId(pack.getId());
        servicesContainer.getChildren().clear();
        for (Service service : allServices) {
            CheckBox checkBox = new CheckBox(service.getNom_service());
            checkBox.setUserData(service);
            if (associatedServiceIds.contains(service.getId_service())) {
                checkBox.setSelected(true);
            }
            servicesContainer.getChildren().add(checkBox);
        }
    }


    @FXML
    private void handleUpdate(ActionEvent event) {
        Pack updatedPack = new Pack();
        updatedPack.setId(currentPack.getId());
        updatedPack.setNom(tfNom.getText());
        updatedPack.setDescription(taDescription.getText());
        updatedPack.setPrix(new BigDecimal(tfPrix.getText()));
        updatedPack.setNbreInvitesMax(Integer.parseInt(tfNbreInvitesMax.getText()));
        updatedPack.setBudgetPrevu(new BigDecimal(tfBudgetPrevu.getText()));
        updatedPack.setDateEvenement(dpDateEvenement.getValue());
        updatedPack.setType(cbType.getValue());


        Locaux selectedLocal = cbLieu.getValue();
        if (selectedLocal != null) {
            updatedPack.setLieu(selectedLocal.getAdresse());
        } else {
            updatedPack.setLieu("");
        }

        updatedPack.setStatut(cbStatut.getValue());


        List<Integer> selectedServiceIds = new ArrayList<>();
        for (javafx.scene.Node node : servicesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                if (checkBox.isSelected()) {
                    Service service = (Service) checkBox.getUserData();
                    selectedServiceIds.add(service.getId_service());
                }
            }
        }


        int idLocal = -1;
        if (selectedLocal != null) {
            idLocal = selectedLocal.getIdLocal();
        } else {
            idLocal = serviceGP.recupererIdLocal(updatedPack.getLieu());
        }
        if (idLocal == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Local non trouvé");
            alert.setContentText("Aucun local trouvé pour l'adresse : " + updatedPack.getLieu());
            alert.showAndWait();
            return;
        }

        serviceGP.modifier(updatedPack, selectedServiceIds, idLocal);

        Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
        stage.close();
    }
}
