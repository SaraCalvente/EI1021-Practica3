package reservas;

import java.util.Scanner;

/**
 * Enumeración para representar los días de la semana en español en minúsculas.
 */
public enum DiaSemana {

			lunes, martes, miercoles, jueves, viernes, sabado, domingo;

	public static boolean esDiaValido(String dia) {
		try {
			DiaSemana.valueOf(dia.toLowerCase());
			return true;
		} catch(Exception e) {return false;}
	}

	public static DiaSemana leerDia(Scanner scanner) {
		String entrada;
		do {
			System.out.print("Introduce un dia de la semana (lunes a domingo): ");
			entrada = scanner.nextLine().trim().toLowerCase();
			if (!esDiaValido(entrada)) {
				System.out.println("Dia invalido. Intenta de nuevo.");
			}
		} while (!esDiaValido(entrada));
		return DiaSemana.valueOf(entrada);
	}



}