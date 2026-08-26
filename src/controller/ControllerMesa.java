package controller;

import model.EstadoMesa;
import model.IActualizable;
import model.Mesa;

import java.util.ArrayList;
import java.util.List;

public class ControllerMesa {

    private static List<Mesa> mesas = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

    public static void agregarMesa(Mesa mesa) throws RuntimeException {
        if (mesa == null) {
            throw new RuntimeException("Error: mesa nula.");
        }
        if (mesa.getNumero() <= 0) {
            throw new RuntimeException("Error: el numero de la mesa debe ser mayor que cero.");
        }
        if (mesa.getCapacidad() <= 0) {
            throw new RuntimeException("Error: la capacidad de la mesa debe ser mayor que cero.");
        }
        if (mesa.getEstado() == null) {
            throw new RuntimeException("Error: el estado de la mesa es obligatorio.");
        }
        if (existeId(mesa.getId())) {
            throw new RuntimeException("Error: ya existe una mesa con ese ID.");
        }
        if (existeNumero(mesa.getNumero())) {
            throw new RuntimeException("Error: ya existe una mesa con ese numero.");
        }
        mesas.add(mesa);
        actualizar();
    }

    public static List<Mesa> listarMesas() {
        return new ArrayList<>(mesas);
    }

    public static List<Mesa> listarMesasLibres() {
        List<Mesa> libres = new ArrayList<>();
        for (Mesa mesa : mesas) {
            if (mesa.getEstado() == EstadoMesa.LIBRE) {
                libres.add(mesa);
            }
        }
        return libres;
    }

    public static Mesa buscarMesa(int id) {
        for (Mesa mesa : mesas) {
            if (mesa.getId() == id) {
                return mesa;
            }
        }
        return null;
    }

    public static Mesa buscarMesaPorNumero(int numero) {
        for (Mesa mesa : mesas) {
            if (mesa.getNumero() == numero) {
                return mesa;
            }
        }
        return null;
    }

    public static List<Mesa> buscarMesa(String estado) {
        List<Mesa> resultados = new ArrayList<>();
        for (Mesa mesa : mesas) {
            if (mesa.getEstado().name().equalsIgnoreCase(estado)) {
                resultados.add(mesa);
            }
        }
        return resultados;
    }

    public static void actualizarMesa(Mesa mesaActualizada) throws RuntimeException {
        if (mesaActualizada == null
                || mesaActualizada.getNumero() <= 0
                || mesaActualizada.getCapacidad() <= 0
                || mesaActualizada.getEstado() == null) {
            throw new RuntimeException("Error: campos invalidos para la mesa.");
        }
        for (int i = 0; i < mesas.size(); i++) {
            if (mesas.get(i).getId() == mesaActualizada.getId()) {
                mesas.set(i, mesaActualizada);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no existe una mesa con ese ID.");
    }

    public static void eliminarMesa(int id) throws RuntimeException {
        for (Mesa mesa : mesas) {
            if (mesa.getId() == id) {
                if (mesa.getEstado() != EstadoMesa.LIBRE) {
                    throw new RuntimeException("Error: no se puede eliminar una mesa " + mesa.getEstado() + ".");
                }
                mesas.remove(mesa);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no se encontro una mesa con ese ID.");
    }

    private static boolean existeId(int id) {
        for (Mesa mesa : mesas) {
            if (mesa.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private static boolean existeNumero(int numero) {
        return buscarMesaPorNumero(numero) != null;
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