package um.edu.prog2.guarnier.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import um.edu.prog2.guarnier.domain.Orden;

@Service
@Transactional
public class ServicioExternoService {

    private final Logger log = LoggerFactory.getLogger(ServicioExternoService.class);

    public void simularOrdenes() {
        log.debug("Simulando ordenes");
        ProcesamientoDeOrdenesService procesamientoDeOrdenesService = new ProcesamientoDeOrdenesService();
        CatedraAPIService cs = new CatedraAPIService();

        // JsonNode respuestaCliente = cs.get("https://www.mockachino.com/2e3476f6-949b-42/api/ordenes/ordenes");
        JsonNode respuestaCliente = cs.getConJWT("http://192.168.194.254:8000/api/ordenes/ordenes");

        List<List<Orden>> listas = procesamientoDeOrdenesService.analizarOrdenes(respuestaCliente);
    }

}
