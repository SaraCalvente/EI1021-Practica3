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
		this.myDataSocket = myDataSocket;
	    this.gestor = unGestor;
	}

	/**
	 * Gestiona una sesión con un cliente
	 */
	public void run( ) {
		String operacion = "0";
		boolean done = false;
	    JSONParser parser = new JSONParser();
		// ...
		try {
			while (!done) {
				String peticion = myDataSocket.receiveMessage();

	            if (peticion == null) break; 
	            JSONObject jsonPeticion = (JSONObject) parser.parse(peticion);
	            operacion = jsonPeticion.get("operacion").toString();

	            JSONObject respuesta = new JSONObject();
	            JSONArray arrayRespuesta = new JSONArray();
				switch (operacion) {
				
				case "0": 
					gestor.guardaDatos();
					myDataSocket.sendMessage("FIN");
                    done = true;
                    break;

                case "1": { // Listar reservas del usuario
                    String codUsuario = (String) jsonPeticion.get("codUsuario");
                    arrayRespuesta = gestor.listaReservasUsuario(codUsuario);
                    myDataSocket.sendMessage(arrayRespuesta.toJSONString());
                    break;
                }

                case "2": { // Listar plazas disponibles
                    String nombreActividad = jsonPeticion.get("actividad").toString();
                    arrayRespuesta = gestor.listaPlazasDisponibles(nombreActividad);
                    myDataSocket.sendMessage(arrayRespuesta.toJSONString());
                    break;
                }

                case "3": { // Hacer reserva
                    String codUsuario = (String) jsonPeticion.get("codUsuario");
                    String nombreActividad = (String) jsonPeticion.get("actividad");
                    DiaSemana dia = DiaSemana.valueOf((String) jsonPeticion.get("dia"));
                    long hora = (long) jsonPeticion.get("hora");

                    respuesta = gestor.hazReserva(codUsuario, nombreActividad, dia, hora);
                    myDataSocket.sendMessage(respuesta.toJSONString());
                    break;
                }

                case "4": { // Modificar reserva
                    String codUsuario = (String) jsonPeticion.get("codUsuario");
                    long codReserva = (long) jsonPeticion.get("codReserva");
                    DiaSemana dia = DiaSemana.valueOf((String) jsonPeticion.get("nuevoDia"));
                    long hora = (long) jsonPeticion.get("nuevaHora");

                    respuesta = gestor.modificaReserva(codUsuario, codReserva, dia, hora);
                    myDataSocket.sendMessage(respuesta.toJSONString());
                    break;
                }

                case "5": { // Cancelar reserva
                    String codUsuario = (String) jsonPeticion.get("codUsuario");
                    long codReserva = (long) jsonPeticion.get("codReserva");

                    respuesta = gestor.cancelaReserva(codUsuario, codReserva);
                    myDataSocket.sendMessage(respuesta.toJSONString());
                    break;
                }
                default:
                    System.out.println("Operación no reconocida: " + operacion);
                    break;

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
