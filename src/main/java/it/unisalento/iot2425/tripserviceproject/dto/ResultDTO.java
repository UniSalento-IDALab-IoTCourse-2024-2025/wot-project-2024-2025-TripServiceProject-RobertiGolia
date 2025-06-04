package it.unisalento.iot2425.tripserviceproject.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultDTO {

    public final static int OK = 1;
    public final static int ERRORE = 2;
    public final static int ELIMINATO = 3;
    public final static int AGGIORNATO = 4;

    private TripDTO trip;
    private int result;
    private String message;
    private Map<String, Object> data;
    private List<String> steps;


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TripDTO getTrip() {
        return trip;
    }

    public void setTrip(TripDTO trip) {
        this.trip = trip;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
