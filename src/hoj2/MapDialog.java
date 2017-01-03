package hoj2;

// Kartankatseluohjelman graafinen k�ytt�liittym�

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class MapDialog extends JFrame {

	// K�ytt�liittym�n komponentit

	private JLabel imageLabel = new JLabel();
	private JPanel leftPanel = new JPanel();

	private JButton refreshB = new JButton("P�ivit�");
	private JButton leftB = new JButton("<");
	private JButton rightB = new JButton(">");
	private JButton upB = new JButton("^");
	private JButton downB = new JButton("v");
	private JButton zoomInB = new JButton("+");
	private JButton zoomOutB = new JButton("-");
	private Map mape;
	public MapDialog() throws Exception {

		// Valmistele ikkuna ja lis�� siihen komponentit

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		
		
		// ALLA OLEVAN TESTIRIVIN VOI KORVATA JOLLAKIN MUULLA ERI ALOITUSN�KYM�N
		// LATAAVALLA RIVILL�
		imageLabel.setIcon(new ImageIcon(new URL("http://demo.mapserver.org/cgi-bin/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&BBOX=145,-45,150,-40&SRS=EPSG:4326&WIDTH=480&HEIGHT=480&LAYERS=bluemarble,country_bounds,continents,cities&STYLES=&FORMAT=image/png&TRANSPARENT=true")));
		
		// Alustetaan aloitusn�kym�� vastaava Map-olio.
		
		mape = new Map(-10, 25, 55, 90,"bluemarble,country_bounds,continents,cities");
		
		add(imageLabel, BorderLayout.EAST);
		
		ButtonListener bl = new ButtonListener();
		refreshB.addActionListener(bl);  
		leftB.addActionListener(bl);
		rightB.addActionListener(bl);
		upB.addActionListener(bl);
		downB.addActionListener(bl);
		zoomInB.addActionListener(bl);
		zoomOutB.addActionListener(bl);

		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		leftPanel.setMaximumSize(new Dimension(100, 600));

		for (Layer l : XMLParser.parse()){
			leftPanel.add(new LayerCheckBox(l.name, l.title, true));
		}

		leftPanel.add(refreshB);
		leftPanel.add(Box.createVerticalStrut(20));
		leftPanel.add(leftB);
		leftPanel.add(rightB);
		leftPanel.add(upB);
		leftPanel.add(downB);
		leftPanel.add(zoomInB);
		leftPanel.add(zoomOutB);

		add(leftPanel, BorderLayout.WEST);

		pack();
		setVisible(true);

	}

	public static void main(String[] args) throws Exception {
		new MapDialog();
	}

	// Kontrollinappien kuuntelija
	// KAIKKIEN NAPPIEN YHTEYDESS� VOINEE HY�DYNT�� updateImage()-METODIA
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == refreshB) {
				try {
					updateImage(mape);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (e.getSource() == leftB) {
				try {
					updateImage(mape, "left");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (e.getSource() == rightB) {
				try {
					updateImage(mape, "right");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (e.getSource() == upB) {
				try {
					updateImage(mape, "up");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (e.getSource() == downB) {
				try {
					updateImage(mape, "down");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (e.getSource() == zoomInB) {
				try {
					updateImage(mape, "zoomin");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
			if(e.getSource() == zoomOutB) {
				try {
					updateImage(mape, "zoomout");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	// Valintalaatikko, joka muistaa karttakerroksen nimen
	private class LayerCheckBox extends JCheckBox {
		private String name = "";
		public LayerCheckBox(String name, String title, boolean selected) {
			super(title, null, selected);
			this.name = name;
		}
		public String getName() { return name; }
	}

	// Tarkastetaan mitk� karttakerrokset on valittu,
	// tehd��n uudesta karttakuvasta pyynt� palvelimelle ja p�ivitet��n kuva
	public void updateImage(Map orig) throws Exception {
		String s = "";

		// Tutkitaan, mitk� valintalaatikot on valittu, ja
		// ker�t��n s:��n pilkulla erotettu lista valittujen kerrosten
		// nimist� (k�ytet��n haettaessa uutta kuvaa)
		Component[] components = leftPanel.getComponents();
		for(Component com:components) {
			if(com instanceof LayerCheckBox)
				if(((LayerCheckBox)com).isSelected()) s = s + com.getName() + ",";
		}
		if (s.endsWith(",")) s = s.substring(0, s.length() - 1);
		orig.changeLAYERS(s);
		fetchImage(orig);
	}
	
	// updateImage on overloadattu kartan siirtelemist� ja zoomausta varten. Ottaa toisena parametrina halutun toiminnnan.
	
	public void updateImage(Map orig, String action) throws Exception {
		switch (action) {
		case "left":
			orig.changeCOORDINATE(-5, 0, -5, 0);
			break;
		case "right":
			orig.changeCOORDINATE(5, 0, 5, 0);
			break;
		case "up":
			orig.changeCOORDINATE(0, 5, 0, 5);
			break;
		case "down":
			orig.changeCOORDINATE(0, -5, 0, -5);
			break;
		case "zoomout":
			orig.changeCOORDINATE(-5, -5, 5, 5);
			break;
		case "zoomin":
			orig.changeCOORDINATE(5, 5, -5, -5);
			break;
		}
		fetchImage(orig);
	}
	
	// Apumetodi, joka generoi 	URL-osoitteen Map-olion tietojen perusteella ja palauttaa kyseisen URLin.
	
	public URL generateURL(Map map) throws Exception {
		int[] coordinates = map.giefCOORDINATES();
		String layers = map.giefLAYERS();
		URL url = new URL("http://demo.mapserver.org/cgi-bin/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&BBOX=" + coordinates[0] + "," + coordinates[1] + ","  + coordinates[2] + ","  + coordinates[3] + "&SRS=EPSG:4326&WIDTH=480&HEIGHT=480&LAYERS=" + layers + "&STYLES=&FORMAT=image/png&TRANSPARENT=true");
		return url;
		
	}
	
	// Apumetodi, joka hoitaa kuvan hakemisen palvelimelta erillisess� s�ikeess�.
	
	public void fetchImage(final Map map) throws Exception {
		new Thread(){
			public void run(){
				System.err.println("fetching image..");
				try {
					System.err.println(generateURL(map).toString());
					imageLabel.setIcon(new ImageIcon(generateURL(map)));
				} catch (Exception e) {
					System.out.println(e);
				}
				System.err.println("done.");
			}
		}.start();
	}

} // MapDialog
