import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Main {	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        
        
        final JFrame frame = new JFrame();
        frame.setSize(new Dimension(300,200));
        frame.setResizable(false);
        final JPanel mainPanel = new JPanel(new GridLayout(2,1));
  
		final JPanel InputPanel = new JPanel();
		final JTextField userName = new JTextField("Enter User Name", 20);
		final JTextField userPass = new JTextField("Enter Password", 20);
		InputPanel.add(userName);
		InputPanel.add(userPass);
		
		final JPanel buttonPanel = new JPanel();
		
		final JButton button = new JButton();
		button.setText("Log In");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(!userName.getText().equals("Enter User Name") || !userPass.getText().equals("Enter Password")) {
					EmbroideryDB DB = EmbroideryDB.getDb();
					DB.setUser(userName.getText());
					DB.setPass(userPass.getText());
					try {
						DB.connect();
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					try {
						DB.createTables();
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
					GUI gui = new GUI(DB);
					gui.start();
					frame.dispose();
				}
			}
		});
		
		buttonPanel.add(button);
		mainPanel.add(InputPanel);
		mainPanel.add(buttonPanel);
		frame.add(mainPanel);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
	
	

}
