package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;

import um.edu.prog2.guarnier.domain.Orden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcesamientoDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesService.class);

    public void analizarOrdenes(JsonNode ordenesJson) {
        //! Crear objetos Ordenes a partir del json y revisa si se puede realizar la operacion
        log.debug("Analizando ordenes");

        try{
            JsonNode ordenes = ordenesJson.get("ordenes");

            for (JsonNode orden : ordenes) {
                int cliente = orden.get("cliente").asInt();
                int accionId = orden.get("accionId").asInt();
                String accion = orden.get("accion").asText();
                String operacion = orden.get("operacion").asText();
                Float precio = (float) orden.get("precio").asDouble();
                int cantidad = orden.get("cantidad").asInt();
                String fechaOperacion = orden.get("fechaOperacion").asText();
                String modo = orden.get("modo").asText();
                
                Orden ordenObj = new Orden(cliente, accionId, accion, operacion, precio, cantidad, fechaOperacion, modo, "pendiente");
                
                if (ordenObj.puedeRealizarOperacion()) {

                //TODO Revisar si funciona
                    if (ordenObj.getOperacion().equals("compra")) {
                        if (comprarOrden()) {
                            ordenObj.setEstado("comprada");
                        }
                    } else if (ordenObj.getOperacion().equals("venta")) {
                        if (venderOrden()) {
                            ordenObj.setEstado("vendida");
                        }
                    }

                } else {
                    noEsPosibleOperar();
                }
                //TODO -------------------

            }
        
        } catch (Exception e) {
            log.error("Error al analizar las ordenes", e);
        }
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
