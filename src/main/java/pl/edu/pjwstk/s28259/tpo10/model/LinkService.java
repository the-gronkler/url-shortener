package pl.edu.pjwstk.s28259.tpo10.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkResponse;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class LinkService {
    public final static char[] POSSIBLE_CHARS =
            "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ID_LENGTH = 10;

    @Value("${app.redirectPrefix}")
    private String prefix;
    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Link create(String password, String name, String targetUrl){
        List<String> urls = linkRepository.findAllIds();
        String shortUrlId = generateRandomId();

        while( urls.contains(shortUrlId) )
            shortUrlId = generateRandomId();

        return new Link(shortUrlId, password, name, targetUrl);
    }

    public String getRedirectUrl(String shortUrlId){
        return prefix + shortUrlId;
    }

    public LinkResponse toResponseDto(Link link){
        return new LinkResponse(
                link.getId(),
                link.getName(),
                link.getTargetUrl(),
                getRedirectUrl(link.getId()),
                link.getVisits()
        );
    }


    private static String generateRandomId(){
        StringBuilder sb = new StringBuilder(LinkService.ID_LENGTH);
        for(int i = 0; i < LinkService.ID_LENGTH; i++)
            sb.append(POSSIBLE_CHARS[RANDOM.nextInt(0, POSSIBLE_CHARS.length)]);

        return sb.toString();
    }


    public Link save(Link newLink) {
        return linkRepository.save(newLink);
    }

    public Optional<Link> findLinkById(String id) {
        return linkRepository.findLinkById(id);
    }

    public void delete(Link link) {
        linkRepository.delete(link);
    }
}
