package pl.edu.pjwstk.s28259.tpo10.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkResponse;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkRequest;
import pl.edu.pjwstk.s28259.tpo10.model.Link;
import pl.edu.pjwstk.s28259.tpo10.model.LinkService;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "/api/"
        , produces = { MediaType.APPLICATION_JSON_VALUE}
)
public class WebApiController {
    private static final ResponseEntity<?>
            NO_SUCH_LINK = ResponseEntity.status(HttpStatus.NOT_FOUND).build(),
            WRONG_PASSWORD = ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .header("Reason", "wrong password")
                    .build(),
            LINK_HAS_NO_PASSWORD =  ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .header("Reason", "This link is not password protected: cannot edit")
                    .build();
    
   

    private final LinkService linkService;
    public WebApiController(LinkService linkService) {
        this.linkService = linkService;
    }

    private ResponseEntity<?> redirect(Link link) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(link.getTargetUrl()))
                .build();
    }

    @GetMapping(value = "/red/{id}")
    public ResponseEntity<?> getLinkRedirect(@PathVariable String id) {
        Optional<Link> optionalLink = linkService.findLinkById(id);
        if (optionalLink.isPresent()) {
            Link link = optionalLink.get();

            link.incrementVisits();
            linkService.save(link);

            return redirect(link);
        }
        else return NO_SUCH_LINK;
    }
    
    @PostMapping("/links")
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

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newLink.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(linkService.toResponseDto(newLink));
    }

    @GetMapping(value = "/links/{id}")
    public ResponseEntity<?> getLink(@PathVariable String id){
        Optional<Link> optionalLink = linkService.findLinkById(id);

        if(optionalLink.isEmpty())
            return NO_SUCH_LINK;
        
        LinkResponse linkResponse = linkService.toResponseDto(optionalLink.get());
        return ResponseEntity.ok().body(linkResponse);
    }


    @PatchMapping(value = "/links/{id}")
    public ResponseEntity<?> updateLink(@PathVariable String id,
                                        @RequestBody LinkRequest linkRequest) {
        Optional<Link> optionalLink = linkService.findLinkById(id);
        if(optionalLink.isEmpty())
            return NO_SUCH_LINK;

        Link link = optionalLink.get();
        if(!link.hasPassword())
            return LINK_HAS_NO_PASSWORD;
        if (!link.isPasswordCorrect(linkRequest.getPassword()))
            return WRONG_PASSWORD;

        String name = linkRequest.getName();
        String targetUrl = linkRequest.getTargetUrl();

        if (name != null)
            link.setName(name);
        if (targetUrl != null)
            link.setTargetUrl(targetUrl);

        linkService.save(link);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/links/{id}")
    public ResponseEntity<?> deleteLink(@PathVariable String id,
                                        @RequestParam String password) {
        Optional<Link> optionalLink = linkService.findLinkById(id);


        if(optionalLink.isEmpty())
            return NO_SUCH_LINK;

        Link link = optionalLink.get();

        if(!link.hasPassword())
            return LINK_HAS_NO_PASSWORD;
        if (!link.isPasswordCorrect(password))
            return WRONG_PASSWORD;

        linkService.delete(link);
        return ResponseEntity.noContent().build();
    }
}

