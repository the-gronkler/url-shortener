package pl.edu.pjwstk.s28259.tpo10.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
        var targetUrl = URI.create(link.getTargetUrl());
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(targetUrl)
                .body(targetUrl.toString());
    }

    @Tag(name = "GET", description = "Redirect to the target URL of the link with the given ID")
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

