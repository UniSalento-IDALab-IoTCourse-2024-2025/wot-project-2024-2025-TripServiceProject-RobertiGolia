package it.unisalento.iot2425.tripserviceproject.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("trip")
public class Trip {
    @Id
    private String id;

    private String indirizzoA;
    private String indirizzoB;

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
}
