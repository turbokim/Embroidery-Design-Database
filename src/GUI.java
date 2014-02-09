import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;


public class GUI extends JFrame {
	/* size of text fields */
	private static final int FIELD_SIZE = 20;
	/* width of GUI */
	private static final int WIDTH = 1300;
	/* height of GUI */
	private static final int HEIGHT = 900;
	
	private EmbroideryDB DB;
	
	private JTextField minPrice;
	private JTextField maxPrice;
	private JTextField minThread;
	private JTextField maxThread;
	
	private JTextArea results;
	private JTextArea shoppingCartResults;
	private StringBuilder builder = new StringBuilder();
	private double totalCost;
	private int selectFlag = 1;
	

	public GUI(EmbroideryDB db) {
		super("Embroidery Database");
		DB = db;
	}
	
	/**
	 * initializes gui.
	 */
	public void start() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(new Dimension(WIDTH,HEIGHT));
		setResizable(false);
		
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		final JToolBar sideBar = sideBar();
		add(sideBar, BorderLayout.WEST);
		
		results = new JTextArea(50,50);
		JScrollPane scroll = new JScrollPane(results);
		results.setEditable(false);
		results.setText("     Results");
		add(scroll, BorderLayout.CENTER);
		
		shoppingCartResults = new JTextArea(25,25);
		JScrollPane scrollCart = new JScrollPane(shoppingCartResults);
		shoppingCartResults.setEditable(false);
		shoppingCartResults.setText("     ");
		add(scrollCart, BorderLayout.EAST);
		//pack();
		
		setVisible(true);
	}
	
	/**
	 * helper method to reduce redundancy.
	 * @param field field to be added
	 * @return jpanel containing the field
	 */
	private JPanel createField(JTextField field) {
		final JPanel panel = new JPanel();
		field.setEditable(true);
		panel.add(field);
		return panel;
	}
	
	/**
	 * resets ranges in southBar
	 */
	private void resetRanges() {
		minPrice.setText("Minimum Price");
		minThread.setText("Minimum StitchCount");
		maxPrice.setText("Maximum Price");
		maxThread.setText("Maximum StitchCount");
		results.setText("    Results");
	}
	
	/**
	 * sets up side bar with search criteria.
	 * @return JtoolBar
	 */
	private JToolBar sideBar() {
		final JToolBar sidebar = new JToolBar();
		sidebar.setFloatable(false);
		
		final JPanel majorPanel = new JPanel(new BorderLayout());

		final JPanel sidePanel = new JPanel(new GridLayout(3,1));
		sidePanel.add(designBar());
		final JToolBar southbar = southBar();
		final JToolBar shopbar = shoppingCart();
		sidePanel.add(southbar);
		sidePanel.add(shopbar);
		majorPanel.add(sidePanel);
		
		String[] searchStrings = {"Search by Design", "Search by Thread", "Search by Needles", "Find Threads for design"};
		final JComboBox searchBox = new JComboBox(searchStrings);
		searchBox.setSelectedIndex(0);
		
		// action listener for changing search type
		searchBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String search = (String)((JComboBox)e.getSource()).getSelectedItem();
		
				Component[] comp = sidePanel.getComponents();
				sidePanel.remove(comp[0]);
				sidePanel.remove(southbar);
				sidePanel.remove(shopbar);
				switch(search) {
				case "Search by Design":
					selectFlag = 1;
					sidePanel.add(designBar());
					sidePanel.add(southbar);
					sidePanel.add(shopbar);
					minThread.setVisible(true);
					maxThread.setVisible(true);
					break;
				case "Search by Thread":
					selectFlag = 2;
					sidePanel.add(threadBar());
					sidePanel.add(southbar);
					sidePanel.add(shopbar);
					minThread.setVisible(false);
					maxThread.setVisible(false);
					break;
				case "Search by Needles":
					selectFlag = 3;
					sidePanel.add(needleBar());
					sidePanel.add(southbar);
					sidePanel.add(shopbar);
					minThread.setVisible(false);
					maxThread.setVisible(false);
					break;
				case "Find Threads for design":
					selectFlag = 4;
					sidePanel.add(threadSearchBar());
					sidePanel.add(southbar);
					sidePanel.add(shopbar);
					minThread.setVisible(false);
					maxThread.setVisible(false);
					break;
				}
				
				
				revalidate();
			}
			
			
		});
		majorPanel.add(searchBox, BorderLayout.NORTH);
		
		
		sidebar.add(majorPanel);
		return sidebar;
	}
	
	/**
	 * search tool bar for search by design.
	 * @return JToolBar.
	 */
	private JToolBar designBar() {
		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setOrientation(SwingConstants.VERTICAL);
		
		final JPanel majorPanel = new JPanel();
		majorPanel.setLayout(new GridLayout(10,1));
		
		// Needed???
		//final JTextField design = new JTextField("Enter Design", FIELD_SIZE);
		//majorPanel.add(createField(design));
		final JPanel genrePanel = new JPanel();
		String[] genreStrings = DB.getList("select distinct genre from designs", "Genre");
		final JComboBox genreBox = new JComboBox(genreStrings);
		//genreBox.setSelectedIndex(0);
		genreBox.setEditable(false);
		genrePanel.add(genreBox);
		majorPanel.add(genrePanel);
	//	final JTextField genre = ;new JTextField("Enter Genre", FIELD_SIZE);
		
		//majorPanel.add(createField(genre));
		
		final JTextField subgenre = new JTextField("Enter KeyWord (optional)", FIELD_SIZE);
		majorPanel.add(createField(subgenre));
		
		
		final JPanel machinePanel = new JPanel();
		

		//Gets the make and models of all machines in database
		String[] machineStrings = DB.machineList("select make, model from machines");
		final JComboBox machineBox = new JComboBox(machineStrings);
		machineBox.setSelectedIndex(0);
		machineBox.setEditable(false);
		machinePanel.add(machineBox);
		majorPanel.add(machinePanel);
		
		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// STILL NEED TO CHECK FORMAT COMPATABILITY.
				try {
					results.setForeground(Color.BLACK);
					String machine = (String) machineBox.getSelectedItem();
					String genre = (String)genreBox.getSelectedItem();
					machine = machine.replaceAll("  ", " ");
					int divIndex = machine.indexOf(" ");
					String make = machine.substring(0, divIndex);
					String model = machine.substring(divIndex+1);
					
					String[] machineFormats = DB.getMachineFormats(make, model);					
					String[] machineDims = DB.getMachineDimensions(make, model);
					//System.out.println(MachineDims[0] + ", " + MachineDims[1]);
					StringBuilder q = new StringBuilder();
					 q.append("select DesignID, DesignName, Height, Width, DesignPrice, StitchCount, DesignBrand");
					 q.append(" from designs, DesignFormats where Genre like '%" + genre + "%'");
					 q.append(" AND DesignFormats.Vender = designs.DesignBrand AND (");
					 for (int i = 0; i < machineFormats.length; i++) {
						 if(i < machineFormats.length - 1)
							 q.append("FIND_IN_SET('" + machineFormats[i] + "',format) > 0 OR ");
						 else
							 q.append("FIND_IN_SET('" + machineFormats[i] + "',format) > 0)");
						
					 }
					 q.append(" AND Height <= '" + machineDims[0] +"' AND Width <= '");
					 q.append(machineDims[1] + "'");
		
					
					if (!subgenre.getText().equals("Enter KeyWord (optional)"))
						q.append(" AND SubGenre like '%" + subgenre.getText() + "%'");
					if (!minPrice.getText().equals("Minimum Price"))
						q.append(" AND DesignPrice >= " + minPrice.getText());
					if (!maxPrice.getText().equals("Maximum Price"))
						q.append(" AND DesignPrice <= "+ maxPrice.getText());
					if (!minThread.getText().equals("Minimum StitchCount"))
						q.append(" AND StitchCount >= " + minThread.getText());
					if (!maxThread.getText().equals("Maximum StitchCount"))
						q.append(" AND StitchCount <= " + maxThread.getText());
					q.append(";");
					results.setText(DB.execute(q.toString()));
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		});
		majorPanel.add(searchButton);
		
		final JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//design.setText("Enter Design");
				//genre.setText("Enter Genre");
				subgenre.setText("Enter KeyWord (optional)");
				machineBox.setSelectedIndex(0);
				
				resetRanges();
			}
			
		});
		majorPanel.add(resetButton);
		
		toolbar.add(majorPanel);
		return toolbar;
	}
	
	/**
	 * search for searching by thread.
	 * @return JToolBar.
	 */
	private JToolBar threadBar() {
		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		final JPanel majorPanel = new JPanel();
		majorPanel.setLayout(new GridLayout(10,1));
		
		final JTextField color = new JTextField("Enter Color", FIELD_SIZE);
		majorPanel.add(createField(color));
		
		final JTextField weight = new JTextField("Enter Weight", FIELD_SIZE);
		majorPanel.add(createField(weight));
		
		final JTextField type = new JTextField("Enter Type", FIELD_SIZE);
		majorPanel.add(createField(type));
		
		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int colorFlag = 0x00, weightFlag = 0x00, typeFlag = 0x00;
				try {
					results.setForeground(Color.BLACK);
					StringBuilder q = new StringBuilder();
					 q.append("select ThreadBrand, ThreadID, ThreadType, Weight, ThreadPrice, ColorName, Color");
					 q.append(" from thread ");//where Color like '%" + color.getText() + "%'");
					 //q.append(" AND Weight = '" + weight.getText() +"' AND ThreadType = '");
					 //q.append(type.getText() + "'");
					
					if (!color.getText().equals("Enter Color"))
						colorFlag = 0x01;
					if (!weight.getText().equals("Enter Weight"))
						weightFlag = 0x02;
					if (!type.getText().equals("Enter Type"))
						typeFlag = 0x04;
					 
					int bitFlag = colorFlag | weightFlag | typeFlag;
					switch (bitFlag) {
						case 0:
							break;
						case 1:
							q.append("where Color like '%" + color.getText() + "%'");
							break;
						case 2:
							q.append("where Weight = '" + weight.getText() +"'");
							break;
						case 3:
							q.append("where Color like '%" + color.getText() + "%'");
							q.append(" AND Weight = '" + weight.getText() +"'");
							break;
						case 4:
							q.append("where ThreadType = '" + type.getText() + "'");
							break;
						case 5:
							q.append("where Color like '%" + color.getText() + "%'");
							q.append(" AND ThreadType = '" + type.getText() + "'");
							break;
						case 6:
							q.append("where Weight = '" + weight.getText() +"'");
							q.append(" AND ThreadType = '" + type.getText() + "'");
							break;
						case 7:
							q.append("where Color like '%" + color.getText() + "%'");
							q.append(" AND Weight = '" + weight.getText() +"'");
							q.append(" AND ThreadType = '" + type.getText() + "'");
							break;
						default:
							break;
					}
					
					
					
					if (!minPrice.getText().equals("Minimum Price"))
						q.append(" AND ThreadPrice >= " + minPrice.getText());
					if (!maxPrice.getText().equals("Maximum Price"))
						q.append(" AND ThreadPrice <= "+ maxPrice.getText());
					q.append(";");
					if (bitFlag != 0)
						results.setText(DB.execute(q.toString()));
					else {
						results.setForeground(Color.RED);
						results.setText("Please enter one or more search parameters!");
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		});
		majorPanel.add(searchButton);
		
		final JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				color.setText("Enter Color");
				weight.setText("Enter Weight");
				type.setText("Enter Type");
				
				resetRanges();
				
			}
			
		});
		majorPanel.add(resetButton);
		
		toolbar.add(majorPanel);
		return toolbar;
	}

	/**
	 * 
	 * @return toolbar used for searching by needle.
	 */
	private JToolBar needleBar() {
		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		final JPanel majorPanel = new JPanel();
		majorPanel.setLayout(new GridLayout(10,1));
		
		final JTextField size = new JTextField("Enter Size", FIELD_SIZE);
		majorPanel.add(createField(size));
		
		//final JTextField weight = new JTextField("Enter Weight", FIELD_SIZE);
		//majorPanel.add(createField(weight));
		
		final JTextField type = new JTextField("Enter Type", FIELD_SIZE);
		majorPanel.add(createField(type));
		
		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// DONE!!
				try {
					results.setForeground(Color.BLACK);
					StringBuilder q = new StringBuilder();
					 q.append("select ID, NeedleBrand, NeedleSize, NeedleType, NeedlePrice");
					 q.append(" from needles ");
					 
					if (!size.getText().equals("Enter Size"))
						q.append("where NeedleSize = " + size.getText());
					else if (!type.getText().equals("Enter Type"))
						q.append("where NeedleType = '" + type.getText() + "'");
					else {
						q.append("where NeedleSize = " + size.getText());
						q.append(" AND NeedleType = '" + type.getText() + "'");
					}
					if (!minPrice.getText().equals("Minimum Price"))
						q.append(" AND NeedlePrice >= " + minPrice.getText());
					if (!maxPrice.getText().equals("Maximum Price"))
						q.append(" AND NeedlePrice <= "+ maxPrice.getText());
					q.append(";");
					
					if (size.getText().equals("Enter Size") && type.getText().equals("Enter Type")) {
						results.setForeground(Color.RED);
						results.setText("Please enter at least one search parameter!");
					} else {
						results.setText(DB.execute(q.toString()));
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		});
		majorPanel.add(searchButton);
		
		final JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				size.setText("Enter Size");
				//weight.setText("Enter Weight");
				type.setText("Enter Type");
				
				resetRanges();
			}
			
		});
		majorPanel.add(resetButton);
		
		toolbar.add(majorPanel);
		return toolbar;
	}
	
	/**
	 * 
	 * @return toolbar used for searching by design number and brand.
	 */
	private JToolBar threadSearchBar() {
		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		final JPanel majorPanel = new JPanel();
		majorPanel.setLayout(new GridLayout(10,1));
		
		final JTextField design = new JTextField("Enter Design Number", FIELD_SIZE);
		majorPanel.add(createField(design));
		
		
		String[] designBrandStrings = DB.getList("select distinct DesignBrand from designs", "DesignBrand");
		final JComboBox designBrandBox = new JComboBox(designBrandStrings);
		designBrandBox.setSelectedIndex(0);
		designBrandBox.setEditable(false);
		majorPanel.add(designBrandBox);
		
		
		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					results.setForeground(Color.BLACK);				
					
					String[] colors = DB.getDesignColors((String)designBrandBox.getSelectedItem(), design.getText());
					
					StringBuilder q = new StringBuilder();
					 q.append("select ThreadBrand, ThreadID, ThreadType, Weight, ThreadPrice, ");
					 q.append("ColorName, Color ");
					 q.append(" from thread where (");
					 
					 if(colors.length <= 1 && colors[0].length() == 0) {
						 colors[0] = "xxxxx";
					 }
					 for(int i = 0; i < colors.length; i++) {
						 if(i < colors.length - 1)
							 q.append("Color like '%" + colors[i] + "%' OR ");
						 else
							 q.append("Color like '%" + colors[i] + "%')");
					 }
					
					if (!minPrice.getText().equals("Minimum Price"))
						q.append(" AND ThreadPrice >= " + minPrice.getText());
					if (!maxPrice.getText().equals("Maximum Price"))
						q.append(" AND ThreadPrice <= "+ maxPrice.getText());
					q.append(";");
					
					if (design.getText().equals("Enter Design Number")) {
						results.setForeground(Color.RED);
						results.setText("Please enter a Design Number!");
					} else {
						results.setText(DB.execute(q.toString()));
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		});
		majorPanel.add(searchButton);
		
		final JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//brand.setText("Enter Brand");
				design.setText("Enter Design Number");
				
				resetRanges();
			}
			
		});
		majorPanel.add(resetButton);
		
		toolbar.add(majorPanel);
		return toolbar;
	}
	
	/**
	 * @return toolbar containing price range and thread range.
	 */
	private JToolBar southBar() {
		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		final JPanel majorPanel = new JPanel(new GridLayout(5,1));
		
		// min max price range
		minPrice = new JTextField("Minimum Price", FIELD_SIZE);
		maxPrice = new JTextField("Maximum Price", FIELD_SIZE);
		majorPanel.add(createField(minPrice));
		majorPanel.add(createField(maxPrice));
		
		// min max thread range
		minThread = new JTextField("Minimum StitchCount", FIELD_SIZE);
		maxThread = new JTextField("Maximum StitchCount", FIELD_SIZE);
		majorPanel.add(createField(minThread));
		majorPanel.add(createField(maxThread));
		
		
		
		toolbar.add(majorPanel);
		return toolbar;
	}
	
	private JToolBar shoppingCart() {
		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		final JPanel majorPanel = new JPanel(new GridLayout(9,1));
		
		final JTextField idField = new JTextField("Enter ID Tabulate Prices", FIELD_SIZE);
		majorPanel.add(createField(idField));
		
		final JButton acceptButton = new JButton("Tabulate");
		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Double price = 0.00;
				String q = "";
				String type = "";
				String item = "";
				if(!idField.getText().equals("Enter ID Tabulate Prices")) {
					// need to get price out of DB  here.
					switch (selectFlag) {
						case 1:
							q = "select DesignPrice from designs where DesignID = '" +
									idField.getText() + "'";
							type = "Design - ";
							break;
						case 2:
							q = "select ThreadPrice from thread where ThreadID = '" +
									idField.getText() + "'";
							type = "Thread - ";
							break;
						case 3:
							q = "select NeedlePrice from needles where ID = '" +
									idField.getText() + "'";
							type = "Needle - ";
							break;
						case 4:
							q = "select ThreadPrice from thread where ThreadID = '" +
									idField.getText() + "'";
							type = "Thread - ";
							break;
					}
					price = DB.getPriceByID(q);
					if(price > 0) {
						item = type + idField.getText();
						builder.append(String.format("%18s", item) + "\t" + String.format("$%.2f\n", price));
						totalCost += price;
						shoppingCartResults.setText(builder.toString() + "\n" + "Total Cost\t\t" +String.format("$%.2f\n", totalCost));
					}
				}
				
			     
			}
			
		});
		majorPanel.add(acceptButton);
		final JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				idField.setText("Enter ID Tabulate Prices");
				builder = new StringBuilder();
				shoppingCartResults.setText("");
			}
			
		});
		majorPanel.add(resetButton);
		toolbar.add(majorPanel);
		return toolbar;
	}
}
