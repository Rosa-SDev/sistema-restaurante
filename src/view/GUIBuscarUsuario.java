package view;

import controller.ControllerUsuario;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUIBuscarUsuario extends JFrame {

    private JTextField idTexto, nombreTexto;
    private JTable tabla;

    public GUIBuscarUsuario() {
        ComponentesGUI.configurar(this, "Buscar usuario", 700, 420);

        idTexto = new JTextField(8);
        idTexto.setBorder(EstilosGUI.GRAY_BORDER);
        nombreTexto = new JTextField(15);
        nombreTexto.setBorder(EstilosGUI.GRAY_BORDER);

        JButton buscarBTN = new JButton("Buscar");
        buscarBTN.addActionListener(e -> buscar());

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltros.setBackground(EstilosGUI.COLOR_CLARO);
        panelFiltros.add(new JLabel("Id:"));
        panelFiltros.add(idTexto);
        panelFiltros.add(new JLabel("Nombre:"));
        panelFiltros.add(nombreTexto);
        panelFiltros.add(buscarBTN);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(EstilosGUI.COLOR_CLARO);
        panelNorte.add(ComponentesGUI.titulo("Buscar por Id o por nombre"), BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.CENTER);

        tabla = ComponentesGUI.tabla();

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void buscar() {
        List<Usuario> resultados = new ArrayList<>();
        try {
            if (!idTexto.getText().trim().isEmpty()) {
                Usuario usuario = ControllerUsuario.buscarUsuario(Integer.parseInt(idTexto.getText().trim()));
                if (usuario != null) {
                    resultados.add(usuario);
                }
            } else if (!nombreTexto.getText().trim().isEmpty()) {
                resultados = ControllerUsuario.buscarUsuario(nombreTexto.getText().trim());
            } else {
                ComponentesGUI.aviso(this, "Escriba un Id o un nombre para buscar.");
                return;
            }
        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El Id debe ser un numero entero.");
            return;
        }

        tabla.setModel(new AdaptadorTablaModelo<>(resultados, GUIListarUsuarios.columnas()));

        if (resultados.isEmpty()) {
            ComponentesGUI.aviso(this, "No se encontraron usuarios.");
        }
    }
}
