package ista.M3A.service;

import ista.M3A.dto.WhatsAppRecords.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String apiUrl;
    
    @Value("${whatsapp.api.token}")
    private String apiToken;

    private final RestClient restClient;

    public WhatsAppService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public void processMessage(WhatsAppPayload payload) {
        try {
            if (payload.entry() == null || payload.entry().isEmpty()) return;
            var changes = payload.entry().get(0).changes();
            if (changes == null || changes.isEmpty()) return;
            var messages = changes.get(0).value().messages();
            
            if (messages != null && !messages.isEmpty()) {
                var message = messages.get(0);
                String usuario = message.from();

                // Lógica principal
                if ("text".equals(message.type())) {
                    // Si escriben cualquier texto, mandamos menú principal
                    enviarMenuBienvenida(usuario);
                } else if ("interactive".equals(message.type())) {
                    // Si presionan botones
                    handleInteractive(usuario, message.interactive());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleInteractive(String usuario, InteractiveReply reply) {
        String id = null;
        if (reply.button_reply() != null) id = reply.button_reply().id();
        else if (reply.list_reply() != null) id = reply.list_reply().id();

        switch (id) {
            case "btn_ver_cursos":
                enviarListaCursos(usuario);
                break;
            case "btn_crear_academia":
                enviarInfoAcademia(usuario);
                break;
            default:
                enviarMenuBienvenida(usuario);
        }
    }

    // 1. MENÚ PRINCIPAL (Botones)
    private void enviarMenuBienvenida(String telefono) {
        String texto = "¡Hola! Bienvenido a APECS. Somos expertos en Educación y Capacitación Tecnológica.\n\nPara brindarte la mejor información, por favor selecciona una opción:";
        
        var botones = List.of(
            new Button("reply", new Reply("btn_ver_cursos", "Ver Cursos / Capacitación")),
            new Button("reply", new Reply("btn_crear_academia", "Crear academia virtual"))
        );

        var interactive = new Interactive("button", new Body(texto), new Action(null, botones, null));
        enviarPayload(telefono, "interactive", interactive, null);
    }

    // 2. LISTA DE CURSOS (Lista desplegable)
    private void enviarListaCursos(String telefono) {
        String texto = "¿Qué habilidad quieres dominar hoy? Tenemos el curso perfecto para impulsar tu perfil profesional:";
        
        var filas = List.of(
            new Row("row_ia", "Ofimática con IA", "Domina Excel y herramientas inteligentes"),
            new Row("row_datos", "Análisis de Datos", "Decisiones con datos reales"),
            new Row("row_progra", "Programación", "Crea soluciones y soporte técnico"),
            new Row("row_blandas", "Habilidades Blandas", "Liderazgo y comunicación efectiva"),
            new Row("row_todo", "Ver Todo", "Descarga nuestra oferta completa")
        );

        var seccion = new Section("Cursos Disponibles", filas);
        var interactive = new Interactive("list", new Body(texto), new Action("Ver Opciones", null, List.of(seccion)));
        enviarPayload(telefono, "interactive", interactive, null);
    }

    // 3. INFO ACADEMIA (Texto final)
    private void enviarInfoAcademia(String telefono) {
        String mensaje = """
            ¡Entendido! Nos especializamos en crear Tu Propia Plataforma de Capacitación.
            
            Te entregamos tu Aula Virtual lista para que puedas entrenar a tu equipo de trabajo o publicar tus cursos fácilmente. En este momento estoy conectándote con un Asesor de Proyectos para atenderte mejor.
            
            Por favor, espera un momento y déjanos tus datos:
            1. Tu Nombre.
            2. Tu número de Cédula o RUC.
            """;
            
        enviarPayload(telefono, "text", null, new TextBody(mensaje));
    }

    private void enviarPayload(String to, String type, Interactive interactive, TextBody text) {
        var request = new SendMessageRequest("whatsapp", "individual", to, type, interactive, text);
        
        restClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer " + apiToken)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();
    }
}