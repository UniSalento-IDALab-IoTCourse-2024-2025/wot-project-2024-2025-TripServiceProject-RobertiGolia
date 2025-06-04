package it.unisalento.iot2425.tripserviceproject.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisalento.iot2425.tripserviceproject.dto.ResultDTO;
import it.unisalento.iot2425.tripserviceproject.dto.TripDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip")
public class TripRestController {

    @Autowired
    private Environment env;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}";

    @RequestMapping(value = "/ricerca/{address}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO getAll(@PathVariable String address) throws JsonProcessingException {
        String googleApiKey = env.getProperty("google.api.key");

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(GEOCODE_URL, String.class, address, googleApiKey);

        // Converte la stringa JSON in una mappa
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(response, Map.class);
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setResult(ResultDTO.OK);
        resultDTO.setMessage("OK");
        resultDTO.setData(json);
        return resultDTO;
    }

    @RequestMapping(value = "/travel", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO msg(@RequestBody TripDTO tripDTO) {
        String googleApiKey = env.getProperty("google.api.key");

        String baseUrl = "https://maps.googleapis.com/maps/api/directions/json";
        String url = String.format("%s?origin=%s&destination=%s&key=%s", baseUrl,
                URLEncoder.encode(tripDTO.getAddA(), StandardCharsets.UTF_8),
                URLEncoder.encode(tripDTO.getAddB(), StandardCharsets.UTF_8),
                googleApiKey);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        ResultDTO resultDTO = new ResultDTO();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray routes = json.getJSONArray("routes");
            if (!routes.isEmpty()) {
                JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                JSONArray steps = leg.getJSONArray("steps");
                List<String> instructions = new ArrayList<>();

                for (int i = 0; i < steps.length(); i++) {
                    JSONObject step = steps.getJSONObject(i);
                    String htmlInstruction = step.getString("html_instructions");
                    // Rimuovi i tag HTML se vuoi solo testo semplice
                    String plainInstruction = htmlInstruction.replaceAll("\\<.*?\\>", "");
                    instructions.add(plainInstruction);
                }


                String distance = leg.getJSONObject("distance").getString("text");
                String duration = leg.getJSONObject("duration").getString("text");

                resultDTO.setResult(ResultDTO.OK);
                resultDTO.setSteps(instructions);

                resultDTO.setMessage("Distanza: " + distance + ", Durata: " + duration);
            } else {
                resultDTO.setResult(ResultDTO.ERRORE);
                resultDTO.setMessage("Nessun percorso trovato.");
            }
        } catch (Exception e) {
            resultDTO.setResult(ResultDTO.ERRORE);
            resultDTO.setMessage("Errore durante l'elaborazione del percorso.");
        }

        return resultDTO;
    }

}