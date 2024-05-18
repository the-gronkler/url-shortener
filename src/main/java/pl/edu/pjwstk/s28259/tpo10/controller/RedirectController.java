package pl.edu.pjwstk.s28259.tpo10.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.s28259.tpo10.model.Link;
import pl.edu.pjwstk.s28259.tpo10.model.LinkService;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "/api/red/"
)
public class RedirectController {
    private final LinkService linkService;
    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getLinkRedirect(@PathVariable String id) {
        Optional<Link> optionalUrl = linkService.findLinkById(id);

        if (optionalUrl.isPresent()) {
            Link link = optionalUrl.get();

            link.incrementVisits();
            linkService.save(link);

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(link.getTargetUrl()))
                    .build();
        }
        else return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();

    }

}

