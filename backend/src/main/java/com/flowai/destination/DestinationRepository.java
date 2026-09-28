package com.flowai.destination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {
    List<Destination> findAllByUserId(UUID userId);
    Optional<Destination> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByUserIdAndName(UUID userId, String name);
}