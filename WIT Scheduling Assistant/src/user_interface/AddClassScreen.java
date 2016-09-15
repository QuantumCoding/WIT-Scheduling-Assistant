package user_interface;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import pages.wit.LookupResultsPage.LookupResult;
import util.Choise;
import util.References;
import web_interface.PageLock;

public class AddClassScreen extends JPanel implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private JComboBox<Choise> termComboBox;
	private JList<LookupResult> classesList;
	private DefaultListModel<LookupResult> classesListModel;
	
	private JComboBox<Choise> subjectComboBox;
	private DefaultComboBoxModel<Choise> subjectComboBoxModel;
	private JList<LookupResult> classSelectList;
	private DefaultListModel<LookupResult> classSelectListModel;
	
	private JButton addButton;
	private JButton submitButton;
	private JButton removeButton;
	private JButton clearButton;
	
	private PageLock pages;
	private Display display;

	public AddClassScreen(PageLock pages, Display display) {
		setBackground(Color.WHITE);
		this.pages = pages;
		this.display = display;
		
		setLayout(new MigLayout("", "[][][grow]", "[][][][][grow][]"));
		
		JPanel classesLabelPanel = new JPanel();
		classesLabelPanel.setBackground(new Color(250,250,250));
		classesLabelPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(classesLabelPanel, "flowx,cell 2 0,growx,aligny bottom");
		
		JLabel classesLabel = new JLabel("Classes");
		classesLabelPanel.add(classesLabel);
		classesLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel witIconLabel = new JLabel("");
		witIconLabel.setIcon(References.Icon_WIT_Header);
		add(witIconLabel, "cell 0 0 1 3,alignx center,aligny center");
		
		JPanel termPanel = new JPanel();
		termPanel.setBackground(new Color(250,250,250));
		termPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(termPanel, "cell 0 3,grow");
		termPanel.setLayout(new MigLayout("insets 3px", "[][]", "[]"));
		
		JLabel termLabel = new JLabel("Select Term: ");
		termLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		termPanel.add(termLabel, "cell 0 0,alignx right,aligny center");
		
		termComboBox = new JComboBox<>();
		termComboBox.setModel(new DefaultComboBoxModel<>(pages.getTerms().toArray(new Choise[pages.getTerms().size()])));
		termComboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		termPanel.add(termComboBox, "cell 1 0,growx,aligny top");
		
		JPanel dividerPanel = new JPanel();
		dividerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout fl_dividerPanel = (FlowLayout) dividerPanel.getLayout();
		fl_dividerPanel.setVgap(0);
		fl_dividerPanel.setHgap(0);
		add(dividerPanel, "cell 1 0 1 6,grow");
		
		JScrollPane classesScrollPane = new JScrollPane();
		classesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(classesScrollPane, "cell 2 1 1 4,grow");
		
		classesList = new JList<>();
		classesList.setBackground(SystemColor.menu);
		classesList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		classesList.setModel(classesListModel = new DefaultListModel<>());
		classesScrollPane.setViewportView(classesList);
		
		JPanel subjectPanel = new JPanel();
		subjectPanel.setBackground(Color.WHITE);
		subjectPanel.setBorder(UIManager.getBorder("ScrollPane.border"));
		add(subjectPanel, "cell 0 4,grow");
		subjectPanel.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JPanel subjectSelectPanel = new JPanel();
		subjectSelectPanel.setBackground(Color.WHITE);
		subjectPanel.add(subjectSelectPanel, "cell 0 0,aligny bottom");
		subjectSelectPanel.setLayout(new MigLayout("insets 0px", "[63px][grow]", "[23px]"));
		
		JLabel subjectLabel = new JLabel("Subject: ");
		subjectLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		subjectSelectPanel.add(subjectLabel, "cell 0 0,alignx right,aligny center");
		
		subjectComboBox = new JComboBox<>();
		subjectComboBox.setMaximumRowCount(10);
		subjectComboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		subjectComboBox.setModel(subjectComboBoxModel = new DefaultComboBoxModel<>());
		subjectSelectPanel.add(subjectComboBox, "cell 1 0,growx,aligny top");
		
		JScrollPane subjectScrollPane = new JScrollPane();
		subjectPanel.add(subjectScrollPane, "cell 0 1,grow");
		
		classSelectList = new JList<>();
		classSelectList.setFixedCellHeight(25);
		classSelectList.setVisibleRowCount(16);
		classSelectList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		classSelectList.setBackground(SystemColor.menu);
		classSelectList.setModel(classSelectListModel = new DefaultListModel<>());
		classSelectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		subjectScrollPane.setViewportView(classSelectList);
		
		JPanel addPanel = new JPanel();
		addPanel.setBackground(Color.WHITE);
		addPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(addPanel, "cell 0 5,grow");
		addPanel.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		addButton = new JButton("Add Class");
		addButton.setBackground(Color.WHITE);
		addButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		addPanel.add(addButton, "cell 0 0");
		
		submitButton = new JButton("Create Schedual");
		submitButton.setBackground(Color.WHITE);
		submitButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		addPanel.add(submitButton, "cell 1 0,alignx right");
		
		JPanel removePanel = new JPanel();
		removePanel.setBackground(Color.WHITE);
		removePanel.setBorder(new LineBorder(new Color(204, 0, 0)));
		add(removePanel, "cell 2 5,grow");
		removePanel.setLayout(new MigLayout("", "[grow][grow]", "[]"));
		
		removeButton = new JButton("Remove");
		removeButton.setBackground(Color.WHITE);
		removeButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		removePanel.add(removeButton, "cell 0 0,grow");
		
		clearButton = new JButton("Clear");
		clearButton.setBackground(Color.WHITE);
		clearButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		removePanel.add(clearButton, "cell 1 0,grow");
		
		addButton.addActionListener(this);
		submitButton.addActionListener(this);
		removeButton.addActionListener(this);
		clearButton.addActionListener(this);
		
		termComboBox.addActionListener(this);
		
		classesList.addListSelectionListener(this);
		classSelectList.addListSelectionListener(this);
		
		for(Choise subject : pages.getSubjects()) 
			subjectComboBoxModel.addElement(subject);
		
		subjectComboBoxModel.setSelectedItem(null);
		classSelectListModel.clear();

		subjectComboBox.addActionListener(this);
		
		addButton.setEnabled(false);
		submitButton.setEnabled(false);
		removeButton.setEnabled(false);
		
		classSelectList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1) {
					actionPerformed(new ActionEvent(addButton, 0, ""));
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == removeButton) {
			for(int index : classesList.getSelectedIndices())
				classesListModel.remove(index);
			submitButton.setEnabled(classesListModel.size() > 1);
			return;
		}
		
		if(e.getSource() == addButton) {
			LookupResult value = classSelectList.getSelectedValue();
			if(classesListModel.contains(value)) return;
			classesListModel.addElement(value);
			
			submitButton.setEnabled(classesListModel.size() > 1);
			return;
		}
		
		if(e.getSource() == termComboBox) {
			subjectComboBoxModel.removeAllElements();
			
			SwingUtilities.invokeLater(() -> {
				pages.changeTerm((Choise) termComboBox.getSelectedItem());
				ArrayList<Choise> subjects = pages.getSubjects();
				
				subjectComboBox.removeActionListener(this);
				for(Choise subject : subjects) 
					subjectComboBoxModel.addElement(subject);
				
				subjectComboBoxModel.setSelectedItem(null);
				classSelectListModel.clear();
	
				subjectComboBox.addActionListener(this);
			});
			
			return;
		}
		
		if(e.getSource() == subjectComboBox) {	
			if(subjectComboBox.getSelectedItem() == null) {
				classSelectListModel.clear();
				return;
			}
			
			classSelectListModel.clear();
			SwingUtilities.invokeLater(() -> {
				ArrayList<LookupResult> classes = pages.getClasses((Choise) subjectComboBox.getSelectedItem());
				
				for(LookupResult clazz : classes)
					classSelectListModel.addElement(clazz);
			});
			
			return;
		}
		
		if(e.getSource() == clearButton) {
			int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to Clear your class List?", "Comfirm Clear", 
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			
			if(response == JOptionPane.YES_OPTION)
				classesListModel.clear();
			
			submitButton.setEnabled(classesListModel.size() > 1);
			return;
		}
		
		if(e.getSource() == submitButton) {
			ArrayList<LookupResult> classes = new ArrayList<>(classesListModel.size());
			for(int i = 0; i < classesListModel.size(); i ++)
				classes.add(classesListModel.get(i));
			
			display.submitClasses(classes);
			return;
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == classesList) {
			removeButton.setEnabled(classesList.getSelectedIndex() != -1);
			return;
		}
		
		if(e.getSource() == classSelectList) {
			addButton.setEnabled(classSelectList.getSelectedIndex() != -1);
			return;
		}
	}
}
