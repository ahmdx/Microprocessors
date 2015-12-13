import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class gui {

	private JFrame frame;
	
	private ArrayList<CacheLevel> cacheLevels = new ArrayList<CacheLevel>();
	private ArrayList<String> code;
	private int numCacheLevels;
	private JPanel memory_hierarchy;
	private int cacheLevelsNumber;
	private boolean second_cache = true;
	private int[] S;
	private int[] m;
	int[] cycles;
	private int L;
	private int memoryCycles;
	private MemoryHierarchy M;
	private CacheWriteHitPolicy[] cacheWriteHitPolicy;
	private int[] maxInstrs = new int[11];;
	int[] numCycles = new int[11];;
	private int pipelineWidth, insturctionBuffer;
	private int ROBsize;
	private Processor p;
	private ProgramParser programParser ;
	private static int PCAfterLastInstruction;
	private JComboBox comboBox_1;
	private JTextField textFieldPipelineWidth;
	private JTextField textFieldInstructionBufferSize;
	private JTextField textFieldCachelvlsNumber;
	private JTextField textFieldRS;
	private JTextField textFieldC;
	private JTextField textFieldRob;
	
	private HardwareOrganization h = new HardwareOrganization();
	private JTextField textFieldCyclesNumberOutput;
	private JTextField textFieldIPC;
	private JTextField textFieldAMAT;
	private JTextField textFieldBranchMisPercentage;
	private JTextField textFieldHitRatio;
	private JTextField textFieldL;
	private JTextField textFieldnocMainMemory;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(150, 30, 1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		frame.setTitle("Superscalar out-of-order architectural simulator application");
		
		memory_hierarchy = new JPanel();
		//memory_hierarchy.setPreferredSize(new Dimension(1000, 700));
		//memory_hierarchy.setPreferredSize(memory_hierarchy.getPreferredSize());
		frame.getContentPane().add(memory_hierarchy, "name_408695752159576");
		//memory_hierarchy.setPreferredSize(new Dimension(1000, 700));
		memory_hierarchy.setLayout(null);
				
		
		JLabel lblMemoryHierarchy = new JLabel("Memory Hierarchy :");
		lblMemoryHierarchy.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblMemoryHierarchy.setBounds(10, 11, 159, 55);
		memory_hierarchy.add(lblMemoryHierarchy);
		
		JLabel lblCacheLevels = new JLabel("Number of cache levels");
		lblCacheLevels.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCacheLevels.setBounds(119, 52, 189, 14);
		memory_hierarchy.add(lblCacheLevels);
		memory_hierarchy.setVisible(false);
		
		textFieldCachelvlsNumber = new JTextField();
		textFieldCachelvlsNumber.setBounds(147, 77, 86, 20);
		memory_hierarchy.add(textFieldCachelvlsNumber);
		textFieldCachelvlsNumber.setColumns(10);
		
		JButton btnNewButtonDone = new JButton("Done");
		btnNewButtonDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//JOptionPane.showMessageDialog(null, "My Goodness, this is so concise");
				programParser = new ProgramParser();
				numCacheLevels = Math.min(3, Integer.parseInt(textFieldCachelvlsNumber.getText()));
				cacheLevelsNumber = Integer.parseInt(textFieldCachelvlsNumber.getText());
				
	            S = new int[Math.min(3, cacheLevelsNumber)];
	            m = new int[Math.min(3, cacheLevelsNumber)];
	            cacheWriteHitPolicy = new CacheWriteHitPolicy[Math.min(3, cacheLevelsNumber)];
	            cycles = new int[Math.min(3, cacheLevelsNumber)];
	            L = Integer.parseInt(textFieldL.getText());
	            memoryCycles = Integer.parseInt(textFieldnocMainMemory.getText());
	            
	            
	            /*CachelvlPanel pa = new CachelvlPanel();
				pa.setBounds(10, 272, 964, 140);
				memory_hierarchy.add(pa);
				//memory_hierarchy.setPreferredSize(new Dimension(1000,y+190));
				memory_hierarchy.validate();
				memory_hierarchy.repaint();*/
	            
	            /*CachelvlPanel p = new CachelvlPanel();
				p.setBounds(10, 412, 964, 190);
				memory_hierarchy.add(p);
				//memory_hierarchy.setPreferredSize(new Dimension(1000,y+190));
				memory_hierarchy.validate();
				memory_hierarchy.repaint();*/
	            
				//System.out.println(cacheNumber);
				int y = 132;
				int i;
				for(i =0; i<numCacheLevels; i++){
					CachelvlPanel panel = new CachelvlPanel();
					panel.setBounds(10, y, 964, 140);
					memory_hierarchy.add(panel, i);
					//memory_hierarchy.setPreferredSize(new Dimension(1000,y+190));
					memory_hierarchy.validate();
					memory_hierarchy.repaint();
					y = y + 140;
					/*numCacheLevels -= 1;
					if(numCacheLevels == 0){
						//second_cache = false;
						break;
					}*/
				}
				//if(i == 0) second_cache = false;
				textFieldCachelvlsNumber.setEnabled(false);
				textFieldnocMainMemory.setEnabled(false);
				textFieldL.setEnabled(false);
				//System.out.println(memory_hierarchy.getPreferredSize());
			}
		});
		btnNewButtonDone.setBounds(840, 76, 89, 23);
		memory_hierarchy.add(btnNewButtonDone);
			
		
		JPanel hardware_organization = new JPanel();
		frame.getContentPane().add(hardware_organization, "name_408698865509574");
		hardware_organization.setLayout(null);
		
		JLabel lblHardwareOrganization = new JLabel("Hardware Organization :");
		lblHardwareOrganization.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblHardwareOrganization.setBounds(33, 11, 203, 55);
		hardware_organization.add(lblHardwareOrganization);
		
		JLabel lblPipelineWidth = new JLabel("Pipeline Width (number of instruction issued each cycle)");
		lblPipelineWidth.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblPipelineWidth.setBounds(43, 87, 352, 14);
		hardware_organization.add(lblPipelineWidth);
		
		textFieldPipelineWidth = new JTextField();
		textFieldPipelineWidth.setBounds(405, 84, 291, 20);
		hardware_organization.add(textFieldPipelineWidth);
		textFieldPipelineWidth.setColumns(10);
		
		JLabel lblNewLabel_10 = new JLabel("Instruction buffer size");
		lblNewLabel_10.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_10.setBounds(43, 195, 166, 14);
		hardware_organization.add(lblNewLabel_10);
		
		textFieldInstructionBufferSize = new JTextField();
		textFieldInstructionBufferSize.setBounds(405, 192, 291, 20);
		hardware_organization.add(textFieldInstructionBufferSize);
		textFieldInstructionBufferSize.setColumns(10);
		
		JLabel lblReservationStation = new JLabel("Type of instruction");
		lblReservationStation.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblReservationStation.setBounds(43, 316, 133, 14);
		hardware_organization.add(lblReservationStation);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(190, 316, 81, 20);
		comboBox.addItem("LW");
		comboBox.addItem("SW");
		comboBox.addItem("JMP");
		comboBox.addItem("BEQ");
		comboBox.addItem("JALR");
		comboBox.addItem("RET");
		comboBox.addItem("ADD");
		comboBox.addItem("SUB");
		comboBox.addItem("ADDI");
		comboBox.addItem("NAND");
		comboBox.addItem("MUL");
		comboBox.setSelectedIndex(-1);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if((String)comboBox.getSelectedItem() == "LW" ||(String)comboBox.getSelectedItem() == "SW" )
					textFieldC.setEnabled(false);
				else
					textFieldC.setEnabled(true);
				
			}
		});
		comboBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie)
			{
			   if(ie.getStateChange() == ItemEvent.DESELECTED) //edit: bracket was missing
			   {
				   String item = ie.getItem().toString();
				   switch(item){
				   case "LW":
						maxInstrs[InstrType.LW.ordinal()] = Integer.parseInt(textFieldRS.getText());
						break;
					case "SW":
						maxInstrs[InstrType.SW.ordinal()] = Integer.parseInt(textFieldRS.getText());
						break;
					case "JMP":
						maxInstrs[InstrType.JMP.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.JMP.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "BEQ":
						maxInstrs[InstrType.BEQ.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.BEQ.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "JALR":
						maxInstrs[InstrType.JALR.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.JALR.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "RET":
						maxInstrs[InstrType.RET.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.RET.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "ADD":
						maxInstrs[InstrType.ADD.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.ADD.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "SUB":
						maxInstrs[InstrType.SUB.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.SUB.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "ADDI":
						maxInstrs[InstrType.ADDI.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.ADDI.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "NAND":
						maxInstrs[InstrType.NAND.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.NAND.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
					case "MUL":
						maxInstrs[InstrType.MUL.ordinal()] = Integer.parseInt(textFieldRS.getText());
						numCycles[InstrType.MUL.ordinal()] = Integer.parseInt(textFieldC.getText());
						break;
						
					default:
						break;
				
				   }
			      //System.out.println("Previous item: " + ie.getItem());
			   }
			   else if(ie.getStateChange() == ItemEvent.SELECTED)
			   {
			      System.out.println("New item: " + ie.getItem());
			   }
			}
		});
		hardware_organization.add(comboBox);
		
		
		JLabel lblNumberOfReservation = new JLabel("Number of reservation stations");
		lblNumberOfReservation.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNumberOfReservation.setBounds(324, 316, 184, 14);
		hardware_organization.add(lblNumberOfReservation);
		
		textFieldRS = new JTextField();
		textFieldRS.setBounds(518, 313, 86, 20);
		hardware_organization.add(textFieldRS);
		textFieldRS.setColumns(10);
		
		JLabel lblNumberOfCycles = new JLabel("Number of cycles");
		lblNumberOfCycles.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNumberOfCycles.setBounds(667, 316, 121, 14);
		hardware_organization.add(lblNumberOfCycles);
		
		textFieldC = new JTextField();
		textFieldC.setBounds(798, 313, 86, 20);
		hardware_organization.add(textFieldC);
		textFieldC.setColumns(10);
		
		JLabel lblNumberOfRob = new JLabel("Number of ROB entries");
		lblNumberOfRob.setBounds(43, 468, 203, 14);
		hardware_organization.add(lblNumberOfRob);
		
		textFieldRob = new JTextField();
		textFieldRob.setBounds(405, 465, 291, 20);
		hardware_organization.add(textFieldRob);
		textFieldRob.setColumns(10);
		
		JPanel program = new JPanel();
		frame.getContentPane().add(program, "name_408701866184179");
		program.setLayout(null);
		
		JScrollPane scrollPaneTextArea = new JScrollPane();
		scrollPaneTextArea.setBounds(10, 11, 680, 547);
		program.add(scrollPaneTextArea);
		
		JTextArea textArea = new JTextArea();
		scrollPaneTextArea.setViewportView(textArea);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(729, 431, 89, 20);
		comboBox_1.setSelectedIndex(-1);
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int cacheLevel = comboBox_1.getSelectedIndex() + 1;
				//textFieldHitRatio.setText(t);										//here the hit ratio of the corresponding cache level is inserted 
				
			}
		});
		comboBox_1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				   if(arg0.getStateChange() == ItemEvent.SELECTED) //edit: bracket was missing
				   {
					   String item = arg0.getItem().toString();
					   double hit_ratio;
					   switch(item){
					   case "Level 1":
						   hit_ratio = (double) (p.M.getCaches().get(0).getAccesses()+p.M.getCaches().get(1).getAccesses()-p.M.getCaches().get(0).getMisses()-p.M.getCaches().get(1).getMisses()) / (p.M.getCaches().get(0).getAccesses()+p.M.getCaches().get(1).getAccesses()) ;
						   textFieldHitRatio.setText(String.valueOf(hit_ratio));
							break;
						case "Level 2":
							hit_ratio = (double) (p.M.getCaches().get(2).getAccesses()-p.M.getCaches().get(2).getMisses()) / p.M.getCaches().get(2).getAccesses();
							textFieldHitRatio.setText(String.valueOf(hit_ratio));
							break;
						case "Level 3":
							hit_ratio = (double) (p.M.getCaches().get(3).getAccesses()-p.M.getCaches().get(3).getMisses()) / p.M.getCaches().get(3).getAccesses();
							textFieldHitRatio.setText(String.valueOf(hit_ratio));
							break;
						default:
							break;
					
					   }
				      //System.out.println("Previous item: " + ie.getItem());
				   }
				
			}
		});
		program.add(comboBox_1);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				//comboBox_1.setEnabled(true);
				for(int i =1; i <= cacheLevels.size(); i++){
					comboBox_1.addItem("Level " + i);
				}
				String s[] = textArea.getText().split("\\r?\\n");
			    code = new ArrayList<>(Arrays.asList(s)) ;
			    String org = code.get(0);
	            System.out.println(org);

			    int startingPC = 32768;
	            String[] stringsORG = programParser.match(org);
	            if(stringsORG != null && stringsORG[0].equals(".ORG")) {
	            	startingPC+= Integer.parseInt(stringsORG[1]);
	            }
	            for(int i = 1; i<code.size(); i++) {
	            	String[] strings = programParser.match(code.get(i));
	            	if(strings == null) {
	            		System.out.println("Invalid Instruction: " + code.get(i));
	            	}
	            	else {
	            		if(strings[0].equals(".DATA")) {
	            			
	            			M.getMemory().write(Integer.parseInt(strings[2]), strings[1], false);
	            		}
	            		else {
	            			System.out.println(startingPC);
	            			System.out.println(programParser.match(code.get(i)));
			            	M.getMemory().write(startingPC, String.join(" ", programParser.match(code.get(i))), false);
			            	startingPC+= 2;
	            		}
	            	}
	            }
	            PCAfterLastInstruction = startingPC;
	    		System.out.println(PCAfterLastInstruction);

	            while(p.simulate());
	            
	            double amat;
	    		double level1Misses = p.M.getCaches().get(0).getMisses() + p.M.getCaches().get(1).getMisses();
	    		double level1Accesses = p.M.getCaches().get(0).getAccesses() + p.M.getCaches().get(1).getAccesses();
	    		System.out.println(level1Misses);
	    		System.out.println(level1Accesses);
	    		if(p.M.getCaches().size() == 2) { // 1 Cache Level
	        		amat = 
	        		(double) level1Misses/ level1Accesses * p.M.getMemory().getCycles();
	        	}
	        	else if(p.M.getCaches().size() == 3) { // 2 Cache Levels
	            	amat =
	            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getCycles() +
	            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getMisses() / p.M.getCaches().get(2).getAccesses() * p.M.getMemory().getCycles();
	        	}
	        	else { // 3 Cache Levels
	            	amat = 
	            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getCycles() +
	            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getMisses() / p.M.getCaches().get(2).getAccesses() * p.M.getCaches().get(3).getCycles() +
	            	(double) level1Misses/ level1Accesses * p.M.getCaches().get(2).getMisses() / p.M.getCaches().get(2).getAccesses() * p.M.getCaches().get(3).getMisses() / p.M.getCaches().get(3).getAccesses() * p.M.getMemory().getCycles();
	        	}
	        	double cpiLoad = amat * p.loadInstructions / p.totalInstructions;
	        	double cpiBranch = amat * p.mispredictions / p.totalInstructions;
	        	double cpi = 1.0 + cpiLoad + cpiBranch;
	        	double ipc = 1.0 / cpi;
	        	
	        	textFieldIPC.setText(String.valueOf(ipc));
	        	textFieldCyclesNumberOutput.setText(Integer.toString(p.cyclesSimulated));
	        	
	        	textFieldAMAT.setText(String.valueOf(amat));
	        	if(p.beqInstructions == 0) {
		        	textFieldBranchMisPercentage.setText("Not available");
	            }
	            else {
		        	textFieldBranchMisPercentage.setText(String.valueOf(p.mispredictions * 100.0 / p.beqInstructions));
	            }
			}
		});
		btnRun.setBounds(10, 569, 89, 52);
		program.add(btnRun);
		
		JLabel lblNumberOfCyles = new JLabel("Execution time(in cycles)");
		lblNumberOfCyles.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNumberOfCyles.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumberOfCyles.setBounds(744, 36, 180, 14);
		program.add(lblNumberOfCyles);
		
		textFieldCyclesNumberOutput = new JTextField();
		textFieldCyclesNumberOutput.setEditable(false);
		textFieldCyclesNumberOutput.setBounds(787, 61, 86, 20);
		program.add(textFieldCyclesNumberOutput);
		textFieldCyclesNumberOutput.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("IPC");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(744, 115, 180, 14);
		program.add(lblNewLabel);
		
		textFieldIPC = new JTextField();
		textFieldIPC.setEditable(false);
		textFieldIPC.setBounds(787, 140, 86, 20);
		program.add(textFieldIPC);
		textFieldIPC.setColumns(10);
		
		JLabel lblGlobalAmat = new JLabel("Global AMAT (in cycles)");
		lblGlobalAmat.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblGlobalAmat.setHorizontalAlignment(SwingConstants.CENTER);
		lblGlobalAmat.setBounds(744, 209, 180, 14);
		program.add(lblGlobalAmat);
		
		textFieldAMAT = new JTextField();
		textFieldAMAT.setEditable(false);
		textFieldAMAT.setBounds(787, 234, 86, 20);
		program.add(textFieldAMAT);
		textFieldAMAT.setColumns(10);
		
		JLabel lblBranchMispredictionPercentage = new JLabel("Branch misprediction percentage");
		lblBranchMispredictionPercentage.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblBranchMispredictionPercentage.setHorizontalAlignment(SwingConstants.CENTER);
		lblBranchMispredictionPercentage.setBounds(744, 306, 180, 14);
		program.add(lblBranchMispredictionPercentage);
		
		textFieldBranchMisPercentage = new JTextField();
		textFieldBranchMisPercentage.setEditable(false);
		textFieldBranchMisPercentage.setBounds(787, 331, 86, 20);
		program.add(textFieldBranchMisPercentage);
		textFieldBranchMisPercentage.setColumns(10);
		
		JLabel lblTheHitRatio = new JLabel("The hit ratio");
		lblTheHitRatio.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblTheHitRatio.setHorizontalAlignment(SwingConstants.CENTER);
		lblTheHitRatio.setBounds(744, 402, 180, 14);
		program.add(lblTheHitRatio);
		
		textFieldHitRatio = new JTextField();
		textFieldHitRatio.setEditable(false);
		textFieldHitRatio.setBounds(888, 431, 86, 20);
		program.add(textFieldHitRatio);
		textFieldHitRatio.setColumns(10);
		program.setVisible(false);
		
		
		JButton btnNewButton = new JButton("Next");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				h.pipelineWidth = Integer.parseInt(textFieldPipelineWidth.getText());
				pipelineWidth = Integer.parseInt(textFieldPipelineWidth.getText());
				h.instructionBufferSize = Integer.parseInt(textFieldInstructionBufferSize.getText());
				insturctionBuffer = Integer.parseInt(textFieldInstructionBufferSize.getText());
				h.robentries = Integer.parseInt(textFieldRob.getText());
				ROBsize = Integer.parseInt(textFieldRob.getText());
				
				p = new Processor(M, pipelineWidth, insturctionBuffer, ROBsize, maxInstrs, numCycles);
				
				//System.out.println(h.toString());
				hardware_organization.setVisible(false);
				program.setVisible(true);
			}
		});
		btnNewButton.setBounds(468, 614, 89, 23);
		hardware_organization.add(btnNewButton);
		hardware_organization.setVisible(false);		
		
				
		JButton btnNextMemoryHierarchy = new JButton("Next");
		btnNextMemoryHierarchy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//MessageBox m = new MessageBox();
				//take inputs and create objects
				for(int i = 0; i<numCacheLevels; i++){
					CachelvlPanel ps = (CachelvlPanel)memory_hierarchy.getComponent(i);
					CacheLevel c = new CacheLevel();
					cacheLevels.add(c);
					c.m = Integer.parseInt(ps.textFieldM.getText());
					c.access_cycles = Integer.parseInt(ps.textFieldAccessTime.getText());
					c.s = Integer.parseInt(ps.textFieldS.getText());
					c.write_back = Integer.parseInt(ps.textFieldWb.getText());

				}
				/*CachelvlPanel ps = (CachelvlPanel)memory_hierarchy.getComponent(0);
				CacheLevel c = new CacheLevel();
				cacheLevels.add(c);
				//c.hit_rate = Float.parseFloat(ps.textFieldHitRate.getText());
				c.m = Integer.parseInt(ps.textFieldM.getText());
				//c.miss_rate = Float.parseFloat(ps.textFieldMissRate.getText());
				c.access_cycles = Integer.parseInt(ps.textFieldAccessTime.getText());
				c.s = Integer.parseInt(ps.textFieldS.getText());
				c.write_back = Integer.parseInt(ps.textFieldWb.getText());
				//System.out.println(c.hit_rate);
				if(second_cache){
					CachelvlPanel cp = (CachelvlPanel)memory_hierarchy.getComponent(1);
					CacheLevel cl = new CacheLevel();
					cacheLevels.add(cl);
					//cl.hit_rate = Float.parseFloat(cp.textFieldHitRate.getText());
					cl.m = Integer.parseInt(cp.textFieldM.getText());
					//cl.miss_rate = Float.parseFloat(cp.textFieldMissRate.getText());
					cl.access_cycles = Integer.parseInt(cp.textFieldAccessTime.getText());
					cl.s = Integer.parseInt(cp.textFieldS.getText());
					c.write_back = Integer.parseInt(ps.textFieldWb.getText());
					//System.out.println(cl.hit_rate);
				}*/
				//if(numCacheLevels <= 0){
					
					
					
					for(int i =0; i<cacheLevels.size(); i++){
						m[i] = cacheLevels.get(i).m;
						System.out.println(cacheLevels.get(i).m);
						S[i] = cacheLevels.get(i).s;
						cycles[i] = cacheLevels.get(i).access_cycles;
						if(cacheLevels.get(i).write_back == 0)
							cacheWriteHitPolicy[i] = CacheWriteHitPolicy.WriteBack;
						else
							cacheWriteHitPolicy[i] = CacheWriteHitPolicy.WriteThrough;
						//System.out.println(cacheLevels.get(i).toString());
					}
					
					
					
					/*for(int i = 0; i< m.length ; i++){
						System.out.println("");
						System.out.print(m[i] + " ");
						System.out.println("");
						System.out.print(S[i] + " ");
						System.out.println("");
						System.out.print(cycles[i] + " ");
						System.out.println("");
						System.out.print(cacheWriteHitPolicy[i] + " ");
						System.out.println("");
					}*/
					try {
						//System.out.println(cacheLevelsNumber);
						//System.out.println(L);
						//System.out.println(memoryCycles);
						M = new MemoryHierarchy(cacheLevelsNumber, L, S, m, cycles, memoryCycles, cacheWriteHitPolicy);
						//System.out.println(M.toString());
					} catch (InvalidNumberOfBanksException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					memory_hierarchy.setVisible(false);
					hardware_organization.setVisible(true);
					/*}else{
					//memory_hierarchy.remove(memory_hierarchy.getComponent(0));
					memory_hierarchy.remove(memory_hierarchy.getComponent(1));
					/*current.setVisible(false);
					current = new JPanel();
					frame.getContentPane().add(current);
					current.setLayout(null);
					btnNextMemoryHierarchy.setBounds(468, 614, 89, 23);
					current.add(btnNextMemoryHierarchy);
					current.setVisible(true);
					CachelvlPanel panel = new CachelvlPanel();
					//panel.textFieldnocMainMemory.getText();
					panel.setBounds(10, 132, 964, 190);
					memory_hierarchy.add(panel);
					memory_hierarchy.validate();
					memory_hierarchy.repaint();
					numCacheLevels -= 1;
					second_cache = false;
					
					int y = 132;
					int i;
					/*for(i = 0; i<2 /*|| cacheNumber > 0; i++){
						CachelvlPanel panel = new CachelvlPanel();
						//panel.textFieldnocMainMemory.getText();
						panel.setBounds(10, y, 964, 190);
						current.add(panel, i);
						//memory_hierarchy.setPreferredSize(new Dimension(1000,y+190));
						current.validate();
						current.repaint();
						numCacheLevels -= 1;
						if(numCacheLevels == 0){
							//second_cache = false;
							break;
						}
						System.out.println(numCacheLevels);
						y += 190;
					}
					//if(i == 0) second_cache = false;
				}*/
				
				//System.out.println(cacheLevels.size());
				
				
				
				/*for(int i =0; i<S.length; i++){
	            	System.out.print(S[i] + "  ");
	            }
	            System.out.println("");
	            for(int i =0; i<m.length; i++){
	            	System.out.print(m[i] + "  ");
	            }*/
				
			}
		});
		btnNextMemoryHierarchy.setBounds(468, 614, 89, 23);
		memory_hierarchy.add(btnNextMemoryHierarchy);
		
		JLabel lblNewLabel_1 = new JLabel("Line size (L) of cache(s)");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_1.setBounds(387, 52, 164, 14);
		memory_hierarchy.add(lblNewLabel_1);
		
		textFieldL = new JTextField();
		textFieldL.setBounds(397, 77, 86, 20);
		memory_hierarchy.add(textFieldL);
		textFieldL.setColumns(10);
		
		JLabel label = new JLabel("Main memory access time (in cycles)");
		label.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label.setBounds(566, 53, 230, 14);
		memory_hierarchy.add(label);
		
		textFieldnocMainMemory = new JTextField();
		textFieldnocMainMemory.setColumns(10);
		textFieldnocMainMemory.setBounds(611, 77, 86, 20);
		memory_hierarchy.add(textFieldnocMainMemory);
				
	}
}
