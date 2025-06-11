package it.unisalento.iot2425.tripserviceproject.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisalento.iot2425.tripserviceproject.domain.Trip;
import it.unisalento.iot2425.tripserviceproject.dto.ResultDTO;
import it.unisalento.iot2425.tripserviceproject.dto.TripDTO;
import it.unisalento.iot2425.tripserviceproject.repository.TripRepository;
import it.unisalento.iot2425.tripserviceproject.security.JwtUtilities;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/trip")
public class TripRestController {

    @Autowired
    private Environment env;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}";

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping(value = "/ricerca/{address}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO getAll(@PathVariable String address) throws JsonProcessingException {
        String googleApiKey = env.getProperty("google.api.key");

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(GEOCODE_URL, String.class, address, googleApiKey);

        // Converte la stringa JSON in una mappa
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(response, new TypeReference<Map<String, Object>>() {});

        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setResult(ResultDTO.OK);
        resultDTO.setMessage("OK");
        resultDTO.setData(json);
        return resultDTO;
    }

    @RequestMapping(value = "/travel", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO travel(@RequestBody TripDTO tripDTO) {
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

    @RequestMapping(value = "/reserve",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO prenota(@RequestBody TripDTO tripDTO, @RequestHeader("Authorization") String token){
        ResultDTO resultDTO = new ResultDTO();

        String jwtToken = token.substring(7);

        // Verifica e decodifica il token JWT utilizzando il segreto condiviso.
        Date expirationDate = jwtUtilities.extractExpiration(jwtToken);
        if (expirationDate.before(new Date())) {
            // Token scaduto
            return null;
        }
        //prende l'id dell'utente per collegarlo ad un mezzo

        System.out.println(tripDTO.getAddA());
        System.out.println(tripDTO.getAddB());
        System.out.println(tripDTO.getIdAutista());

        String userId = jwtUtilities.extractClaim(jwtToken, claims -> claims.get("userId", String.class));
        String url = "http://userSerProIoT:8080/api/users/" + userId;

        WebClient webClient = webClientBuilder.build();

        String response = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // ⏳ Bloccante

        System.out.println("Risposta arrivata dal payment degli utenti: " + System.currentTimeMillis());
        System.out.println(response);


        System.out.println("Sto dando la risposta all'utente" + System.currentTimeMillis());
        resultDTO.setMessage("Prenotazione effettuata");
        resultDTO.setResult(ResultDTO.OK);
        return resultDTO;
    }

}