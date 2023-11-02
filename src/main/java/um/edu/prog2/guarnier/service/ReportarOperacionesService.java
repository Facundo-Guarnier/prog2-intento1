package um.edu.prog2.guarnier.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import um.edu.prog2.guarnier.domain.Orden;

@Service
@Transactional
public class ReportarOperacionesService {

    private final Logger log = LoggerFactory.getLogger(ReportarOperacionesService.class);

    //TODO Guardar en una DB y usar el endpoint de reporte-operaciones de la cátedra.
    public void reportarOperaciones(List<List<Orden>> ordenes) {
        log.debug("Reportaando ordenes. " + ordenes);


    }
}
