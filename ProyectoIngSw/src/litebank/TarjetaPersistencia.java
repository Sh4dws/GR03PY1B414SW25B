package litebank;

import java.util.ArrayList;

public class TarjetaPersistencia {
    private final ArrayList<Tarjeta> datos = new ArrayList<>();

    public void guardar(Tarjeta t) {
        int idx=-1;
        for (int i=0;i<datos.size();i++) if (datos.get(i).getNumero().equals(t.getNumero())) { idx=i; break; }
        if (idx>=0) datos.set(idx, t); else datos.add(t);
    }

    public Tarjeta buscarPorNumero(String numero) {
        for (Tarjeta t: datos) if (t.getNumero().equals(numero)) return t;
        return null;
    }

    public ArrayList<Tarjeta> listarPorCuenta(String cuentaNumero) {
        ArrayList<Tarjeta> out = new ArrayList<>();
        for (Tarjeta t: datos) if (t.getCuentaNumero().equals(cuentaNumero)) out.add(t);
        return out;
    }
}
