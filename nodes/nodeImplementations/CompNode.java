package projects.comp.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

import projects.comp.nodes.messages.CompMessage;
import projects.comp.nodes.timers.initTimer;
import projects.comp.nodes.timers.waitTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

public class CompNode extends Node {
    private int lvl;

    private int pere;

    private int value = (int) (Math.random() * 100);
    private boolean racine;

    private int Voisins[];

    public void preStep() {
    }

    public void init() {
        (new initTimer()).startRelative(1, this);
    }

    public int getPere() {
        return pere;
    }

    public void start() {
        if (this.ID == 1) {
            this.racine = true;
        } else {
            this.racine = false;
        }

        if (this.nbVoisins() > 0) {
            this.Voisins = new int[this.nbVoisins()];
            for (int i = 0; i < this.nbVoisins(); i++)
                this.Voisins[i] = CompNode.nbNoeuds();
        }
        (new waitTimer()).startRelative(20, this);
    }

    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof CompMessage) {
                CompMessage msg = (CompMessage) m;
                this.Voisins[this.getIndex(msg.ID)] = msg.d;
            }
        }
        this.Actions();
    }

    public void postStep() {
    }

    public void checkRequirements() throws
            WrongConfigurationException {
    }

    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
        this.setColor(Color.ORANGE);

        String text = String.valueOf(this.ID);
        super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, Color.black);
    }

    private int getIndex(int ID) {
        int j = 0;
        for (Edge e : this.outgoingConnections) {
            if (e.endNode.ID == ID)
                return j;
            j++;
        }
        return 0;
    }

    private static int nbNoeuds() {
        return Tools.getNodeList().size();
    }

    public int getVoisin(int indice) {
        if (indice >= this.nbVoisins() || indice < 0)
            return this.ID;
        Iterator<Edge> iter = this.outgoingConnections.iterator();
        for (int j = 0; j < indice; j++)
            iter.next();
        return iter.next().endNode.ID;
    }

    private int nbVoisins() {
        if (this.outgoingConnections == null) return 0;
        return this.outgoingConnections.size();
    }

    public void envoi() {
        this.broadcast(new CompMessage(this.ID, this.lvl));
        (new waitTimer()).startRelative(20, this);
    }

    private int minimum() {
        int min = CompNode.nbNoeuds();
        for (int lvl : Voisins) {
            min = Math.min(min, lvl);
        }
        return min;
    }

    private boolean R0() {
        return this.lvl != nbNoeuds() && this.lvl != (etatVoisins[pere].getVoisinLvl() + 1) && etatVoisins[pere].getVoisinLvl() != nbNoeuds();
    }

    private boolean R1() {
        return this.lvl != nbNoeuds() && etatVoisins[pere].getVoisinLvl() == nbNoeuds();
    }

    private  boolean R2() {
        boolean result = false;
        for (int v: Voisins) {
            if (this.lvl == nbNoeuds() && etatVoisins[v].getVoisinLvl() < (nbNoeuds() - 1)) {
                result = true;
            }
        }
        return result;
    }

    /*
    private boolean CD() {
        return this.lvl!= (minimum() + 1);
    }

    private boolean CP() {
        return minimum() == (this.lvl- 1) && Voisins[pere] != (this.lvl- 1);
    }
    */

    private void Actions() {
        if (this.racine) {
            this.pere = -1;
            this.lvl = 0;
        } else {
            if (this.CD())
                this.lvl = minimum() + 1;
            if (this.CP()) {
                for (int i = 0; i < this.nbVoisins(); i++) {
                    if (this.lvl - 1 == Voisins[i]) {
                        pere = i;
                    }
                }
            }
        }
    }

    @Override
    public void neighborhoodChange() {

    }
}
