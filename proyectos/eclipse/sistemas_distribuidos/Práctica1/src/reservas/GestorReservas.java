package reservas;

import org.json.simple.JSONArray;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class GestorReservas {

	private FileWriter os;			// stream para escribir los datos de las reservas en el fichero

	// Sesiones de la próxima semana indexadas por el día de la semana. Empiezan mañana, cuando se puede reservar
	final private HashMap<DiaSemana, Vector<Sesion>> sesionesSemana;
	// Reservas indexadas por el código de usuario
	final private HashMap<String, Vector<Reserva>> reservas;

	/**
	 * Constructor del gestor de reservas
	 * Crea o lee el fichero con datos de prueba por defecto, dependiendo de que exista el fichero
	 */
	public GestorReservas() {

		this.sesionesSemana = new HashMap<DiaSemana, Vector<Sesion>>();
		this.reservas = new HashMap<String, Vector<Reserva>>();

		// Genera las sesiones de la próxima semana con todas las plazas libres inicialmente
		generaSesiones();

		File file = new File("reservas.json");
		try {
			if (!file.exists() ) {
				// Si no existen el fichero de datos, los genera con valores por defecto.
				// Hace las correspondientes reservas modificando los diccionarios de sesiones y reservas
				os = new FileWriter(file);
				generaReservas();
				escribeFichero(os);
				os.close();
			}
			else {
				// Si existe el fichero, lo lee, y hace las correspondientes reservas
				FileReader is = new FileReader(file);
				leeFichero(is);
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Genera y almacena las sesiones disponibles para cada día de la próxima semana.
	 * 
	 * Se asignan diferentes actividades a cada día con horarios y plazas predefinidas.
	 * Las sesiones se almacenan en un diccionario indexado por `DiaSemana`.
	 */
	private void generaSesiones() {
		for (DiaSemana diaSemana : DiaSemana.values()) {
			Vector<Sesion> sesionesDia = new Vector<>(); // Asegura que nunca sea null

			switch (diaSemana) {
			case lunes -> {
				sesionesDia.add(new Sesion("Taichí", 9, 3));
				sesionesDia.add(new Sesion("Ironfit", 10, 3));
			}
			case martes -> {
				sesionesDia.add(new Sesion("Ironfit", 10, 3));
				sesionesDia.add(new Sesion("Yoga", 18, 3));
			}
			case miercoles -> {
				sesionesDia.add(new Sesion("Taichí", 9, 3));
				sesionesDia.add(new Sesion("Yoga", 18, 3));
			}
			case jueves -> {
				sesionesDia.add(new Sesion("Taichí", 9, 3));
				sesionesDia.add(new Sesion("Ironfit", 10, 3));
			}
			case viernes -> {
				sesionesDia.add(new Sesion("Ironfit", 10, 3));
				sesionesDia.add(new Sesion("Yoga", 18, 3));
			}
			default -> {} // No se definen sesiones para sábado y domingo
			}

			// Solo guarda los posibles sesiones del día
			if (!sesionesDia.isEmpty()) {
				sesionesSemana.put(diaSemana, sesionesDia);
			}
		}
	}

	/**
	 * Genera los datos iniciales y los guarda en los diccionarios
	 */
	private void generaReservas() {

		hazReserva("cli01", "Taichí", DiaSemana.lunes, 9);     
		hazReserva("cli01", "Taichí", DiaSemana.miercoles, 9);  
		hazReserva("cli02", "Taichí", DiaSemana.lunes, 9);     
		hazReserva("cli03", "Ironfit", DiaSemana.martes, 10); 
		hazReserva("cli04", "Ironfit", DiaSemana.martes, 10); 
		hazReserva("cli05", "Ironfit", DiaSemana.martes, 10); 
		hazReserva("cli05", "Yoga", DiaSemana.martes, 18);    
		hazReserva("cli05", "Yoga", DiaSemana.miercoles, 18);    
		hazReserva("cli06", "Ironfit", DiaSemana.jueves, 10); 
		hazReserva("cli07", "Ironfit", DiaSemana.jueves, 10); 
	}



	/**
	 * Cuando cada usuario cierra su sesión volcamos los datos en el fichero para mantenerlo actualizado
	 */
	public void guardaDatos(){
		File file = new File("reservas.json");
		try {
			os = new FileWriter(file);
			escribeFichero(os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Escribe en el fichero un array JSON con los datos de las reservas guardadas en el diccionario
	 *
	 * @param os	stream de escritura asociado al fichero de datos
	 */
	@SuppressWarnings("unchecked")
	private void escribeFichero(FileWriter os) {
		// POR IMPLEMENTAR
		JSONObject[] reservas_guardadas =  new JSONObject[];
		for (Map.Entry<String, Vector<Reserva>> entry : getReservas().entrySet()) {
			String codUsuario = entry.getKey();
			Vector<Reserva> reservasUsuario = entry.getValue();
			for (Reserva reserva : reservasUsuario) {
				JSONObject jsonReserva = reserva.toJSON()
				reservas_guardadas.add(jsonReserva);
			}
			try {
				os.write(reservasGuardadas.toJSONString());
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
		}
	}



	/**
	 * Lee los datos almacenados de un archivo JSON y los carga en el diccionario en memoria.
	 *
	 * @param is Stream de lectura del archivo que contiene los datos en formato JSON.
	 */
	private void leeFichero(FileReader is) {
		JSONParser parser = new JSONParser();

		try {
			// Leemos toda la información del fichero en un array de objetos JSON
			JSONArray array = (JSONArray) parser.parse(is);

			// Si hay sesiones, las incluimos en el diccionario
			if (array != null && !array.isEmpty()) {
				rellenaDiccionarios(array);
			} else {
				System.err.println("Advertencia: El archivo JSON está vacío o no contiene datos válidos.");
			}
		} catch (IOException e) {
			System.err.println("Error al leer el archivo JSON: " + e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.err.println("Error al parsear el archivo JSON: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	/**
	 * Rellena y modifica los diccionarios a partir de los datos en un JSONArray
	 *
	 * @param array	JSONArray con los datos de los paquetes
	 */
	private void rellenaDiccionarios(JSONArray array) {
        // POR IMPLEMENTAR
	}



	/**
	 * Busca y devuelve una sesión determinada en función de la actividad, el día y la hora especificados.
	 *
	 * @param actividad Nombre de la actividad a buscar.
	 * @param dia Día de la semana en el que se realiza la sesión.
	 * @param hora Hora de la sesión en formato 24 horas.
	 * @return La sesión encontrada o `null` si no existe una sesión con esos parámetros.
	 */
	Sesion buscaSesion(String actividad, DiaSemana dia, long hora) {
        // POR IMPLEMENTAR
        return null; // MODIFICAR
	}


	/**
	 * Obtiene una lista de todas las reservas del usuario específico.
	 *
	 * @param codUsuario El código del usuario cuyas reservas se desea listar
	 * @return Un `JSONArray` que contiene la representación JSON de cada reserva del usuario.
	 */
	@SuppressWarnings("unchecked")
	public JSONArray listaReservasUsuario(String codUsuario) {
        // POR IMPLEMENTAR
        return null; // MODIFICAR
	}


	/**
	 * Obtiene una lista de todas las sesiones con plazas disponibles para una actividad específica.
	 * 
	 * @param actividad Nombre de la actividad de la cual se desean obtener las plazas disponibles.
	 * @return Un `JSONArray` con las sesiones disponibles, incluyendo día, hora y número de plazas.
	 *         Si no hay sesiones disponibles, se devuelve un JSONArray vacío.
	 */
	@SuppressWarnings("unchecked")
	public JSONArray listaPlazasDisponibles(String actividad) {
        // POR IMPLEMENTAR
        return null; // MODIFICAR
	}


	/**
	 * Realiza una reserva si hay la sesión existe y hay plazas disponibles.
	 * 
	 * @param codUsuario Código del usuario que solicita la reserva.
	 * @param actividad Nombre de la actividad que se quiere reservar.
	 * @param dia Día de la semana en que se quiere reservar la sesión.
	 * @param hora Hora de la sesión en formato 24 horas.
	 * @return Un objeto `JSONObject` con el código de la reserva si se realizó con éxito, o vacío si no fue posible reservar.
	 */
	@SuppressWarnings("unchecked")
	public JSONObject hazReserva(String codUsuario, String actividad, DiaSemana dia, long hora) {
        // POR IMPLEMENTAR
        return null; // MODIFICAR
	}


	/**
	 * Busca una reserva dentro de una lista de reservas de un usuario a partir de su código.
	 * 
	 * @param vector Lista de reservas asociadas a un usuario.
	 * @param codReserva Código único de la reserva que se desea buscar.
	 * @return La reserva encontrada o `null` si no existe una reserva con ese código.
	 */
	private Reserva buscaReserva(Vector<Reserva> vector, long codReserva) {
        // POR IMPLEMENTAR
        return null; // MODIFICAR
	}




	/**
	 * Modifica una reserva existente, cambiándola a otro día y hora si hay plazas disponibles,
	 * pero manteniendo la actividad y el código de la reserva
	 *
	 * @param codUsuario Código del usuario que posee la reserva.
	 * @param codReserva Código de la reserva a modificar.
	 * @param nuevoDia Nuevo día de la semana para la reserva.
	 * @param nuevaHora Nueva hora de la sesión en formato 24 horas.
	 * @return Un `JSONObject` con la representación de la reserva modificada, o vacío si no se pudo modificar.
	 */
	public JSONObject modificaReserva(String codUsuario, long codReserva, DiaSemana nuevoDia, long nuevaHora) {
        // POR IMPLEMENTAR
        return null; // MODIFICAR
	}


	/**
	 * Cancela una reserva existente de un usuario y libera la plaza en la sesión correspondiente.
	 *
	 * @param codUsuario Código del usuario que ha hecho la reserva.
	 * @param codReserva Código único de la reserva a cancelar.
	 * @return Un `JSONObject` con la representación de la reserva cancelada, o vacío si no se encontró.
	 */
	public JSONObject cancelaReserva(String codUsuario, long codReserva) {
        // POR IMPLEMENTAR
        return null; // MODIFICAR
	}


	private  HashMap<String, Vector<Reserva>> getReservas() {
		return reservas;
	}

}
