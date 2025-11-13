package litebank;

public class SolicitudCuenta {
    public String cedula;
    public String tipo;       
    public String estado;     
    public String cuentaCreada; 

    public SolicitudCuenta(String cedula, String tipo) {
        this.cedula = cedula;
        this.tipo = tipo;
        this.estado = "PENDIENTE";
        this.cuentaCreada = null;
    }
}
