package gestor;

import org.json.simple.JSONArray;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import comun.DiaSemana;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


public class GestorReservas {

	// Sustituye esta clase por tu versión de la misma de la práctica 1.
	// Modifícalo para tener en cuenta el acceso concurrente de todos los hilos.

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
		// IMPLEMENTADO
		JSONArray reservasGuardadas =  new JSONArray();
		
		for (Vector<Reserva> reservasUsuario : getReservas().values()) // Recorremos las reservas realizadas de cada usuario
			for (Reserva reserva : reservasUsuario) {	// Cada reserva
				JSONObject jsonReserva = reserva.toJSON(); // La transformamos a JSON
				reservasGuardadas.add(jsonReserva); // y la añadimos a nuestro JSONArray
			}
		
		// Una vez tenemos todas, escribimos nuestro JSONArray en el fichero
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
        // IMPLEMENTADO
		// Para cada objeto del JSONArray (lo leído de reservas.json), lo casteamos a JSONObject
		// y construímos una Reserva con este mismo, a su vez lo guardamos en el HashMap reservas
		for (Object objectReserva : array)
			guardaReserva(new Reserva((JSONObject) objectReserva));
	}


	// Función para evitar repetición de código
	private void guardaReserva(Reserva reserva) {
		String codUsuario = reserva.getCodUsuario();
		
		// Si reservas contiene el código del usuario, añadimos al vector de las reservas la nueva reserva
		if (reservas.containsKey(codUsuario)) reservas.get(codUsuario).add(reserva);
		
		// si no lo contiene, creamos un nuevo vector de reservas, añadimos la reserva al vector, y añadimos la nueva entrada a reservas
		else {
			Vector<Reserva> vectorReservasUsuario = new Vector<Reserva>();
			vectorReservasUsuario.add(reserva);
			reservas.put(codUsuario, vectorReservasUsuario);
		}
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
        // IMPLEMENTADO
		Vector<Sesion> sesionesDia = sesionesSemana.get(dia); // Vector con todas las sesiones que hay ese día
		for (Sesion sesion : sesionesDia) 
			// Si la sesión es de la misma actividad y a la misma hora, la hemos encontrado
			if (sesion.getActividad().equals(actividad) && sesion.getHora() == hora)
				return sesion;
		return null;
	}


	/**
	 * Obtiene una lista de todas las reservas del usuario específico.
	 *
	 * @param codUsuario El código del usuario cuyas reservas se desea listar
	 * @return Un `JSONArray` que contiene la representación JSON de cada reserva del usuario.
	 */
	@SuppressWarnings("unchecked")
	public JSONArray listaReservasUsuario(String codUsuario) {
        // IMPLEMENTADO
		JSONArray jsonReservasArray = new JSONArray();
		Vector<Reserva> vectorReservasUsuario = reservas.get(codUsuario); // Vector con todas las reservas del usuario
		if (vectorReservasUsuario == null) return jsonReservasArray; // Si el vector es null, no tiene reservas, devolvemos vacío
		
		// Si no está vacío, guardamos todas las reservas del vector y devolvemos el JSONArray
		for (Reserva reserva : vectorReservasUsuario) jsonReservasArray.add(reserva.toJSON());
        return jsonReservasArray;
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
	}


	/**
	 * Busca una reserva dentro de una lista de reservas de un usuario a partir de su código.
	 * 
	 * @param vector Lista de reservas asociadas a un usuario.
	 * @param codReserva Código único de la reserva que se desea buscar.
	 * @return La reserva encontrada o `null` si no existe una reserva con ese código.
	 */
	private Reserva buscaReserva(Vector<Reserva> vector, long codReserva) {
		// IMPLEMENTADO
	    if (vector == null) return null;
	    // Revisamos el vector de reservas, si alguna de ellas coincide con el codigo proporcionado la devolvemos
	    for (Reserva r : vector) {
	        if (r.getCodReserva() == codReserva) {
	            return r;
	        }
	    }
	    return null;
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
	}


	/**
	 * Cancela una reserva existente de un usuario y libera la plaza en la sesión correspondiente.
	 *
	 * @param codUsuario Código del usuario que ha hecho la reserva.
	 * @param codReserva Código único de la reserva a cancelar.
	 * @return Un `JSONObject` con la representación de la reserva cancelada, o vacío si no se encontró.
	 */
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
	}


	private  HashMap<String, Vector<Reserva>> getReservas() {
		return reservas;
	}

}
