package pl.edu.pjwstk.s28259.tpo10.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.pjwstk.s28259.tpo10.UrlManagerService;
import pl.edu.pjwstk.s28259.tpo10.UrlRepository;
import pl.edu.pjwstk.s28259.tpo10.model.LinkRequest;
import pl.edu.pjwstk.s28259.tpo10.model.Url;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "/api/red/"
//        , produces = { MediaType.APPLICATION_JSON_VALUE,
//                MediaType.APPLICATION_XML_VALUE,
//                MediaType.TEXT_PLAIN_VALUE }
)
public class RedirectController {
    private final UrlManagerService urlManagerService;
    private final UrlRepository urlRepository;

    public RedirectController(UrlManagerService urlManagerService, UrlRepository urlRepository) {
        this.urlManagerService = urlManagerService;
        this.urlRepository = urlRepository;
    }



//    @GetMapping(value = "/whatevr/{id}")
//    public ResponseEntity<?> getLinkRedirect(@PathVariable String id) {
//        Optional<Url> optionalUrl = urlRepository.findUrlByShortUrlId(id);
//
//        if (optionalUrl.isPresent()) {
//            Url url = optionalUrl.get();
//            return ResponseEntity
//                    .status(HttpStatus.FOUND)
//                    .location(URI.create(url.getTargetUrl()))
//                    .build();
//        }
//        else return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .build();
//
//    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getLink(@PathVariable String id) {
        Optional<Url> optionalUrl = urlRepository.findUrlByShortUrlId(id);

        if (optionalUrl.isPresent()) {
            Url url = optionalUrl.get();
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .body(url.getTargetUrl());

        }
        else return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();

    }

}

