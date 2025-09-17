package reservas;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;
import org.json.simple.JSONObject;



/**
 * Clase que representa una reserva de una sesión de una actividad deportiva durante la próxima semana
 */
public class Reserva implements Serializable {
	
    @Serial
    private static final long serialVersionUID = 1L;

    private long codReserva;		// Código único de la reserva generado al crearla
    private String codUsuario;		// Código único del usuario que hace la reserva
    private String actividad;		// Nombre de la actividad deportiva (p.e. Taichí, Yoga, etc.)
    private DiaSemana dia;			// Día de la semana de la sesión reservada
    private long hora;				// Hora de la reserva (en formato 24h)

    /**
     * Constructor para crear una reserva.
     *
     * @param codUsuario Código del usuario que realiza la reserva.
     * @param actividad Actividad reservada.
     * @param dia Día de la reserva.
     * @param hora Hora de la reserva en formato 24 horas.
     */
    public Reserva(String codUsuario, String actividad, DiaSemana dia, long hora) {
        this.codReserva = generaCodReserva(); // El código de la reserva se genera al construirla
        this.codUsuario = codUsuario;
        this.actividad = actividad;
        this.dia = dia;
        this.hora = hora;
    }

    /**
     * Constructor para crear una reserva a partir de un objeto JSON.
     *
     * @param jsonReserva Objeto JSON con la información de la reserva.
     */
    public Reserva(JSONObject jsonReserva) {
    	// POR IMPLEMENTAR
    	this.codReserva = jsonReserva.getLong("codReserva");
    	this.codUsuario = jsonReserva.getString("codUsuario");
    	this.actividad = jsonReserva.getString("actividad");
    	this.dia = (DiaSemana) jsonReserva.get("dia");
    	this.hora = jsonReserva.getLong("hora");
    }

    /**
     * Devuelve la representación JSON de la reserva.
     *
     * @return Objeto JSON con la información de la reserva.
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
    	// POR IMPLEMENTAR
        JSONObject jsonReserva = new JSONObject();
        jsonReserva.put("codReserva", getCodReserva());
        jsonReserva.put("codUsuario", getCodUsuario());
        jsonReserva.put("actividad", getActividad());
        jsonReserva.put("dia", getDia());
        jsonReserva.put("hora", getHora());
        return jsonReserva;
    }
    

	@Override
	public String toString() {
		return this.toJSON().toJSONString();
	}

	/*
	 * Genera el código 'unico' de la reserva como un entero aleatorio de 4 dígitos.
	 * Usar ThreadLocalRandom hace que sea seguro en entornos multihebra
	 */
    private long generaCodReserva() {
        return ThreadLocalRandom.current().nextLong(1000, 10000);
    }
    
	public long getCodReserva() {
		return codReserva;
	}

	public String getCodUsuario() {
		return codUsuario;
	}

	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public DiaSemana getDia() {
		return dia;
	}

	public void setDia(DiaSemana dia) {
		this.dia = dia;
	}

	public long getHora() {
		return hora;
	}

    public void setHora(long hora) {
        if (hora < 0 || hora > 23) {
            throw new IllegalArgumentException("La hora debe estar en el rango de 0 a 23.");
        }
        this.hora = hora;
    }

    // Getters y setters
    
    


}
