package um.edu.prog2.guarnier;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import um.edu.prog2.guarnier.domain.Orden;
import um.edu.prog2.guarnier.service.ProcesamientoDeOrdenesService;

public class Main {
    public static void main(String[] args) {
        ProcesamientoDeOrdenesService procesamientoDeOrdenesService = new ProcesamientoDeOrdenesService();

        JsonNode respuestaCliente = solicitudHTTP("https://www.mockachino.com/2e3476f6-949b-42/api/ordenes/ordenes");

        List<List<Orden>> listas = procesamientoDeOrdenesService.analizarOrdenes(respuestaCliente);

        System.out.println("Procesadas" + listas.get(0));
        System.out.println("Fallidas" + listas.get(1));

    }

    public static JsonNode solicitudHTTP(String apiUrl) {

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode response = objectMapper.readTree(connection.getInputStream());
            return response;

        } catch (Exception e) {
            System.out.println("Error al hacer la solicitud HTTP" + e);
        }

        return null;
    }

}

// www.mockachino.com/
// Ejemplo, solo los 2 primeros tienen que fallar
// {
//   "ordenes": [
//     {
//       "cliente": 26364,
//       "accionId": 1,
//       "accion": "AAPL",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 10,
//       "fechaOperacion": "2023-09-25T03:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 3,
//       "accion": "INTC",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 0,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 2,
//       "accion": "GOOGL",
//       "operacion": "VENTA",
//       "precio": null,
//       "cantidad": 5,
//       "fechaOperacion": "2023-09-25T03:00:00Z",
//       "modo": "FINDIA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 4,
//       "accion": "KO",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 80,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 6,
//       "accion": "YPF",
//       "operacion": "VENTA",
//       "precio": null,
//       "cantidad": 5,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     }
//   ]
// }