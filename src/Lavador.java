import java.util.logging.Logger;

public class Lavador implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Lavador.class.getName());

    private final Escorredor escorredor;
    private volatile boolean done;
    private int totalLavados;

    public Lavador(Escorredor escorredor) {
        this.escorredor = escorredor;
    }

    public void parar() {
        done = true;
    }

    public int getTotalLavados() {
        return totalLavados;
    }

    @Override
    public void run() {

        try {
            while (!done) {
                escorredor.aguardarEspaco();

                if (done) {
                    break;
                }

                Prato prato = new Prato();
                LOGGER.fine(Thread.currentThread().getName() + " comecou a lavar o prato "
                        + prato.getNumeroSerie() + " (" + prato.getNivelSujeira() + ").");

                int limite;
                switch (prato.getNivelSujeira()) {
                    case BAIXO:
                        limite = 30_000_000;
                        break;
                    case MEDIO:
                        limite = 50_000_000;
                        break;
                    default:
                        limite = 100_000_000;
                }

                int contador = 0;
                for (int i = 0; i < limite; i++) {
                    if (i % 2 == 0) {
                        contador++;
                    }
                }


                LOGGER.fine("Contagem de lavados: " + contador);

                escorredor.colocar(prato);
                totalLavados++;
                
                LOGGER.fine(Thread.currentThread().getName() + " lavou o prato "
                        + prato.getNumeroSerie() + ".");
            }

        } catch (InterruptedException e) {
            if (!done) {
                LOGGER.warning(Thread.currentThread().getName() + " foi interrompido.");
                Thread.currentThread().interrupt();

            }
        }

        LOGGER.info(Thread.currentThread().getName() + " terminou.");
    }
}
