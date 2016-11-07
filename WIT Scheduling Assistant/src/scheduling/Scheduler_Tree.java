package scheduling;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import pages.ClassOption;
import pages.DepartmentSelectPage;

public class Scheduler_Tree {
	
	public static enum Stage {
		Initializing(true, "Initializing"),
		CollectingSections(false, "Gathering all viable Sections"),
		CalculatingConfigs(false, "Calculating possible class configurations"),
		BuilingTree(false, "Building Tree"),
		RunningTree(false, "Accumulating valid Schedules"),
		CalculatingWeights(false, "Calculating Schedule Rankings"),
		Done(false, "Done");
		
		private String message;
		private boolean indeterminate;
		
		private Stage(boolean indeterminate, String message) {
			this.message = message;
			this.indeterminate = indeterminate;
		}

		public String getMessage() { return message; }
		public boolean isIndeterminate() { return indeterminate; }
	}
	
	private Stage stage;
	private int current, max;
	
	private ArrayList<ClassOption> classes;
	private HashMap<ClassOption, ArrayList<Section>> allSections;
	private ArrayList<TreeSchedule> schedules;
	
	private Node root;
	private int size;

	public Scheduler_Tree(ArrayList<ClassOption> classes) { this(); run(classes, null, null, null); }
	public Scheduler_Tree() { this.stage = Stage.Initializing; }

	public void run(ArrayList<ClassOption> classes, float[][] rankings,
		HashMap<ClassOption, ArrayList<Section>> preCollectedSections, HashMap<ClassOption, ArrayList<Boolean>> nonViable) {
	
		this.current = 0; 
		this.max = classes.size();
		visualDelay();
		
		this.stage = Stage.CollectingSections;
	
		if(preCollectedSections != null) {
			for(ClassOption key : preCollectedSections.keySet()) {
				ListIterator<Section> clean = preCollectedSections.get(key).listIterator();
				ListIterator<Boolean> marks = nonViable.get(key).listIterator();
	
				while(clean.hasNext()) {
					clean.next();
					
					if(!marks.next()) 
						clean.remove();
				}
				
				current ++;
			}
		}
		
		allSections = new HashMap<>();
		
		ArrayList<Section> pre;
		for(ClassOption option : classes) {
			allSections.put(option, (pre = preCollectedSections.get(option)) != null ? pre :
					DepartmentSelectPage.selectClass(option.getTerm(), option.getDepartment(), option).getViableSections()
				);
			if(pre == null) this.current ++;
		}
		
		this.classes = new ArrayList<>(allSections.keySet());
		
		size = buildTree();
		schedules = collectSchedules();
		if(rankings != null) {
			weighSchedules(rankings);
			schedules.sort(Comparator.reverseOrder());
		}
		
		this.current = 1; this.max = 1; 
		this.stage = Stage.Done;
	}
	
	private void weighSchedules(float[][] rankings) {
		visualDelay();
		this.current = 0; this.max = schedules.size(); 
		this.stage = Stage.CalculatingWeights;
		
		for(TreeSchedule schedule : schedules) {
			schedule.calculateWeight(rankings);
			current ++;
		}
	}
	
	static HashMap<ClassOption, Color> createColorMap(Collection<? extends ClassAccessor> sections) {
		HashMap<ClassOption, Color> colorMap = new HashMap<>();
		HashMap<ClassOption, Integer> numberMap = new HashMap<>();
		
		Iterator<? extends ClassAccessor> iter = sections.iterator(); int i = 0;
		while(iter.hasNext()) numberMap.putIfAbsent(iter.next().getClassOption(), i ++);
		
		float colorIncrement = .8f / (float) i;
		
		iter = sections.iterator();
		while(iter.hasNext()) {
			ClassOption clazz = iter.next().getClassOption();
			colorMap.put(clazz, Color.getHSBColor(numberMap.get(clazz) * colorIncrement, .85f, 1));
		}
		
		return colorMap;
	}
	
	private ArrayList<TreeSchedule> collectSchedules() {
		visualDelay();
		this.current = 0; this.max = size; this.stage = Stage.RunningTree;
		
		HashMap<ClassOption, Color> colorMap = Scheduler_Tree.createColorMap(classes);
		
		ArrayList<TreeSchedule> schedules = new ArrayList<>();
		for(ArrayList<ClassConfig> sections : runBranch(root, null))
			schedules.add(new TreeSchedule(sections, colorMap));
		return schedules;
	}
	
	public ArrayList<ArrayList<ClassConfig>> runBranch(Node root, ArrayList<ArrayList<ClassConfig>> accumulator) {
		if(accumulator == null)
			accumulator = new ArrayList<>();
		
		if(root.isInvalid()) { this.current += root.subLayerSize; return accumulator; }
		
		if(root.children.length < 1) {
			ArrayList<ClassConfig> sections = new ArrayList<>();
			do sections.add(root.config); while((root = root.parent).parent != null);
			accumulator.add(sections);
			
			this.current += 1;
			return accumulator;
		}
		
		for(Node child : root.children)
			runBranch(child, accumulator);
		return accumulator;
	}
	
	private int buildTree() {
		ClassConfig[][] allConfigs = new ClassConfig[classes.size()][];
		
		visualDelay();
		this.current = 0; this.max = allConfigs.length;
		this.stage = Stage.CalculatingConfigs;
		
		int last = 1; int nextMax = 0;
		for(int i = 0; i < allConfigs.length; i ++) {
			ArrayList<ClassConfig> allClassConfigs = new ArrayList<>();
			ClassOption subject = classes.get(i);
			
			for(Section section : allSections.get(subject)) 
				allClassConfigs.addAll(section.calculateConfigurations());
			
			if(allClassConfigs.isEmpty()) 
				throw new SchedulingException("No Sections avalible for " + subject.getName() + ":" + subject.getNumber());
			
			allConfigs[i] = allClassConfigs.toArray(new ClassConfig[0]);
			nextMax += last *= allConfigs[i].length;
			this.current = i;
		}

		visualDelay();
		this.max = nextMax; this.current = 0;
		this.stage = Stage.BuilingTree; 
		
		return expandBranch(allConfigs, 0, root = Node.makeRoot(allConfigs[0].length));
	}
	
	private int expandBranch(ClassConfig[][] configs, int level, Node root) {
		boolean hasNext = level + 1 < configs.length;
		this.current += 1;
		
		for(int j = 0; j < configs[level].length; j ++)
			root.createNode(configs[level][j], hasNext ? configs[level + 1].length : 0);
		if(!hasNext) return 1;
		
		int size = 0;
		for(Node child : root.children) {
			if(child.isInvalid()) { 
				this.current += root.subLayerSize = calcSubLayerSize(configs, level + 1); 
				continue; 
			}
			
			size += expandBranch(configs, level + 1, child);
		}
		
		return size;
	}
	
	private int calcSubLayerSize(ClassConfig[][] configs, int layer) {
		int total = 0, last = 1;
		for(int i = layer + 1; i < configs.length; i ++)
			total += last*= configs[i].length;
		return total;
	}
	
	private void visualDelay(double... multi) {
		try { Thread.sleep((long) (250 * (multi.length > 0 ? multi[0] : 1))); }
		catch(InterruptedException e) { }
	}
	
	public static final class Node {
		private Node parent;
		
		private ClassConfig config;
		
		private int index;
		private Node[] children;
		private boolean invalid;
		
		private int subLayerSize;

		public static Node makeRoot(int childCount) {
			return new Node(null, null, childCount);
		}
		
		private Node(Node parent, ClassConfig config, int childCount) {
			this.parent = parent;
			this.config = config;
			
			this.children = new Node[childCount];
			this.invalid = false;
		}
		
		public Node createNode(ClassConfig config, int childCount) {
			if(invalid) throw new IllegalStateException("Node is INVALID child can not be created");
			if(index >= children.length) throw new ArrayIndexOutOfBoundsException("All children have been created!");
			
			ArrayList<ClassConfig> toComp = new ArrayList<>(); Node next = this; 
			do toComp.add(next.config); while((next = next.parent) != null);
			
			Node newNode = new Node(this, config, childCount);
			
			for(ClassConfig primary : toComp) {
				if(config.doesConflict(primary)) {
					newNode.invalid = true;
					break;
				}
			}
			
			return children[index ++] = newNode;
		}
		
		public boolean isInvalid() { return invalid; }
	}
	
	public Stage getStage() { return stage; }
	public int getStageMax() { return max; }
	public int getCurrent() { return current;}
	
	public ArrayList<TreeSchedule> getSchedules() { return schedules; }
}
