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
    /* If node is root, racine == true */
    private boolean racine;

    /* The node level */
    private int lvl;

    /* Father ID */
    private int pere;

    /* Node's value e */
    private int e = (int) (Math.random() * 100);

    /* Max (in e values) of the node's sub tree */
    private int max_subtree = e;

    /* Array of the Neighbors state (ID, lvl, Value, son) */
    private EtatVoisin etatVoisins[];

    /* Output of the algorithm */
    private int sortie;

    public boolean getRacine() {
        return this.racine;
    }

    public int getLvl() {
        return this.lvl;
    }

    public int getPere() {
        return this.pere;
    }

    public EtatVoisin getEtatVoisins(int i) {
        if (i >= 0 && i < nbVoisins()) {
            return this.etatVoisins[i];
        }
        return null;
    }

    public int getMax_SubTree() {
        return this.max_subtree;
    }

    public int getSortie() {
        return this.sortie;
    }

    public void preStep() {
    }

    public void init() {
        (new initTimer()).startRelative(1, this);
    }

    public void start() {

        /* The node with ID == 1 will be considered as root */
        this.racine = (this.ID == 1);

        /* Init it lvl at a random value between 0 and nbNoeuds */
        this.lvl = (int) (Math.random() * (CompNode.nbNoeuds() + 1));

        /* Init the array of the neighbors state if the node has neighbors */
        if (this.nbVoisins() > 0) {
            etatVoisins = new EtatVoisin[this.nbVoisins()];
            for (int i = 0; i < this.nbVoisins(); i++) {
                this.etatVoisins[i] = new EtatVoisin(getVoisin(i));
            }
        }

        /* Get a pere randomly in the neighbors */
        this.pere = this.etatVoisins[(int) (Math.random() * nbVoisins())].getVoisinID();

        (new waitTimer()).startRelative(20, this);
    }

    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof CompMessage) {
                CompMessage msg = (CompMessage) m;
                int neighbors_ID = this.getIndex(msg.ID);
                // Update the state of the neighbor with the value sent
                this.etatVoisins[neighbors_ID].updateValues(msg.lvl, msg.max, msg.pere == this.ID, msg.sortie);
            }
        }
        this.Actions();
    }

    public void postStep() {
    }

    public void checkRequirements() throws WrongConfigurationException {
    }

    private Color Couleur(int v) {
        Color[] tabColor = new Color[9];
        tabColor[0] = new Color(109, 0, 42);
        tabColor[1] = new Color(161, 0, 42);
        tabColor[2] = new Color(194, 32, 34);
        tabColor[3] = new Color(219, 79, 48);
        tabColor[4] = new Color(228, 140, 67);
        tabColor[5] = new Color(235, 176, 84);
        tabColor[6] = new Color(244, 216, 124);
        tabColor[7] = new Color(250, 236, 162);
        tabColor[8] = new Color(255, 255, 205);
        return tabColor[v % 9];
    }

    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
        this.setColor(this.Couleur(this.lvl));
        String text = String.valueOf(
                this.max_subtree + ":" + this.sortie);
        super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, Color.black);
    }

    public String toString() {
        return "LVL:" + this.lvl + "; VALUE:" + this.e;
    }

    static int nbNoeuds() {
        return Tools.getNodeList().size();
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

    private int getVoisin(int indice) {
        if (indice >= this.nbVoisins() || indice < 0)
            return this.ID;
        Iterator<Edge> iter = this.outgoingConnections.iterator();
        for (int j = 0; j < indice; j++)
            iter.next();
        return iter.next().endNode.ID;
    }

    private int nbVoisins() {
        if (this.outgoingConnections == null)
            return 0;
        return this.outgoingConnections.size();
    }

    public void envoi() {
        this.broadcast(new CompMessage(this.ID, this.lvl, this.max_subtree, this.pere, this.sortie));
        (new waitTimer()).startRelative(20, this);
    }

    public int maximumValue() {
        int max = e;
        for (EtatVoisin e : this.etatVoisins) {
            if (e.getVoisinIsFils()) {
                max = Math.max(max, e.getVoisinMax());
            }
        }
        return max;
    }

    private boolean R0() {
        return this.lvl != nbNoeuds() && this.lvl != (this.etatVoisins[getIndex(this.pere)].getVoisinLvl() + 1)
                && etatVoisins[getIndex(pere)].getVoisinLvl() != nbNoeuds();
    }

    private boolean R1() {
        return this.lvl != nbNoeuds() && this.etatVoisins[getIndex(this.pere)].getVoisinLvl() == nbNoeuds();
    }

    private int R2() {
        int result = -1;
        for (EtatVoisin e : this.etatVoisins) {
            if (this.lvl == nbNoeuds() && e.getVoisinLvl() < (nbNoeuds() - 1)) {
                return e.getVoisinID();
            }
        }
        return result;
    }

    private boolean MSA() {
        return maximumValue() != this.max_subtree;
    }

    private boolean Max() {
        if (this.racine) {
            return this.max_subtree != this.sortie;
        }
        return this.sortie != this.etatVoisins[getIndex(pere)].getVoisinSortie();
    }

    private void Actions() {
        if (this.racine) {
            this.pere = -1;
            this.lvl = 0;
            if (MSA()) {
                this.max_subtree = maximumValue();
            } else if (Max()) {
                this.sortie = this.max_subtree;
            }
        } else {
            if (this.R0()) {
                this.lvl = this.etatVoisins[getIndex(this.pere)].getVoisinLvl() + 1;
            } else if (this.R1()) {
                this.lvl = nbNoeuds();
            } else {
                int idR2 = this.R2();
                if (idR2 != -1) {
                    this.pere = this.etatVoisins[getIndex(idR2)].getVoisinID();
                    this.lvl = this.etatVoisins[getIndex(idR2)].getVoisinLvl() + 1;
                } else if (MSA()) {
                    this.max_subtree = maximumValue();
                } else if (Max()) {
                    this.sortie = this.etatVoisins[getIndex(this.pere)].getVoisinSortie();
                }
            }
        }
    }

    @Override
    public void neighborhoodChange() {

    }
}
