package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcesamientoDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesService.class);

    public void analizarOrdenes(JsonNode ordenesJson) {
        log.debug("Analizando ordenes");

        JsonNode ordenes = ordenesJson.get("ordenes");

        if (ordenes != null && ordenes.isArray()) {
            for (JsonNode orden : ordenes) {
                int cliente = orden.get("cliente").asInt();
                int accionId = orden.get("accionId").asInt();
                String accion = orden.get("accion").asText();
                String operacion = orden.get("operacion").asText();
                // Realiza la lógica deseada con cada orden
            }
        } else {
            System.out.println("La lista de órdenes no está presente o no es un array válido.");
        }
    }

    public boolean puedeRealizarOperacion() {
        return false;
    }

    public void noEsPosibleOperar() {
        log.debug("No es posible realizar la operacion");
    }

    public void EsPosibleOperar() {
        log.debug("Es posible realizar la operacion");
    }

    public boolean venderOrden() {
        log.debug("Vendiendo orden");
        return true;
    }

    public boolean comprarOrden() {
        log.debug("Comprando orden");
        return true;
    }
}
