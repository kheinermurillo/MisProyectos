package Logica;

import Interfaz.Ppal_panel;
import Interfaz.movimientofichas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class MiJuego {

    private int x, y, turJugPar, contador_fichas_disponibles, cantidad_jugadores,
            matfichas[][], mayorFichaPar[], xvista, contaPasan, turJugPasa, numFicPasan[];
    public static MiFicha mf[], mfPanJug[];
    public static int turno, extremo[][], contador_fjugadas, jugadorActual,
            vecjug[][][], contador_fichas_jugador[], fichaInicial, puntajes[], cont;
    public static Logica.Jugador player[];
    private static boolean vistaExtremo, verext, sw, todosPasan;
    public static JFrame domino;

    public MiJuego(JFrame juego) {
        initVariables();
        String nom = null;
        int idyFilaInsert[] = new int[2];
        for (int i = 1; i <= cantidad_jugadores; i++) {
            nom = movimientofichas.getNombresJugadores(i);
            player[i] = new Jugador();
            player[i].setNombre(nom);
            idyFilaInsert = Jugador.insertarJugador(nom);   //inserta y retorna numeroFilasInsertadas y Id Jugador
            if (idyFilaInsert[0] == 0) {
                JOptionPane.showMessageDialog(null, "No se pudo guardar el jugador " + i);
            } else {
                player[i].setIdent(idyFilaInsert[1]);
            }
        }
        int c = 1, dc = 0;
        for (int i = 0; i <= 6; i++) {
            for (int j = 0 + dc; j <= 6; j++) {
                matfichas[c][0] = i;
                matfichas[c][1] = j;
                c++;
            }
            dc++;
        }
        domino = juego;
        repartirFichas();
        turnoInicial();
        llenarPanelFichasJugador();
    }

    public static void fichaJugada(int poJ, MiFicha esta, int angul, int extrm) {
        movimientofichas.restablecerBorde();
        puntajes[jugadorActual] = puntajes[jugadorActual] - (esta.getParte_1() + esta.getParte_2());
        contador_fjugadas++;
        mf[contador_fjugadas] = esta;
        fichaJugadaNoDisp(poJ);
        if (puntajes[jugadorActual] == 0 && contador_fichas_jugador[jugadorActual] == 0) {
            mf[contador_fjugadas].setLocation(movimientofichas.lblcuadro[extrm + 1].getLocation());
            int acump = 0;
            for (int i = 1; i <= movimientofichas.getCantidadJugadores(); i++) {
                if (i != jugadorActual) {
                    acump += puntajes[i];
                }
            }
            for (int i = 1; i <= movimientofichas.cantidadJugadores; i++) {
                System.out.println(player[i].getNombre()+" "+puntajes[i]);
            }
            puntajes[jugadorActual] = ( acump * movimientofichas.getCantidadJugadores() ) + 50;
            player[jugadorActual].actualizarpuntaje(player[jugadorActual].getIdent(), puntajes[jugadorActual]);
            JOptionPane.showMessageDialog(null, "Jugador: \"" + player[jugadorActual].getNombre() + "\" HAS GANADO!", "FELICITACIONES!", 1);
            Ppal_panel ppal = new Ppal_panel();
            movimientofichas.hilo.suspend();
            movimientofichas.issuspended = true;
            movimientofichas.ds = movimientofichas.seg = movimientofichas.min = movimientofichas.hora = 0;
            movimientofichas.tiempos0();
            domino.dispose();
            ppal.setVisible(true);
            cont = 0;
            sw = true;
        } else {
            if (contador_fjugadas == 1) {
                mf[contador_fjugadas].setLocation((movimientofichas.pnlJuegoDormino.getWidth() - mf[contador_fjugadas].getWidth()) / 2,
                        (movimientofichas.pnlJuegoDormino.getHeight() - mf[contador_fjugadas].getHeight() / 2) - 500);
                extremo[0][0] = contador_fjugadas;    //posicion de la ficha en el vector
                extremo[1][0] = contador_fjugadas;
                extremo[0][1] = 2;    //Lado jugado(no disponible): 1. Izquierdas, 2. Derecha
                extremo[1][1] = 1;
            } else {
                mf[contador_fjugadas].setLocation(movimientofichas.lblcuadro[extrm + 1].getLocation());
                extremo[extrm][0] = contador_fjugadas;
            }
            mf[contador_fjugadas].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            turno++;
            if (turno == movimientofichas.cantidadJugadores + 1) {
                turno = 1;
            }
            redimensionPanel(mf[contador_fjugadas]);
            movimientofichas.btnCambExtrem.setEnabled(false);
            movimientofichas.btnFinTurno.setFocusPainted(true);
            movimientofichas.btnFinTurno.setEnabled(true);
            movimientofichas.btnFinTurno.requestFocusInWindow();
            movimientofichas.pnlJuegoDormino.repaint();
        }
    }

    private void repartirFichas() {
        int ran = 0, l = 0;    //numero aleatorio entre fichas disponibles
        Random rd = new Random();
        l = rd.nextInt(cantidad_jugadores - 1 + 1) + 1;  //*(N-M+1)+M) Valor entre M y N, incluidos.
        vecjug[l][1][0] = 6;
        vecjug[l][1][1] = 6;
        mayorFicha(matfichas[28][0], matfichas[28][1], l, 1);
        fichaRepartidaNoDisp(28);
        for (int k = 1; k <= cantidad_jugadores; k++) {
            for (int i = 1; i <= 7; i++) {
                if (vecjug[k][i][0] == -1) {    //cualquiera es -1 al inicio; y porque ya asigno par6
                    ran = rd.nextInt(contador_fichas_disponibles - 1 + 1) + 1;
                    vecjug[k][i][0] = matfichas[ran][0];
                    vecjug[k][i][1] = matfichas[ran][1];
                    mayorFicha(matfichas[ran][0], matfichas[ran][1], k, i);
                    fichaRepartidaNoDisp(ran);
                }
            }
        }
    }

    private void fichaRepartidaNoDisp(int posVF) {
        if (posVF != contador_fichas_disponibles) {
            for (int i = posVF; i < contador_fichas_disponibles; i++) {
                matfichas[i][0] = matfichas[i + 1][0];
                matfichas[i][1] = matfichas[i + 1][1];
            }
        }
        contador_fichas_disponibles--;
    }

    public static void fichaJugadaNoDisp(int poJ) {
        if (poJ != contador_fichas_jugador[turno]) {
            for (int i = poJ; i < contador_fichas_jugador[turno]; i++) {
                vecjug[turno][i][0] = vecjug[turno][i + 1][0];
                vecjug[turno][i][1] = vecjug[turno][i + 1][1];
            }
        }
        contador_fichas_jugador[turno]--;
    }

    private void mayorFicha(int parte1, int parte2, int jugR, int posiMay) {
        if (parte1 == parte2 && parte1 > mayorFichaPar[0]) {
            mayorFichaPar[0] = parte1;
            mayorFichaPar[1] = posiMay;
            turJugPar = jugR;
        }
    }

    private void turnoInicial() {
        turno = turJugPar;
        jugadorActual = turno;
        fichaInicial = mayorFichaPar[1];
        movimientofichas.lblJugador.setText(movimientofichas.nombresJugadores[turno]);
    }

    private void llenarPanelFichasJugador() {
        jugadorActual = turno;
        cont++;
        int contFJ = contador_fichas_jugador[turno];
        for (int i = 1; i <= contFJ; i++) {
            Nuev_Fich(vecjug[turno][i][0], vecjug[turno][i][1], i);
            if (sw == true) {
                puntajes[jugadorActual] = puntajes[jugadorActual] + (vecjug[turno][i][0] + vecjug[turno][i][1]);
            }
        }
        if (cont == cantidad_jugadores) {
            sw = false;
        }
        movimientofichas.lblPuntaje.setText(String.valueOf(puntajes[jugadorActual]) + " puntos");
        movimientofichas.lblnomJugador.setText(movimientofichas.nombresJugadores[turno]);
        movimientofichas.lblPuntaje.setHorizontalTextPosition(SwingConstants.CENTER);
        movimientofichas.lblPuntaje.setVerticalTextPosition(SwingConstants.CENTER);
        movimientofichas.lblPuntaje.setHorizontalAlignment(SwingConstants.CENTER);
        movimientofichas.lblPuntaje.setVerticalAlignment(SwingConstants.CENTER);
        movimientofichas.lblnomJugador.setHorizontalTextPosition(SwingConstants.CENTER);
        movimientofichas.lblnomJugador.setVerticalTextPosition(SwingConstants.CENTER);
        movimientofichas.lblnomJugador.setHorizontalAlignment(SwingConstants.CENTER);
        movimientofichas.lblnomJugador.setVerticalAlignment(SwingConstants.CENTER);
        movimientofichas.pnlJuegoDormino.repaint();
        movimientofichas.btnFinTurno.setFocusPainted(false);
        movimientofichas.btnFinTurno.setEnabled(false);
    }

    public void TerminarTurno() {
        movimientofichas.restablecerBorde();
        movimientofichas.calculartiempo();
        movimientofichas.setTiempos(movimientofichas.getTiempos(jugadorActual) + movimientofichas.getTiempototal(), jugadorActual);
        player[jugadorActual].actualizartiempo(player[jugadorActual].getIdent(), movimientofichas.getTiempos(jugadorActual));
        MiFicha.disableDragPasa = false;
        if (todosPasan == false) {
            for (int i = 1; i <= 21; i++) {
                if (mfPanJug[i] != null) {
                    if (mfPanJug[i].getEstado() == false) {
                        movimientofichas.pnlJuegoDormino.remove(mfPanJug[i]);
                    }
                } else {
                    break;
                }
            }
            movimientofichas.btngirar.setFocusPainted(false);
            movimientofichas.btngirar.setEnabled(false);
            movimientofichas.lblJugador.setText(movimientofichas.nombresJugadores[turno]);
            movimientofichas.lblJugador.setHorizontalTextPosition(SwingConstants.CENTER);
            movimientofichas.lblJugador.setVerticalTextPosition(SwingConstants.CENTER);
            movimientofichas.lblJugador.setHorizontalAlignment(SwingConstants.CENTER);
            movimientofichas.lblJugador.setVerticalAlignment(SwingConstants.CENTER);

            if (verext == true) {
                movimientofichas.btnCambExtrem.setEnabled(true);
                movimientofichas.btnCambExtrem.requestFocusInWindow();
            }
            movimientofichas.pnlJuegoDormino.repaint();
            player[jugadorActual].actualizarpuntaje(player[jugadorActual].getIdent(), puntajes[jugadorActual]);
            if (puntajes[jugadorActual] != 0 || contador_fichas_jugador[jugadorActual] > 0) {
                llenarPanelFichasJugador();
            }
            if (contador_fjugadas != 0) {
                valiPasar();
            }
        } else {
            int i, menor = 99999999, acumpunt = 0, aux = 0;
            for (i = 1; i <= cantidad_jugadores; i++) {
                if (puntajes[i] < menor) {
                    menor = puntajes[i];
                    aux = i;
                }
            }
            for (int j = 1; j <= cantidad_jugadores; j++) {
                if (j != aux) {
                    acumpunt += puntajes[j] - puntajes[aux];
                }
            }
            for (i = 1; i <= cantidad_jugadores; i++) {
                System.out.println(player[i].getNombre()+" "+puntajes[i]);
            }
            if(puntajes[aux]!=0)
            {
                puntajes[aux] = (puntajes[aux] * cantidad_jugadores) + acumpunt;
            }
            else
            {
                puntajes[aux] = acumpunt * cantidad_jugadores;
            }            
            player[aux].actualizarpuntaje(player[aux].getIdent(), puntajes[aux]);
            JOptionPane.showMessageDialog(null, "Jugador: \"" + player[aux].getNombre() + "\" HAS GANADO!", "FELICITACIONES!", 1);
            Ppal_panel ppal = new Ppal_panel();
            movimientofichas.hilo.suspend();
            movimientofichas.issuspended = true;
            movimientofichas.ds = movimientofichas.seg = movimientofichas.min = movimientofichas.hora = 0;
            movimientofichas.tiempos0();
            domino.dispose();
            ppal.setVisible(true);
            cont = 0;
            sw = true;
        }
    }

    public void Pasar() {
        contaPasan++;
        if (contaPasan == 1) {
            turJugPasa = jugadorActual;
            for (int i = 1; i <= cantidad_jugadores; i++) {
                numFicPasan[i] = contador_fichas_jugador[i];
            }
        } else {
            turJugPasa++;
            if (turJugPasa == cantidad_jugadores + 1) {
                turJugPasa = 1;
            }
            if (turJugPasa == jugadorActual
                    && contador_fichas_jugador[jugadorActual] == numFicPasan[turJugPasa]) {
                if (contaPasan == cantidad_jugadores) {
                    todosPasan = true;
                }
            } else {
                contaPasan = 1;
                turJugPasa = jugadorActual;
                for (int i = 1; i <= cantidad_jugadores; i++) {
                    numFicPasan[i] = contador_fichas_jugador[i];
                }
            }
        }
        System.out.println("pasa "+contaPasan+": "+movimientofichas.nombresJugadores[jugadorActual]);
        turno++;
        if (turno == cantidad_jugadores + 1) {
            turno = 1;
        }
        TerminarTurno();
    }

    private void valiPasar() {
        int extr1 = 0, extr2 = 0;
        if (mf[extremo[0][0]].getSentido() == 0) {
            if (mf[extremo[0][0]].getAngulo() == 270) {
                extr1 = mf[extremo[0][0]].getParte_1();
            } else {
                extr1 = mf[extremo[0][0]].getParte_2();
            }
        } else {
            extr1 = mf[extremo[0][0]].getParte_1();
        }
        if (mf[extremo[1][0]].getSentido() == 0) {
            if (mf[extremo[1][0]].getAngulo() == 90) {
                extr2 = mf[extremo[1][0]].getParte_1();
            } else {
                extr2 = mf[extremo[1][0]].getParte_2();
            }
        } else {
            extr2 = mf[extremo[1][0]].getParte_1();
        }
        int[] vecextr = {extr1, extr2};
        boolean band = false;
        for (int i = 1; i <= contador_fichas_jugador[turno]; i++) {
            for (int j = 0; j < 2; j++) {
                if (vecjug[turno][i][0] == vecextr[j] || vecjug[turno][i][1] == vecextr[j]) {
                    band = true;
                }
            }
        }
        if (band == true) {
            movimientofichas.btnPasarTurno.setFocusPainted(false);
            movimientofichas.btnPasarTurno.setEnabled(false);
        } else {
            movimientofichas.btnPasarTurno.setFocusPainted(true);
            movimientofichas.btnPasarTurno.setEnabled(true);
            movimientofichas.btnPasarTurno.requestFocusInWindow();
            MiFicha.disableDragPasa = true;
        }
    }

    private void Nuev_Fich(int parte1, int parte2, int cont) {
        MiFicha tmp = new MiFicha(false, parte1, parte2);
        if (cont == 1) {
            if (vistaExtremo == true) {
                x = xvista;
            } else {
                x = 20;
            }
            tmp.setLocation((x), y);
        } else {
            x += 115;
            tmp.setLocation((x), y);
        }
        tmp.setPosx(tmp.getLocation().x);
        tmp.setPosy(tmp.getLocation().y);
//        redimensionPanel(2, tmp);   //derecha
        tmp.setNumeroFicha(cont);
        mfPanJug[cont] = tmp;
        movimientofichas.pnlJuegoDormino.add(mfPanJug[cont]);
        movimientofichas.pnlJuegoDormino.repaint();
    }

    private static void redimensionPanel(MiFicha tmp) {
        int incremento = 220, tope = 220;
        Dimension dm = null, oldm = null;
        if (tmp.getLocation().x < tope) {
            Point xy = null;
            dm = new Dimension();
            oldm = movimientofichas.pnlJuegoDormino.getSize();
            dm.setSize(oldm.getWidth() + incremento, oldm.height);
            movimientofichas.pnlJuegoDormino.setSize(dm);
            movimientofichas.pnlJuegoDormino.setPreferredSize(dm);
            for (int i = 1; i <= contador_fjugadas; i++) {
                xy = mf[i].getLocation();
                mf[i].setLocation(xy.x + incremento, xy.y);
            }
            verext = true;
        } else {
            tope = movimientofichas.pnlJuegoDormino.getWidth() - tope;
            if (tmp.getLocation().x > tope) {
                dm = new Dimension();
                oldm = movimientofichas.pnlJuegoDormino.getSize();
                dm.setSize(oldm.getWidth() + incremento, oldm.height);
                movimientofichas.pnlJuegoDormino.setSize(dm);
                movimientofichas.pnlJuegoDormino.setPreferredSize(dm);
                verext = true;
            }
        }
    }

    public static void reiniciarpuntajes(int cant) {
        for (int i = 1; i <= cant; i++) {
            player[i].actualizarpuntaje(player[i].getIdent(), 0);
        }
    }

    public static void reiniciartiempo(int cant) {
        for (int i = 1; i <= cant; i++) {
            player[i].actualizartiempo(player[i].getIdent(), 0);
        }
    }

    public void cambiarentreExtremos() {
        if (vistaExtremo == false) {
            int vi = 0;
            xvista = mf[extremo[1][0]].getLocation().x;
            if (xvista > (movimientofichas.anchoInicialPanel - 220)) {
                xvista = movimientofichas.pnlJuegoDormino.getWidth() - 840;
                vi = movimientofichas.pnlJuegoDormino.getWidth() - movimientofichas.anchoInicialPanel;
                Point vex = new Point(vi, movimientofichas.ScrollPane1.getViewport().getViewPosition().y);
                movimientofichas.ScrollPane1.getViewport().setViewPosition(vex);
                vistaExtremo = true;
            }
        } else {
            vistaExtremo = false;
            movimientofichas.ScrollPane1.getViewport().setViewPosition(
                    new Point(0, movimientofichas.ScrollPane1.getViewport().getViewPosition().y));
        }
        TerminarTurno();
    }

    public void Rotar_Imagen() {
        if (jugadorActual == turno) {
            mfPanJug[MiFicha.getObj()].Rotar_Imagen();
            movimientofichas.pnlJuegoDormino.repaint();
        }
    }

    public int rndNum(int value) {
        int num = (int) Math.floor(Math.random() * value + 1);
        return num;
    }

    public static void setCont() {
        MiJuego.cont = 0;
    }

    public static boolean isSw() {
        return sw;
    }

    public static void setSw(boolean sw) {
        MiJuego.sw = sw;
    }

    public static void setPuntajes0(int cant) {
        for (int i = 1; i <= cant; i++) {
            puntajes[cant] = 0;
        }
    }

    private void initVariables() {
        x = 20;
        y = movimientofichas.pnlJuegoDormino.getHeight() - 150;
        cantidad_jugadores = movimientofichas.getCantidadJugadores();
        player = new Jugador[cantidad_jugadores + 1];
        vecjug = new int[cantidad_jugadores + 1][29][2];
        for (int k = 1; k <= cantidad_jugadores; k++) {
            for (int i = 1; i <= 7; i++) {
                vecjug[k][i][0] = -1;
                vecjug[k][i][1] = -1;
            }
        }
        puntajes = new int[cantidad_jugadores + 1];
        mf = new MiFicha[29];
        mfPanJug = new MiFicha[29];
        matfichas = new int[29][2];
        contador_fichas_disponibles = 28;
        contador_fjugadas = 0;
        contador_fichas_jugador = new int[5];
        for (int i = 1; i <= cantidad_jugadores; i++) {
            contador_fichas_jugador[i] = 7;
        }
        turno = 0;
        jugadorActual = 0;
        turJugPar = 0;
        mayorFichaPar = new int[2];
        mayorFichaPar[0] = -1;
        mayorFichaPar[1] = 0;
        extremo = new int[2][2];
        fichaInicial = 0;
        vistaExtremo = false;
        xvista = 0;
        verext = false;
        sw = true;
        cont = 0;
        todosPasan = false;
        contaPasan = 0;
        turJugPasa = 0;
        numFicPasan = new int[cantidad_jugadores + 1];
    }
}
