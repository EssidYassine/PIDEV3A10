
package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationLocaux {
    private int idReservation;
    private int idLocal;
    private int idUser;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;

    public ReservationLocaux(int idReservation, int idLocal, int idUser, LocalDateTime dateDebut, LocalDateTime dateFin, String statut) {
        this.idReservation = idReservation;
        this.idLocal = idLocal;
        this.idUser = idUser;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
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

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "ReservationLocaux{" +
                "idReservation=" + idReservation +
                ", idLocal=" + idLocal +
                ", idUser=" + idUser +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                '}';
    }
}
