package ista.M3A.dto;

import java.util.List;

public class WhatsAppRecords {


    //lo que recibes
    public static record WhatsAppPayload(List<Entry> entry) {}
    public static record Entry(List<Change> changes) {}
    public static record Change(Value value) {}
    public static record Value(String messaging_product, List<Message> messages) {}
    public static record Message(String from, String id, String type, TextBody text, InteractiveReply interactive) {}
    public static record TextBody(String body) {}
    public static record InteractiveReply(String type, Reply list_reply, Reply button_reply) {}

    //lo que envias
    public static record SendMessageRequest(String messaging_product, String recipient_type, String to, String type, Interactive interactive, TextBody text) {}
    public static record Interactive(String type, Body body, Action action) {}
    public static record Body(String text) {}
    public static record Action(String button, List<Button> buttons, List<Section> sections) {}
    public static record Button(String type, Reply reply) {}
    public static record Reply(String id, String title) {}
    public static record Section(String title, List<Row> rows) {}
    public static record Row(String id, String title, String description) {}
    
}
