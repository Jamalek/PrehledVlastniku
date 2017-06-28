import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GUI extends JFrame{
	public static JTextPane textPane;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GUI().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public GUI() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Vyberte soubor pro vytvo�en� p�ehledu");
		setLayout(new BorderLayout());
		int width = 600;
		int height = 460;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-width/2, dim.height/2-height/2);
		setSize(width, height);
		UIManager.put("FileChooser.acceptAllFileFilterText", "V�echny Soubory");
		UIManager.put("FileChooser.cancelButtonText", "Zav��t");
		UIManager.put("FileChooser.cancelButtonToolTipText", "Zav�e okno a ukon�� program");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Detaily");
		UIManager.put("FileChooser.fileNameHeaderText", "N�zev souboru:");
		UIManager.put("FileChooser.fileNameLabelText", "N�zev souboru:");
		UIManager.put("FileChooser.filesOfTypeLabelText", "Soubory typu:");
		UIManager.put("FileChooser.listViewButtonToolTipText", "Seznam");
		UIManager.put("FileChooser.lookInLabelText", "Adres�t:");
		UIManager.put("FileChooser.newFolderErrorText", "Chyba ve vytvo�en� slo�ky");
		UIManager.put("FileChooser.newFolderToolTipText", "Vytvo�it novou slo�ku");
		UIManager.put("FileChooser.openButtonText", "Vybrat");
		UIManager.put("FileChooser.openButtonToolTipText", "Vybere soubor pro konverzi");
		UIManager.put("FileChooser.upFolderToolTipText", "Do adres��e o �rove� v��");
		final JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File(new File("W:\\NYMBURK\\KP�").getCanonicalPath()));
		jfc.setFileFilter(new FileNameExtensionFilter("Excel", "xls", "csv"));
		jfc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) {
				if (action.getActionCommand().equalsIgnoreCase("ApproveSelection")) {
					try {
						VymeraLV_2_0.zkonvertuj(jfc.getSelectedFile().getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.exit(0);
				}
			}
		});
		add(jfc, BorderLayout.CENTER);
		textPane = new JTextPane();
		textPane.setText("Program vytvo�il Jan M�lek. E-mail: 2janmalek@gmail.com\nAktu�ln� verzi naleznete na adrese: prehledvlastniku.xf.cz");
		textPane.setEditable(false);
		add(textPane, BorderLayout.PAGE_END);
	}

}
