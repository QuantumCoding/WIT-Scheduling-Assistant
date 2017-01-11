package user_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import pages.Choise;
import pages.ClassOption;
import pages.DepartmentSelectPage;
import pages.TermPage;
import scheduling.Section;
import util.Fonts;
import util.References;

public class AddClassScreen extends JPanel implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private JComboBox<Choise> termComboBox;
	private JList<ClassOption> classesList;
	private DefaultListModel<ClassOption> classesListModel;
	
	private JComboBox<Choise> subjectComboBox;
	private DefaultComboBoxModel<Choise> subjectComboBoxModel;
	private JList<ClassOption> classSelectList;
	private DefaultListModel<ClassOption> classSelectListModel;
	
	private JButton addButton;
	private JButton submitButton;
	private JButton removeButton;
	private JButton clearButton;
	
	private Display display;
	private JPanel limitSectionsList;
	
	private HashMap<ClassOption, ArrayList<Section>> nonInvalidSections;
	private HashMap<ClassOption, ArrayList<Boolean>> invalidSectionMarkings;
	private ClassOption limitingSection;
	private JButton menuButton;
	private JCheckBox useSceduleCheckBox;

	public AddClassScreen(Display display) {
		setBackground(Color.WHITE);
		this.display = display;
		
		nonInvalidSections = new HashMap<>();
		invalidSectionMarkings = new HashMap<>();
		
		setLayout(new MigLayout("", "[][][][][grow]", "[][][][grow][][grow][grow][]"));
		
		JLabel witIconLabel = new JLabel("");
		witIconLabel.setIcon(References.Icon_WIT_Header);
		add(witIconLabel, "cell 0 0 1 4,alignx center,aligny center");
		
		menuButton = new JButton("<HTML><Center>Return<BR>To<BR>Menu</Center></HTML>");
		menuButton.setFont(Fonts.MENU_BUTTON);
		menuButton.setBackground(Color.WHITE);
		add(menuButton, "cell 1 1 1 3,growy");
		
		useSceduleCheckBox = new JCheckBox("Build atop of Current Scedule");
		useSceduleCheckBox.setBackground(Color.WHITE);
		useSceduleCheckBox.setFont(Fonts.STANDARD_LABEL);
		add(useSceduleCheckBox, "cell 4 1,alignx left");
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		panel.setBackground(Color.WHITE);
		add(panel, "cell 4 2,grow");
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(Color.WHITE);
		add(rightPanel, "cell 4 3 1 4,grow");
		rightPanel.setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]", "[][grow][][grow]"));
		
		JPanel limitSectionLabePanel = new JPanel();
		limitSectionLabePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		limitSectionLabePanel.setBackground(new Color(250, 250, 250));
		limitSectionLabePanel.setPreferredSize(new Dimension(References.Icon_WIT_Header.getIconWidth(), 10));
		rightPanel.add(limitSectionLabePanel, "cell 0 0,grow");
		
		JLabel limitSectionLabel = new JLabel("Limit Sections");
		limitSectionLabel.setFont(Fonts.MEDIUM_LABEL);
		limitSectionLabePanel.add(limitSectionLabel);
		
		JScrollPane limitSectionsScrollPane = new JScrollPane();
		limitSectionsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		rightPanel.add(limitSectionsScrollPane, "cell 0 1,grow");
		
		limitSectionsList = new JPanel();
		limitSectionsScrollPane.setViewportView(limitSectionsList);
		limitSectionsList.setLayout(new BoxLayout(limitSectionsList, BoxLayout.Y_AXIS));
		limitSectionsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		JPanel classesLabelPanel = new JPanel();
		rightPanel.add(classesLabelPanel, "cell 0 2");
		classesLabelPanel.setBackground(new Color(250,250,250));
		classesLabelPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		
		JLabel classesLabel = new JLabel("Classes");
		classesLabelPanel.add(classesLabel);
		classesLabel.setFont(Fonts.MEDIUM_LABEL);
		
		JScrollPane classesScrollPane = new JScrollPane();
		rightPanel.add(classesScrollPane, "cell 0 3,grow");
		classesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		classesList = new JList<>();
		classesList.setBackground(SystemColor.menu);
		classesList.setFont(Fonts.STANDARD_LABEL);
		classesList.setModel(classesListModel = new DefaultListModel<>());
		classesScrollPane.setViewportView(classesList);
		
		classesList.addListSelectionListener(this);
		
		JPanel termPanel = new JPanel();
		termPanel.setBackground(new Color(250,250,250));
		termPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(termPanel, "cell 0 4 3 1,grow");
		termPanel.setLayout(new MigLayout("insets 3px", "[][grow]", "[]"));
		
		JLabel termLabel = new JLabel("Select Term: ");
		termLabel.setFont(Fonts.MEDIUM_LABEL);
		termPanel.add(termLabel, "cell 0 0,alignx right,aligny center");
		
		termComboBox = new JComboBox<>();
		termComboBox.setModel(new DefaultComboBoxModel<>(TermPage.getTerms().toArray(new Choise[1])));
		termComboBox.setFont(Fonts.STANDARD_LABEL);
		termPanel.add(termComboBox, "cell 1 0,growx,aligny top");
		
		JPanel dividerPanel = new JPanel();
		dividerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout fl_dividerPanel = (FlowLayout) dividerPanel.getLayout();
		fl_dividerPanel.setVgap(0);
		fl_dividerPanel.setHgap(0);
		add(dividerPanel, "cell 3 0 1 8,grow");
		
		JPanel subjectPanel = new JPanel();
		subjectPanel.setBackground(Color.WHITE);
		subjectPanel.setBorder(UIManager.getBorder("ScrollPane.border"));
		add(subjectPanel, "cell 0 5 3 2,grow");
		subjectPanel.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JPanel subjectSelectPanel = new JPanel();
		subjectSelectPanel.setBackground(Color.WHITE);
		subjectPanel.add(subjectSelectPanel, "cell 0 0,growx,aligny bottom");
		subjectSelectPanel.setLayout(new MigLayout("insets 0px", "[63px][grow]", "[23px]"));
		
		JLabel subjectLabel = new JLabel("Subject: ");
		subjectLabel.setFont(Fonts.MEDIUM_LABEL);
		subjectSelectPanel.add(subjectLabel, "cell 0 0,alignx right,aligny center");
		
		subjectComboBox = new JComboBox<>();
		subjectComboBox.setMaximumRowCount(10);
		subjectComboBox.setFont(Fonts.STANDARD_LABEL);
		subjectComboBox.setModel(subjectComboBoxModel = new DefaultComboBoxModel<>());
		subjectSelectPanel.add(subjectComboBox, "cell 1 0,growx,aligny top");
		
		JScrollPane subjectScrollPane = new JScrollPane();
		subjectPanel.add(subjectScrollPane, "cell 0 1,grow");
		
		classSelectList = new JList<>();
		classSelectList.setFixedCellHeight((int) (25 * Fonts.HEIGHT_SCALE));
		classSelectList.setVisibleRowCount(16);
		classSelectList.setFont(Fonts.STANDARD_LABEL);
		classSelectList.setBackground(SystemColor.menu);
		classSelectList.setModel(classSelectListModel = new DefaultListModel<>());
		classSelectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		subjectScrollPane.setViewportView(classSelectList);
		
		JPanel addPanel = new JPanel();
		addPanel.setBackground(Color.WHITE);
		addPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(addPanel, "cell 0 7 3 1,grow");
		addPanel.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		addButton = new JButton("Add Class");
		addButton.setBackground(Color.WHITE);
		addButton.setFont(Fonts.STANDARD_LABEL);
		addPanel.add(addButton, "cell 0 0");
		
		submitButton = new JButton("Create Schedule");
		submitButton.setBackground(Color.WHITE);
		submitButton.setFont(Fonts.STANDARD_LABEL);
		addPanel.add(submitButton, "cell 1 0,alignx right");
		
		JPanel removePanel = new JPanel();
		removePanel.setBackground(Color.WHITE);
		removePanel.setBorder(new LineBorder(new Color(204, 0, 0)));
		add(removePanel, "cell 4 7,grow");
		removePanel.setLayout(new MigLayout("", "[grow][grow]", "[]"));
		
		removeButton = new JButton("Remove");
		removeButton.setBackground(Color.WHITE);
		removeButton.setFont(Fonts.STANDARD_LABEL);
		removePanel.add(removeButton, "cell 0 0,grow");
		
		clearButton = new JButton("Clear");
		clearButton.setBackground(Color.WHITE);
		clearButton.setFont(Fonts.STANDARD_LABEL);
		removePanel.add(clearButton, "cell 1 0,grow");
		
		addButton.addActionListener(this);
		submitButton.addActionListener(this);
		removeButton.addActionListener(this);
		clearButton.addActionListener(this);
		
		termComboBox.addActionListener(this);
		classSelectList.addListSelectionListener(this);
		
		for(Choise subject : DepartmentSelectPage.getDepartments((Choise) termComboBox.getSelectedItem())) 
			subjectComboBoxModel.addElement(subject);
		
		subjectComboBoxModel.setSelectedItem(null);
		classSelectListModel.clear();

		subjectComboBox.addActionListener(this);
		menuButton.addActionListener(this);
		useSceduleCheckBox.addActionListener(this);
		
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
		if(e.getSource() == useSceduleCheckBox) {
			int classCount = classesListModel.getSize();
			submitButton.setEnabled(classCount > 1 || (classCount > 0 && useSceduleCheckBox.isSelected()));
			return;
		}
		
		if(e.getSource() == menuButton) {
			display.switchToMainMenu();
			return;
		}
		
		if(e.getSource() == removeButton) {
			for(int index : classesList.getSelectedIndices())
				classesListModel.remove(index);
			submitButton.setEnabled(classesListModel.size() > 1);
			
			limitSectionsList.removeAll();
			limitSectionsList.updateUI();
			
			return;
		}
		
		if(e.getSource() == addButton) {
			ClassOption value = classSelectList.getSelectedValue();
			if(classesListModel.contains(value)) return;
			classesListModel.addElement(value);
			
			submitButton.setEnabled(classesListModel.size() > 1);
			return;
		}
		
		if(e.getSource() == termComboBox) {
			subjectComboBoxModel.removeAllElements();
			
			SwingUtilities.invokeLater(() -> {
				
				ArrayList<Choise> subjects = DepartmentSelectPage.getDepartments((Choise) termComboBox.getSelectedItem());
				
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
				ArrayList<ClassOption> classes = DepartmentSelectPage.selectDepartment(
						(Choise) termComboBox.getSelectedItem(), (Choise) subjectComboBox.getSelectedItem()).getClassList();
				
				for(ClassOption clazz : classes)
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
			ArrayList<ClassOption> classes = new ArrayList<>(classesListModel.size());
			for(int i = 0; i < classesListModel.size(); i ++)
				classes.add(classesListModel.get(i));
			
			display.submitClasses(classes, nonInvalidSections, invalidSectionMarkings, useSceduleCheckBox.isSelected());
			return;
		}
		
		if(e.getActionCommand().startsWith("invalidate|")) {
			if(limitingSection == null) return;
			
			int compIndex = Integer.parseInt(((JCheckBox) e.getSource()).getName());
			invalidSectionMarkings.get(limitingSection).set(compIndex, ((JCheckBox) e.getSource()).isSelected());
			return;
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == classesList) {
			removeButton.setEnabled(classesList.getSelectedIndex() != -1);
			
			if(classesList.getSelectedValue() != null && !classesList.getSelectedValue().equals(limitingSection)) {
				limitingSection = classesList.getSelectedValue();
				if(limitingSection == null) return;

				limitSectionsList.removeAll();
				limitSectionsList.updateUI();
				
				SwingUtilities.invokeLater(() -> {
					limitSectionsList.removeAll();
					limitSectionsList.updateUI();
					
					ArrayList<Section> sections;
					if((sections = nonInvalidSections.get(limitingSection)) == null) {
						
						sections = DepartmentSelectPage.selectClass(
								limitingSection.getTerm(), limitingSection.getDepartment(), limitingSection).getSections();
						nonInvalidSections.put(limitingSection, sections);
						
						ArrayList<Boolean> markings = new ArrayList<>();
						for(int i = 0; i < sections.size(); i ++)
							markings.add(true);
						invalidSectionMarkings.put(limitingSection, markings);
					}
					
					int i = 0;
					ArrayList<Boolean> markings = invalidSectionMarkings.get(limitingSection);
					
					for(Section section : sections) {
						JCheckBox valid = new JCheckBox(section.getSectionId() + " | " + 
								section.getCourseNumber() + "  " + section.getInstructor());
						
						valid.setActionCommand("invalidate" + "|" + section.getSectionId());
						valid.setFont(Fonts.STANDARD_LABEL);
						valid.addActionListener(AddClassScreen.this);
						valid.setSelected(markings.get(i));
						valid.setName(i ++ + "");
						
						limitSectionsList.add(valid);
					}
					
					limitSectionsList.updateUI();
				});
			}
			
			return;
		}
		
		if(e.getSource() == classSelectList) {
			addButton.setEnabled(classSelectList.getSelectedIndex() != -1);
			return;
		}
	}
}
