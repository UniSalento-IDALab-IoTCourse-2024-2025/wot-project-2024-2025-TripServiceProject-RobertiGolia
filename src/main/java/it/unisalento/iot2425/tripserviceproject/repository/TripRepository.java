package it.unisalento.iot2425.tripserviceproject.repository;

import it.unisalento.iot2425.tripserviceproject.domain.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends MongoRepository<Trip, String> {
    Optional<Trip> findById(String id);
    List<Trip> findByIdUser(String idUser);
    List<Trip> findByIdAutista(String idUser);
    Optional<Trip> findByIdAndIdUser(String id, String idUser);
}
