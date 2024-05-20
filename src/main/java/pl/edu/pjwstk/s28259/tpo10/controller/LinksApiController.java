package pl.edu.pjwstk.s28259.tpo10.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkResponse;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkRequest;
import pl.edu.pjwstk.s28259.tpo10.model.Link;
import pl.edu.pjwstk.s28259.tpo10.service.LinkService;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "${app.linksPath}"
        , produces = { MediaType.APPLICATION_JSON_VALUE}
)
public class LinksApiController {
    @Value("${app.linksPath}")
    private String linksPath;

    private final LinkService linkService;

    public LinksApiController(LinkService linkService) {
        this.linkService = linkService;
    }
    private ResponseEntity<?> noSuchLinkResponse() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    private ResponseEntity<?> wrongPasswordResponse() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("Reason", "wrong password")
                .build();
    }
    private ResponseEntity<?> linkHasNoPasswordResponse() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("Reason", "This link is not password protected: cannot edit")
                .build();
    }

    private URI getLocation(Link link) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(linksPath + "/{id}")
                .buildAndExpand(link.getId())
                .toUri();
    }

    @PostMapping("")
    public ResponseEntity<?> addLink(@RequestBody LinkRequest linkRequest)
    {
        String  password = linkRequest.getPassword(),
                name = linkRequest.getName(),
                targetUrl = linkRequest.getTargetUrl();
        if (name == null || targetUrl == null)
            return ResponseEntity.badRequest()
                    .body("Invalid data: name and targetUrl parameters must be provided");


        Link newLink = linkService.create(password, name, targetUrl);
        linkService.save(newLink);

        return ResponseEntity
                .created(getLocation(newLink))
                .body(linkService.toResponseDto(newLink));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getLink(@PathVariable String id){
        Optional<Link> optionalLink = linkService.findLinkById(id);

        if(optionalLink.isEmpty())
            return noSuchLinkResponse();
        
        LinkResponse linkResponse = linkService.toResponseDto(optionalLink.get());
        return ResponseEntity.ok().body(linkResponse);
    }


    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateLink(@PathVariable String id,
                                        @RequestBody LinkRequest linkRequest) {
        Optional<Link> optionalLink = linkService.findLinkById(id);
        if(optionalLink.isEmpty())
            return noSuchLinkResponse();

        Link link = optionalLink.get();
        if(!link.hasPassword())
            return linkHasNoPasswordResponse();
        if (!link.isPasswordCorrect(linkRequest.getPassword()))
            return wrongPasswordResponse();

        String name = linkRequest.getName();
        String targetUrl = linkRequest.getTargetUrl();

        if (name != null)
            link.setName(name);
        if (targetUrl != null)
            link.setTargetUrl(targetUrl);

        linkService.save(link);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteLink(@PathVariable String id,
                                        @RequestParam String password) {
        Optional<Link> optionalLink = linkService.findLinkById(id);


        if(optionalLink.isEmpty())
            return noSuchLinkResponse();

        Link link = optionalLink.get();

        if(!link.hasPassword())
            return linkHasNoPasswordResponse();
        if (!link.isPasswordCorrect(password))
            return wrongPasswordResponse();

        linkService.delete(link);
        return ResponseEntity.noContent().build();
    }
}

