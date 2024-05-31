package pl.edu.pjwstk.s28259.tpo10.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkPatchRequest;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkResponse;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkRequest;
import pl.edu.pjwstk.s28259.tpo10.model.Link;
import pl.edu.pjwstk.s28259.tpo10.service.DuplicateTargetUrlException;
import pl.edu.pjwstk.s28259.tpo10.service.LinkService;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Links API")
@RequestMapping(
        path = "${app.linksPath}"
        , produces = { MediaType.APPLICATION_JSON_VALUE}
)
public class LinksApiController {
    @Value("${app.linksPath}")
    private String linksPath;

    private final LinkService linkService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public LinksApiController(LinkService linkService, ObjectMapper objectMapper, Validator validator) {
        this.linkService = linkService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }
    private ResponseEntity<?> noSuchLinkResponse() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    private ResponseEntity<?> wrongPasswordResponse() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("reason", "wrong password")
                .build();
    }
    private ResponseEntity<?> linkHasNoPasswordResponse() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("reason", "This link is not password protected:  cannot edit")
                .build();
    }

    private URI getLocation(Link link) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(linksPath + "/{id}")
                .buildAndExpand(link.getId())
                .toUri();
    }

    @Operation(summary = "add new link")
    @PostMapping("")
    public ResponseEntity<?> addLink(@Valid @RequestBody LinkRequest linkRequest)
    {


        if (linkRequest.getName() == null || linkRequest.getTargetUrl() == null)
            return ResponseEntity.badRequest()
                    .body("Invalid data: name and targetUrl parameters must be provided");

        try{
            Link newLink = linkService.create(linkRequest);
            linkService.save(newLink);

            return ResponseEntity
                    .created(getLocation(newLink))
                    .body(linkService.toResponseDto(newLink));
        }
        catch (DuplicateTargetUrlException e) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", e.getMessage());
            responseBody.put("existingLink", e.getLink());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(responseBody);
        }

    }

    @Operation(summary = "get all links")
    @GetMapping("")
    public ResponseEntity<?> getAllLinks() {
        List<LinkResponse> linkResponses = linkService.getAllLinksAsDto();
        return ResponseEntity.ok().body(linkResponses);
    }

    @Operation(summary = "get link by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getLink(@PathVariable String id){
        Optional<Link> optionalLink = linkService.findLinkById(id);

        if(optionalLink.isEmpty())
            return noSuchLinkResponse();
        
        LinkResponse linkResponse = linkService.toResponseDto(optionalLink.get());
        return ResponseEntity.ok().body(linkResponse);
    }

    @Operation(summary = "delete link by id", description = "Delete link with the given id if the link is password-protected and your password is correct.")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteLink(@PathVariable String id,
                                        @RequestParam(required = false) String password) {

        Optional<Link> optionalLink = linkService.findLinkById(id);
        if(optionalLink.isEmpty())
            return noSuchLinkResponse();

        Link link = optionalLink.get();
        if(link.hasNoPassword())
            return linkHasNoPasswordResponse();

        if (link.isPasswordIncorrect(password))
            return wrongPasswordResponse();

        linkService.delete(link);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "update link", description = "Update link data if the link with the given id is password-protected and your password is correct.")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateLink(@PathVariable String id,
                                        @Valid @RequestBody LinkPatchRequest linkPatchRequest) {
        Optional<Link> optionalLink = linkService.findLinkById(id);
        if (optionalLink.isEmpty())
            return noSuchLinkResponse();

        Link link = optionalLink.get();
        if (link.hasNoPassword())
            return linkHasNoPasswordResponse();

        if (link.isPasswordIncorrect(linkPatchRequest.getPass()))
            return wrongPasswordResponse();

        linkService.updateLink(link, linkPatchRequest);

        return ResponseEntity.noContent().build();

    }
}

