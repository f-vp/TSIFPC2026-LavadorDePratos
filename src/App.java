import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final int CAPACIDADE_ESCORREDOR = 5;

    private final Escorredor escorredor;
    private final Lavador[] lavadores;
    private final Enxugador[] enxugadores;

    private final Thread[] threadsLavadores;
    private final Thread[] threadsEnxugadores;

    public App(int quantidadeLavadores, int quantidadeEnxugadores) {
        if (quantidadeLavadores <= 0 || quantidadeEnxugadores <= 0) {
            throw new IllegalArgumentException("Informe pelo menos um lavador e um enxugador.");
        }

        escorredor = new Escorredor(CAPACIDADE_ESCORREDOR);

        lavadores = new Lavador[quantidadeLavadores];
        enxugadores = new Enxugador[quantidadeEnxugadores];

        threadsLavadores = new Thread[quantidadeLavadores];
        threadsEnxugadores = new Thread[quantidadeEnxugadores];

        for (int i = 0; i < quantidadeLavadores; i++) {
            lavadores[i] = new Lavador(escorredor);
            threadsLavadores[i] = new Thread(lavadores[i], "Lavador " + (i + 1));
        }

        for (int i = 0; i < quantidadeEnxugadores; i++) {
            enxugadores[i] = new Enxugador(escorredor);
            threadsEnxugadores[i] = new Thread(enxugadores[i], "Enxugador " + (i + 1));
        }
    }

    public void iniciar() {
        LOGGER.info("Iniciando lavadores e enxugadores.");

        for (Thread thread : threadsEnxugadores) {
            thread.start();
        }
        for (Thread thread : threadsLavadores) {
            thread.start();
        }
    }

    private boolean esperarTerminar(Thread[] threads) {
        boolean interrompido = false;

        for (Thread thread : threads) {

            while (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    interrompido = true;
                }
            }
        }

        return interrompido;
    }

    public void parar() {

        LOGGER.info("Solicitando o encerramento dos trabalhos.");

        for (Lavador lavador : lavadores) {
            lavador.parar();
        }
        for (Thread thread : threadsLavadores) {
            thread.interrupt();
        }
        boolean interrompido = esperarTerminar(threadsLavadores);


        escorredor.encerrarProducao();
        for (Enxugador enxugador : enxugadores) {
            enxugador.parar();
        }
        for (Thread thread : threadsEnxugadores) {
            thread.interrupt();
        }
        if (esperarTerminar(threadsEnxugadores)) {
            interrompido = true;
        }

        int lavados = 0;

        for (Lavador lavador : lavadores) {
            lavados += lavador.getTotalLavados();
        }

        int enxugados = 0;
        for (Enxugador enxugador : enxugadores) {
            enxugados += enxugador.getTotalEnxugados();
        }

        LOGGER.info("Pratos colocados no escorredor: " + lavados);
        LOGGER.info("Pratos enxugados: " + enxugados);
        LOGGER.info("Pratos restantes no escorredor: " + escorredor.getQuantidade());
        LOGGER.info("Terminou.");

        if (interrompido) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        int quantidadeLavadores = 2;
        int quantidadeEnxugadores = 2;
        int tempoSegundos = 60;

        try {
            if (args.length != 0 && args.length != 2 && args.length != 3) {
                LOGGER.warning("Parametros aceitos [lavadores enxugadores [segundos]]");
                return;
            }
            if (args.length >= 2) {
                quantidadeLavadores = Integer.parseInt(args[0]);
                quantidadeEnxugadores = Integer.parseInt(args[1]);
            }
            if (args.length == 3) {
                tempoSegundos = Integer.parseInt(args[2]);
            }
            
            if (tempoSegundos <= 0) {
                LOGGER.warning("O tempo deve ser maior que zero.");
                return;
            }

            App prog = new App(quantidadeLavadores, quantidadeEnxugadores);
            prog.iniciar();

            boolean interrompido = false;
            try {
                Thread.sleep(tempoSegundos * 1000L);
            } catch (InterruptedException e) {
                LOGGER.warning("O tempo de espera foi interrompido. Encerrando.");
                interrompido = true;
            }

            prog.parar();
            if (interrompido) {
                Thread.currentThread().interrupt();
            }
        } catch (NumberFormatException e) {

            LOGGER.warning("Informe numeros inteiros para lavadores, enxugadores e tempo.");
        } catch (IllegalArgumentException e) {
            LOGGER.warning(e.getMessage());
        }
    }
}
