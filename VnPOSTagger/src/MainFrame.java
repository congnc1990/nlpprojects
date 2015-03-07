import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import linkgrammar.vnpostagger.JLinkGrammar;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import utils.StringUtils;

import javax.swing.SwingConstants;


public class MainFrame extends JFrame implements ActionListener {
	private JPanel contentPane;
	private	JTextArea txtInput;
	private	JTextArea txtOutput;
	private	JSpinner spinner;
	private	JButton btnParse;
	private	JButton btnImport;
	private static final String CMD_LOAD_DICT = "LOAD_DICT";
	private static final String CMD_PARSE = "PARSE";
	private static final String CMD_IMPORT = "IMPORT";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private class ParseThread extends Thread {
        @Override
        public void run() {
            btnParse.setEnabled(false);
            btnImport.setEnabled(false);
            txtInput.setEditable(false);
            
            txtInput.setEditable(true);
        	btnParse.setEnabled(true);
            btnImport.setEnabled(true);
        }
	}

	Config config = new Config();
	JLinkGrammar linkgrammar = new JLinkGrammar();
	
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd == CMD_IMPORT) {
			//JOptionPane.showMessageDialog(contentPane, "IMPORT");
			JFileChooser chooser = new JFileChooser();
	        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        int retval = chooser.showOpenDialog(this);
	        if (retval == JFileChooser.APPROVE_OPTION) {
	        	String filepath = chooser.getSelectedFile().getAbsolutePath();
	            try {
					txtInput.read(new FileReader(filepath), null);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(contentPane, "IMPORT FAILED ! "+filepath);
					e.printStackTrace();
				}
	        }
	    }
		else if (cmd == CMD_PARSE) {
			try {
				String textInput = txtInput.getText();//new String(txtInput.getText().getBytes(),"UTF-8");
				//System.out.println(textInput);
				int nMaxResult = (int)(spinner.getModel().getValue());
				String output = linkgrammar.parse(textInput, nMaxResult);
				txtOutput.setText(output);
				//JOptionPane.showMessageDialog(contentPane, "PARSE OK ");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(contentPane, "Có lỗi phân tích !" + e.toString());
				e.printStackTrace();
				txtOutput.setText(e.getMessage());
			}
		}
		else if (cmd == CMD_LOAD_DICT)
		{
			try {
				JLinkGrammar.reloadDictionary();
				String dictpath = new File(config.getDictionaryFile()).getCanonicalPath();
				txtOutput.setText(String.format("Working dir: %s\r\nDictionary: %s",
						System.getProperty("user.dir"), dictpath));
				JOptionPane.showMessageDialog(contentPane, "Nạp từ điển linkgrammar thành công !");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(contentPane, "Không nạp được từ điển linkgrammar !");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(contentPane, "Lỗi phân tích cấu trúc từ điển linkgrammar !");
			}
		}
	}
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setBackground(Color.LIGHT_GRAY);
		setTitle("DEMO gán nhãn Tiếng Việt bằng văn phạm liên kết");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JScrollPane scrollPaneInput = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPaneInput, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPaneInput, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPaneInput, -130, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPaneInput);
		
		txtInput = new JTextArea();
		txtInput.grabFocus();
		txtInput.setWrapStyleWord(true);
		txtInput.setLineWrap(true);
		txtInput.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		scrollPaneInput.add(txtInput);
		scrollPaneInput.setViewportView(txtInput);
		
		btnImport = new JButton("Nh\u1EADp t\u1EEB file");
		btnImport.setActionCommand(CMD_IMPORT);
		btnImport.addActionListener(this);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnImport, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnImport, 5, SpringLayout.EAST, scrollPaneInput);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnImport, 50, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnImport, -10, SpringLayout.EAST, contentPane);
		contentPane.add(btnImport);
		
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPaneInput, -10, SpringLayout.NORTH, scrollPane);
		
		JLabel lblInputText = new JLabel("Nh\u1EADp v\u0103n b\u1EA3n");
		lblInputText.setFont(new Font("Tahoma", Font.BOLD, 14));
		scrollPaneInput.setColumnHeaderView(lblInputText);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 200, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, contentPane);
		
		txtOutput = new JTextArea();
		txtOutput.setFont(new Font("Courier New", Font.PLAIN, 13));
		//txtOutput.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		txtOutput.setEditable(false);
		scrollPane.add(txtOutput);
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(txtOutput);
		
		JLabel lblResult = new JLabel("K\u1EBFt qu\u1EA3 ph\u00E2n t\u00EDch");
		lblResult.setFont(new Font("Tahoma", Font.BOLD, 14));
		scrollPane.setColumnHeaderView(lblResult);
		
		btnParse = new JButton("Phân tích");
		btnParse.setActionCommand(CMD_PARSE);
		btnParse.addActionListener(this);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnParse, 20, SpringLayout.SOUTH, btnImport);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnParse, 0, SpringLayout.WEST, btnImport);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnParse, 60, SpringLayout.SOUTH, btnImport);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnParse, 0, SpringLayout.EAST, btnImport);
		contentPane.add(btnParse);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(config.getDefaultMaxResults(), 1, null, 1));
		sl_contentPane.putConstraint(SpringLayout.NORTH, spinner, 17, SpringLayout.SOUTH, btnParse);
		sl_contentPane.putConstraint(SpringLayout.WEST, spinner, -46, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, spinner, -10, SpringLayout.EAST, contentPane);
		contentPane.add(spinner);
		
		JLabel lblMaxResults = new JLabel("Số kết quả");
		lblMaxResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblMaxResults.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblMaxResults.setForeground(Color.BLUE);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMaxResults, 3, SpringLayout.NORTH, spinner);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMaxResults, 0, SpringLayout.WEST, btnImport);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblMaxResults, -6, SpringLayout.WEST, spinner);
		contentPane.add(lblMaxResults);
		
		JButton btnReloadDictionary = new JButton("Nạp lại từ điển");
		btnReloadDictionary.setActionCommand(CMD_LOAD_DICT);
		btnReloadDictionary.addActionListener(this);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnReloadDictionary, 8, SpringLayout.SOUTH, spinner);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnReloadDictionary, 0, SpringLayout.SOUTH, scrollPaneInput);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnReloadDictionary, 0, SpringLayout.WEST, btnImport);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnReloadDictionary, -8, SpringLayout.EAST, contentPane);
		contentPane.add(btnReloadDictionary);
		
		String dictpath;
		try {
			File dictfile = new File(config.getDictionaryFile());
			dictpath = dictfile.getCanonicalPath();
			txtOutput.setText(String.format("Working dir: %s\r\nDictionary: %s",
					System.getProperty("user.dir"), dictpath));
			if (!dictfile.isFile())
				txtOutput.append("\r\nCảnh báo: file từ điển không tồn tại !");
			linkgrammar.SetDictionaryPath(dictpath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(contentPane, "Không tìm thấy từ điển "+config.getDictionaryFile());
		}
	}
}
