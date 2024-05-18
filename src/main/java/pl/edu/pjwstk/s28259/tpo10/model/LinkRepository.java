package pl.edu.pjwstk.s28259.tpo10.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends CrudRepository<Link, Integer> {
    @Query("SELECT l.id FROM Link l")
    List<String> findAllIds();

    Optional<Link> findLinkById(String id);
}

