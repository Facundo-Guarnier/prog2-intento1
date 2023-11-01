-   Una orden debe tener asociado un cliente y una acción de una compañía. Se debe verificar que el Id de cliente y el Id de la acción sean válidos. Para esto se debe consultar el servicio cátedra buscando por Id de ambos.
    Problema: No se puede buscar por id (?id=98) devuelve cualquier cosa. Pasa con todo (acciones, clientes)

-   Una orden no puede tener un número de acciones <=0. Para verificar este punto se deberá hacer una consulta a servicios de la cátedra.
    Problema: tengo que hacer un "if (cantidad > 0)" pero ¿Qué tiene que ver la consulta a la cátedra?

-   Que funcione el .env
