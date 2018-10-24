package projects.comp.nodes.timers;

import projects.comp.nodes.nodeImplementations.CompNode;
import sinalgo.nodes.timers.Timer;

public class initTimer extends Timer {
    public void fire() {
        CompNode n = (CompNode) this.node;
        n.start();
    }
}
