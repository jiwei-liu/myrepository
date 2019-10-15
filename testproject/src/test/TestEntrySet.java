package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TestEntrySet {

	public static void main(String[] args) {
		HashMap<Integer, String> m = new  HashMap<Integer, String>();
		m.put(3, "a");
		m.put(2, "b");
		m.put(1, "c");
		m.put(4, "c");
		
		Set<Map.Entry<Integer, String>> s = m.entrySet();
		for(Map.Entry e : s) {
			System.out.println(e.getKey());
		}
		
		
		SortedSet<Integer> ss = new TreeSet<Integer>(m.keySet());
		for(Integer i : ss) {
			System.out.println(i);
		}
	}

}
