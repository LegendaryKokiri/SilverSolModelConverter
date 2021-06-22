package main;

import javax.swing.JFrame;

public class Window {
	
	public static void main(String[] args) {
		JFrame window = new JFrame();
		window.setVisible(true);
		window.setContentPane(new WindowPanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
	}
	
}
