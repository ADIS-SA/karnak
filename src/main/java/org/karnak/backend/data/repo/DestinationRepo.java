package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.DestinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationRepo extends JpaRepository<DestinationEntity, Long> {

}
