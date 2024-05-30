package pl.edu.pjwstk.s28259.tpo10.service;

import pl.edu.pjwstk.s28259.tpo10.dto.LinkResponse;

public class DuplicateTargetUrlException extends IllegalArgumentException {
    private final LinkResponse link;
    public DuplicateTargetUrlException(LinkResponse existingLink) {
        super("Target URL already exists in the database at url " + existingLink.getRedirectUrl());
        this.link = existingLink;
    }

    public LinkResponse getLink() {
        return link;
    }
}
