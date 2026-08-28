package controller;

import model.IActualizable;
import model.Platillo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD en memoria de la carta.
 *
 * Aqui vive la validacion de los platillos: el modelo no valida (decision F),
 * asi que cualquier regla sobre nombre, categoria o precio se comprueba en este
 * controlador y se avisa con una RuntimeException en español.
 *
 * Es ademas el sujeto del patron Observer: las vistas de listado se registran
 * con addActualizable y se repintan solas cuando la carta cambia.
 */
public class ControllerPlatillo {

    private static List<Platillo> platillos = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

    public static void agregarPlatillo(Platillo platillo) throws RuntimeException {
        validar(platillo);
        if (existeId(platillo.getId())) {
            throw new RuntimeException("Error: ya existe un platillo con ese ID.");
        }
        platillos.add(platillo);
        actualizar();
    }

    public static List<Platillo> listarPlatillos() {
        return new ArrayList<>(platillos);
    }

    public static int contarDisponibles() {
        int disponibles = 0;
        for (Platillo platillo : platillos) {
            if (platillo.isDisponible()) {
                disponibles++;
            }
        }
        return disponibles;
    }

    /**
     * Precio medio de la carta, con dos decimales.
     * Una carta vacia devuelve cero, para no dividir entre cero.
     */
    public static BigDecimal calcularPrecioPromedio() {
        if (platillos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal suma = BigDecimal.ZERO;
        for (Platillo platillo : platillos) {
            suma = suma.add(platillo.getPrecio());
        }
        return suma.divide(new BigDecimal(platillos.size()), 2, RoundingMode.HALF_UP);
    }

    public static Platillo buscarPlatillo(int id) {
        for (Platillo platillo : platillos) {
            if (platillo.getId() == id) {
                return platillo;
            }
        }
        return null;
    }

    public static List<Platillo> buscarPlatillo(String nombre) {
        List<Platillo> resultados = new ArrayList<>();
        for (Platillo platillo : platillos) {
            if (platillo.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultados.add(platillo);
            }
        }
        return resultados;
    }

    public static void actualizarPlatillo(Platillo platilloActualizado) throws RuntimeException {
        validar(platilloActualizado);
        for (int i = 0; i < platillos.size(); i++) {
            if (platillos.get(i).getId() == platilloActualizado.getId()) {
                platillos.set(i, platilloActualizado);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no existe un platillo con ese ID.");
    }

    public static void eliminarPlatillo(int id) throws RuntimeException {
        for (Platillo platillo : platillos) {
            if (platillo.getId() == id) {
                platillos.remove(platillo);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no se encontro un platillo con ese ID.");
    }

    public static void eliminarPlatillo(String nombre) {
        platillos.removeIf(platillo -> nombre.equalsIgnoreCase(platillo.getNombre()));
        actualizar();
    }

    /**
     * Reglas comunes al alta y a la actualizacion.
     *
     * Un precio nulo llega aqui como cero, porque el constructor de Platillo lo
     * normaliza, y lo rechaza la misma comprobacion de "mayor que cero".
     */
    private static void validar(Platillo platillo) throws RuntimeException {
        if (platillo == null) {
            throw new RuntimeException("Error: platillo nulo.");
        }
        if (platillo.getNombre() == null || platillo.getNombre().isBlank()) {
            throw new RuntimeException("Error: el nombre del platillo es obligatorio.");
        }
        if (platillo.getCategoria() == null || platillo.getCategoria().isBlank()) {
            throw new RuntimeException("Error: la categoria del platillo es obligatoria.");
        }
        if (platillo.getPrecio() == null || platillo.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Error: el precio del platillo debe ser mayor que cero.");
        }
    }

    private static boolean existeId(int id) {
        for (Platillo platillo : platillos) {
            if (platillo.getId() == id) {
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
