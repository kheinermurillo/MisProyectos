using System;
using System.ComponentModel;
using System.Data;
using System.Text;

public class Cuenta
{
    public int Id { get; set; }
    public String Numero { get; set; }
    public DateTime FechaApertura { get; set; }
    public Sucursal SucursalCuenta { get; set; }
    public TipoCuenta TipoCuentaCuenta { get; set; }

    //Metodo Constructor
    public Cuenta()
    {
        Id = -1;
        Numero = "";
        FechaApertura = DateTime.MinValue;
        SucursalCuenta = null;
        TipoCuentaCuenta = null;
    }

    //Metodo Constructor
    public Cuenta(int Id, String Numero, DateTime FechaApertura,
                    int IdSucursal, String NombreSucursal,
                    int IdCiudad, String NombreCiudad,
                    int IdTipoCuenta, String TipoCuenta)
    {
        this.Id = Id;
        this.Numero = Numero;
        this.FechaApertura = FechaApertura;
        this.SucursalCuenta = new Sucursal(IdSucursal, NombreSucursal, IdCiudad, NombreCiudad);
        this.TipoCuentaCuenta = new TipoCuenta(IdTipoCuenta, TipoCuenta);
    }


    //Metodo que devuelve el contenido del objeto
    public override string ToString()
    {
        return Numero + " (" + SucursalCuenta.ToString() + ")";
    }//ToString



    //Metodo que duevuelve el saldo de una cuenta
    public static double ObtenerSaldo(BaseDatos bd, int IdCuenta)
    {
        //Definir la consulta
        String strSQL = "SELECT dbo.fCalcularSaldoCuenta(" + IdCuenta + ") AS Saldo";
        try
        {
            //Ejecutar la consulta
            DataTable tbl = bd.Consultar(strSQL);
            //Procesar los registros obtenidos
            if (tbl != null && tbl.Rows.Count > 0)
            {
                return Utilidades.ObtenerReal(tbl.Rows[0], "Saldo");
            }
        }
        catch (Exception ex)
        {
            throw new ArgumentException("Error al consultar Cuentas\n" + ex.Message);
        }
        return 0;
    }

    //Metodo que obtiene la lista registros
    public static Movimiento[] ObtenerMovimientos(BaseDatos bd, int IdCuenta)
    {
        //Definir la consulta
        String strSQL = "EXEC spListarMovimientosCuenta '" + IdCuenta + "'";
        try
        {
            //Ejecutar la consulta
            DataTable tbl = bd.Consultar(strSQL);
            //Procesar los registros obtenidos
            if (tbl != null && tbl.Rows.Count > 0)
            {
                Movimiento[] m = new Movimiento[tbl.Rows.Count];
                for (int i = 0; i < tbl.Rows.Count; i++)
                {
                    DataRow dr = tbl.Rows[i];
                    m[i] = new Movimiento(
                            Utilidades.ObtenerEntero(dr, "Id"),
                            Utilidades.ObtenerEntero(dr, "IdSucursal"),
                            Utilidades.ObtenerTexto(dr, "Sucursal"),
                            Utilidades.ObtenerEntero(dr, "IdCiudad"),
                            Utilidades.ObtenerTexto(dr, "Ciudad"),
                            Utilidades.ObtenerEntero(dr, "IdTipoMovimiento"),
                            Utilidades.ObtenerTexto(dr, "TipoMovimiento"),
                            Utilidades.ObtenerFecha(dr, "Fecha"),
                            Utilidades.ObtenerReal(dr, "Valor"),
                            Utilidades.ObtenerTexto(dr, "Descripcion")
                            );
                }
                return m;
            }
        }
        catch (Exception ex)
        {
            throw new ArgumentException("Error al consultar Movimientos\n" + ex.Message);
        }
        return null;
    }//ObtenerMovimientos

}