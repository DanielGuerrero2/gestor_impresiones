package proyecto_estructuras;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Gestor central del sistema de impresión.
 * <p>
 * Administra dos estructuras de datos principales:
 * <ul>
 * <li>{@link PriorityQueue}{@code <TrabajoImpresion>} — <b>Cola activa:</b>
 * almacena los trabajos
 * listos para imprimir, ordenados automáticamente por prioridad (GERENCIA >
 * URGENTE > NORMAL).</li>
 * <li>{@link ArrayList}{@code <TrabajoImpresion>} — <b>Lista de
 * programados:</b> almacena trabajos
 * que aún no han alcanzado su hora programada.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Patrón de uso:</b> Los trabajos inmediatos entran directamente a la cola
 * activa.
 * Los trabajos programados se almacenan en la lista de espera y se mueven
 * automáticamente
 * a la cola activa cuando se invoca {@link #verificarProgramados()} y su hora
 * ha llegado.
 * </p>
 *
 * @see TrabajoImpresion
 * @see MainFrame
 */
public class GestorImpresion {

    /**
     * Cola de prioridad para trabajos activos (listos para imprimir).
     * Utiliza el {@link TrabajoImpresion#compareTo(TrabajoImpresion)} para mantener
     * el orden automáticamente.
     */
    private PriorityQueue<TrabajoImpresion> colaActiva;

    /**
     * Lista de trabajos programados que aún no han alcanzado su hora de activación.
     * No requiere ordenamiento, ya que el criterio de movimiento es temporal.
     */
    private List<TrabajoImpresion> listaProgramados;

    /**
     * Crea un nuevo gestor de impresión con colas vacías.
     */
    public GestorImpresion() {
        // PriorityQueue usa compareTo() de TrabajoImpresion
        this.colaActiva = new PriorityQueue<>();
        this.listaProgramados = new ArrayList<>();
    }

    /**
     * Envía un trabajo de impresión inmediato a la cola activa.
     * <p>
     * El trabajo se agrega directamente a la {@link PriorityQueue} y se posiciona
     * según su prioridad. No tiene fecha de programación.
     * </p>
     *
     * @param nombreDocumento nombre del documento a imprimir
     * @param prioridad       nivel de prioridad del trabajo
     * @throws NullPointerException     si algún parámetro es nulo
     * @throws IllegalArgumentException si el nombre del documento está vacío
     */
    public void enviarTrabajoInmediato(String nombreDocumento, TrabajoImpresion.Prioridad prioridad) {
        TrabajoImpresion trabajo = new TrabajoImpresion(nombreDocumento, prioridad);
        colaActiva.add(trabajo);
        System.out.println(" Enviado inmediato: " + trabajo);
    }

    /**
     * Programa un trabajo de impresión para una fecha/hora futura.
     * <p>
     * El trabajo se almacena en la lista de programados y permanecerá allí hasta
     * que
     * {@link #verificarProgramados()} detecte que su hora ha llegado.
     * </p>
     *
     * @param nombreDocumento nombre del documento a imprimir
     * @param prioridad       nivel de prioridad del trabajo
     * @param fechaHora       fecha y hora en que el trabajo se activará
     * @throws NullPointerException     si algún parámetro es nulo
     * @throws IllegalArgumentException si el nombre del documento está vacío
     */
    public void programarTrabajo(String nombreDocumento, TrabajoImpresion.Prioridad prioridad,
            LocalDateTime fechaHora) {
        TrabajoImpresion trabajo = new TrabajoImpresion(nombreDocumento, prioridad, fechaHora);
        listaProgramados.add(trabajo);
        System.out.println(" Trabajo programado: " + trabajo);
    }

    /**
     * Extrae e imprime el siguiente trabajo de la cola activa (el de mayor
     * prioridad).
     * <p>
     * Utiliza {@link PriorityQueue#poll()} que retorna y elimina el elemento con
     * mayor prioridad en O(log n).
     * </p>
     *
     * @return el trabajo impreso, o {@code null} si la cola activa está vacía
     */
    public TrabajoImpresion imprimirSiguiente() {
        if (colaActiva.isEmpty()) {
            System.out.println(" No hay trabajos en la cola activa.");
            return null;
        }
        TrabajoImpresion trabajo = colaActiva.poll();
        System.out.println(" Imprimiendo: " + trabajo);
        return trabajo;
    }

    /**
     * Verifica si algún trabajo programado debe moverse a la cola activa.
     * <p>
     * Recorre la lista de programados y mueve a la cola activa aquellos trabajos
     * cuya fecha/hora programada ya ha pasado o es igual al momento actual.
     * Si la cola activa está vacía, mueve todos los programados independientemente
     * de la hora.
     * </p>
     */
    public void verificarProgramados() {
        if (listaProgramados.isEmpty())
            return;

        LocalDateTime ahora = LocalDateTime.now();
        List<TrabajoImpresion> aMover = new ArrayList<>();

        if (colaActiva.isEmpty()) {
            aMover.addAll(listaProgramados);
        } else {
            for (TrabajoImpresion trabajo : listaProgramados) {
                if (trabajo.getFechaHoraProgramada() != null &&
                        !trabajo.getFechaHoraProgramada().isAfter(ahora)) {
                    aMover.add(trabajo);
                }
            }
        }

        for (TrabajoImpresion trabajo : aMover) {
            listaProgramados.remove(trabajo);
            colaActiva.add(trabajo);
            System.out.println(" Trabajo movido a cola activa: " + trabajo);
        }
    }

    /**
     * Muestra en consola el estado actual de ambas colas (activa y programados).
     * <p>
     * Para la cola activa, copia los elementos y los ordena ya que
     * {@link PriorityQueue} no garantiza orden al iterar.
     * </p>
     */
    public void mostrarEstado() {
        System.out.println("\n=== ESTADO ACTUAL ===");
        System.out.println(" Cola activa (por orden de prioridad):");
        if (colaActiva.isEmpty()) {
            System.out.println("   (vacía)");
        } else {
            // PriorityQueue no garantiza orden al iterar, así que copiamos y ordenamos
            List<TrabajoImpresion> temp = new ArrayList<>(colaActiva);
            temp.sort(TrabajoImpresion::compareTo);
            for (TrabajoImpresion t : temp) {
                System.out.println("   → " + t);
            }
        }

        System.out.println(" Trabajos programados:");
        if (listaProgramados.isEmpty()) {
            System.out.println("   (ninguno)");
        } else {
            for (TrabajoImpresion t : listaProgramados) {
                System.out.println("   → " + t);
            }
        }
        System.out.println("========================\n");
    }

    /**
     * Indica si hay trabajos pendientes en la cola activa.
     *
     * @return {@code true} si la cola activa no está vacía; {@code false} en caso
     *         contrario
     */
    public boolean hayTrabajosActivos() {
        return !colaActiva.isEmpty();
    }

    /**
     * Retorna una copia ordenada de los trabajos en la cola activa.
     * <p>
     * Se retorna una <b>copia defensiva</b> para proteger la integridad de la cola
     * interna.
     * </p>
     *
     * @return lista de trabajos activos ordenados por prioridad descendente
     */
    public List<TrabajoImpresion> getTrabajosActivos() {
        List<TrabajoImpresion> temp = new ArrayList<>(colaActiva);
        temp.sort(TrabajoImpresion::compareTo);
        return temp;
    }

    /**
     * Retorna una copia de la lista de trabajos programados.
     * <p>
     * Se retorna una <b>copia defensiva</b> para proteger la lista interna.
     * </p>
     *
     * @return lista de trabajos programados
     */
    public List<TrabajoImpresion> getTrabajosProgramados() {
        return new ArrayList<>(listaProgramados);
    }
}