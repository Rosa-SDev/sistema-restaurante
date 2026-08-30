package controller;

import model.EstadoMesa;
import model.EstadoReserva;
import model.IActualizable;
import model.Reserva;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD en memoria y reglas de aplicacion de las reservas.
 *
 * Recoge las tres validaciones que Reserva dejo dichas en sus javadoc y no podia
 * hacer por la decision F: que quepan los comensales en la mesa, que la mesa no
 * este ocupada al confirmar, y el paso a CUMPLIDA, que el diagrama no da como
 * operacion porque es una regla de aplicacion.
 */
public class ControllerReserva {

    private static List<Reserva> reservas = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

    /**
     * Da de alta la reserva en estado PENDIENTE.
     *
     * No mira el estado de la mesa: una reserva se toma para mas tarde, asi que
     * la mesa puede estar ocupada ahora mismo por otros comensales. Quien retiene
     * la mesa es confirmarReserva(), y es ahi donde el estado importa.
     */
    public static void registrarReserva(Reserva reserva) throws RuntimeException {
        if (reserva == null) {
            throw new RuntimeException("Error: reserva nula.");
        }
        if (reserva.getCliente() == null) {
            throw new RuntimeException("Error: la reserva necesita un cliente.");
        }
        if (reserva.getMesa() == null) {
            throw new RuntimeException("Error: la reserva necesita una mesa.");
        }
        if (reserva.getFechaHora() == null) {
            throw new RuntimeException("Error: la reserva necesita una fecha.");
        }
        // Se compara por minutos, no por segundos: el formulario solo muestra
        // "dd/MM/yyyy HH:mm", asi que un rechazo por medio minuto seria invisible
        // para quien lo sufre.
        if (reserva.getFechaHora().truncatedTo(ChronoUnit.MINUTES)
                .isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
            throw new RuntimeException("Error: no se puede reservar para una fecha que ya pasó.");
        }
        if (reserva.getNumPersonas() <= 0) {
            throw new RuntimeException("Error: el número de personas debe ser mayor que cero.");
        }
        if (reserva.getNumPersonas() > reserva.getMesa().getCapacidad()) {
            throw new RuntimeException("Error: la mesa " + reserva.getMesa().getNumero()
                    + " admite " + reserva.getMesa().getCapacidad() + " personas y se piden "
                    + reserva.getNumPersonas() + ".");
        }
        if (existeId(reserva.getId())) {
            throw new RuntimeException("Error: ya existe una reserva con ese ID.");
        }
        reservas.add(reserva);
        actualizar();
    }

    /**
     * Confirma la reserva y retiene la mesa.
     *
     * Reserva.confirmar() retiene la mesa sin condiciones a proposito, asi que la
     * comprobacion de que no este ya OCUPADA tiene que estar aqui: si no, se
     * retendria una mesa con gente sentada y pedido abierto.
     */
    public static void confirmarReserva(Reserva reserva) throws RuntimeException {
        validarExistente(reserva);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("Error: solo se puede confirmar una reserva pendiente.");
        }
        if (reserva.getMesa().getEstado() == EstadoMesa.OCUPADA) {
            throw new RuntimeException("Error: la mesa " + reserva.getMesa().getNumero()
                    + " está ocupada y no se puede reservar.");
        }
        reserva.confirmar();
        actualizar();
    }

    public static void cancelarReserva(Reserva reserva) throws RuntimeException {
        validarExistente(reserva);
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("Error: la reserva ya está cancelada.");
        }
        if (reserva.getEstado() == EstadoReserva.CUMPLIDA) {
            throw new RuntimeException("Error: no se puede cancelar una reserva ya cumplida.");
        }
        reserva.cancelar();
        actualizar();
    }

    /**
     * Marca la reserva como cumplida: el cliente llego y se le va a abrir pedido.
     *
     * No libera la mesa ni la ocupa. La deja en RESERVADA, que es justo el estado
     * desde el que ControllerPedido.crearPedido() acepta abrir la cuenta y la
     * pasa a OCUPADA. Liberarla aqui abriria un hueco en el que otro pedido
     * podria colarse en la mesa.
     */
    public static void marcarCumplida(Reserva reserva) throws RuntimeException {
        validarExistente(reserva);
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new RuntimeException("Error: solo una reserva confirmada puede darse por cumplida.");
        }
        reserva.setEstado(EstadoReserva.CUMPLIDA);
        actualizar();
    }

    public static List<Reserva> listarReservas() {
        return new ArrayList<>(reservas);
    }

    public static Reserva buscarReserva(int id) {
        for (Reserva reserva : reservas) {
            if (reserva.getId() == id) {
                return reserva;
            }
        }
        return null;
    }

    public static List<Reserva> buscarReserva(EstadoReserva estado) {
        List<Reserva> resultados = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getEstado() == estado) {
                resultados.add(reserva);
            }
        }
        return resultados;
    }

    /** Comun a confirmar, cancelar y cumplir: las tres operan sobre una reserva ya registrada. */
    private static void validarExistente(Reserva reserva) throws RuntimeException {
        if (reserva == null) {
            throw new RuntimeException("Error: reserva nula.");
        }
        if (!reservas.contains(reserva)) {
            throw new RuntimeException("Error: no existe esa reserva.");
        }
    }

    private static boolean existeId(int id) {
        for (Reserva reserva : reservas) {
            if (reserva.getId() == id) {
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
