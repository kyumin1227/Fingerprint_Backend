package com.example.fingerprint_backend.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LineWebhookRequest {
    private String destination;
    private List<Event> events;

    @Data
    public static class Event {
        private String type;
        private Message message;
        private String webhookEventId;
        private DeliveryContext deliveryContext;
        private long timestamp;
        private Source source;
        private String replyToken;
        private String mode;
    }

    @Data
    public static class Message {
        private String type;
        private String id;
        private String quotedMessageId;
        private String quoteToken;
        private String text;
    }

    @Data
    public static class DeliveryContext {
        @JsonProperty("isRedelivery")
        private boolean isRedelivery;
    }

    @Data
    public static class Source {
        private String type;
        private String groupId;
        private String userId;
    }
}