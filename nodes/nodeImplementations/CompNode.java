package projects.comp.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    public int lvl;

    /* Father ID */
    public int pere;

    private int value = (int) (Math.random() * 100);
    private boolean racine;

    public EtatVoisin etatVoisins[];

    public void preStep() {
    }

    public void init() {
        (new initTimer()).startRelative(1, this);
    }

    public int getPere() {
        return this.pere;
    }

    public void start() {
        this.racine = (this.ID == 1);


        this.lvl = this.ID;
        System.out.println(this.ID + ":" + this.lvl);

        if (this.nbVoisins() > 0) {
            etatVoisins = new EtatVoisin[this.nbVoisins()];
            for (int i = 0; i < this.nbVoisins(); i++) {
                this.etatVoisins[i] = new EtatVoisin(getVoisin(i), getVoisin(i));
                //System.out.println("ID:"+this.ID+"-> VoisinID:"+this.etatVoisins[i].getVoisinID()+" + VoisinLvl:"+this.etatVoisins[i].getVoisinLvl());
            }
        }
        (new waitTimer()).startRelative(20, this);
    }

    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof CompMessage) {
                CompMessage msg = (CompMessage) m;
                this.etatVoisins[this.getIndex(msg.ID)].setVoisinLvl(msg.d);
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

        String text = String.valueOf(this.ID+":"+this.lvl+"["+this.pere+"]");
        super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, Color.black);
    }

    public int getIndex(int ID) {
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

    private int getVoisin(int indice) {
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
        for (EtatVoisin e : etatVoisins) {
            min = Math.min(min, e.getVoisinLvl());
        }
        return min;
    }

    private boolean R0() {
        return this.lvl != nbNoeuds() && this.lvl != (etatVoisins[getIndex(pere)].getVoisinLvl() + 1) && etatVoisins[getIndex(pere)].getVoisinLvl() != nbNoeuds();
    }

    private boolean R1() {
        return this.lvl != nbNoeuds() && etatVoisins[getIndex(pere)].getVoisinLvl() == nbNoeuds();
    }

    private int R2() {
        int result = -1;
        for (EtatVoisin e : etatVoisins) {
            if (this.lvl == nbNoeuds() && e.getVoisinLvl() < (nbNoeuds() - 1)) {
                return e.getVoisinID();
            }
        }
        return result;
    }

    private void Actions() {
        if (this.racine) {
            this.pere = -1;
            this.lvl = 0;
        } else {
            if (this.R0()) {
                this.lvl = etatVoisins[getIndex(pere)].getVoisinLvl() + 1;
            }
            if (this.R1()) {
                this.lvl = nbNoeuds();
            }
            int idR2 = this.R2();
            if (idR2 != -1) {
                this.pere = this.etatVoisins[getIndex(idR2)].getVoisinID();
                this.lvl = this.etatVoisins[getIndex(idR2)].getVoisinLvl() + 1;
            }
        }
    }

    @Override
    public void neighborhoodChange() {

    }
}
