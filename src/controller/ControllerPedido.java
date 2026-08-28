package controller;

import model.EstadoMesa;
import model.EstadoPedido;
import model.IActualizable;
import model.Pedido;
import model.Platillo;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD en memoria y reglas de aplicacion del pedido.
 *
 * Aqui vive el bucle de la cantidad: como no existe DetallePedido (decision B),
 * pedir dos hamburguesas son dos llamadas a Pedido.agregarPlatillo(). La vista
 * recoge el numero y este controlador lo repite.
 *
 * Tambien es quien ocupa la mesa. Pedido.cerrar() y Pedido.cancelar() la
 * liberan, pero ocuparla no cabia en el modelo: el diagrama no tiene un metodo
 * abrir() y un constructor no deberia tener efectos sobre otro objeto. Sin este
 * ocupar() la mesa jamas saldria de LIBRE.
 */
public class ControllerPedido {

    private static List<Pedido> pedidos = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

    /**
     * Da de alta el pedido y ocupa su mesa.
     *
     * Una mesa RESERVADA si acepta pedido: esta esperando a alguien, y abrirle la
     * cuenta cuando llega es justo la transicion RESERVADA -> OCUPADA. Lo que se
     * rechaza es una mesa ya OCUPADA, porque dos pedidos sobre la misma mesa
     * harian que el primero en cerrar la liberara con el otro todavia abierto.
     */
    public static void crearPedido(Pedido pedido) throws RuntimeException {
        if (pedido == null) {
            throw new RuntimeException("Error: pedido nulo.");
        }
        if (pedido.getMesa() == null) {
            throw new RuntimeException("Error: el pedido necesita una mesa.");
        }
        if (pedido.getMesero() == null) {
            throw new RuntimeException("Error: el pedido necesita un mesero.");
        }
        if (existeId(pedido.getId())) {
            throw new RuntimeException("Error: ya existe un pedido con ese ID.");
        }
        if (pedido.getMesa().getEstado() == EstadoMesa.OCUPADA) {
            throw new RuntimeException("Error: la mesa " + pedido.getMesa().getNumero()
                    + " ya esta ocupada por otro pedido.");
        }
        pedidos.add(pedido);
        pedido.getMesa().ocupar();
        actualizar();
    }

    public static List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public static Pedido buscarPedido(int id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId() == id) {
                return pedido;
            }
        }
        return null;
    }

    public static List<Pedido> buscarPedido(EstadoPedido estado) {
        List<Pedido> resultados = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            if (pedido.getEstado() == estado) {
                resultados.add(pedido);
            }
        }
        return resultados;
    }

    /**
     * Agrega el platillo tantas veces como diga la cantidad.
     *
     * Pedido.agregarPlatillo() no comprueba el null a proposito (decision F), asi
     * que esta es la unica defensa que hay: si aqui no se mira, la lista se llena
     * de nulos y revienta despues, al calcular el total.
     */
    public static void agregarPlatillo(Pedido pedido, Platillo platillo, int cantidad) throws RuntimeException {
        if (pedido == null) {
            throw new RuntimeException("Error: pedido nulo.");
        }
        if (platillo == null) {
            throw new RuntimeException("Error: platillo nulo.");
        }
        if (cantidad <= 0) {
            throw new RuntimeException("Error: la cantidad debe ser mayor que cero.");
        }
        if (pedido.getEstado() != EstadoPedido.ABIERTO) {
            throw new RuntimeException("Error: solo se pueden agregar platillos a un pedido abierto.");
        }
        if (!platillo.isDisponible()) {
            throw new RuntimeException("Error: el platillo " + platillo.getNombre() + " no está disponible.");
        }
        for (int i = 0; i < cantidad; i++) {
            pedido.agregarPlatillo(platillo);
        }
        actualizar();
    }

    /** Quita una sola unidad, no todas las iguales. */
    public static void quitarPlatillo(Pedido pedido, Platillo platillo) throws RuntimeException {
        if (pedido == null) {
            throw new RuntimeException("Error: pedido nulo.");
        }
        if (platillo == null) {
            throw new RuntimeException("Error: platillo nulo.");
        }
        if (pedido.getEstado() != EstadoPedido.ABIERTO) {
            throw new RuntimeException("Error: solo se pueden quitar platillos de un pedido abierto.");
        }
        if (!pedido.getPlatillos().contains(platillo)) {
            throw new RuntimeException("Error: el pedido no contiene ese platillo.");
        }
        pedido.quitarPlatillo(platillo);
        actualizar();
    }

    /**
     * Cierra el pedido, lo que ademas libera su mesa.
     *
     * Exige que haya un pago registrado: cerrar() deja el pedido en PAGADO y
     * suelta la mesa, asi que sin esta guarda se liberarian mesas sin haber
     * cobrado. Quien registra el pago es ControllerFactura, que cobra el total
     * con impuestos y solo despues llama aqui.
     */
    public static void cerrarPedido(Pedido pedido) throws RuntimeException {
        if (pedido == null) {
            throw new RuntimeException("Error: pedido nulo.");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new RuntimeException("Error: no se puede cerrar un pedido cancelado.");
        }
        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            throw new RuntimeException("Error: el pedido ya esta cerrado.");
        }
        if (!pedido.estaPagado()) {
            throw new RuntimeException("Error: no se puede cerrar el pedido sin un pago registrado.");
        }
        pedido.cerrar();
        actualizar();
    }

    public static void cancelarPedido(Pedido pedido) throws RuntimeException {
        if (pedido == null) {
            throw new RuntimeException("Error: pedido nulo.");
        }
        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            throw new RuntimeException("Error: no se puede cancelar un pedido ya pagado.");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new RuntimeException("Error: el pedido ya esta cancelado.");
        }
        pedido.cancelar();
        actualizar();
    }

    private static boolean existeId(int id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId() == id) {
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
