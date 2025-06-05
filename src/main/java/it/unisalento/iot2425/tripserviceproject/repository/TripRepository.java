package it.unisalento.iot2425.tripserviceproject.repository;

import it.unisalento.iot2425.tripserviceproject.domain.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TripRepository extends MongoRepository<Trip, String> {
    Optional<Trip> findById(String id);
    Optional<Trip> findByIdUser(String idUser);
    Optional<Trip> findByIdAndIdUser(String id, String idUser);
}
