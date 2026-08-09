package co.edu.sena.farmacia.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import co.edu.sena.farmacia.logica.ExcepcionNegocio;

/**
 * Traduce los parametros de texto que llegan desde los formularios HTML
 * a los tipos de dato que manejan las clases del modelo.
 *
 * Toda peticion HTTP entrega valores como String; esta clase evita repetir
 * la misma conversion y validacion en cada servlet.
 */
public final class ConversorDatos {

    private ConversorDatos() {
    }

    /**
     * @return true cuando el texto es nulo o solo contiene espacios
     */
    public static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Devuelve el texto sin espacios sobrantes, o null si venia vacio.
     */
    public static String limpiar(String texto) {
        return estaVacio(texto) ? null : texto.trim();
    }

    /**
     * Convierte un parametro a entero.
     *
     * @param valor texto recibido del formulario
     * @param campo nombre del campo, usado en el mensaje de error
     */
    public static int aEntero(String valor, String campo) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException | NullPointerException error) {
            throw new ExcepcionNegocio("El campo " + campo + " debe ser un numero entero valido.");
        }
    }

    /**
     * Convierte un parametro a entero permitiendo que llegue vacio.
     *
     * @return el valor convertido o null si el parametro no fue enviado
     */
    public static Integer aEnteroOpcional(String valor, String campo) {
        return estaVacio(valor) ? null : aEntero(valor, campo);
    }

    /**
     * Convierte un parametro a decimal para precios y valores monetarios.
     */
    public static BigDecimal aDecimal(String valor, String campo) {
        if (estaVacio(valor)) {
            throw new ExcepcionNegocio("El campo " + campo + " es obligatorio.");
        }
        try {
            return new BigDecimal(valor.trim().replace(",", "."));
        } catch (NumberFormatException error) {
            throw new ExcepcionNegocio("El campo " + campo + " debe ser un valor numerico valido.");
        }
    }

    /**
     * Convierte un parametro a numero de punto flotante para cantidades y stock.
     */
    public static double aDouble(String valor, String campo) {
        if (estaVacio(valor)) {
            throw new ExcepcionNegocio("El campo " + campo + " es obligatorio.");
        }
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException error) {
            throw new ExcepcionNegocio("El campo " + campo + " debe ser un valor numerico valido.");
        }
    }

    /**
     * Convierte el valor de un input HTML de tipo date (formato aaaa-MM-dd).
     */
    public static LocalDate aFecha(String valor, String campo) {
        if (estaVacio(valor)) {
            throw new ExcepcionNegocio("El campo " + campo + " es obligatorio.");
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException error) {
            throw new ExcepcionNegocio("El campo " + campo + " debe tener el formato aaaa-mm-dd.");
        }
    }

    /**
     * Convierte una fecha opcional; devuelve null cuando el campo llega vacio.
     */
    public static LocalDate aFechaOpcional(String valor, String campo) {
        return estaVacio(valor) ? null : aFecha(valor, campo);
    }

    /**
     * Convierte el valor de un input HTML de tipo datetime-local.
     */
    public static LocalDateTime aFechaHora(String valor, String campo) {
        if (estaVacio(valor)) {
            throw new ExcepcionNegocio("El campo " + campo + " es obligatorio.");
        }
        try {
            return LocalDateTime.parse(valor.trim());
        } catch (DateTimeParseException error) {
            throw new ExcepcionNegocio("El campo " + campo + " debe tener el formato aaaa-mm-dd hh:mm.");
        }
    }

    /**
     * Interpreta un checkbox HTML: presente significa true, ausente false.
     */
    public static boolean aBooleano(String valor) {
        return valor != null && (valor.equalsIgnoreCase("on")
                || valor.equalsIgnoreCase("true")
                || valor.equals("1"));
    }
}
