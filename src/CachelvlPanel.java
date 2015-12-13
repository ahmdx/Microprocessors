import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class CachelvlPanel extends JPanel {
	
	JTextField textFieldS;
	JTextField textFieldM;
	JTextField textFieldAccessTime;
	JTextField textFieldWb;
	

	/**
	 * Create the panel.
	 */
	public CachelvlPanel() {
		setLayout(null);
		
		JLabel lblCacheLevel = new JLabel("New Cache Level");
		lblCacheLevel.setBounds(23, 11, 138, 14);
		add(lblCacheLevel);
		
		JLabel lblNewLabel = new JLabel("Full cache geometry");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(66, 50, 138, 14);
		add(lblNewLabel);
		
		JLabel lblS1 = new JLabel("S");
		lblS1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblS1.setBounds(276, 50, 20, 14);
		add(lblS1);
		
		textFieldS = new JTextField();
		textFieldS.setBounds(321, 47, 86, 20);
		add(textFieldS);
		textFieldS.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("M");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_2.setBounds(748, 50, 46, 14);
		add(lblNewLabel_2);
		
		textFieldM = new JTextField();
		textFieldM.setBounds(804, 47, 86, 20);
		add(textFieldM);
		textFieldM.setColumns(10);
		
		JLabel lblNumberOfCycles = new JLabel("Access time (in cycles)");
		lblNumberOfCycles.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNumberOfCycles.setBounds(66, 111, 263, 14);
		add(lblNumberOfCycles);
		
		textFieldAccessTime = new JTextField();
		textFieldAccessTime.setBounds(390, 108, 86, 20);
		add(textFieldAccessTime);
		textFieldAccessTime.setColumns(10);
		
		JLabel lblTheWritingPolicy = new JLabel("The writing policy (0 for WriteBack, 1 for WriteThrough)");
		lblTheWritingPolicy.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblTheWritingPolicy.setBounds(66, 78, 341, 14);
		add(lblTheWritingPolicy);
		
		textFieldWb = new JTextField();
		textFieldWb.setBounds(436, 75, 86, 20);
		add(textFieldWb);
		textFieldWb.setColumns(10);
		

	}
}
