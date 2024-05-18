package pl.edu.pjwstk.s28259.tpo10;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.s28259.tpo10.model.*;

import java.security.SecureRandom;
import java.util.List;

@Service
//@PropertySource("classpath:custom.properties")
public class UrlManagerService {
    public final static char[] POSSIBLE_CHARS =
            "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 10;

    @Value("${app.redirectPrefix}")
    private String prefix;
    private final UrlRepository urlRepository;

    public UrlManagerService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Url createUrlObject(String password, String name, String targetUrl){
        List<String> urls = urlRepository.findAllShortUrlIds();
        String shortUrlId = generateRandomId();

        while( urls.contains(shortUrlId) )
            shortUrlId = generateRandomId();

        return new Url(password, name, targetUrl, shortUrlId);
    }

    public UrlDto toDto(Url url){
        return new UrlDto(
                url.getShortUrlId(),
                url.getName(),
                url.getTargetUrl(),
                prefix + url.getShortUrlId(),
                url.getVisits()
        );
    }


    private static String generateRandomId(){
        StringBuilder sb = new StringBuilder(UrlManagerService.LENGTH);
        for(int i = 0; i < UrlManagerService.LENGTH; i++)
            sb.append(POSSIBLE_CHARS[RANDOM.nextInt(0, POSSIBLE_CHARS.length)]);

        return sb.toString();
    }



}
