package projects.comp.nodes.messages;

import sinalgo.nodes.messages.Message;

public class CompMessage extends Message {
    public int ID;
    public int lvl;
    public int max;
    public int pere;
    public int sortie;

    public CompMessage(int ID, int lvl, int max, int pere, int sortie) {
        this.ID = ID;
        this.lvl = lvl;
        this.max = max;
        this.pere = pere;
        this.sortie = sortie;
    }

    public Message clone() {
        return new CompMessage(ID, lvl, max, pere, sortie);
    }
}
