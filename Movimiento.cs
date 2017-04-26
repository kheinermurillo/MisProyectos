using System;
using System.Data;
using System.Text;

public class Movimiento
{
    public int Id { get; set; }
    public Sucursal SucursalMovimiento { get; set; }
    public TipoMovimiento TipoMovimientoMovimiento { get; set; }
    public DateTime Fecha { get; set; }
    public double Valor { get; set; }
    public String Descripcion { get; set; }

    //Metodo Constructor
    public Movimiento()
    {
        this.Id = -1;
        this.SucursalMovimiento = null;
        this.TipoMovimientoMovimiento = null;
        this.Fecha = DateTime.MinValue;
        this.Valor = 0.0;
        this.Descripcion = "";
    }


    //Metodo Constructor
    public Movimiento(int Id, int IdSucursal, String NombreSucursal, int IdCiudad, String NombreCiudad,
                    int IdTipoMovimiento, String TipoMovimiento, DateTime Fecha,
                    double Valor, String Descripcion)
    {
        this.Id = Id;
        this.SucursalMovimiento = new Sucursal(IdSucursal, NombreSucursal, IdCiudad, NombreCiudad);
        this.TipoMovimientoMovimiento = new TipoMovimiento(IdTipoMovimiento, TipoMovimiento);
        this.Fecha = Fecha;
        this.Valor = Valor;
        this.Descripcion = Descripcion;
    }

}

