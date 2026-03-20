package com.blog.writeapi.configs.api.metadata;

import is.tagomor.woothee.Classifier;

public record ClientMetadataDTO(
        String ipAddress,
        String userAgent,
        String fingerprint
) {
    public boolean isBot() {
        return Classifier.isCrawler(userAgent);
    }
}
