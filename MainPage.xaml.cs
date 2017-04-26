using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Collections.ObjectModel;
using System.IO;
using System.IO.IsolatedStorage;
using Microsoft.Phone.Controls;
using clWPwsConsultasCopaMundo.rwConsultasCopaMundo;

namespace clWPwsConsultasCopaMundo
{
    public partial class MainPage : PhoneApplicationPage
    {
        IwsConsultasCopaMundoClient wsCCM = new IwsConsultasCopaMundoClient();

        Campeonato[] c; //Almacenará la lista de campeonatos
        Grupo[] g; //Almacenará la lista de grupos de un campeonato
        TablaPosiciones[] t; //Almacenará la tabla de posiciones de un grupo

        bool sw = false; //para controlar doble ejecucion de los metodos de captura de eventos.

        // Constructor
        public MainPage()
        {
            InitializeComponent();

            //Solicita al servicio web la lista de campeonatos
            wsCCM.ObtenerCampeonatosAsync();

            //crea un capturador de evento para resivir la informacion desde el servicio web
            wsCCM.ObtenerCampeonatosCompleted += new EventHandler
        <rwConsultasCopaMundo.ObtenerCampeonatosCompletedEventArgs>(wsCCM_ObtenerCampeonatosCompleted);

            // se Crean las Columnas del Grid
            for (int i = 0; i < 8; i++)
            {
                gvPosiciones.ColumnDefinitions.Add(new ColumnDefinition());

                RowDefinition gridRow = new RowDefinition();
                gridRow.Height = new GridLength(45);
                gvPosiciones.RowDefinitions.Add(gridRow);

            }

        }

        //metodo que se ejecuta cuando se resive la lista de campeonatos desde el servidor
        private void wsCCM_ObtenerCampeonatosCompleted
    (object sender, rwConsultasCopaMundo.ObtenerCampeonatosCompletedEventArgs e)
        {
            //para controlar doble ejecucion del metodo
            if (!sw)
            {
                if (e.Result != null)
                {
                    c = e.Result.ToArray();
                    foreach (Campeonato item in c)
                    {
                        lbxCampeonato.Items.Add(item.NombreCampeonato);
                    }
                    sw = true;
                }
            }
            else
            {
                sw = false;
            }
        }

        private void lbxCampeonato_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            //se limpia el contenido de los controles
            if (g != null)
            {
                int fin = g.Length;
                for (int i = 0; i < g.Length; i++)
                {
                    lbxGrupo.Items.RemoveAt(0);
                }
                g = null;
            }
            //lbxGrupo = new ListBox();
            lbxGrupo.SelectedIndex = -1;
            gvPosiciones.Children.Clear();

            //se obtienen los grupos del campeonato
            if (lbxCampeonato.SelectedIndex != -1)
            {
                sw = false;
                wsCCM.ObtenerGruposAsync(c[lbxCampeonato.SelectedIndex].Id);
                wsCCM.ObtenerGruposCompleted += new EventHandler
            <rwConsultasCopaMundo.ObtenerGruposCompletedEventArgs>(wwsCCM_ObtenerGruposCompleted);
            }
        }

        private void wwsCCM_ObtenerGruposCompleted
    (object sender, rwConsultasCopaMundo.ObtenerGruposCompletedEventArgs e)
        {
            //para controlar doble ejecucion del metodo
            if (!sw)
            {
                //si no es nulo, agrega cada elemento a la lista de grupos
                if (e.Result != null)
                {
                    g = e.Result.ToArray();
                    //para comprobar que el listbox este vacio
                    if (lbxGrupo.Items.Count != 0)
                    {
                        int cl = lbxGrupo.Items.Count;
                        for (int i = 0; i < cl; i++)
                        {
                            lbxGrupo.Items.RemoveAt(0);
                        }
                    }
                    foreach (Grupo item in g)
                    {
                        lbxGrupo.Items.Add(item.NombreGrupo);
                    }
                    sw = true;
                }
            }
            else
            {
                sw = false;
            }
        }

        private void lbxGrupo_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            gvPosiciones.Children.Clear();
            if (lbxGrupo.SelectedIndex != -1)
            {
                sw = false;
                wsCCM.ObtenerPosicionesAsync(g[lbxGrupo.SelectedIndex].Id);
                wsCCM.ObtenerPosicionesCompleted += new EventHandler
            <rwConsultasCopaMundo.ObtenerPosicionesCompletedEventArgs>(wsCCM_ObtenerPosicionesCompleted);
            }
        }

        private void wsCCM_ObtenerPosicionesCompleted
    (object sender, rwConsultasCopaMundo.ObtenerPosicionesCompletedEventArgs e)
        {
            //para controlar doble ejecucion del metodo
            if (!sw)
            {
                if (e.Result != null)
                {
                    t = e.Result.ToArray();

                    // Add column headers to Grid
                    //se crea un vector con los encabezados y se agregan al grid.
                    string[] encabezados = { "Pais", "PJ", "PG", "PE", "PP", "GF", "GC", "Puntos" };
                    for (int i = 0; i < 8; i++)
                    {
                        TextBlock txtBlock = new TextBlock();
                        txtBlock.Text = encabezados[i];
                        txtBlock.FontSize = 17;
                        txtBlock.FontWeight = FontWeights.Bold;
                        txtBlock.Foreground = new SolidColorBrush(Colors.Green);
                        txtBlock.VerticalAlignment = VerticalAlignment.Top;
                        Grid.SetRow(txtBlock, 0);
                        Grid.SetColumn(txtBlock, i);

                        gvPosiciones.Children.Add(txtBlock);
                    }

                    // Fill Grid
                    int k = 1;
                    foreach (TablaPosiciones item in t)
                    {
                        //se crea un vector con cada campo del registro y se recorre agregando cada uno en
                        //orden segun las columnas de la fila.
                        string[] registro = { item.Pais, item.PJ.ToString(), item.PG.ToString(), item.PE.ToString(), 
                    item.PP.ToString(), item.GF.ToString(), item.GC.ToString(), item.Puntos.ToString() };
                        for (int i = 0; i < 8; i++)
                        {
                            TextBlock cellText = new TextBlock();
                            cellText.Text = registro[i];
                            cellText.FontSize = 13;
                            cellText.FontWeight = FontWeights.Bold;
                            cellText.TextWrapping = TextWrapping.Wrap;
                            Grid.SetRow(cellText, k);
                            Grid.SetColumn(cellText, i);
                            gvPosiciones.Children.Add(cellText);
                        }
                        k++;
                    }
                    sw = true;
                }
            }
            else
            {
                sw = false;
            }
        }

        private void btnImportar_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                //instancia el almacenamiento de usuario y lee el archivo.
                //Para que funcione, se debe cargar el archivo a la raiz del almacenamiento de usuario
                //con el siguiente comando se carga el archivo desde la carpeta en el disco fisico al emulador de WP7, con la aplicacion ejecutandose
                //C:\Program Files (x86)\Microsoft SDKs\Windows Phone\v7.1\Tools\IsolatedStorageExplorerTool\ISETool.exe rs deviceindex:1 6842a5ff-6a47-49fe-9a62-80d278cbfb54 c:\temp\files
                IsolatedStorageFile myFile = IsolatedStorageFile.GetUserStoreForApplication();
                StreamReader reader = new StreamReader(new IsolatedStorageFileStream("Importacion WORLD CUP 2014 Fase GRUPOS.txt", FileMode.Open, myFile));

                //string rawData = reader.ReadToEnd();

                int indice = 0;
                string line;
                string[] ls;
                List<GrupoPais> GruposPaises = new List<GrupoPais>();
                List<Encuentro> Encuentros = new List<Encuentro>();
                bool formatoValido = true, swiche = false;

                //crea una lista con las lineas del archivo
                while ((line = reader.ReadLine()) != null
                    && formatoValido)
                {
                    formatoValido = false;
                    //si la primera linea cumple el formato, se empieza a recorrer desde la siguiente linea
                    if (line == "Grupo;Pais")
                    {
                        swiche = false;
                        indice++;
                    }
                    //si la linea actual cumple el formato, se cambia el swiche y continua la siguiente linea agregando Encuentros
                    else if (line == "Fecha;Pais1;Goles1;Pais2;Goles2;Estadio;Ciudad")
                    {
                        swiche = true;
                        indice++;
                    }

                    //si en la primera ejecucuón 'indice == 0', significa que no ingresó a los condicionales, por tanto, el archivo no es valido
                    if (indice > 0)
                    {
                        //Swiche en falso para importar Grupos, y en verdadero para importar Encuentros
                        if (swiche == false)
                        {
                            //lineaSeparada = line.Split(new Char[] { ' ', ';', ',', '.', ':', '\t' });

                            ls = line.Split(new Char[] { ';' });

                            //verifica que la linea solo tenga 2 campos (IdGrupo y IdPais)
                            if (ls.Length == 2)
                            {
                                GrupoPais gp = new GrupoPais();
                                gp.Grupo = ls[0];
                                gp.Pais = ls[1];
                                GruposPaises.Add(gp);
                            }
                            else
                            {
                                MessageBox.Show("Error: FORMATO DE ARCHIVO INCORRECTO");
                                formatoValido = false;
                            }
                        }
                        else
                        {
                            ls = line.Split(new Char[] { ';' });

                            //verifica que la linea solo tenga 7 campos (Fecha, Pais1 Goles1 Pais2 Goles2 Estadio y Ciudad)
                            if (ls.Length == 7)
                            {
                                Encuentro ec = new Encuentro();
                                ec.Fecha = DateTime.Parse(ls[0]);
                                ec.Pais1 = ls[1];
                                ec.Goles1 = int.Parse(ls[2]);
                                ec.Pais2 = ls[3];
                                ec.Goles2 = int.Parse(ls[4]);
                                ec.Estadio = ls[5];
                                ec.Ciudad = ls[6];
                                Encuentros.Add(ec);
                            }
                            else
                            {
                                MessageBox.Show("Error: FORMATO DE ARCHIVO INCORRECTO");
                                formatoValido = false;
                            }
                        }
                    }
                    else
                    {
                        MessageBox.Show("Error: FORMATO DE ARCHIVO INCORRECTO");
                        formatoValido = false;
                    }
                }
                reader.Close();

                //envia al servicio web la lista con el contenido del archivo
                if (formatoValido)
                {

                    if (GruposPaises.Count > 0
                            && Encuentros.Count > 0)
                    {
                        MessageBox.Show("ERROR: El archivo no contiene grupos ni encuentros para importar");
                    }
                    else
                    {

                        ObservableCollection<GrupoPais> observableGruposPaises = new ObservableCollection<GrupoPais>(GruposPaises.ToArray());
                        ObservableCollection<Encuentro> observableEncuentros = new ObservableCollection<Encuentro>(Encuentros.ToArray());

                        sw = false;

                        wsCCM.ImportarGruposAsync(c[lbxCampeonato.SelectedIndex].Id, observableGruposPaises);
                        wsCCM.ImportarGruposCompleted += new EventHandler
                    <rwConsultasCopaMundo.ImportarGruposCompletedEventArgs>(wsCCM_ImportarGruposCompleted);

                        wsCCM.ImportarEncuentrosAsync(c[lbxCampeonato.SelectedIndex].Id, observableEncuentros);
                        wsCCM.ImportarEncuentrosCompleted += new EventHandler
                    <rwConsultasCopaMundo.ImportarEncuentrosCompletedEventArgs>(wsCCM_ImportarEncuentrosCompleted);

                        //se limpia el contenido de los controles
                        int fin = lbxGrupo.Items.Count;
                        for (int i = 0; i < fin; i++)
                        {
                            lbxGrupo.Items.RemoveAt(i);
                        }
                        //lbxGrupo = new ListBox();
                        lbxGrupo.SelectedIndex = -1;
                        gvPosiciones.Children.Clear();
                    }
                }
                else
                {
                    MessageBox.Show("error: archivo vacio");
                }

            }
            catch (Exception ee)
            {
                MessageBox.Show("Error: " + ee.InnerException.Message);
            }

        }

        private void wsCCM_ImportarGruposCompleted
    (object sender, rwConsultasCopaMundo.ImportarGruposCompletedEventArgs e)
        {
            //para evitar datos duplicados
            if (!sw)
            {
                bool resultado = e.Result;
                if (resultado)
                {
                    MessageBox.Show("GRUPOS importados con exito!!");
                }
                else
                {
                    MessageBox.Show("No se agregaron GRUPOS o ya existían");
                }
                sw = true;
            }
            else
            {
                sw = false;
            }
        }

        private void wsCCM_ImportarEncuentrosCompleted
    (object sender, rwConsultasCopaMundo.ImportarEncuentrosCompletedEventArgs e)
        {
            //para evitar datos duplicados
            if (!sw)
            {
                bool resultado = e.Result;
                if (resultado)
                {
                    MessageBox.Show("ENCUENTROS importados con exito!!");
                }
                else
                {
                    MessageBox.Show("No se importaron ENCUENTROS o ya existían");
                }
                sw = true;
            }
            else
            {
                sw = false;
            }
        }

    }
}