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
import pages.wit.LookupResultsPage.LookupResult;
import scheduling.Section;
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
	private JPanel limitSectionsList;
	
	private HashMap<LookupResult, ArrayList<Section>> nonInvalidSections;
	private HashMap<LookupResult, ArrayList<Boolean>> invalidSectionMarkings;
	private volatile LookupResult limitingSection;

	public AddClassScreen(PageLock pages, Display display) {
		setBackground(Color.WHITE);
		this.pages = pages;
		this.display = display;
		
		nonInvalidSections = new HashMap<>();
		invalidSectionMarkings = new HashMap<>();
		
		setLayout(new MigLayout("", "[][][grow]", "[][][grow][][grow][grow][]"));
		
		JLabel witIconLabel = new JLabel("");
		witIconLabel.setIcon(References.Icon_WIT_Header);
		add(witIconLabel, "cell 0 0 1 3,alignx center,aligny center");
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(Color.WHITE);
		add(rightPanel, "cell 2 1 1 5,grow");
		rightPanel.setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]", "[][grow][][grow]"));
		
		JPanel limitSectionLabePanel = new JPanel();
		limitSectionLabePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		limitSectionLabePanel.setBackground(new Color(250, 250, 250));
		rightPanel.add(limitSectionLabePanel, "cell 0 0,grow");
		
		JLabel limitSectionLabel = new JLabel("Limit Sections");
		limitSectionLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
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
		classesLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JScrollPane classesScrollPane = new JScrollPane();
		rightPanel.add(classesScrollPane, "cell 0 3,grow");
		classesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		classesList = new JList<>();
		classesList.setBackground(SystemColor.menu);
		classesList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		classesList.setModel(classesListModel = new DefaultListModel<>());
		classesScrollPane.setViewportView(classesList);
		
		classesList.addListSelectionListener(this);
		
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
		add(dividerPanel, "cell 1 0 1 7,grow");
		
		JPanel subjectPanel = new JPanel();
		subjectPanel.setBackground(Color.WHITE);
		subjectPanel.setBorder(UIManager.getBorder("ScrollPane.border"));
		add(subjectPanel, "cell 0 4 1 2,grow");
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
		add(addPanel, "cell 0 6,grow");
		addPanel.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		addButton = new JButton("Add Class");
		addButton.setBackground(Color.WHITE);
		addButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		addPanel.add(addButton, "cell 0 0");
		
		submitButton = new JButton("Create Schedule");
		submitButton.setBackground(Color.WHITE);
		submitButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		addPanel.add(submitButton, "cell 1 0,alignx right");
		
		JPanel removePanel = new JPanel();
		removePanel.setBackground(Color.WHITE);
		removePanel.setBorder(new LineBorder(new Color(204, 0, 0)));
		add(removePanel, "cell 2 6,grow");
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
			
			display.submitClasses(classes, nonInvalidSections, invalidSectionMarkings);
			return;
		}
		
		if(e.getActionCommand().startsWith("invalidate|")) {
			if(limitingSection == null) return;
			
			int compIndex = Integer.parseInt(((JCheckBox) e.getSource()).getName());
			invalidSectionMarkings.get(getLimitingSection()).set(compIndex, ((JCheckBox) e.getSource()).isSelected());
			return;
		}
	}

	private boolean currentlyAdding;
	private final Object lock = new Object();
	
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == classesList) {
			removeButton.setEnabled(classesList.getSelectedIndex() != -1);
			if(!removeButton.isEnabled() && !currentlyAdding) {
				limitSectionsList.removeAll();
				limitSectionsList.updateUI();
			}
			
			new Thread(() -> {
				if(currentlyAdding) {
					synchronized(lock) {
						try { lock.wait(250); } 
						catch(Exception e1) { }
					}
				}

				currentlyAdding = true;
				setLimitingSection(null);
				
				if(removeButton.isEnabled()) {
					setLimitingSection(classesList.getSelectedValue());
					if(getLimitingSection() == null) {
						currentlyAdding = false;
						return;
					}
					
					ArrayList<LookupResult> wrapper = new ArrayList<>();
					wrapper.add(getLimitingSection());

					ArrayList<Section> sections;
					if((sections = nonInvalidSections.get(getLimitingSection())) == null) {
						sections = pages.collectSections(wrapper).get(getLimitingSection());
						if(sections == null) return;
						
						nonInvalidSections.put(getLimitingSection(), sections);
						
						ArrayList<Boolean> markings = new ArrayList<>();
						for(int i = 0; i < sections.size(); i ++)
							markings.add(true);
						invalidSectionMarkings.put(getLimitingSection(), markings);
					}
						
					int i = 0;
					ArrayList<Boolean> markings = invalidSectionMarkings.get(getLimitingSection());
					limitSectionsList.removeAll();
					
					for(Section section : sections) {
						JCheckBox valid = new JCheckBox(section.getSectionId() + " | " + 
								section.getCourseNumber() + "  " + section.getInstructor());
						
						valid.setActionCommand("invalidate" + "|" + section.getSectionId());
						valid.setFont(new Font("Tahoma", Font.PLAIN, 14));
						valid.addActionListener(AddClassScreen.this);
						valid.setSelected(markings.get(i));
						valid.setName(i ++ + "");
						
						limitSectionsList.add(valid);
					}
					
					limitSectionsList.updateUI();
					currentlyAdding = false;
					
					synchronized(lock) {
						lock.notifyAll();
					}
				}
			}).start();
			
			return;
		}
		
		if(e.getSource() == classSelectList) {
			addButton.setEnabled(classSelectList.getSelectedIndex() != -1);
			return;
		}
	}
	
	private LookupResult getLimitingSection() { return limitingSection; }
	private void setLimitingSection(LookupResult limitingSection) { this.limitingSection = limitingSection; }
}
