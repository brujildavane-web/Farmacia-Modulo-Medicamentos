package co.edu.sena.farmacia.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cifra las contrasenas de los usuarios antes de guardarlas en la base de datos.
 *
 * Responde al requisito no funcional de seguridad que exige almacenar las
 * credenciales cifradas y nunca en texto plano.
 */
public final class EncriptadorPassword {

    private static final String ALGORITMO = "SHA-256";

    private EncriptadorPassword() {
    }

    /**
     * Convierte una contrasena en su resumen criptografico hexadecimal.
     *
     * @param passwordPlano contrasena escrita por el usuario
     * @return cadena de 64 caracteres hexadecimales
     */
    public static String generarHash(String passwordPlano) {
        if (passwordPlano == null) {
            throw new IllegalArgumentException("La contrasena no puede ser nula");
        }

        try {
            MessageDigest resumen = MessageDigest.getInstance(ALGORITMO);
            byte[] bytesResumen = resumen.digest(passwordPlano.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexadecimal = new StringBuilder(bytesResumen.length * 2);
            for (byte posicion : bytesResumen) {
                hexadecimal.append(String.format("%02x", posicion));
            }
            return hexadecimal.toString();

        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("El algoritmo " + ALGORITMO + " no esta disponible", error);
        }
    }

    /**
     * Compara la contrasena escrita por el usuario contra el hash almacenado.
     *
     * @param passwordPlano contrasena digitada en el formulario de ingreso
     * @param hashAlmacenado valor guardado en la columna Usu_Password
     * @return true si las credenciales coinciden
     */
    public static boolean coincide(String passwordPlano, String hashAlmacenado) {
        if (passwordPlano == null || hashAlmacenado == null) {
            return false;
        }
        return generarHash(passwordPlano).equalsIgnoreCase(hashAlmacenado.trim());
    }
}
