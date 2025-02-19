package Models;

import java.math.BigDecimal;

public class Locaux {

    private int idLocal;
    private int idUser;
    private String adresse;
    private int capacite;
    private String type;
    private String photo;
    private String equipement;
    private BigDecimal tarifs;

    public Locaux() {
    }

    public Locaux(int idUser, String adresse, int capacite, String type, String photo, String equipement, BigDecimal tarifs) {
        this.idUser = idUser;
        this.adresse = adresse;
        this.capacite = capacite;
        this.type = type;
        this.photo = photo;
        this.equipement = equipement;
        this.tarifs = tarifs;
    }

    public Locaux(int idLocal, int idUser, String adresse, int capacite, String type, String photo, String equipement, BigDecimal tarifs) {
        this.idLocal = idLocal;
        this.idUser = idUser;
        this.adresse = adresse;
        this.capacite = capacite;
        this.type = type;
        this.photo = photo;
        this.equipement = equipement;
        this.tarifs = tarifs;

    }


    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEquipement() {
        return equipement;
    }

    public void setEquipement(String equipement) {
        this.equipement = equipement;
    }

    public BigDecimal getTarifs() {
        return tarifs;
    }

    public void setTarifs(BigDecimal tarifs) {
        this.tarifs = tarifs;
    }

    @Override
    public String toString() {
        return "Locaux{" +
                "idLocal=" + idLocal +
                ", idUser=" + idUser +
                ", adresse='" + adresse + '\'' +
                ", capacite=" + capacite +
                ", type='" + type + '\'' +
                ", photo='" + photo + '\'' +
                ", equipement='" + equipement + '\'' +
                ", tarifs=" + tarifs +
                '}';
    }


}
