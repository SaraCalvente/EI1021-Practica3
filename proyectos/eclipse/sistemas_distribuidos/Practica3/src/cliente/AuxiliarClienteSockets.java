package cliente;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import comun.DiaSemana;
import comun.MyStreamSocket;

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



	@SuppressWarnings("unchecked")
	public JSONArray listaReservasUsuario(String codUsuario) {
		// POR IMPLEMENTAR
		return null; // cambiar por el retorno correcto
	} // end listaReservasUsuario
	
	@SuppressWarnings("unchecked")
	public JSONArray listaPlazasDisponibles(String actividad) {
		// POR IMPLEMENTAR
		return null; // cambiar por el retorno correcto
	} // end listaPlazasDisponibles


	@SuppressWarnings("unchecked")
	JSONObject hazReserva(String codUsuario, String actividad, DiaSemana dia, long hora) {
		// POR IMPLEMENTAR
		return null; // cambiar por el retorno correcto
	} // end hazReserva


	@SuppressWarnings("unchecked")
	public JSONObject modificaReserva(String codUsuario, long codReserva, DiaSemana nuevoDia, long nuevaHora) {
		// POR IMPLEMENTAR
		return null; // cambiar por el retorno correcto
	} // end modificaReserva

	@SuppressWarnings("unchecked")
	public JSONObject cancelaReserva(String codUsuario, long codReserva) {
		// POR IMPLEMENTAR
		return null; // cambiar por el retorno correcto

	} // cancelaReserva


	/**
	 * Finaliza la conexión con el servidor
	 */
	@SuppressWarnings("unchecked")
	public void cierraSesion( ) {
		// POR IMPLEMENTAR
	} // end done 
} //end class
