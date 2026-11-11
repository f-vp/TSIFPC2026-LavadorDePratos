import java.util.logging.Logger;

public class Escorredor {

    private static final Logger LOGGER = Logger.getLogger(Escorredor.class.getName());

    private final Prato[] pratos;
    private int inicio;
    private int fim;
    private int quantidade;
    private boolean producaoEncerrada;

    
    private final Object esperaLavador = new Object();
    private final Object esperaEnxugador = new Object();

    public Escorredor(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade deve ser maior que 0.");
        }

        pratos = new Prato[capacidade];
        inicio = 0;
        fim = 0;
        quantidade = 0;

        LOGGER.info("Escorredor vazio: 0 pratos.");
    }

    
    public void aguardarEspaco() throws InterruptedException {
        synchronized (esperaLavador) {
            while (true) {
                synchronized (this) {
                    if (quantidade < pratos.length) {
                        return;
                    }
                }
                LOGGER.fine("Lavador aguardando espaco no escorredor.");
                esperaLavador.wait();
            }
        }
    }

    public void colocar(Prato prato) throws InterruptedException {
        boolean estavaVazio;

        synchronized (esperaLavador) {
            while (true) {
                synchronized (this) {
                    if (quantidade < pratos.length) {
                        estavaVazio = quantidade == 0;
                        pratos[fim] = prato;
                        fim = (fim + 1) % pratos.length;
                        quantidade++;
                        LOGGER.fine("Prato " + prato.getNumeroSerie() + " colocado no escorredor.");

                        if (quantidade == pratos.length) {
                            LOGGER.info("Escorredor cheio: " + quantidade + " pratos. Ultimo colocado: "
                                    + prato.getNumeroSerie() + ".");
                        }
                        break;
                    }
                }
                LOGGER.fine("Escorredor cheio, lavador aguardando para colocar o prato.");
                esperaLavador.wait();
            }
        }

        if (estavaVazio) {
            synchronized (esperaEnxugador) {
                esperaEnxugador.notify();
            }
        }
    }

    public Prato retirar() throws InterruptedException {
        Prato prato;
        boolean estavaCheio;

        synchronized (esperaEnxugador) {
            while (true) {
                synchronized (this) {
                    if (quantidade > 0) {
                        estavaCheio = quantidade == pratos.length;
                        prato = pratos[inicio];
                        pratos[inicio] = null;
                        inicio = (inicio + 1) % pratos.length;
                        quantidade--;
                        LOGGER.fine("Prato " + prato.getNumeroSerie() + " retirado do escorredor.");

                        if (quantidade == 0) {
                            LOGGER.info("Escorredor vazio: " + quantidade + " pratos. Ultimo retirado: "
                                    + prato.getNumeroSerie() + ".");
                        }
                        break;
                    }

                    if (producaoEncerrada) {
                        return null;
                    }
                }
                LOGGER.fine("Enxugador aguardando pratos no escorredor.");

                esperaEnxugador.wait();
            }
        }

        if (estavaCheio) {
            synchronized (esperaLavador) {
                esperaLavador.notify();
            }
        }

        return prato;
    }

    public synchronized void encerrarProducao() {
        producaoEncerrada = true;
    }

    public synchronized int getQuantidade() {
        return quantidade;
    }
}
