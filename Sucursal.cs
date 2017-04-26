using System;
using System.ComponentModel;
using System.Data;
using System.Text;

public class Sucursal
{
    public int Id { get; set; }
    public String NombreSucursal { get; set; }
    public Ciudad CiudadSucursal { get; set; }
    public TipoSucursal TipoSucursalSucursal { get; set; }
    public String Direccion { get; set; }
    public String Telefono { get; set; }

    //Metodo Constructor
    public Sucursal()
    {
        Id = -1;
        NombreSucursal = "";
        CiudadSucursal = null;
        TipoSucursalSucursal = null;
        Direccion = "";
        Telefono = "";
    }

    //Metodo Constructor
    public Sucursal(int Id, String NombreSucursal, int IdCiudad, String NombreCiudad)
    {
        this.Id = Id;
        this.NombreSucursal = NombreSucursal;
        this.CiudadSucursal = new Ciudad(IdCiudad, NombreCiudad);
        TipoSucursalSucursal = null;
        Direccion = "";
        Telefono = "";
    }


    //Metodo que devuelve el contenido del objeto
    public override string ToString()
    {
        return NombreSucursal + " (" + CiudadSucursal.ToString() + ")";
    }//ToString

}