package hoj2;

/*
 * Map on Mapdialog-luokan tarkoituksiin tehty olioluokka, joka sis�lt�� haettavan kartan
 * rajat ja kerrokset.
 * Se my�s hoitaa kartan rajojen siirtymisen.
 * 
 */


public class Map {
	/* Alustetaan muuttujat
	 * rajataan x ja y arvojoukko v�lille [-180, 180] ja [-90, 90]
	 * 
	 */
	private int[] coordinates = new int[4];
	private String layers;
	private final int limitX = 180;
	private final int limitXlow = -180;
	private final int limitY = 90;
	private final int limitYlow = -90;
	
	/* Konstruktori
	 * ALKUEHTO:
	 * layerit pit�� antaa stringin� pilkkujen erottamana, integerit eiv�t saa olla null
	 */
	public Map(int minX, int minY, int maxX, int maxY, String newlayers) {
		coordinates[0] = minX;
		coordinates[1] = minY;
		coordinates[2] = maxX;
		coordinates[3] = maxY;
		layers = newlayers;
	}
	/* Palautusfunktiot olion k�ytt�� varten.
	 * 
	 */
	public int[] giefCOORDINATES(){
		return coordinates;
	}
	
	public String giefLAYERS(){
		return layers;
	}
	/* Koordinaattien muutosmetodi. 
	 * Pit�� huolen siit�, ett� koordinaatit eiv�t ylit� kartan rajoja.
	 * Alkuehto: integerit eiv�t saa olla null
	 */
	public void changeCOORDINATE(int nminX, int nminY, int nmaxX, int nmaxY){
		 if(coordinates[0] + nminX < limitXlow){
			coordinates[2] += limitXlow - coordinates[0];
			coordinates[0] = limitXlow;
		}
		else if(coordinates[1] + nminY < limitYlow){
			coordinates[3] += limitYlow - coordinates[1];	
			coordinates[1] = limitYlow;
		}
		else if(coordinates[2] + nmaxX > limitX){
			coordinates[0] += limitX - coordinates[2];
			coordinates[2] = limitX;
		}
		else if(coordinates[3] + nmaxY > limitY){
			coordinates[1] += limitY - coordinates[3];
			coordinates[3] = limitY;
		} else {
			coordinates[0] += nminX;
			coordinates[1] += nminY;
			coordinates[2] += nmaxX;
			coordinates[3] += nmaxY;
		}
	}
	/* Muuttaa layerit GUIst� tehtyjen valintojen mukaiseksi.
	 */
	
	public void changeLAYERS(String nlayers){
		layers = nlayers;
	}
	
}
