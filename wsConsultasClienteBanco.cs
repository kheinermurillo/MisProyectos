using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;

/// <summary>
/// Descripción breve de wsConsultasClienteBanco
/// </summary>
[WebService(Namespace = "http://tempuri.org/")]
[WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
// Para permitir que se llame a este servicio web desde un script, usando ASP.NET AJAX, quite la marca de comentario de la línea siguiente. 
// [System.Web.Script.Services.ScriptService]
public class wsConsultasClienteBanco : System.Web.Services.WebService {

    public wsConsultasClienteBanco () {

        //Elimine la marca de comentario de la línea siguiente si utiliza los componentes diseñados 
        //InitializeComponent(); 
    }

    [WebMethod]
    public Persona ValidarAcceso(String Usuario,
                                        String Clave)
    {
        return Persona.ValidarAcceso(ConectarBD.Conectar(),
                                        Usuario, Clave);
    }

    [WebMethod]
    public Cuenta[] ObtenerCuentas(int IdPersona)
    {
        return Persona.ObtenerCuentas(ConectarBD.Conectar(), 
                                        IdPersona);
    }

    [WebMethod]
    public double ObtenerSaldos(int IdCuenta)
    {
        return Cuenta.ObtenerSaldo(ConectarBD.Conectar(),
                                        IdCuenta);
    }

    [WebMethod]
    public Movimiento[] ObtenerMovimientos(int IdCuenta)
    {
        return Cuenta.ObtenerMovimientos(ConectarBD.Conectar(),
                                        IdCuenta);
    }
    
}
