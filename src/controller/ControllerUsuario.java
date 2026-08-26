package controller;

import model.Cocinero;
import model.IActualizable;
import model.Mesero;
import model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ControllerUsuario {

    private static List<Usuario> usuarios = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

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
        if (existeId(usuario.getId())) {
            throw new RuntimeException("Error: ya existe un usuario con ese ID.");
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
            throw new RuntimeException("Error: campos invalidos para el usuario.");
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
        throw new RuntimeException("Error: no se encontro un usuario con ese ID.");
    }

    public static void eliminarUsuario(String nombre) {
        usuarios.removeIf(usuario -> nombre.equalsIgnoreCase(usuario.getNombre()));
        actualizar();
    }

    /** Devuelve el usuario si las credenciales son correctas, o null si no. */
    public static Usuario autenticar(String correo, String clave) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo) && usuario.iniciarSesion(clave)) {
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