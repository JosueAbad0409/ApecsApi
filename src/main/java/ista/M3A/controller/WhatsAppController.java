package ista.M3A.controller;

import ista.M3A.dto.WhatsAppRecords.*;
import ista.M3A.service.WhatsAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    private final WhatsAppService service;
    
    // Este token lo inventas tú aquí, luego lo pones en Facebook
    private final String VERIFY_TOKEN = "APECS123"; 

    public WhatsAppController(WhatsAppService service) {
        this.service = service;
    }

    // Para verificar el Webhook (Lo primero que hace Facebook)
    @GetMapping
    public ResponseEntity<Integer> verify(@RequestParam("hub.verify_token") String token,
                                          @RequestParam("hub.challenge") int challenge) {
        if (VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).build();
    }

    // Para recibir mensajes
    @PostMapping
    public ResponseEntity<String> receive(@RequestBody WhatsAppPayload payload) {
        service.processMessage(payload);
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}