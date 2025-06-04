package it.unisalento.iot2425.tripserviceproject.repository;

import it.unisalento.iot2425.tripserviceproject.domain.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TripRepository extends MongoRepository<Trip, String> {
}
