package Logica;

import Interfaz.movimientofichas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MiFicha extends JLabel implements MouseListener, MouseMotionListener /* , KeyListener */ {

    private final Dimension d = new Dimension(70, 112);
    private Point start_loc, start_drag, offset;
    private int parte_1, parte_2, angulo, posx, posy, numeroFicha,
            sentido;// 0 - Horizontal; 1- Vertical
    private static int obj;
    private static MiFicha fichaSeleccionada;
    private boolean drag, estado; //true - disponible; false - no disponible
    public static boolean disableDragPasa = false;

    public MiFicha(boolean estado, int parte1, int parte2) {
        drag = false;
        setBorder(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setSize(d);
        setPreferredSize(d);
        this.estado = estado;
        parte_1 = parte1;
        parte_2 = parte2;
        setText("");
        setIcon(new ImageIcon(getClass().getResource("/Fichas Domino/" + (parte_1) + "_" + (parte_2) + ".png")));
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        sentido = 1;
    }

    public void Rotar_Imagen() {
        ImageIcon img = new ImageIcon(getClass().getResource("/Fichas Domino/" + (parte_1) + "_" + (parte_2) + ".png"));
        int w = img.getIconWidth();
        int h = img.getIconHeight();
        angulo += 90;
        angulo %= 360;
        Dimension dms = new Dimension(w, h);;
        BufferedImage bi = null;
        Graphics2D bg = null;
        if (angulo == 90 || angulo == 270) {
            sentido = 0;
            bi = new BufferedImage(w + 40, h, BufferedImage.TYPE_INT_RGB);
            bg = bi.createGraphics();
            bg.rotate(Math.toRadians(angulo), (w + 40) / 2, h / 2);
            bg.drawImage(img.getImage(), 20, 0, w, h, null);
            bg.dispose();
            setIcon(new ImageIcon(bi));
            dms.setSize(h, w);
        } else {
            sentido = 1;
            if (angulo == 180) {
                bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                bg = bi.createGraphics();
                bg.rotate(Math.toRadians(angulo), w / 2, h / 2);
                bg.drawImage(img.getImage(), 0, 0, w, h, null);
                bg.dispose();
                setIcon(new ImageIcon(bi));
            } else {
                setIcon(img);
            }
        }
        setSize(dms);
        setPreferredSize(dms);
//        movimientofichas.lblAngulo.setText(String.valueOf(angulo));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (MiJuego.jugadorActual == MiJuego.turno && disableDragPasa == false) {
            if (estado == false) {
                if (getBorder() != null) {
                    if (angulo != 0) {
                        angulo = 270;
                        Rotar_Imagen(); //incrementa angulo a 360 y lo convierte a 0
                    }
                    setBorder(null);
                    fichaSeleccionada = null;
                    setObj(0);
                    movimientofichas.btngirar.setFocusPainted(false);
                    movimientofichas.btngirar.setEnabled(false);
                    movimientofichas.activarResaltado = true;
                } else {
                    movimientofichas.restablecerBorde();
                    movimientofichas.activarResaltado = false;
                    setBorder(BorderFactory.createLineBorder(new java.awt.Color(204, 0, 51), 2));
                    setObj(numeroFicha);
                    if (fichaSeleccionada != null) {
                        if (fichaSeleccionada.getAngulo() != 0) {
                            fichaSeleccionada.setAngulo(270);
                            fichaSeleccionada.Rotar_Imagen(); //incrementa angulo a 360 y lo convierte a 0
                        }
                        fichaSeleccionada.setBorder(null);
                    }
                    fichaSeleccionada = this;
                    if (parte_1 != parte_2) {
                        movimientofichas.btngirar.setFocusPainted(true);
                        movimientofichas.btngirar.setEnabled(true);
                        movimientofichas.btngirar.requestFocusInWindow();
                    } else {
                        movimientofichas.btngirar.setFocusPainted(false);
                        movimientofichas.btngirar.setEnabled(false);
                    }
                }
                movimientofichas.pnlJuegoDormino.repaint();
            } else {
                setBorder(null);
                obj = 0;
                if (fichaSeleccionada != null) {
                    if (fichaSeleccionada != this) {
                        if (fichaSeleccionada.getAngulo() != 0) {
                            fichaSeleccionada.setAngulo(270);
                            fichaSeleccionada.Rotar_Imagen(); //incrementa angulo a 360 y lo convierte a 0
                        }
                        fichaSeleccionada.setBorder(null);
                    }
                    fichaSeleccionada = null;
                }
                movimientofichas.btngirar.setFocusPainted(false);
                movimientofichas.btngirar.setEnabled(false);
            }
        }
    }

    private boolean validLocation(int cuadro) {
        boolean swl = false;
        int xe = 30;
        if ((getLocation().x >= movimientofichas.lblcuadro[cuadro].getLocation().x - xe
                && getLocation().x <= movimientofichas.lblcuadro[cuadro].getLocation().x
                + movimientofichas.lblcuadro[cuadro].getWidth() + xe)
                && (getLocation().y >= movimientofichas.lblcuadro[cuadro].getLocation().y - xe
                && getLocation().y <= movimientofichas.lblcuadro[cuadro].getLocation().y
                + movimientofichas.lblcuadro[cuadro].getHeight() + xe)) {
            swl = true;
        }
        return swl;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (MiJuego.jugadorActual == MiJuego.turno && disableDragPasa == false) {
            if (estado == false) {
                if (MiJuego.contador_fjugadas != 0) {
                    if (drag == true) {
                        if (validLocation(1) == true) {
                            estado = true;
                            mouseClicked(e);
                            MiJuego.fichaJugada(numeroFicha, this, angulo, 0);
                        } else {
                            if (validLocation(2) == true) {
                                estado = true;
                                mouseClicked(e);
                                MiJuego.fichaJugada(numeroFicha, this, angulo, 1);
                            } else {
                                setLocation(getPosx(), getPosy());
                            }
                        }
                    } else {
                        setLocation(getPosx(), getPosy());
                    }
                } else {
                    if (parte_1 == MiJuego.vecjug[MiJuego.turno][MiJuego.fichaInicial][0]
                            && parte_2 == MiJuego.vecjug[MiJuego.turno][MiJuego.fichaInicial][1]) {
                        estado = true;
                        mouseClicked(e);
                        MiJuego.fichaJugada(numeroFicha, this, angulo, -1);
                    } else {
                        setLocation(getPosx(), getPosy());
                    }
                }
            }
            movimientofichas.lblcuadro[1].setLocation(0, 0);
            movimientofichas.lblcuadro[1].setSize(new Dimension());
            movimientofichas.lblcuadro[1].setPreferredSize(new Dimension());
            movimientofichas.lblcuadro[1].setVisible(false);
//            movimientofichas.lblcuadro[1].setBounds(0, 0, 0, 0);
//            movimientofichas.lblcuadro[2].setBounds(0, 0, 0, 0);
            movimientofichas.lblcuadro[2].setLocation(0, 0);
            movimientofichas.lblcuadro[2].setSize(new Dimension());
            movimientofichas.lblcuadro[2].setPreferredSize(new Dimension());
            movimientofichas.lblcuadro[2].setVisible(false);
            drag = false;
            movimientofichas.activarResaltado = true;
        }
    }

    private void setCuadros(int cuad, int extr, int xd, int yd, int anch, int alt) {
        if (MiJuego.contador_fjugadas != 0) {
            movimientofichas.restablecerBorde();
            movimientofichas.activarResaltado = false;
            drag = true;
        }
        movimientofichas.lblcuadro[cuad].setLocation(MiJuego.mf[MiJuego.extremo[extr][0]].getLocation().x + xd,
                MiJuego.mf[MiJuego.extremo[extr][0]].getLocation().y + yd);
        movimientofichas.lblcuadro[cuad].setSize(new Dimension(anch, alt));
        movimientofichas.lblcuadro[cuad].setPreferredSize(new Dimension(anch, alt));
        movimientofichas.lblcuadro[cuad].setVisible(true);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (MiJuego.jugadorActual == MiJuego.turno && disableDragPasa == false) {
            if (estado == false) {
                Point current = getScreenLocation(e);
                offset = new Point((int) current.getX() - (int) start_drag.getX(), (int) current.getY() - (int) start_drag.getY());
                Point new_location = new Point((int) (start_loc.getX() + offset.getX()), (int) (start_loc.getY() + offset.getY()));
                setLocation(new_location);
                for (int i = 0; i <= 1; i++) {
                    int parteDisponible = -1;
                    switch (MiJuego.extremo[i][1]) {
                        case 1:
                            if (MiJuego.mf[MiJuego.extremo[i][0]].sentido == 0) {
                                if (MiJuego.mf[MiJuego.extremo[i][0]].angulo == 90) {
                                    parteDisponible = MiJuego.mf[MiJuego.extremo[i][0]].parte_1;
                                } else {
                                    parteDisponible = MiJuego.mf[MiJuego.extremo[i][0]].parte_2;
                                }
                                if (sentido == 0) {
                                    if (angulo == 90) {
                                        if (parteDisponible == parte_2) {
                                            setCuadros(2, i, 112, 0, 112, 70);
                                        }
                                    } else {
                                        if (parteDisponible == parte_1) {
                                            setCuadros(2, i, 112, 0, 112, 70);
                                        }
                                    }
                                } else {
                                    if (parteDisponible == parte_1 && parte_1 == parte_2) {   // cualquier parte
                                        setCuadros(2, i, 112, -21, 70, 112);
                                    }
                                }
                            } else {
                                parteDisponible = MiJuego.mf[MiJuego.extremo[i][0]].parte_1;    //cualquiera (ficha par)
                                if (sentido == 0) {
                                    if (angulo == 90) {
                                        if (parteDisponible == parte_2) {
                                            setCuadros(2, i, 70, 21, 112, 70);
                                        }
                                    } else {
                                        if (parteDisponible == parte_1) {
                                            setCuadros(2, i, 70, 21, 112, 70);
                                        }
                                    }
                                }
                            }
                            break;
                        case 2:
                            if (MiJuego.mf[MiJuego.extremo[i][0]].sentido == 0) {
                                if (MiJuego.mf[MiJuego.extremo[i][0]].angulo == 270) {
                                    parteDisponible = MiJuego.mf[MiJuego.extremo[i][0]].parte_1;
                                } else {
                                    parteDisponible = MiJuego.mf[MiJuego.extremo[i][0]].parte_2;
                                }
                                if (sentido == 0) {
                                    if (angulo == 270) {
                                        if (parteDisponible == parte_2) {
                                            setCuadros(1, i, -112, 0, 112, 70);
                                        }
                                    } else {
                                        if (parteDisponible == parte_1) {
                                            setCuadros(1, i, -112, 0, 112, 70);
                                        }
                                    }
                                } else {
                                    if (parteDisponible == parte_1 && parte_1 == parte_2) {   // cualquier parte
                                        setCuadros(1, i, -70, -21, 70, 112);
                                    }
                                }
                            } else {
                                parteDisponible = MiJuego.mf[MiJuego.extremo[i][0]].parte_2;    //ficha par
                                if (sentido == 0) {
                                    if (angulo == 270) {
                                        if (parteDisponible == parte_2) {
                                            setCuadros(1, i, -112, 21, 112, 70);
                                        }
                                    } else {
                                        if (parteDisponible == parte_1) {
                                            setCuadros(1, i, -112, 21, 112, 70);
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start_drag = getScreenLocation(e);
        start_loc = getLocation();
//        mouseDragged(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
//    
//    @Override
//    public void keyPressed(KeyEvent e) {
//        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//            mouseDragged(null);
//            if (movimientofichas.lblcuadro[1].isVisible() == true) {
//                setLocation(movimientofichas.lblcuadro[1].getLocation());
//                mouseReleased(null);
//            } else {
//                if (movimientofichas.lblcuadro[2].isVisible() == true) {
//                    setLocation(movimientofichas.lblcuadro[2].getLocation());
//                    mouseReleased(null);
//                }
//            }
//        }
//        if (angulo != 270 && (e.getKeyCode() == KeyEvent.VK_KP_LEFT || e.getKeyCode() == KeyEvent.VK_LEFT)) {
//            angulo = 180;
//            Rotar_Imagen();
//        }
//        if (angulo != 90 && e.getKeyCode() == KeyEvent.VK_KP_RIGHT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
//            angulo = 0;
//            Rotar_Imagen();
//        }
//        if (angulo != 180 && e.getKeyCode() == KeyEvent.VK_KP_DOWN || e.getKeyCode() == KeyEvent.VK_DOWN) {
//            angulo = 90;
//            Rotar_Imagen();
//        }
//        if (angulo != 0 && e.getKeyCode() == KeyEvent.VK_KP_UP || e.getKeyCode() == KeyEvent.VK_UP) {
//            angulo = 270;
//            Rotar_Imagen();
//        }
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e) {
//        
//    }
//    
//    @Override
//    public void keyReleased(KeyEvent e) {
//        
//    }
//                

    public int getSentido() {
        return sentido;
    }

    public void setSentido(int sentido) {
        this.sentido = sentido;
    }

    public int getPosx() {
        return posx;
    }

    public void setPosx(int posx) {
        this.posx = posx;
    }

    public int getPosy() {
        return posy;
    }

    public void setPosy(int posy) {
        this.posy = posy;
    }

    public int getAngulo() {
        return angulo;
    }

    public void setAngulo(int angulo) {
        this.angulo = angulo;
    }

    public int getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(int numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public static int getObj() {
        return obj;
    }

    public void setObj(int obj) {
        this.obj = obj;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Dimension getD() {
        return d;
    }

    public int getParte_1() {
        return parte_1;
    }

    public void setParte_1(int parte_1) {
        this.parte_1 = parte_1;
    }

    public int getParte_2() {
        return parte_2;
    }

    public void setParte_2(int parte_2) {
        this.parte_2 = parte_2;
    }

    private Point getScreenLocation(MouseEvent evt) {
        Point cursor = evt.getPoint();
        Point target_location = getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()),
                (int) (target_location.getY() + cursor.getY()));
    }
}
