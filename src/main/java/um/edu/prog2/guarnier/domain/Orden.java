package um.edu.prog2.guarnier.domain;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import um.edu.prog2.guarnier.service.ProcesamientoDeOrdenesService;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Orden.
 */
@Entity
@Table(name = "orden")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orden")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Orden implements Serializable {
    
    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesService.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "cliente")
    private Integer cliente;

    @Column(name = "accion_id")
    private Integer accionId;

    @Column(name = "accion")
    private String accion;

    @Column(name = "operacion")
    private String operacion;

    @Column(name = "precio")
    private Float precio;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "fecha_operacion")
    private String fechaOperacion;

    @Column(name = "modo")
    private String modo;

    @Column(name = "estado")
    private String estado;



    public Orden(Integer cliente, Integer accionId, String accion, String operacion, Float precio,
            Integer cantidad, String fechaOperacion, String modo, String estado) {
        this.cliente = cliente;
        this.accionId = accionId;
        this.accion = accion;
        this.operacion = operacion;
        this.precio = (precio != null) ? precio : -1.0f;
        this.cantidad = cantidad;
        this.fechaOperacion = fechaOperacion;
        this.modo = modo;  //! "AHORA", "FINDIA", "INICIODIA"
        this.estado = estado;
    }


    public boolean puedeRealizarOperacion() {
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime fechaHora = ZonedDateTime.parse(this.fechaOperacion, formatter);
        ZoneId zonaHoraria = ZoneId.of("UTC");
        ZonedDateTime fechaHoraLocal = fechaHora.withZoneSameInstant(zonaHoraria);
        int hora = fechaHoraLocal.getHour();
        
        //! Condiciones:
        //! 1• Una orden instantánea no puede ejecutarse fuera del horario de transacciones, 
        //!    antes de las 09:00 y después de las 18:00. 
        if (this.modo == "AHORA" && hora <= 9 || hora > 18) {
            System.out.println(hora);
            log.debug("La hora está fuera del rango de 9:00 AM y 6:00 PM para una orden inmediata. Hora:" + hora);
            return false;
        }

        //! 2• Una orden debe tener asociado un cliente y una acción de una compañía. Se debe 
        //!    verificar que el Id de cliente y el Id de la acción sean válidos. Para esto 
        //!    se debe consultar el servicio cátedra buscando por Id de ambos.
        //!    Apuntar al endpoint http://192.168.194.254:8000/api/clientes/buscar?id=26365
        //!                        http://192.168.194.254:8000/api/acciones/buscar?id=2
        if (this.cliente == null || this.accionId == null) {
            log.debug("La orden no tiene un cliente o una acción asociada.");
            return false;
        }

        //! Cliente ID
        // String urlCliente = "http://192.168.194.254:8000/api/clientes/buscar?id=" + this.cliente;
        String urlCliente = "http://192.168.194.254:8000/api/clientes/buscar?nombre=" + "Tapia";
        JsonNode respuestaCliente = this.solicitudHTTP(urlCliente);
        JsonNode clientes = respuestaCliente.get("clientes");
        if (clientes.isArray() && clientes.size() > 0) {
            JsonNode cliente = clientes.get(0);     // El primer cliente de la lista
            int id = cliente.get("id").asInt();
            if (id != this.cliente) {
                log.debug("El cliente asociado a la orden no es válido.");
                return false;
            }
        } 
        else {
            log.debug("El cliente asociado a la orden no es válido.");
            return false;
        }

        //! Acción ID
        // String urlAccion = "http://192.168.194.254:8000/api/acciones/buscar?id=" + this.accionId;
        String urlAccion = "http://192.168.194.254:8000/api/acciones/buscar?empresa=" + this.accion;
        JsonNode respuestaAccion = this.solicitudHTTP(urlAccion);
        JsonNode acciones = respuestaAccion.get("acciones");
        if (acciones.isArray() && acciones.size() > 0) {
            JsonNode accion = acciones.get(0);     // La primera acción de la lista
            int id = accion.get("id").asInt();
            if (id != this.accionId) {
                log.debug("La acción asociada a la orden no es válida.");
                return false;
            }
        } 
        else {
            log.debug("La acción asociada a la orden no es válida.");
            return false;
        }

        //! 3• Una orden no puede tener un número de acciones <=0. Para verificar este punto 
        //!    se deberá hacer una consulta a servicios de la cátedra. 
        //!    Endpoint: http://192.168.194.254:8000/api/acciones/buscar?id=4
        // JsonNode respuesta = this.solicitudHTTP("http://192.168.194.254:8000/api/acciones/buscar?id=4");
        if (this.cantidad <= 0) {
            log.debug("La cantidad de acciones de la orden es menor o igual a 0.");
            return false;
        }


        //! 4• Revisar los valores del atributo MODO
        if (this.modo != "AHORA" && this.modo != "FINDIA" && this.modo != "INICIODIA") {
            log.debug("El modo de la orden no es válido: " + this.modo);
            return false;
        }

        //! Si todo está bien, devuelve true.
        return true;
    }


    private JsonNode solicitudHTTP(String apiUrl) {
        // String jwtToken = System.getenv("JWT_TOKEN");
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYWN1bmRvZ3Vhcm5pZXIiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzI5NzUzNzcyfQ.pklknWchQH_Y8kM8Is-XCfu6hYxWVJJqgg0rNBAH9IisOWKPW1n-jC3Xqecv6HFjwHvWc3nugiaB5gtMaNlShg";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode response = objectMapper.readTree(connection.getInputStream());
            return response;

        } catch (Exception e) {
            log.error("Error en la solicitud HTTP", e);
        }

        return null;
    }










    //!-------------------------------------------------------------------
    //! jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return this.id;
    }

    public Orden id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCliente() {
        return this.cliente;
    }

    public Orden cliente(Integer cliente) {
        this.setCliente(cliente);
        return this;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public Integer getAccionId() {
        return this.accionId;
    }

    public Orden accionId(Integer accionId) {
        this.setAccionId(accionId);
        return this;
    }

    public void setAccionId(Integer accionId) {
        this.accionId = accionId;
    }

    public String getAccion() {
        return this.accion;
    }

    public Orden accion(String accion) {
        this.setAccion(accion);
        return this;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getOperacion() {
        return this.operacion;
    }

    public Orden operacion(String operacion) {
        this.setOperacion(operacion);
        return this;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public Float getPrecio() {
        return this.precio;
    }

    public Orden precio(Float precio) {
        this.setPrecio(precio);
        return this;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public Orden cantidad(Integer cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getFechaOperacion() {
        return this.fechaOperacion;
    }

    public Orden fechaOperacion(String fechaOperacion) {
        this.setFechaOperacion(fechaOperacion);
        return this;
    }

    public void setFechaOperacion(String fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public String getModo() {
        return this.modo;
    }

    public Orden modo(String modo) {
        this.setModo(modo);
        return this;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getEstado() {
        return this.estado;
    }

    public Orden estado(String estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Orden)) {
            return false;
        }
        return id != null && id.equals(((Orden) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Orden{" +
            "id=" + getId() +
            ", cliente=" + getCliente() +
            ", accionId=" + getAccionId() +
            ", accion='" + getAccion() + "'" +
            ", operacion='" + getOperacion() + "'" +
            ", precio=" + getPrecio() +
            ", cantidad=" + getCantidad() +
            ", fechaOperacion='" + getFechaOperacion() + "'" +
            ", modo='" + getModo() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
