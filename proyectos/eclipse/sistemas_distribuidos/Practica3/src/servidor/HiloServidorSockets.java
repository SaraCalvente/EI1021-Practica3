package servidor;

import java.io.IOException;
import java.net.SocketException;

import gestor.GestorReservas;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import comun.DiaSemana;
import comun.MyStreamSocket;

/**
 * Clase ejecutada por cada hebra encargada de servir a un cliente del servicio de reservas.
 * El método run contiene la lógica para gestionar una sesión con un cliente.
 */

class HiloServidorSockets implements Runnable {


	final private MyStreamSocket myDataSocket;
	final private GestorReservas gestor;

	/**
	 * Construye el objeto a ejecutar por la hebra para servir a un cliente
	 * @param	myDataSocket	socket stream para comunicarse con el cliente
	 * @param	unGestor		gestor de viajes
	 */
	HiloServidorSockets(MyStreamSocket myDataSocket, GestorReservas unGestor) {
		// POR IMPLEMENTAR
	}

	/**
	 * Gestiona una sesión con un cliente
	 */
	public void run( ) {
		String operacion = "0";
		boolean done = false;
		// ...
		try {
			while (!done) {
				// Recibe una petición del cliente
				// Extrae la operación y sus parámetros

				switch (operacion) {
					case "0":
						// ...
						break;

					case "1": { // Devuelve una lista de reservas de un usuario
						// ...

						break;
					}
					case "2": { // Devuelve una lista de plazas disponibles de una actividad
						// ...

						break;
					}
					case "3": { // Un usuario hace una reserva
						// ...

						break;
					}
					case "4": { // Un usuario modifica una de sus reservas
						// ...

						break;
					}
					case "5": { // Un usuario cancela una de sus reservas
						// ...

						break;
					}

				} // fin switch
			} // fin while   
		} // fin try
		catch (SocketException ex) {
			System.out.println("Capturada SocketException");
		}
		catch (IOException ex) {
			System.out.println("Capturada IOException");
		}
		catch (Exception ex) {
			System.out.println("Exception caught in thread: " + ex);
		} // fin catch
	} //fin run

} //fin class 
