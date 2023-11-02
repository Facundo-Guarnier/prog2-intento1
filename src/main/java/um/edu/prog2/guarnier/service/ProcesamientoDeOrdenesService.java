package um.edu.prog2.guarnier.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import um.edu.prog2.guarnier.domain.Orden;

@Service
@Transactional
public class ProcesamientoDeOrdenesService {
    
    private List<Orden> ordenesProcesadas = new ArrayList<Orden>();
    private List<Orden> ordenesFallidas = new ArrayList<Orden>();

    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesService.class);

    public List<List<Orden>> analizarOrdenes(JsonNode ordenesJson) {
        //! Crear objetos Ordenes a partir del json y revisa si se puede realizar la operacion
        log.debug("Analizando ordenes");

        try{
            JsonNode ordenes = ordenesJson.get("ordenes");

            //TODO ¿Esto se puede hacer con un DTO?
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
                    esPosibleOperar(ordenObj);
                } else {
                    noEsPosibleOperar(ordenObj);
                }
            
                System.out.println(ordenObj +"\n\n");
            }
        
        } catch (Exception e) {
            log.error("Error al analizar las ordenes", e);
        }

        //! Devuelve una lista de listas, la primera con las ordenes procesadas y la segunda con las fallidas
        List<List<Orden>> resultado = new ArrayList<>();
        resultado.add(ordenesProcesadas);
        resultado.add(ordenesFallidas);
        return resultado;
    }


    //! Se almacenará la operación en una lista de operaciones fallidas y continuamos con la siguiente. 
    public void noEsPosibleOperar(Orden orden) {
        log.debug("No es posible realizar la operacion");
        orden.setEstado("FALLIDO");
        this.ordenesFallidas.add(orden);
    }


    public void esPosibleOperar(Orden orden) {
        log.debug("Es posible realizar la operacion");
        
        if (!orden.getModo().equals("AHORA")) {
            programarOrden(orden);
            
        } else if (orden.getOperacion().equals("COMPRA")) {
            comprarOrden(orden);
            
        } else if (orden.getOperacion().equals("VENTA")) {
            venderOrden(orden);
        }
        
        this.ordenesProcesadas.add(orden);
    }


    //TODO ¿Que hay que hacer acá?
    public void programarOrden(Orden orden) {
        log.debug("Programando operacion");
        orden.setEstado("PROGRAMADO");
    }


    public boolean venderOrden(Orden orden) {
        log.debug("Vendiendo orden");
        orden.setEstado("COMPLETADO");
        return true;
    }


    public boolean comprarOrden(Orden orden) {
        log.debug("Comprando orden");
        orden.setEstado("COMPLETADO");
        return true;
    }
}
