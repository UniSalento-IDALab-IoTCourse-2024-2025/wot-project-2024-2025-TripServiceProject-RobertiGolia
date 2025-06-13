package it.unisalento.iot2425.tripserviceproject.dto;

import java.util.List;

public class ListTripDTO {
    private List<TripDTO> usersList;

    public List<TripDTO> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<TripDTO> usersList) {
        this.usersList = usersList;
    }
}
