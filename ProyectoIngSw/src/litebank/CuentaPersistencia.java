package litebank;

import java.util.ArrayList;

public class CuentaPersistencia {
    private final ArrayList<Cuenta> datos = new ArrayList<>();

    public void guardar(Cuenta c) {
        int idx=-1;
        for (int i=0;i<datos.size();i++) if (datos.get(i).getNumero().equals(c.getNumero())) { idx=i; break; }
        if (idx>=0) datos.set(idx, c); else datos.add(c);
    }

    public Cuenta buscarPorNumero(String numero) {
        for (Cuenta c: datos) if (c.getNumero().equals(numero)) return c;
        return null;
    }

    public ArrayList<Cuenta> listarPorCedula(String cedula) {
        ArrayList<Cuenta> out = new ArrayList<>();
        for (Cuenta c: datos) if (c.getCedulaCliente().equals(cedula)) out.add(c);
        return out;
    }
}
