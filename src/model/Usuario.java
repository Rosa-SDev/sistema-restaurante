package model;

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
     * @param passwordHash hash SHA-256 de la contrasena, YA calculado. La entidad
     *                     no cifra: de eso se encarga ControllerUsuario.hash().
     *                     Nunca se le debe pasar una contrasena en texto plano.
     */
    protected Usuario(int id, String nombre, String correo, String passwordHash) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
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
     * Decide si este usuario puede entrar comparando su hash con el recibido.
     * Un usuario inactivo no puede entrar aunque la clave sea correcta.
     *
     * Recibe el hash ya calculado, no la clave: la criptografia vive en
     * ControllerUsuario y el modelo no la conoce.
     */
    public boolean iniciarSesion(String hashRecibido) {
        return activo && passwordHash.equals(hashRecibido);
    }

    public void cerrarSesion() {
        // El estado de la sesion lo mantiene la capa de vista, no la entidad.
        // El metodo existe porque el diagrama lo define como operacion del usuario.
    }

    /** Recibe el hash ya calculado por ControllerUsuario.hash(). */
    public void cambiarPassword(String nuevoHash) {
        this.passwordHash = nuevoHash;
    }

    /** Cada rol lo redefine: es lo que permite listar usuarios de distinto tipo en una sola tabla. */
    @Override
    public String toString() {
        return nombre + " (" + correo + ")";
    }
}
