package pl.edu.pjwstk.s28259.tpo10.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pjwstk.s28259.tpo10.model.Link;
import pl.edu.pjwstk.s28259.tpo10.service.LinkService;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "${app.redirectPath}"
)
public class RedirectController {
    private final LinkService linkService;

    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }
    private ResponseEntity<?> redirectResponse(Link link) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(link.getTargetUrl()))
                .build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getLinkRedirect(@PathVariable String id) {
        Optional<Link> optionalLink = linkService.findLinkById(id);
        if (optionalLink.isPresent()) {
            Link link = optionalLink.get();

            link.incrementVisits();
            linkService.save(link);

            return redirectResponse(link);
        }
        else
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }
}

