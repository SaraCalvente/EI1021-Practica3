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
	 * @param	puerto		número de puerto asociado al servicio en el servidor
	 */
	AuxiliarClienteSockets(String hostName, int puerto)
			throws SocketException, UnknownHostException, IOException {

		// IP del servidor
		InetAddress serverHost = InetAddress.getByName(hostName);
		// Puerto asociado al servicio en el servidor
		int serverPort = (puerto);
		// Instantiates a stream-mode socket and wait for a connection.
		this.mySocket = new MyStreamSocket(serverHost, serverPort);
		/**/  System.out.println("Hecha peticion de conexion");
		parser = new JSONParser();
	} // end constructor



	@SuppressWarnings("unchecked")
	public JSONArray listaReservasUsuario(String codUsuario) {
	// IMPLEMENTADO
			JSONArray jsonReservasArray = new JSONArray();
			Vector<Reserva> vectorReservasUsuario = reservas.get(codUsuario); // Vector con todas las reservas del usuario
			if (vectorReservasUsuario == null) return jsonReservasArray; // Si el vector es null, no tiene reservas, devolvemos vacío
			
			// Si no está vacío, guardamos todas las reservas del vector y devolvemos el JSONArray
			for (Reserva reserva : vectorReservasUsuario) jsonReservasArray.add(reserva.toJSON());
	        return jsonReservasArray;
	} // end listaReservasUsuario
	
	@SuppressWarnings("unchecked")
	public JSONArray listaPlazasDisponibles(String actividad) {
		// IMPLEMENTADO
	    JSONArray jsonPlazasDisponiblesArray = new JSONArray();
	    for (Map.Entry<DiaSemana, Vector<Sesion>> entry : sesionesSemana.entrySet()) {
	        for (Sesion sesion : entry.getValue()) {
	            if (sesion.getActividad().equalsIgnoreCase(actividad) && sesion.getPlazas() > 0) {
	                JSONObject jsonSesion = sesion.toJSON();
	                jsonSesion.put("dia", entry.getKey().name()); // añadimos el día
	                jsonPlazasDisponiblesArray.add(jsonSesion);
	            }
	        }
	    }
	    return jsonPlazasDisponiblesArray;
	} // end listaPlazasDisponibles


	@SuppressWarnings("unchecked")
	JSONObject hazReserva(String codUsuario, String actividad, DiaSemana dia, long hora) {
	// IMPLEMENTADO
			Sesion sesion = buscaSesion(actividad, dia, hora); // Buscamos la sesión
			
			// Si la sesión no se encuentra o no quedan plazas y devolvemos JSONObject vacío
			if (sesion == null || sesion.getPlazas() <= 0) return new JSONObject();
			
			
			// Revisamos si existe una reserva de la misma sesión creada por el mismo usuario, si existe, devolvemos JSONObject vacío
			for (Object reservaUsuario : listaReservasUsuario(codUsuario)) {
				JSONObject jsonReservaUsuario = (JSONObject) reservaUsuario;
				if (actividad.equals((String) jsonReservaUsuario.get("actividad"))
				 &&	dia == DiaSemana.valueOf((String) jsonReservaUsuario.get("dia"))
				 && hora == (long)	jsonReservaUsuario.get("hora")) {
					return new JSONObject();
				}
			}
			
			// Creamos la nueva reserva
			Reserva reserva = new Reserva(codUsuario, actividad, dia, hora);
			
			sesion.setPlazas(sesion.getPlazas() - 1); // Decrementamos el número de plazas
			guardaReserva(reserva); // Guardamos la reserva en el HashMap reservas
			return reserva.toJSON();
	} // end hazReserva


	@SuppressWarnings("unchecked")
	public JSONObject modificaReserva(String codUsuario, long codReserva, DiaSemana nuevoDia, long nuevaHora) {
	//IMPLEMENTADO
			Vector<Reserva> reservasUsuario = reservas.get(codUsuario);
		    Reserva r = buscaReserva(reservasUsuario, codReserva);
		    if (r == null) return new JSONObject();

		    // Buscar sesión nueva
		    Sesion nuevaSesion = buscaSesion(r.getActividad(), nuevoDia, nuevaHora);
		    if (nuevaSesion == null || nuevaSesion.getPlazas() <= 0) {
		        return new JSONObject(); // No existe o no hay plazas
		    }

		    // Liberar plaza en la sesión antigua
		    Sesion antiguaSesion = buscaSesion(r.getActividad(), r.getDia(), r.getHora());
		    if (antiguaSesion != null) {
		        antiguaSesion.setPlazas(antiguaSesion.getPlazas() + 1);
		    }

		    // Actualizar reserva
		    r.setDia(nuevoDia);
		    r.setHora(nuevaHora);
		    nuevaSesion.setPlazas(nuevaSesion.getPlazas() - 1);

		    return r.toJSON();
	} // end modificaReserva

	@SuppressWarnings("unchecked")
	public JSONObject cancelaReserva(String codUsuario, long codReserva) {
	// IMPLEMENTADO
			Vector<Reserva> reservasUsuario = reservas.get(codUsuario);
		    if (reservasUsuario == null) return new JSONObject();

		    Reserva r = buscaReserva(reservasUsuario, codReserva);
		    if (r == null) return new JSONObject();

		    // Liberar plaza en la sesión correspondiente
		    Sesion sesion = buscaSesion(r.getActividad(), r.getDia(), r.getHora());
		    if (sesion != null) {
		        sesion.setPlazas(sesion.getPlazas() + 1);
		    }

		    // Eliminar reserva
		    reservasUsuario.remove(r);

		    return r.toJSON();
	} // cancelaReserva


	/**
	 * Finaliza la conexión con el servidor
	 */
	@SuppressWarnings("unchecked")
	public void cierraSesion( ) {
		// POR IMPLEMENTAR
	} // end done 
} //end class
