package controller;

import model.Cajero;
import model.Cocinero;
import model.IActualizable;
import model.Mesero;
import model.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class ControllerUsuario {

    private static List<Usuario> usuarios = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

    /**
     * Un hash SHA-256 son 64 caracteres hexadecimales. Si lo que llega no tiene
     * esa forma, alguien paso la contrasena en claro al constructor: sin esta
     * comprobacion se guardaria tal cual, el usuario nunca podria entrar y no
     * habria ningun error que lo delatara.
     */
    private static boolean hashValido(String passwordHash) {
        return passwordHash != null && passwordHash.matches("[0-9a-f]{64}");
    }

    public static void agregarUsuario(Usuario usuario) throws RuntimeException {
        if (usuario == null) {
            throw new RuntimeException("Error: usuario nulo.");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new RuntimeException("Error: el nombre del usuario es obligatorio.");
        }
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new RuntimeException("Error: el correo del usuario es obligatorio.");
        }
        if (!correoValido(usuario.getCorreo())) {
            throw new RuntimeException("Error: el correo del usuario no tiene un formato válido.");
        }
        if (existeId(usuario.getId())) {
            throw new RuntimeException("Error: ya existe un usuario con ese ID.");
        }
        if (existeCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Error: ya existe un usuario con ese correo.");
        }
        if (!hashValido(usuario.getPasswordHash())) {
            throw new RuntimeException("Error: la contrasena debe cifrarse con ControllerUsuario.hash().");
        }
        usuarios.add(usuario);
        actualizar();
    }

    public static List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public static List<Mesero> listarMeseros() {
        List<Mesero> meseros = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Mesero) {
                meseros.add((Mesero) usuario);
            }
        }
        return meseros;
    }

    public static List<Cocinero> listarCocineros() {
        List<Cocinero> cocineros = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Cocinero) {
                cocineros.add((Cocinero) usuario);
            }
        }
        return cocineros;
    }

    public static List<Cajero> listarCajeros() {
        List<Cajero> cajeros = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Cajero) {
                cajeros.add((Cajero) usuario);
            }
        }
        return cajeros;
    }

    public static Usuario buscarUsuario(int id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        return null;
    }

    public static List<Usuario> buscarUsuario(String nombre) {
        List<Usuario> resultados = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultados.add(usuario);
            }
        }
        return resultados;
    }

    public static void actualizarUsuario(Usuario usuarioActualizado) throws RuntimeException {
        if (usuarioActualizado == null
                || usuarioActualizado.getNombre() == null || usuarioActualizado.getNombre().isBlank()
                || usuarioActualizado.getCorreo() == null || usuarioActualizado.getCorreo().isBlank()) {
            throw new RuntimeException("Error: campos inválidos para el usuario.");
        }
        if (!correoValido(usuarioActualizado.getCorreo())) {
            throw new RuntimeException("Error: el correo del usuario no tiene un formato válido.");
        }
        if (existeCorreoEnOtro(usuarioActualizado.getCorreo(), usuarioActualizado.getId())) {
            throw new RuntimeException("Error: ya existe otro usuario con ese correo.");
        }
        if (!hashValido(usuarioActualizado.getPasswordHash())) {
            throw new RuntimeException("Error: la contrasena debe cifrarse con ControllerUsuario.hash().");
        }
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() == usuarioActualizado.getId()) {
                usuarios.set(i, usuarioActualizado);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no existe un usuario con ese ID.");
    }

    public static void eliminarUsuario(int id) throws RuntimeException {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() == id) {
                usuarios.remove(usuario);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no se encontró un usuario con ese ID.");
    }

    public static void eliminarUsuario(String nombre) {
        usuarios.removeIf(usuario -> nombre.equalsIgnoreCase(usuario.getNombre()));
        actualizar();
    }

    /** Devuelve el usuario si las credenciales son correctas, o null si no. */
    /**
     * SHA-256 en hexadecimal. Sin salt: es un prototipo academico y asi se
     * declara. Lo que se evita es lo indefendible, que es guardar la
     * contrasena en claro en un campo llamado passwordHash.
     *
     * Vive aqui y no en Usuario porque cifrar es una regla de la aplicacion,
     * no una responsabilidad de la entidad: el modelo guarda el hash, el
     * controlador decide como se calcula.
     */
    public static String hash(String texto) {
        String entrada = (texto == null) ? "" : texto;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] resumen = md.digest(entrada.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumen);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en esta JVM", e);
        }
    }

    /**
     * El controlador cifra la clave recibida y deja que la entidad compare.
     * Mismo mensaje para correo inexistente, clave incorrecta y usuario
     * inactivo: distinguirlos revelaria que correos estan registrados.
     */
    public static Usuario autenticar(String correo, String clave) {
        String hashRecibido = hash(clave);
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo) && usuario.iniciarSesion(hashRecibido)) {
                return usuario;
            }
        }
        return null;
    }

    private static boolean existeId(int id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprobacion minima del correo: una sola arroba, con texto a cada lado, y
     * un punto dentro del dominio que no lo abra ni lo cierre.
     *
     * No hay expresion regular a proposito. La sintaxis real de un correo no cabe
     * en una, y las que circulan por internet rechazan correos validos. Estas
     * cuatro condiciones se leen y se defienden una por una.
     */
    private static boolean correoValido(String correo) {
        int arroba = correo.indexOf('@');
        // una sola arroba: la primera tiene que ser tambien la ultima
        if (arroba < 0 || arroba != correo.lastIndexOf('@')) {
            return false;
        }
        String parteLocal = correo.substring(0, arroba);
        String dominio = correo.substring(arroba + 1);
        if (parteLocal.isBlank() || dominio.isBlank()) {
            return false;
        }
        // ni ".com" ni "gmail.": el punto necesita texto a los dos lados
        int punto = dominio.indexOf('.');
        return punto > 0 && punto < dominio.length() - 1;
    }

    private static boolean existeCorreo(String correo) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Igual que existeCorreo, pero sin mirar al usuario con ese ID.
     *
     * Al actualizar, el usuario conserva su propio correo: sin esta exclusion
     * chocaria consigo mismo y no se podria guardar ningun cambio.
     */
    private static boolean existeCorreoEnOtro(String correo, int idPropio) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() != idPropio && usuario.getCorreo().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    public static void addActualizable(IActualizable actualizable) {
        observadores.add(actualizable);
    }

    public static void removeActualizable(IActualizable actualizable) {
        observadores.remove(actualizable);
    }

    public static void actualizar() {
        for (IActualizable observador : observadores) {
            observador.actualizar();
        }
    }
}