package it.unisalento.iot2425.tripserviceproject.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("trip")
public class Trip {
    @Id
    private String id;

    private String indirizzoA;
    private String indirizzoB;
    private String idUser; //id dell'utente che ha prenotato il viaggio
    private String idAutista; //id dell'autista che si occuperà del viaggio

    public String getIndirizzoA() {
        return indirizzoA;
    }

    public void setIndirizzoA(String indirizzoA) {
        this.indirizzoA = indirizzoA;
    }

    public String getIndirizzoB() {
        return indirizzoB;
    }

    public void setIndirizzoB(String indirizzoB) {
        this.indirizzoB = indirizzoB;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdAutista() {
        return idAutista;
    }

    public void setIdAutista(String idAutista) {
        this.idAutista = idAutista;
    }
}
