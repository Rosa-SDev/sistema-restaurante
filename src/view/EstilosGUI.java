package view;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/**
 * Constantes de estilo de la aplicacion.
 *
 * Es una clase final con constructor privado, no una interfaz: una interfaz
 * declara comportamiento, no guarda colores. Heredar constantes implementando
 * una interfaz es el "constant interface antipattern".
 */
public final class EstilosGUI {

    private EstilosGUI() {
    }

    public static final Color COLOR = new Color(178, 76, 51);
    public static final Color COLOR_CLARO = new Color(245, 235, 224);
    public static final Border GRAY_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
    public static final Font FUENTE_TITULO = new Font("Arial", Font.BOLD, 20);
}
