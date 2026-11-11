import java.util.Random;

public class Prato {

    private static int ultimoNumero = 0;

    private final int numeroSerie;
    private final NivelSujeira nivelSujeira;

    public Prato() {
        numeroSerie = proximoNumero();

        Random random = new Random();
        
        int sorteio = random.nextInt(10);

        if (sorteio < 3) {
            nivelSujeira = NivelSujeira.BAIXO;
        } else if (sorteio < 9) {
            nivelSujeira = NivelSujeira.MEDIO;
        } else {
            nivelSujeira = NivelSujeira.ENGORDURADO;
        }
    }

    private static synchronized int proximoNumero() {

        ultimoNumero++;
        return ultimoNumero;
    }

    public int getNumeroSerie() {
        return numeroSerie;
    }

    public NivelSujeira getNivelSujeira() {
        return nivelSujeira;
    }
}
