import java.util.Random;
import java.util.logging.Logger;

public class Enxugador implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Enxugador.class.getName());

    private final Escorredor escorredor;
    
    private volatile boolean done;
    private int totalEnxugados;

    public Enxugador(Escorredor escorredor) {
        this.escorredor = escorredor;
    }

    public void parar() {
        done = true;
    }

    public int getTotalEnxugados() {
        return totalEnxugados;
    }

    @Override
    public void run() {
        Random random = new Random();

        while (!done || escorredor.getQuantidade() > 0) {
            Prato prato;

            try {
                prato = escorredor.retirar();
            } catch (InterruptedException e) {
                if (!done) {
                    LOGGER.warning(Thread.currentThread().getName() + " foi interrompido.");
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            if (prato == null) {
                break;
            }

            int limite = (3 + random.nextInt(8)) * 10_000_000;
            LOGGER.fine(Thread.currentThread().getName() + " comecou a enxugar o prato "
                    + prato.getNumeroSerie() + " (" + limite + " contagens).");

            int contador = 0;

            for (int i = 0; i < limite; i++) {
                if (i % 2 == 0) {
                    contador++;
                }
            }
            LOGGER.fine("Contagem do enxugamento: " + contador);

            totalEnxugados++;
            LOGGER.fine(Thread.currentThread().getName() + " enxugou o prato "
                    + prato.getNumeroSerie() + ".");
        }

        LOGGER.info(Thread.currentThread().getName() + " terminou.");
    }
}
