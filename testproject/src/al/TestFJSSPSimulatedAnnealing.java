package al;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestFJSSPSimulatedAnnealing {
	
	public static void main(String[] args) {
		
		Long start = System.currentTimeMillis();
		
		//1  初始化，设定模拟退火算法的参数 T0 = 1，α = 0.999，Tend = 10-3
		//        取一个足够大的初始温度T0，令当前温度T = T0，给定一个初始解S1。
		double T0 = 1; double alpha = 0.999; double Tend = 0.001;
		double T = T0;
		Chromosome2 S1 = initializeChromosome();
		int count = 0;
		do {
			//2  随机调换当前解 S1 的两个位置的数值，产生一个新解 S2
			Chromosome2 S2 = generateNewSolution(S1);
			//3  计算增量，Δ = f(S2)– f(S1)，其中f为计算解的总生产时间的函数。
			int delta = (int)Tools.getPlanFromSolution(S2).get("totalTime") - (int)Tools.getPlanFromSolution(S1).get("totalTime");
			//4  如果Δ < 0 则接受 S2作为新的当前解，S2=S1 。否则，计算 S2的接受概率c1.
			//  即在[0,1]这个区间上面产生一个均匀分布的随机数rand，如果c1 > rand，
			//	则接受 S2作为新的当前解，否则保留当前解 S1。
			if(delta < 0) {
				S1 = S2;
			}
			else {
				double c1 = Math.pow(Math.E, -(delta/T));
				double rand = Math.random() * 1.0;
				if(c1 > rand) {
					S1 = S2;
				}
			}
		
			//5  进行温度衰减，这里采取 T = α × T。
			T = alpha * T;
			count++;
			//6  如果温度没有衰减到设定值 Tend以下，继续执行第2~5步。
		} while (T > Tend);
		
		Long end = System.currentTimeMillis();
		
		System.out.println("final count : " + count);
		System.out.println("final time cost : " + (end - start) + " ms");
		Map<String, Object> map = Tools.getPlanFromSolution(S1);
		System.out.println("final solution time : " + map.get("totalTime"));
		Tools.drawGaunt((Map<Integer, Machine>) map.get("machineMap"));
	}
	
	private static Chromosome2 initializeChromosome() {
		Chromosome2 first = new Chromosome2();
		first.os = new int[]{1,1,2,2,2};
		first.ms = Tools.generateMs();
		return first;
	}
	
	private static Chromosome2 generateNewSolution(Chromosome2 old) {
		Chromosome2 newc = new Chromosome2();

		int[] newOs = old.os;
		Random random = new Random();
		int first = random.nextInt(5);
		int second = random.nextInt(5);
		
		int temp = newOs[first];
		newOs[first] = newOs[second];
		newOs[second] = temp;
		
		newc.os = newOs;
		newc.ms = Tools.generateMs();
		
		return newc;
	}
	
	
}
