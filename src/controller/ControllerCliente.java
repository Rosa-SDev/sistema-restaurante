package controller;

import model.Cliente;
import model.IActualizable;

import java.util.ArrayList;
import java.util.List;

public class ControllerCliente {

    private static List<Cliente> clientes = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

    public static void agregarCliente(Cliente cliente) throws RuntimeException {
        if (cliente == null) {
            throw new RuntimeException("Error: cliente nulo.");
        }
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new RuntimeException("Error: el nombre del cliente es obligatorio.");
        }
        if (cliente.getDocumento() == null || cliente.getDocumento().isBlank()) {
            throw new RuntimeException("Error: el documento del cliente es obligatorio.");
        }
        if (existeId(cliente.getId())) {
            throw new RuntimeException("Error: ya existe un cliente con ese ID.");
        }
        clientes.add(cliente);
        actualizar();
    }

    public static List<Cliente> listarClientes() {
        return new ArrayList<>(clientes);
    }

    public static Cliente buscarCliente(int id) {
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    public static List<Cliente> buscarCliente(String nombre) {
        List<Cliente> resultados = new ArrayList<>();
        for (Cliente cliente : clientes) {
            if (cliente.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultados.add(cliente);
            }
        }
        return resultados;
    }

    public static void actualizarCliente(Cliente clienteActualizado) throws RuntimeException {
        if (clienteActualizado == null
                || clienteActualizado.getNombre() == null || clienteActualizado.getNombre().isBlank()
                || clienteActualizado.getDocumento() == null || clienteActualizado.getDocumento().isBlank()) {
            throw new RuntimeException("Error: campos inválidos para el cliente.");
        }
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == clienteActualizado.getId()) {
                clientes.set(i, clienteActualizado);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no existe un cliente con ese ID.");
    }

    public static void eliminarCliente(int id) throws RuntimeException {
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                clientes.remove(cliente);
                actualizar();
                return;
            }
        }
        throw new RuntimeException("Error: no se encontró un cliente con ese ID.");
    }

    public static void eliminarCliente(String nombre) {
        clientes.removeIf(cliente -> nombre.equalsIgnoreCase(cliente.getNombre()));
        actualizar();
    }

    private static boolean existeId(int id) {
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
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