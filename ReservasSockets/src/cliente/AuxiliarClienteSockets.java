package cliente;

import java.io.IOException;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import comun.DiaSemana;
import comun.MyStreamSocket;
import gestor.Reserva;
import gestor.Sesion;

/**
 * Esta clase es un módulo que proporciona la lógica de aplicación
 * para el Cliente del servicio de reservas usando sockets de tipo stream
 */

public class AuxiliarClienteSockets {

	private final MyStreamSocket mySocket; // Socket de datos para comunicarse con el servidor
	JSONParser parser;

	/**
	 * Construye un objeto auxiliar asociado a un cliente del servicio.
	 * Crea un socket para conectar con el servidor.
	 * @param	hostName	nombre de la máquina que ejecuta el servidor
	 * @param	portNum		número de puerto asociado al servicio en el servidor
	 */
	AuxiliarClienteSockets(String hostName, String portNum)
			throws SocketException, UnknownHostException, IOException {

		// IP del servidor
		InetAddress serverHost = InetAddress.getByName(hostName);
		// Puerto asociado al servicio en el servidor
		int serverPort = Integer.parseInt(portNum);
		// Instantiates a stream-mode socket and wait for a connection.
		this.mySocket = new MyStreamSocket(serverHost, serverPort);
		/**/  System.out.println("Hecha peticion de conexion");
		parser = new JSONParser();
	} // end constructor


	private JSONObject communicate(String message) {
		String response = null;
		JSONObject jsonResponse = new JSONObject();
		try {
			mySocket.sendMessage(message);
			response = mySocket.receiveMessage();
			if (response == "CLOSE")
				mySocket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error de comunicación con el servidor.");
        }
		if (response != null) {
			try {
				Object o = parser.parse(response);
			
				jsonResponse = (JSONObject) o;
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Error al parsear.");
			}
		}
		
		return jsonResponse;
	}

	@SuppressWarnings("unchecked")
	public JSONArray listaReservasUsuario(String codUsuario) {
		// IMPLEMENTADO
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("operacion", 1);
		jsonMessage.put("codUsuario", codUsuario);
		String message = jsonMessage.toJSONString();
		
		JSONObject jsonResponse = communicate(message);
		
		JSONArray reservasUsuario = (JSONArray) jsonResponse.get("reservasUsuario");
		
		return reservasUsuario; // cambiar por el retorno correcto	
	} // end listaReservasUsuario
	
	@SuppressWarnings("unchecked")
	public JSONArray listaPlazasDisponibles(String actividad) {
		// IMPLEMENTADO
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("operacion", 2);
		jsonMessage.put("actividad", actividad);
		String message = jsonMessage.toJSONString();
		
		JSONObject jsonResponse = communicate(message);
		
		JSONArray plazasActividad = (JSONArray) jsonResponse.get("plazasActividad");
		return plazasActividad; // cambiar por el retorno correcto
	} // end listaPlazasDisponibles


	@SuppressWarnings("unchecked")
	JSONObject hazReserva(String codUsuario, String actividad, DiaSemana dia, long hora) {
		// IMPLEMENTADO
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("operacion", 3);
		jsonMessage.put("codUsuario", codUsuario);
		jsonMessage.put("actividad", actividad);
		jsonMessage.put("dia", dia.toString());
		jsonMessage.put("hora", hora);
		String message = jsonMessage.toJSONString();
		
		JSONObject jsonResponse = communicate(message);
		
		JSONObject reserva = (JSONObject) jsonResponse.get("reserva");
		return reserva; // cambiar por el retorno correcto
	} // end hazReserva


	@SuppressWarnings("unchecked")
	public JSONObject modificaReserva(String codUsuario, long codReserva, DiaSemana nuevoDia, long nuevaHora) {
		// IMPLEMENTADO
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("operacion", 4);
		jsonMessage.put("codUsuario", codUsuario);
		jsonMessage.put("codReserva", codReserva);
		jsonMessage.put("nuevoDia", nuevoDia.toString());
		jsonMessage.put("nuevaHora", nuevaHora);
		String message = jsonMessage.toJSONString();
		
		JSONObject jsonResponse = communicate(message);
		
		JSONObject reserva = (JSONObject) jsonResponse.get("reserva");
		return reserva; // cambiar por el retorno correcto
	} // end modificaReserva

	@SuppressWarnings("unchecked")
	public JSONObject cancelaReserva(String codUsuario, long codReserva) {
		// IMPLEMENTADO
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("operacion", 5);
		jsonMessage.put("codUsuario", codUsuario);
		jsonMessage.put("codReserva", codReserva);
		String message = jsonMessage.toJSONString();
		
		JSONObject jsonResponse = communicate(message);
		
		JSONObject reserva = (JSONObject) jsonResponse.get("reserva");
		return reserva; // cambiar por el retorno correcto

	} // cancelaReserva


	/**
	 * Finaliza la conexión con el servidor
	 */
	@SuppressWarnings("unchecked")
	public void cierraSesion( ) {
		// IMPLEMENTADO
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("operacion", 0);
		String message = jsonMessage.toJSONString();
		
		communicate(message);
	} // end done 
} //end class