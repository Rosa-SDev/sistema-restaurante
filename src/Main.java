import controller.ControllerCliente;
import controller.ControllerMesa;
import controller.ControllerPlatillo;
import controller.ControllerUsuario;
import model.Administrador;
import model.Cajero;
import model.Cliente;
import model.Cocinero;
import model.EstadoMesa;
import model.Mesa;
import model.Mesero;
import model.Platillo;
import model.Restaurante;
import view.GUILogin;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;

/**
 * Punto de entrada de la aplicacion.
 *
 * Arranca el Singleton del restaurante, carga los datos de demostracion y abre
 * la ventana de inicio de sesion. GUIPrincipal no se abre desde aqui: la abre
 * GUILogin cuando alguien se autentica.
 *
 * Los datos son de prueba y viven en memoria: al cerrar la aplicacion se pierden.
 *
 * Credenciales de demostracion, una por cada rol del diagrama:
 *
 *   admin@restaurante.com    / admin123     Administrador
 *   mesero@restaurante.com   / mesero123    Mesero
 *   cocinero@restaurante.com / cocinero123  Cocinero
 *   cajero@restaurante.com   / cajero123    Cajero
 *
 * Las contrasenas no se guardan en claro: Usuario las convierte a SHA-256 en el
 * constructor. Estas de aqui son las que hay que teclear en el login.
 */
public class Main {

    public static void main(String[] args) {
        Restaurante.getInstancia();
        cargarDatosDemo();

        // Swing exige que las ventanas se construyan en el hilo de eventos
        SwingUtilities.invokeLater(() -> new GUILogin().setVisible(true));
    }

    private static void cargarDatosDemo() {
        cargarUsuarios();
        cargarClientes();
        cargarMesas();
        cargarCarta();
    }

    /** Un usuario por rol, para poder probar el filtrado de menus de cada uno. */
    private static void cargarUsuarios() {
        ControllerUsuario.agregarUsuario(
                new Administrador(1, "Laura Medina", "admin@restaurante.com", "admin123"));
        ControllerUsuario.agregarUsuario(
                new Mesero(2, "Ana Torres", "mesero@restaurante.com", "mesero123"));
        ControllerUsuario.agregarUsuario(
                new Cocinero(3, "Beto Ruiz", "cocinero@restaurante.com", "cocinero123"));
        ControllerUsuario.agregarUsuario(
                new Cajero(4, "Luis Paz", "cajero@restaurante.com", "cajero123"));
    }

    private static void cargarClientes() {
        ControllerCliente.agregarCliente(new Cliente(1, "Pedro Gil", "1077111", "3101112233"));
        ControllerCliente.agregarCliente(new Cliente(2, "Sofía Ramos", "1077222", "3102223344"));
    }

    /** Una mesa reservada, para que se vean los tres estados desde el arranque. */
    private static void cargarMesas() {
        ControllerMesa.agregarMesa(new Mesa(1, 1, 2, EstadoMesa.LIBRE));
        ControllerMesa.agregarMesa(new Mesa(2, 2, 4, EstadoMesa.LIBRE));
        ControllerMesa.agregarMesa(new Mesa(3, 3, 4, EstadoMesa.RESERVADA));
        ControllerMesa.agregarMesa(new Mesa(4, 4, 6, EstadoMesa.LIBRE));
    }

    /** La categoria es un String, no una clase (decision del equipo). */
    private static void cargarCarta() {
        ControllerPlatillo.agregarPlatillo(new Platillo(1, "Empanadas", "6 unidades",
                "Entradas", new BigDecimal("12000"), true));
        ControllerPlatillo.agregarPlatillo(new Platillo(2, "Patacones", "Con hogao",
                "Entradas", new BigDecimal("15000"), true));
        ControllerPlatillo.agregarPlatillo(new Platillo(3, "Sancocho de gallina", "Plato típico",
                "Platos fuertes", new BigDecimal("28000"), true));
        ControllerPlatillo.agregarPlatillo(new Platillo(4, "Bandeja paisa", "Plato típico",
                "Platos fuertes", new BigDecimal("32000"), true));
        ControllerPlatillo.agregarPlatillo(new Platillo(5, "Limonada de coco", "Vaso de 350 ml",
                "Bebidas", new BigDecimal("9000"), true));
        // Uno agotado, para poder ensenar que el controlador lo rechaza en un pedido
        ControllerPlatillo.agregarPlatillo(new Platillo(6, "Mojarra frita", "Con patacón",
                "Platos fuertes", new BigDecimal("35000"), false));
    }
}
