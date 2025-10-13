package cliente;

import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import comun.DiaSemana;

public class ClienteSockets {

    /**
     * Muestra el menú de opciones y lee repetidamente de teclado hasta obtener una opción válida.
     */
    public static int menu(Scanner teclado) {
        int opcion;
        System.out.println("\n\n");
        System.out.println("=====================================================");
        System.out.println("============            MENU        =================");
        System.out.println("=====================================================");
        System.out.println("0. Salir");
        System.out.println("1. Listar las reservas");
        System.out.println("2. Listar plazas disponibles de una actividad");
        System.out.println("3. Hacer una reserva");
        System.out.println("4. Modificar una reserva");
        System.out.println("5. Cancelar una reserva");
        do {
            System.out.print("\nElige una opcion (0..5): ");
            opcion = teclado.nextInt();
        } while ((opcion < 0) || (opcion > 5));
        teclado.nextLine(); // limpia buffer
        return opcion;
    }

    /**
     * Programa principal. Muestra el menú repetidamente y atiende las peticiones del usuario.
     *
     * @param args	no se usan argumentos de entrada al programa principal
     */
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        String host = "localhost";  // o IP del servidor
        int puerto = 6000;          // puerto donde escucha el servidor

        try {
            // Crea un auxiliar de cliente sockets (en lugar del gestor local)
            AuxiliarClienteSockets cliente = new AuxiliarClienteSockets(host, puerto);

            System.out.print("Introduce tu código de usuario: ");
            String codUsuario = teclado.nextLine();

            int opcion;
            do {
                opcion = menu(teclado);
                switch (opcion) {
                    case 0 -> {// Guardar los datos en el fichero y salir del programa
                        // Llamamos a guardaDatos() y como opcion es igual a 0 se sale del while y finaliza el programa
                        cliente.cierraSesion();
                        System.out.println("Saliendo del programa...");
                        System.out.println("Adiós :)");
                    }

                    case 1 -> { // Listar los paquetes enviados por el cliente
                        // Obtenemos un JSONArray con los JSONObjects que representan las reservas del usuario
                        JSONArray jsonArrayReservas = cliente.listaReservasUsuario(codUsuario);
                    	// Recorremos el JSONArray y mostramos por pantalla la información pedida

                        int i = 1;
                        for (Object o : jsonArrayReservas) {
                            JSONObject jsonReserva = (JSONObject) o;
                            System.out.println(i + "- Actividad: " + jsonReserva.get("actividad")
                                    + " Código: " + jsonReserva.get("codReserva")
                                    + "\nEl día " + jsonReserva.get("dia") + " a las " + jsonReserva.get("hora") + ".");
                            i++;
                        }
                        if (i == 1) System.out.println("No tienes reservas realizadas.");
                    }

                    case 2 -> { // Listar los plazas disponibles de una actividad
                    	// Pedimos el nombre de la actividad de la cuál queremos obtener un array con todas sus sesiones disponibles
                        String nombreActividad = pedirNombreActividad(teclado, "¿De qué actividad quieres ver las plazas?");
                        JSONArray jsonArraySesiones = cliente.listaPlazasDisponibles(nombreActividad);

                        int i = 1;
                        for (Object o : jsonArraySesiones) {
                            JSONObject jsonSesion = (JSONObject) o;
                            System.out.println(i + "- Día " + jsonSesion.get("dia") + " a las " + jsonSesion.get("hora")
                                    + ". Plazas: " + jsonSesion.get("plazas"));
                            i++;
                        }
                        if (i == 1) System.out.println("No hay sesiones disponibles para esa actividad.");
                    }

                    case 3 -> { // Hacer una reserva
                        // Pedimos la información necesaria para realizar la reserva
                        String nombreActividad = pedirNombreActividad(teclado, "¿Qué actividad quieres reservar?");
                        DiaSemana dia = DiaSemana.leerDia(teclado);
                        long hora = pedirHora(teclado, "¿A qué hora quieres reservar?\n");

                        JSONObject jsonReserva = cliente.hazReserva(codUsuario, nombreActividad, dia, hora);
                        if (!jsonReserva.isEmpty()) {
                            System.out.println("Reserva realizada con éxito. Código: " + jsonReserva.get("codReserva"));
                        } else {
                            System.out.println("No se ha podido realizar la reserva.");
                        }
                    }

                    case 4 -> { // Cambiar de día y hora una reserva
                        // Pedimos la información necesaria para modificar la reserva
                        long codReserva = pedirCodReserva(teclado, "Introduce el código de la reserva que quieres modificar: ");
                        DiaSemana dia = DiaSemana.leerDia(teclado);
                        long hora = pedirHora(teclado, "¿A qué hora quieres cambiar tu reserva?\n");

                        JSONObject jsonReserva = cliente.modificaReserva(codUsuario, codReserva, dia, hora);
                        if (jsonReserva.isEmpty()) {
                            System.out.println("No se ha podido modificar la reserva.");
                        } else {
                            System.out.println("Reserva modificada con éxito:");
                            System.out.println("Actividad: " + jsonReserva.get("actividad")
                                    + " Código: " + jsonReserva.get("codReserva")
                                    + "\nEl día " + jsonReserva.get("dia") + " a las " + jsonReserva.get("hora") + ".");
                        }
                    }

                    case 5 -> { // Cancelar una reserva
                        // Pedimos la información necesaria para eliminar la reserva
                        long codReserva = pedirCodReserva(teclado, "Introduce el código de la reserva que quieres eliminar: ");
                        JSONObject jsonReserva = cliente.cancelaReserva(codUsuario, codReserva);
                        if (jsonReserva.isEmpty()) {
                            System.out.println("No se ha podido cancelar la reserva.");
                        } else {
                            System.out.println("Reserva cancelada con éxito.");
                        }
                    }

                } // fin switch

            } while (opcion != 0);

        } catch (Exception e) {
            System.out.println("Error en la conexión con el servidor: " + e.getMessage());
        }
    }

    // ========= Métodos auxiliares para pedir datos =========

    private static String pedirNombreActividad(Scanner teclado, String pregunta) {
        System.out.println(pregunta);
        return teclado.nextLine();
    }

    private static long pedirHora(Scanner teclado, String pregunta) {
        long hora;
        System.out.println(pregunta);
        do {
            System.out.print("Dame una hora (0-23): ");
            hora = teclado.nextLong();
        } while (hora < 0 || hora > 23);
        teclado.nextLine();
        return hora;
    }

    private static long pedirCodReserva(Scanner teclado, String pregunta) {
        System.out.println(pregunta);
        long codReserva = teclado.nextLong();
        teclado.nextLine();
        return codReserva;
    }
}