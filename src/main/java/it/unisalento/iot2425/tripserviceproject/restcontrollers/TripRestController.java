package it.unisalento.iot2425.tripserviceproject.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisalento.iot2425.tripserviceproject.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @RequestMapping(value = "/msg/{address}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO msg(@PathVariable String address) {
        System.out.println("address: " + address);
        System.out.println();
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setResult(ResultDTO.OK);
        return resultDTO;
    }
}