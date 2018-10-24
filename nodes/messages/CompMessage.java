package projects.comp.nodes.messages;

import sinalgo.nodes.messages.Message;

public class CompMessage extends Message {
    public int ID;
    public int d;

    public CompMessage(int ID, int d) {
        this.ID = ID;
        this.d = d;
    }

    public Message clone() {
        return new CompMessage(ID, d);
    }
}
