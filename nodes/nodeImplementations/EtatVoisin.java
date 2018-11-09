package projects.comp.nodes.nodeImplementations;

public class EtatVoisin {
	private int ID;
	private int lvl;
	private int max;
	private int sortie;
	private boolean isfils;

	EtatVoisin(int ID) {
		this.ID = ID;
		this.lvl = (int) (Math.random() * (CompNode.nbNoeuds() + 1));
		this.max = (int) (Math.random() * 100);
		/*
		 * We get randomly a number between 0 and 1, if this number is 1 then
		 * the neighbors is considered as the node son
		 */
		this.isfils = ((int) (Math.random() * 2)) == 1;
	}

	public int getVoisinID() {
		return ID;
	}

	public int getVoisinLvl() {
		return lvl;
	}

	public int getVoisinMax() {
		return max;
	}

	public boolean getVoisinIsFils() {
		return isfils;
	}
	
	public int getVoisinSortie() {
		return sortie;
	}

	public void setVoisinLvl(int lvl) {
		this.lvl = lvl;
	}

	public void setVoisinIsFils(boolean bool) {
		this.isfils = bool;
	}

	public void updateValues(int lvl, int max, boolean b, int sortie) {
		this.lvl = lvl;
		this.max = max;
		this.isfils = b;
		this.sortie = sortie;
	}
}
