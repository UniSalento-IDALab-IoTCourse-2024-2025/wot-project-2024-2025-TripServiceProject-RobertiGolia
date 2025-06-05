package it.unisalento.iot2425.tripserviceproject.dto;

public class TripDTO {

    private String id;
    private String addA; //address
    private String addB;
    private String idUser;

    public String getAddA() {
        return addA;
    }

    public void setAddA(String addA) {
        this.addA = addA;
    }

    public String getAddB() {
        return addB;
    }

    public void setAddB(String addB) {
        this.addB = addB;
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
}
