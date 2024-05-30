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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkResponse;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkRequest;
import pl.edu.pjwstk.s28259.tpo10.model.Link;
import pl.edu.pjwstk.s28259.tpo10.service.DuplicateTargetUrlException;
import pl.edu.pjwstk.s28259.tpo10.service.LinkService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public LinksApiController(LinkService linkService, ObjectMapper objectMapper) {
        this.linkService = linkService;
        this.objectMapper = objectMapper;
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

    private Link applyPatch(Link link, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        JsonNode linkNode = objectMapper.valueToTree(link);
        JsonNode patchedNode = patch.apply(linkNode);
        return objectMapper.treeToValue(patchedNode, Link.class);
    }

    private String extractParam(JsonMergePatch patch, String paramName) {
        JsonNode node =  objectMapper.convertValue(patch, JsonNode.class);
        return extractParam(node, paramName);
    }
    private String extractParam(JsonNode node, String paramName) {
        JsonNode paramNode = node.get(paramName);
        return paramNode == null
                ? null
                : paramNode.asText();
    }

    @Operation(summary = "add new link")
    @PostMapping("")
    public ResponseEntity<?> addLink(@Valid @RequestBody LinkRequest linkRequest)
    {
        String  password = linkRequest.getPassword(),
                name = linkRequest.getName(),
                targetUrl = linkRequest.getTargetUrl();

        if (name == null || targetUrl == null)
            return ResponseEntity.badRequest()
                    .body("Invalid data: name and targetUrl parameters must be provided");

        try{
            Link newLink = linkService.create(password, name, targetUrl);
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
    // I wanted to add some sort of info about what parameters to include in the jsonMergePatch, but it doesn't seem to be working
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pass", value = "password to access link",
                    required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "password",  dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "name",      dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "targetUrl", dataType = "string", paramType = "body")
    })
    public ResponseEntity<?> updateLink(@PathVariable String id,
                                        @RequestBody JsonMergePatch patch) {

        Optional<Link> optionalLink = linkService.findLinkById(id);
        if (optionalLink.isEmpty())
            return noSuchLinkResponse();

        Link link = optionalLink.get();
        if (link.hasNoPassword())
            return linkHasNoPasswordResponse();


        JsonNode node = objectMapper.convertValue(patch, JsonNode.class);
        String recievedPass = extractParam(node, "pass");
        if (link.isPasswordIncorrect(recievedPass))
            return wrongPasswordResponse();

        String newName = extractParam(node, "name");
        String newTargetUrl = extractParam(node, "targetUrl");
        String newPassword = extractParam(node, "password");

        if (newName != null)
            link.setName(newName);
        if (newTargetUrl != null)
            link.setTargetUrl(newTargetUrl);
        if (newPassword != null)
            link.setPassword(newPassword);

        linkService.save(link);

        return ResponseEntity.noContent().build();

    }


//    @Operation(summary = "UPDATE", description = "Update link data if the link with the fiven id is password-protected and your password is correct.")
//    @PatchMapping(value = "/{id}")
//    // I wanted to add some sort of info about what parameters to include in the jsonMergePatch, but it doesn't seem to be working
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pass", value = "password to access link",
//                    required = true, dataType = "string", paramType = "body"),
//            @ApiImplicitParam(name = "id",        dataType = "string", paramType = "body"),
//            @ApiImplicitParam(name = "password",  dataType = "string", paramType = "body"),
//            @ApiImplicitParam(name = "name",      dataType = "string", paramType = "body"),
//            @ApiImplicitParam(name = "targetUrl", dataType = "string", paramType = "body"),
//            @ApiImplicitParam(name = "visits",    dataType = "integer", paramType = "body")
//    })
//    public ResponseEntity<?> updateLinkAllParams(@PathVariable String id,
//                                        @RequestBody JsonMergePatch patch) {
//        try {
//            Optional<Link> optionalLink = linkService.findLinkById(id);
//            if (optionalLink.isEmpty())
//                return noSuchLinkResponse();
//
//            Link link = optionalLink.get();
//            if (link.hasNoPassword())
//                return linkHasNoPasswordResponse();
//
//            String recievedPass = extractParam(patch, "pass");
//            if (link.isPasswordIncorrect(recievedPass))
//                return wrongPasswordResponse();
//
//
//            String newId = extractParam(patch, "id");
//            if( newId != null
//                && ! link.idEquals(newId)
//                && linkService.existsWithId(id)
//            )
//                return ResponseEntity.badRequest().body("Invalid data: link with such id already exists");
//
//            Link patchedLink = applyPatch(link, patch);
//            linkService.save(patchedLink);
//
//            return ResponseEntity.noContent().build();
//
//        }
//        catch (JsonPatchException | JsonProcessingException e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
//        }
//    }

}

