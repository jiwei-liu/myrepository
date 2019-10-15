package al;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Tools {
	
	static int[][] sequenceMatrix = generateSequenceMatrix();
	
	static int[][] timeMatrix = generateTimeMatrix();
	
	static int[][] timeAndSequenceMatrix = generateTimeAndSequenceMatrix();
	
	static Map<String, Object> getPlanFromSolution(int[] solution){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(1, 1);
		map.put(2, 1);
		map.put(3, 1);
		map.put(4, 1);
		map.put(5, 1);
		map.put(6, 1);
		
		int[] mArray = new int[6];
		int[] iArray = new int[6];
		
		Map<Integer, Machine> machineMap = new HashMap<Integer, Machine>();
		
		for(int s : solution){
			int k = map.get(s);
			calculateStartTime(s, k, iArray, mArray, machineMap);
			map.put(s, ++k);
		}
		
		int total = calculateTotalTime(iArray, mArray);
		
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("totalTime", total);
		returnMap.put("machineMap", machineMap);
		return returnMap;
	}
	
	static Map<String, Object> getPlanFromSolution(Chromosome2 solution){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(1, 1);
		map.put(2, 1);
		
		int[] mArray = new int[5];
		int[] iArray = new int[2];
		
		Map<Integer, Machine> machineMap = new HashMap<Integer, Machine>();

		for(int s : solution.os){
			int k = map.get(s);
			calculateStartTimeFJSSP(solution,s, k, iArray, mArray, machineMap);
			map.put(s, ++k);
		}
		
		int total = calculateTotalTime(iArray, mArray);
		
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("totalTime", total);
		returnMap.put("machineMap", machineMap);
		return returnMap;
	}
	
	static int calculateStartTimeFJSSP(Chromosome2 solution, int s, int k, int[] iArray, int[] mArray, 
					Map<Integer, Machine> machineMap ) {
		int m = generateMachineNumberFJSSP(solution, s, k);
//		System.out.println("机器： " + m);
		Machine machine = machineMap.get(m - 1);
		if(machine == null) {
			machine = new Machine();
			machineMap.put(m - 1, machine);
		}
		int startTime = 0;
		if(k == 1) {
			int lastMachineTime = mArray[m - 1];
			startTime = lastMachineTime;
		}
		else {
			int lastMachineTime = mArray[m - 1];
			int lastToyTime = iArray[s - 1];
			startTime = (lastMachineTime > lastToyTime) ? lastMachineTime : lastToyTime;
		}
		int cost = timeAndSequenceMatrix[getOrder(s, k)][m - 1];
		Task t = new Task();
		t.start  = startTime;
		t.cost  = cost;
		t.productNum = s;
		machine.tasks.add(t);
		mArray[m - 1] = startTime + cost;
		iArray[s - 1] = startTime + cost;
		
		return startTime;
	}

	private static int generateMachineNumberFJSSP(Chromosome2 solution, int s, int k) {
		int machine = solution.ms[getOrder(s, k)];
		return machine;
	}
	
	private static int getOrder(int s, int k) {
		int order = s * 10 + k;
		Map<Integer, Integer> m = getMap();
		return m.get(order) - 1;
	}

	/**
	   *    玩具i 的第k 个工序在机器m 上进行加工，其开始时间为：
	 * 
	 * {max(玩具i 第k-1个工序结束时间，机器M当前已安排作业的最后时间) 	k > 1
	 * 	max(机器M当前已安排作业的最后时间)   k = 1
	 * 
	 */
	static int calculateStartTime(int i, int k, int[] iArray, int[] mArray, 
				Map<Integer, Machine> machineMap) {
		int m = generateMachineNumber(i, k);
		Machine machine = machineMap.get(m - 1);
		if(machine == null) {
			machine = new Machine();
			machineMap.put(m - 1, machine);
		}
//		System.out.println("机器： " + m);
		int startTime = 0;
		if(k == 1) {
			int lastMachineTime = mArray[m - 1];
			startTime = lastMachineTime;
		}
		else {
			int lastMachineTime = mArray[m - 1];
			int lastToyTime = iArray[i - 1];
			startTime = (lastMachineTime > lastToyTime) ? lastMachineTime : lastToyTime;
		}
		int cost = timeMatrix[i - 1][m - 1];
		Task t = new Task();
		t.start  = startTime;
		t.cost  = cost;
		t.productNum = i;
		machine.tasks.add(t);
		mArray[m - 1] = startTime + cost;
		iArray[i - 1] = startTime + cost;
		
		return startTime;
	}
	
	static void drawGaunt(Map<Integer, Machine> machineMap) {
		Map<Integer, Machine> map = machineMap;
		for(Map.Entry<Integer, Machine> e: map.entrySet()) {
			Machine m = e.getValue();
			int lastindex = 0;
			StringBuilder sb = new StringBuilder();
			for(Task t : m.tasks) {
				int gap = t.start - lastindex;
				for (int i = 0; i < gap; i++) {
					sb.append(" ");
				}
				for (int i = 0; i < t.cost; i++) {
					sb.append(t.productNum);
				}
				lastindex = t.start + t.cost;
			}
			System.out.println("机器" + (e.getKey() + 1) + " : " + sb);
		}
	}
	
	static int calculateTotalTime(int[] iArray, int[] mArray) {
		int max = 0;
		for(int i : iArray) {
			if(i > max) {
				max = i;
			}
		}
		for(int m : mArray) {
			if(m > max) {
				max = m;
			}
		}
		System.out.println("currentTotalTime:  " + max);
		return max;
	}
	
	private static int generateMachineNumber(int i, int k) {
		int[] sequence = sequenceMatrix[i - 1];
		for(int s = 0;s < sequence.length; s++) {
			if(sequence[s] == k) {
				return s + 1;
			}
		}
		return -1;
	}
	
	private static int[][] generateTimeMatrix() {
		int[] time1 = {3,10,9,5,3,10};
		int[] time2 = {6,8,1,5,3,3};
		int[] time3 = {1,5,5,5,9,1};
		int[] time4 = {7,4,4,3,1,3};
		int[] time5 = {6,10,7,8,5,4};
		int[] time6 = {3,10,8,9,4,9};
		
		int[][] timeMatrix = new int[6][6];
		timeMatrix[0] = time1;
		timeMatrix[1] = time2;
		timeMatrix[2] = time3;
		timeMatrix[3] = time4;
		timeMatrix[4] = time5;
		timeMatrix[5] = time6;
		
		return timeMatrix;
	}

	private static int[][] generateSequenceMatrix() {
		int[] sequence1 = {3,1,2,4,6,5};
		int[] sequence2 = {2,3,5,6,1,4};
		int[] sequence3 = {3,4,6,1,2,5};
		int[] sequence4 = {2,1,3,4,5,6};
		int[] sequence5 = {3,2,5,6,1,4};
		int[] sequence6 = {2,4,6,1,5,3};
		
		int[][] sequenceMatrix = new int[6][6];
		sequenceMatrix[0] = sequence1;
		sequenceMatrix[1] = sequence2;
		sequenceMatrix[2] = sequence3;
		sequenceMatrix[3] = sequence4;
		sequenceMatrix[4] = sequence5;
		sequenceMatrix[5] = sequence6;
		
		return sequenceMatrix;
	}
	
	private static int[][] generateTimeAndSequenceMatrix() {
		int[] sequence1 = {3,0,2,1,4};
		int[] sequence2 = {0,5,0,3,0};
		int[] sequence3 = {4,8,0,0,3};
		int[] sequence4 = {0,8,2,2,1};
		int[] sequence5 = {7,4,3,1,0};
		
		int[][] matrix = new int[5][5];
		matrix[0] = sequence1;
		matrix[1] = sequence2;
		matrix[2] = sequence3;
		matrix[3] = sequence4;
		matrix[4] = sequence5;
		
		return matrix;
	}
	
	public static int[] generateMs() {
		int[] ms = new int[5];
		int count = 0;
		for(int[] row : timeAndSequenceMatrix) {
			for (;;) {
				Random r = new Random();
				int index = r.nextInt(5);
				if (row[index] != 0) {
					ms[count++] = index + 1;
					break;
				} 
			}
		}
		return ms;
	}
	
	static Map  getMap(){
		Map m = new HashMap();
		m.put(11, 1);
		m.put(12, 2);
		m.put(21, 3);
		m.put(22, 4);
		m.put(23, 5);
		
		return m;
	}

}
