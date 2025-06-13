package it.unisalento.iot2425.tripserviceproject.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisalento.iot2425.tripserviceproject.domain.Trip;
import it.unisalento.iot2425.tripserviceproject.dto.ListTripDTO;
import it.unisalento.iot2425.tripserviceproject.dto.ResultDTO;
import it.unisalento.iot2425.tripserviceproject.dto.TripDTO;
import it.unisalento.iot2425.tripserviceproject.repository.TripRepository;
import it.unisalento.iot2425.tripserviceproject.security.JwtUtilities;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

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
        Map<String, Object> json = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
        });

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
    public ResultDTO prenota(@RequestBody TripDTO tripDTO, @RequestHeader String idAutista, @RequestHeader("Authorization") String token) throws JsonProcessingException {
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
        String urlUser = "http://userSerProIoT:8080/api/users/" + userId;

        WebClient webClient = webClientBuilder.build();

        //faccio la chiamata API a user per prendere le informazioni dell'utente
        String responseUser = webClient.get()
                .uri(urlUser)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // ⏳ Bloccante
        System.out.println(responseUser);
        //rendo la strina un JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(responseUser);

        String idUser = root.path("id").asText();
        String nome = root.path("nome").asText();
        String cognome = root.path("cognome").asText();

        System.out.println("Id: " + idUser);
        System.out.println("Nome: " + nome);
        System.out.println("Cognome: " + cognome);

        String urlAutista = "http://userSerProIoT:8080/api/users/" + idAutista;
        String responseAutista = webClient.get()
                .uri(urlAutista)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // ⏳ Bloccante
        System.out.println(responseAutista);

        root = objectMapper.readTree(responseAutista);
        String idAutistaResponce = root.path("id").asText();
        String nomeAutista = root.path("nome").asText();
        String cognomeAutista = root.path("cognome").asText();
        boolean disp = root.path("disponibile").asBoolean();
        System.out.println("Disp: " + disp);
        if(disp) {

            System.out.println("IdAutista: " + idAutistaResponce);
            System.out.println("Nome Autista: " + nomeAutista);
            System.out.println("Cognome Autista: " + cognomeAutista);

            //il controllo della disponibilità lo si può fare sul front-end, mostrando la lista degli automiobilisti disponibili

            //modificare la disponibilità dell'automobilista con una chiamata API
            String urlModificaDisp = "http://userSerProIoT:8080/api/users/changeDispo/" + idAutista;
            String responseDisp = webClient.put()
                    .uri(urlModificaDisp)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // ⏳ Bloccante
            System.out.println(responseDisp);
            root = objectMapper.readTree(responseDisp);
            int result = root.path("result").asInt();
            String message = root.path("message").asText();

            System.out.println("result: " + result);
            System.out.println("message: " + message);
            //ora che ho sia l'id dell'utente e dell'autista devo salvare la configurazione all'interno del repository di Trip
            System.out.println("L'utente " + nome + " " + cognome + " ha prenotato la corsa con " + nomeAutista + " " + cognomeAutista);

            Trip trip = new Trip();
            trip.setIdUser(userId);
            trip.setIdAutista(idAutista);
            trip.setIndirizzoA(tripDTO.getAddA());
            trip.setIndirizzoB(tripDTO.getAddB());
            tripRepository.save(trip);
            resultDTO.setMessage("Prenotazione effettuata");
            resultDTO.setResult(ResultDTO.OK);
        } else {
            resultDTO.setMessage("Autista non disponibile");
            resultDTO.setResult(ResultDTO.ERRORE);
        }




        return resultDTO;
    }

    //fare funzione che ritorna i viaggi in corso
    @RequestMapping(value = "/corse",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public ListTripDTO getCorse() {
        ListTripDTO listTripDTO = new ListTripDTO();
        List<Trip> trips = tripRepository.findAll();
        List<TripDTO> tripDTOs = new ArrayList<>();
        for (Trip trip : trips) {
            TripDTO tripDTO = new TripDTO();
            tripDTO.setId(trip.getId());
            tripDTO.setAddA(trip.getIndirizzoA());
            tripDTO.setAddB(trip.getIndirizzoB());
            tripDTO.setIdUser(trip.getIdUser());
            tripDTO.setIdAutista(trip.getIdAutista());
            tripDTOs.add(tripDTO);
        }
        listTripDTO.setUsersList(tripDTOs);
        return listTripDTO;
    }


    @RequestMapping(value = "/termina_corsa/{idCorsa}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.DELETE)
    public ResultDTO terminaCorsa(@PathVariable String idCorsa) throws JsonProcessingException {
        ResultDTO resultDTO = new ResultDTO();

        Optional<Trip> trip = tripRepository.findById(idCorsa);
        if (trip.isEmpty()) {
            resultDTO.setResult(ResultDTO.ERRORE);
            resultDTO.setMessage("Corsa non trovata");
            return resultDTO;
        }
        Trip t = trip.get(); // Ora è sicuro

        String url = "http://userSerProIoT:8080/api/users/changeDispo/" + trip.get().getIdAutista();
        WebClient webClient = webClientBuilder.build();

        try {
            String responseDisp = webClient.put()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue("{}")  // Serve un body fittizio come fa curl implicitamente
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Risposta da users-service: " + responseDisp);

        } catch (Exception e) {
            e.printStackTrace();
        }


        tripRepository.delete(t);
        resultDTO.setMessage("Corsa finita");
        resultDTO.setResult(ResultDTO.OK);
        return resultDTO;
    }
}