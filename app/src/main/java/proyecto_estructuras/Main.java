package proyecto_estructuras;

/**
 * Punto de entrada principal de la aplicación del Sistema de Gestión de Impresión.
 * <p>
 * Inicializa la interfaz gráfica {@link MainFrame} en el hilo de eventos de Swing
 * utilizando {@link javax.swing.SwingUtilities#invokeLater(Runnable)} para garantizar
 * la seguridad de hilos en la GUI.
 * </p>
 *
 * @see MainFrame
 */
public class Main {

    /**
     * Método principal que arranca la aplicación.
     * <p>
     * Crea y muestra la ventana principal ({@link MainFrame}) de forma segura
     * en el Event Dispatch Thread (EDT) de Swing.
     * </p>
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
