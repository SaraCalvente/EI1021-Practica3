package reservas;

import org.json.simple.JSONObject;

/**
 * Representa una sesión de una actividad en un día y hora específicos.
 */
public class Sesion {
    
    private String actividad; // Nombre de la actividad
    private long hora;        // Hora de la sesión en formato 24h
    private long plazas;      // Número de plazas disponibles

    /**
     * Constructor para inicializar una sesión.
     * 
     * @param actividad Nombre de la actividad.
     * @param hora Hora de la sesión en formato 24 horas.
     * @param plazas Número de plazas disponibles.
     */
    public Sesion(String actividad, long hora, long plazas) {
        this.actividad = actividad;
        this.hora = hora;
        this.plazas = plazas;
    }
    
    /**
     * Devuelve un objeto JSON con la información de la sesión.
     * 
     * @return JSONObject con los detalles de la sesión.
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
    		// POR IMPLEMENTAR
        	JSONOBject jsonSesion = new JSONObject();
        	jsonSesion.put("actividad", getActividad());
        	jsonSesion.put("hora", getHora());
        	jsonSesion.put("plazas", getPlazas());
        return jsonSesion;
    }
    
    /**
     * Devuelve una representación como cadena de la sesión en formato JSON.
     * 
     * @return Cadena con los datos de la sesión en formato JSON.
     */
    @Override
    public String toString() {
        return this.toJSON().toJSONString();
    }


    public String getActividad() {
        return actividad;
    }


    public void setActividad(String actividad) {
        this.actividad = actividad;
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



    public long getPlazas() {
        return plazas;
    }


    public void setPlazas(long plazas) {
        if (plazas < 0) {
            throw new IllegalArgumentException("El número de plazas no puede ser negativo.");
        }
        this.plazas = plazas;
    }
    
}
