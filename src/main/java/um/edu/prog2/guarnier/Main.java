package um.edu.prog2.guarnier;

import um.edu.prog2.guarnier.domain.Orden;

public class Main {
    public static void main(String[] args) {
        Orden orden1 = new Orden(26364, 6, "YPF", "COMPRA", null, 10, "2023-09-25T13:00:00Z", "AHORA", "pendiente");
        System.out.println("Se puede hacer la operacion? " + orden1.puedeRealizarOperacion());

    }
}
