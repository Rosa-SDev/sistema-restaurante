package model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Persona que usa el sistema. Es abstracta porque no existe un usuario generico:
 * todo usuario es Administrador, Mesero, Cocinero o Cajero.
 *
 * Los cuatro roles no agregan atributos propios; se diferencian
 * únicamente por las operaciones que saben hacer.
 */
public abstract class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String passwordHash;
    private boolean activo;

    /**
     * @param password contrasena en texto plano. No se guarda: se almacena
     *                 su hash SHA-256 y el texto original se descarta.
     */
    protected Usuario(int id, String nombre, String correo, String password) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = hash(password);
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /** Solo lectura: el hash se cambia con cambiarPassword(), nunca por asignacion directa. */
    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Compara el hash de la clave recibida contra el almacenado.
     * Un usuario inactivo no puede entrar aunque la clave sea correcta.
     */
    public boolean iniciarSesion(String clave) {
        return activo && passwordHash.equals(hash(clave));
    }

    public void cerrarSesion() {
        // El estado de la sesion lo mantiene la capa de vista, no la entidad.
        // El metodo existe porque el diagrama lo define como operacion del usuario.
    }

    public void cambiarPassword(String nueva) {
        this.passwordHash = hash(nueva);
    }

    /**
     * SHA-256 en hexadecimal. Sin salt: es un prototipo academico y asi se
     * declara. Lo que se evita es lo indefendible, que es guardar la
     * contrasena en claro en un campo llamado passwordHash.
     */
    protected static String hash(String texto) {
        String entrada = (texto == null) ? "" : texto;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] resumen = md.digest(entrada.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumen);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en esta JVM", e);
        }
    }

    /** Cada rol lo redefine: es lo que permite listar usuarios de distinto tipo en una sola tabla. */
    @Override
    public String toString() {
        return nombre + " (" + correo + ")";
    }
}
