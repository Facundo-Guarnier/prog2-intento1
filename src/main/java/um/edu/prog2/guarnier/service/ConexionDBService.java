package um.edu.prog2.guarnier.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ConexionDBService {

    private final Logger log = LoggerFactory.getLogger(ConexionDBService.class);

    //! intento de conexion a la db mysql para guardar las ordenes
    public void conexion(){
        String url = "jdbc:mysql://localhost:3306/prog2";
        String usuario = "root";
        String contraseña = "root";
        
        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos");

            String consultaSQL = "SELECT * FROM orden";
            Statement statement = conexion.createStatement();
            ResultSet resultado = statement.executeQuery(consultaSQL);

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                System.out.println("ID: " + id + ", Nombre: " + nombre);
            }

            resultado.close();
            statement.close();
            conexion.close();
        } catch (SQLException e) {
            System.err.println("Error al conectarse a la base de datos: " + e.getMessage());
        }
    }
}
