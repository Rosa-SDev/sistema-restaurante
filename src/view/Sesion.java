package view;

import model.Usuario;

/**
 * Estado de la sesion abierta en la aplicacion.
 *
 * El usuario conectado se guarda aqui, en la capa de vista, y no dentro de la
 * entidad Usuario: una entidad representa a la persona, no al hecho de que
 * haya iniciado sesion en esta ejecucion. Por eso Usuario.cerrarSesion() tiene
 * el cuerpo vacio.
 *
 * GUIPrincipal consulta getActual() para habilitar los menus segun el rol.
 */
public final class Sesion {

    private static Usuario actual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        actual = usuario;
    }

    public static void cerrar() {
        if (actual != null) {
            // se avisa a la entidad, que es donde el diagrama pone la operacion
            actual.cerrarSesion();
        }
        actual = null;
    }

    public static Usuario getActual() {
        return actual;
    }
}
