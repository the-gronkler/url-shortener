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
        path = "/api/links"
//        , produces = { MediaType.APPLICATION_JSON_VALUE,
//                MediaType.APPLICATION_XML_VALUE,
//                MediaType.TEXT_PLAIN_VALUE }
)
public class WebApiController {
    private final UrlManagerService urlManagerService;
    private final UrlRepository urlRepository;

    public WebApiController(UrlManagerService urlManagerService, UrlRepository urlRepository) {
        this.urlManagerService = urlManagerService;
        this.urlRepository = urlRepository;
    }

    @PostMapping(value = "",
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

    @GetMapping(value = "/{id}",
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


    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateLink(@PathVariable String id,
                                        @RequestBody LinkRequest linkRequest) {
        Optional<Url> optionalUrl = urlRepository.findUrlByShortUrlId(id);

        // no such link
        if(optionalUrl.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Url url = optionalUrl.get();
        String  password = linkRequest.getPassword(),
                name = linkRequest.getName(),
                targetUrl = linkRequest.getTargetUrl();

        // link has no password
        if(!url.hasPassword())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .header("Reason", "This link is not password protected: cannot edit")
                    .build();

        // wrong password
        if (!url.isPasswordCorrect(password))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .header("Reason", "wrong password")
                    .build();



        if (name != null)
            url.setName(linkRequest.getName());

        if (targetUrl != null)
            url.setTargetUrl(linkRequest.getTargetUrl());


        urlRepository.save(url);
        return ResponseEntity.noContent().build();

    }



}

