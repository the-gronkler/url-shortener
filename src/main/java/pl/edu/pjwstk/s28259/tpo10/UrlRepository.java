package pl.edu.pjwstk.s28259.tpo10;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjwstk.s28259.tpo10.model.Url;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Integer> {
    @Query("SELECT u.shortUrlId FROM Url u")
    List<String> findAllShortUrlIds();

    Optional<Url> findUrlByShortUrlId(String shortUrlId);
}

