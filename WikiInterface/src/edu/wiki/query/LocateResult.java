package edu.wiki.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class LocateResult {
	ArrayList<ResultFormat> resultStackFinal = new ArrayList<ResultFormat>();

	public LocateResult(Hashtable<Integer, ArrayList<ArrayList<String>>> result) {

		ArrayList<ResultFormat> resultStack = new ArrayList<ResultFormat>();
		ArrayList<Integer> offsets = new ArrayList<Integer>();

		for (int i : result.keySet()) {
			double rank = 0.0;
			// int lenge = result.get(i).size();
			int min = 0;
			int max = 0;
			int size = 0;
			int times = 0;

			ArrayList<Double> idf = new ArrayList<Double>();
			ArrayList<Integer> fre = new ArrayList<Integer>();

			for (ArrayList<String> arrayList : result.get(i)) {
				String offsetString = arrayList.get(0);
				offsets = deal(offsetString);
				fre.add(offsets.size());
				size = offsets.size() * offsets.size();
				idf.add(Double.parseDouble(arrayList.get(1)));
				int n = offsets.get(0);
				if (times == 0) {
					max = n;
					min = n;
				}
				if (n >= max) {
					max = n;
				}
				if (n <= min) {
					min = n;
				}

				int lenge = idf.size();
				for (int j = 0; j < lenge; j++) {
					rank += fre.get(j) / Math.sqrt(size) * idf.get(j) * idf.get(j);
				}
				times++;
				resultStack.add(new ResultFormat(rank, i, min, max));
			}

		}
		// ArrayList<ResultFormat> resultFinal = setToArray(resultStack);
		int docId = -1;
		int min = 1000000000;

		// System.out.println(resultStack.size());

		Iterator<ResultFormat> it = resultStack.iterator();

		while (it.hasNext()) {
			ResultFormat rf = it.next();
			// System.out.print(rf.id + " "+ rf.low +",");
			// ResultFormat rf = resultFinal.get(i);
			if (rf.id != docId) {
				docId = rf.id;
				min = rf.high - rf.low;
				// System.out.println(1);
			} else {
				it.remove();
				// i++;
			}

		}

		// System.out.println(resultStack.size());
		Collections.sort(resultStack);
		// System.out.println(resultStack.size());
		this.resultStackFinal = resultStack;
	}

	public ArrayList<ResultFormat> setToArray(Set<ResultFormat> in) {
		ArrayList<ResultFormat> out = new ArrayList<ResultFormat>();
		for (ResultFormat rf : in) {
			out.add(rf);
		}
		return out;
	}

	public static ArrayList<ResultFormat> copy(ArrayList<ResultFormat> from) {
		ArrayList<ResultFormat> result = new ArrayList<ResultFormat>();
		for (ResultFormat rf : from) {
			result.add(rf);
		}
		return result;
	}

	public ArrayList<Integer> deal(String s) {
		s = toTakeShellOff(s);
		String[] off = s.split(",");
		ArrayList<Integer> offset = new ArrayList<Integer>();
		for (String str1 : off) {
			int im = Integer.parseInt(str1.trim());
			// System.out.println(im);
			offset.add(im);
		}
		return offset;
	}

	public void printResult() {
		for (ResultFormat rf : this.resultStackFinal) {
			System.out.println(rf.id + " " + rf.low + " " + rf.high);
		}
	}

	public ArrayList<Integer> charToArray(String s) {
		String[] string = s.split(",");
		ArrayList<Integer> offset = new ArrayList<Integer>();
		for (int i = 0; i < string.length; i++) {
			string[i] = string[i].trim();
			offset.add(i, Integer.parseInt(string[i]));
		}
		return offset;
	}

	public static String toTakeShellOff(String s) {
		return s.substring(1, s.length() - 1);
	}

	public List<ResultFormat> getResult() {
		return this.resultStackFinal;
	}
}
