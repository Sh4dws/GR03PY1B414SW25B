package litebank;

import java.util.ArrayList;

public class GestorSolicitudCuenta {
    private final GestorCliente gestorCliente;
    private final GestorCuenta gestorCuenta;
    private final ArrayList<SolicitudCuenta> solicitudes = new ArrayList<>();

    public GestorSolicitudCuenta(GestorCliente gestorCliente, GestorCuenta gestorCuenta) {
        this.gestorCliente = gestorCliente;
        this.gestorCuenta = gestorCuenta;
    }


    public SolicitudCuenta solicitar(String cedula, String nombreSiNuevo, String telefonoSiNuevo, String tipo) {

        Cliente existente = gestorCliente.buscarPorCedula(cedula);
        if (existente == null) {
            Cliente nuevo = Cliente.desdeFormulario(cedula, nombreSiNuevo, telefonoSiNuevo, "1234");
            gestorCliente.registrar(nuevo); 
        }

        SolicitudCuenta s = new SolicitudCuenta(cedula, tipo);
        solicitudes.add(s);
        return s;
    }
    
    public boolean procesar(SolicitudCuenta s, boolean aprobar) {
        if (s == null || !"PENDIENTE".equals(s.estado)) return false;

        if (!aprobar) {
            s.estado = "RECHAZADA";
            return true;
        }
        
        Cuenta c = gestorCuenta.abrirCuenta(s.cedula, s.tipo);
        s.estado = "APROBADA";
        s.cuentaCreada = c.getNumero();
        return true;
    }
    
    public SolicitudCuenta getPrimeraPendiente() {
        for (SolicitudCuenta s : solicitudes) {
            if ("PENDIENTE".equals(s.estado)) return s;
        }
        return null;
    }
    
    public ArrayList<SolicitudCuenta> listar() {
        return new ArrayList<>(solicitudes);
    }
}
