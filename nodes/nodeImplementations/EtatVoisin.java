package projects.comp.nodes.nodeImplementations;

public class EtatVoisin {
    private int ID;
    private int lvl;

    EtatVoisin(int ID, int lvl){
        this.ID = ID;
        this.lvl = lvl;
    }

    public int getVoisinID() {
        return ID;
    }

    public int getVoisinLvl() {
        return lvl;
    }

    public void setVoisinLvl(int lvl) {
        this.lvl = lvl;
    }
}
