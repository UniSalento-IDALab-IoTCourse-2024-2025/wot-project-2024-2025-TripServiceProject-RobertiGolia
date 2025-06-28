package it.unisalento.iot2425.tripserviceproject.dto;

import java.util.List;

public class ListTripDTO {
    private List<TripDTO> tripsList;

    public List<TripDTO> getTripsList() {
        return tripsList;
    }

    public void setTripList(List<TripDTO> usersList) {
        this.tripsList = usersList;
    }
}
