package edu.wiki.query;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanRetrieval {
	private String[] query;
	private Stack<Set<Integer>> stack;
	private ArrayList<String> wordsHaveSearched;
	private ArrayList<WordIndex> wordAndInfo = new ArrayList<WordIndex>();
	private Hashtable<Integer, ArrayList<ArrayList<String>>> result;
	
	
	LocateWord t;

	public BooleanRetrieval(String s, String input, String output) {
		
		this.t = new LocateWord(input, output);
		this.query = s.split("\\s+");
		this.stack = new Stack<Set<Integer>>();
		this.wordsHaveSearched = wordsList(getWordsToSearch());
		this.result = new Hashtable<Integer, ArrayList<ArrayList<String>>>();
		
		
	}

	private String[] getQuery() {
		return this.query;
	}

	// combine all words into a string
	private String getWordsToSearch() {
		String s = "";
		for (String t : getQuery()) {
			if (!t.equals("AND") && !t.equals("OR") && !t.equals("NOT")) {
				s += t + ";";
			}
		}
		return s.substring(0, s.length() - 1);
	}

	private Stack<Set<Integer>> getStack() {
		return this.stack;
	}

	public Set<Integer> runQuery() {
		for (String t : getQuery()) {
			if (t.equals("AND")) {
				performAND();
			} else if (t.equals("OR")) {
				performOR();
			} else if (t.equals("NOT")) {
				performNOT();
			} else {
				pushTerm(t);
			}
		}
		// System.out.println(getStack().size());

		return getStack().pop();

	}

	// input a string of all words needed to search
	// output a array of word and its index
	private ArrayList<String> wordsList(String s) {
		deleteOutputDir();
		String result = "";
		try {
			result = t.doLocateWord(s);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//System.out.println(result);
		return stringToArray(result);
	}

	private void pushTerm(String term) {
		//System.out.println(term);
		Set<Integer> im = new HashSet<Integer>();
		for (String word : this.wordsHaveSearched) {
			//System.out.println(word);
			// split the info into two parts:word and its index
			String[] wordAndInfo = word.split("\\s+", 2);
			if (term.equals(wordAndInfo[0])) {
				WordIndex wi = new WordIndex(wordAndInfo[1]);
				this.wordAndInfo.add(wi);
				// wi.toIdAndOffset();
				im = wi.makeIdSet();
				//System.out.println(im.size());
			}
		}
		stack.push(im);
	}

	private void performOR() {
		Set<Integer> s1 = stack.pop();
		Set<Integer> s2 = stack.pop();

		Set<Integer> sn = new TreeSet<Integer>();

		for (int n : s1) {
			sn.add(n);
		}

		for (int n : s2) {
			sn.add(n);
		}

		stack.push(sn);
	}

	private void performAND() {
		Set<Integer> s1 = stack.pop();
		//System.out.println(s1.size()+"ff");
		Set<Integer> s2 = stack.pop();
		//System.out.println(s2.size()+"tt");
		Set<Integer> sn = new TreeSet<Integer>();

		for (int n : s1) {
			if (s2.contains(n)) {
				sn.add(n);
			}
		}
		stack.push(sn);
	}

	private void performNOT() {
		Set<Integer> s1 = stack.pop();
		Set<Integer> s2 = stack.pop();

		Set<Integer> sn = new TreeSet<Integer>();

		for (int n : s2) {
			if (!s1.contains(n)) {
				sn.add(n);
			}
		}
		stack.push(sn);
		stack.push(sn);
	}

	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
				System.out.println("Directory is deleted : "
						+ file.getAbsolutePath());
			} else {
				// list all the directory contents
				String files[] = file.list();
				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);
					// recursive delete
					delete(fileDelete);
				}
				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					System.out.println("Directory is deleted : "
							+ file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}

	public static void deleteOutputDir() {
		File directory = new File("output");

		// make sure directory exists
		if (!directory.exists()) {
			System.out.println("Directory does not exist.");

		} else {
			try {
				delete(directory);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		System.out.println("Done");
	}

	public ArrayList<String> stringToArray(String s) {
		int n = 0;
		ArrayList<String> result = new ArrayList<String>();
		String[] words = s.split(":");
		for (String word : words) {
			word.trim();
			result.add(n, word);
			n++;
		}
		// System.out.println(result.size());
		return result;

	}

	public Hashtable<Integer, ArrayList<ArrayList<String>>> resultIdAndOffset(Set<Integer> idSet) {
		
		for (int i : idSet) {
			for (WordIndex k : this.wordAndInfo) {
				String str = k.getIndexSetById(i);
				if (str != null) {
					if (result.containsKey(i) == false) {
						ArrayList<ArrayList<String>> set1 = new ArrayList<ArrayList<String>>();
						ArrayList<String> offsetAndIdf = new ArrayList<String>();
						offsetAndIdf.add(0,str);
						offsetAndIdf.add(1,k.getIDF());
						set1.add(offsetAndIdf);
						result.put(i, set1);
					} else {
						ArrayList<ArrayList<String>> set2 = result.get(i);
						ArrayList<String> offsetAndIdf = new ArrayList<String>();
						offsetAndIdf.add(0,str);
						offsetAndIdf.add(1,k.getIDF());
						set2.add(offsetAndIdf);
						result.remove(i);
						result.put(i, set2);
					}
				}
			}
		}
		printResult();
		return this.result;
	}

	public void printResult() {
		Set<Integer> keys = this.result.keySet();
		for (int i : keys) {
			System.out.println(i + " " + arrayListToString(this.result.get(i)));
		}
	}

	public String arrayListToString(ArrayList<ArrayList<String>> input) {
		String result = "";
		for (ArrayList<String> str : input) {
			result += "offset: "+ str.get(0) +  ", idf: " +str.get(1) + ";";
		}
		return result.substring(0, result.length() - 1);
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out
					.println("Usage: <input path> <ouput path>");
			System.exit(1);
		}
		
		String exp = "angiosperms";
		ExpParser e = new ExpParser();
		//System.out.println(e.parse(exp));
		BooleanRetrieval test = new BooleanRetrieval(e.parse(exp), args[0], args[1]);

		Set<Integer> result = test.runQuery();

		if (result.size() > 0) {
			new LocateResult(test.resultIdAndOffset(result)).printResult();
		} else
			System.out.println("No such words");
		// System.out.println(result.size());

	}

}
