
using System;
using System.Data;
public class Persona
{

    public int Id { get; set; }
    public String Nombres { get; set; }
    public String Apellidos { get; set; }
    public String Documento { get; set; }
    //public TipoDocumento TipoDocumentoPersona { get; set; }
    //public Ciudad CiudadPersona { get; set; }
    //public String Direccion { get; set; }
    //public String Telefono { get; set; }
    //public String Correo { get; set; }
    //public Boolean Empleado { get; set; }

    //Metodo Constructor
    public Persona()
    {
        Id = -1;
        Nombres = "";
        Apellidos = "";
        Documento = "";
        //TipoDocumentoPersona = null;
        //CiudadPersona = null;
        //Direccion = "";
        //Telefono = "";
        //Correo = "";
        //Empleado = false;
    }

    //Metodo Constructor
    public Persona(int Id,
                    String Nombres,
                    String Apellidos,
                    String Documento
        //int IdTipoDocumento, String NombreTipoDocumento, String Sigla,
        //int IdCiudad, String NombreCiudad, int IdPais, String NombrePais,
        //String Direccion,
        //String Telefono,
        //String Correo,
        //Boolean Empleado
                    )
    {
        this.Id = Id;
        this.Nombres = Nombres;
        this.Apellidos = Apellidos;
        this.Documento = Documento;
        //this.TipoDocumentoPersona = new TipoDocumento(IdTipoDocumento, NombreTipoDocumento, Sigla);
        //this.CiudadPersona = new Ciudad(IdCiudad, NombreCiudad, IdPais, NombrePais);
        //this.Direccion = Direccion;
        //this.Telefono = Telefono;
        //this.Correo = Correo;
        //this.Empleado = Empleado;
    }

    //Metodo que valida acceso
    public static Persona ValidarAcceso(BaseDatos bd,
                                        String Usuario,
                                        String Clave)
    {
        //Cadena de consulta
        String strSQL = "EXEC spValidarAcceso '" + Usuario +
                        "', '" + Clave + "'";

        try
        {
            //Ejecutar la consulta
            DataTable tbl = bd.Consultar(strSQL);

            //Si la consulta devuelve registros, el acceso es válido
            if (tbl != null && tbl.Rows.Count > 0)
            {
                //Obtener el registro
                DataRow dr = tbl.Rows[0];
                //Instanciar la Persona
                return new Persona(Utilidades.ObtenerEntero(dr, "Id"),
                                    Utilidades.ObtenerTexto(dr, "Nombres"),
                                    Utilidades.ObtenerTexto(dr, "Apellidos"),
                                    Utilidades.ObtenerTexto(dr, "Documento"));
            }
        }
        catch (Exception ex)
        {
            throw new ArgumentException("Error al verificar acceso:\n" + ex.Message);
        }
        return null;
    }//ValidarAcceso


    //Método que devuelve las cuentas de una persona
    public static Cuenta[] ObtenerCuentas(BaseDatos bd,
                                        int IdPersona)
    {
        //Definir la consulta
        String strSQL = "EXEC spListarCuentasPersona '" + IdPersona + "'";
        try
        {
            //Ejecutar la consulta
            DataTable tbl = bd.Consultar(strSQL);
            //Procesar los registros obtenidos
            if (tbl != null && tbl.Rows.Count > 0)
            {
                Cuenta[] c = new Cuenta[tbl.Rows.Count];
                for (int i = 0; i < tbl.Rows.Count; i++)
                {
                    DataRow dr = tbl.Rows[i];
                    c[i] = new Cuenta(Utilidades.ObtenerEntero(dr, "Id"),
                            Utilidades.ObtenerTexto(dr, "Numero"),
                            Utilidades.ObtenerFecha(dr, "FechaApertura"),
                            Utilidades.ObtenerEntero(dr, "IdSucursal"),
                            Utilidades.ObtenerTexto(dr, "Sucursal"),
                            Utilidades.ObtenerEntero(dr, "IdCiudad"),
                            Utilidades.ObtenerTexto(dr, "Ciudad"),
                            Utilidades.ObtenerEntero(dr, "IdTipoCuenta"),
                            Utilidades.ObtenerTexto(dr, "TipoCuenta")
                            );
                }
                return c;
            }
        }
        catch (Exception ex)
        {
            throw new ArgumentException("Error al consultar Cuentas\n" + ex.Message);
        }
        return null;
    }
}

