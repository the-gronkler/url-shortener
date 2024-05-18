package pl.edu.pjwstk.s28259.tpo10.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.pjwstk.s28259.tpo10.UrlManagerService;
import pl.edu.pjwstk.s28259.tpo10.UrlRepository;
import pl.edu.pjwstk.s28259.tpo10.model.LinkRequest;
import pl.edu.pjwstk.s28259.tpo10.model.Url;

import java.net.URI;

@RestController
@RequestMapping(
        path = "/api/"
//        , produces = { MediaType.APPLICATION_JSON_VALUE,
//                MediaType.APPLICATION_XML_VALUE,
//                MediaType.TEXT_PLAIN_VALUE }
)
public class ApiController {
    private final UrlManagerService urlManagerService;
    private final UrlRepository urlRepository;

    public ApiController(UrlManagerService urlManagerService, UrlRepository urlRepository) {
        this.urlManagerService = urlManagerService;
        this.urlRepository = urlRepository;
    }

    @PostMapping(value = "/links",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            })
    public ResponseEntity<?> addLink(@RequestBody LinkRequest linkRequest,
                                     HttpServletRequest request )
    {
        String  password = linkRequest.getPassword(),
                name = linkRequest.getName(),
                targetUrl = linkRequest.getTargetUrl();
        if (name == null || targetUrl == null)
            return ResponseEntity.badRequest()
                    .body("Invalid data: name and targetUrl parameters must be provided");


        Url newUrl = urlManagerService.createUrlObject(password, name, targetUrl);
        urlRepository.save(newUrl);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUrl.getShortUrlId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(urlManagerService.toDto(newUrl));
    }

    @GetMapping(value = "/links/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            })
    public ResponseEntity<?> getLink(@PathVariable String id){


        Url url = urlRepository
                .findUrlByShortUrlId(id)
                .orElse(null);

        if (url == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(urlManagerService.toDto(url));
    }

}

