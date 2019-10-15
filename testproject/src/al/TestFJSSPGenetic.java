package al;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class TestFJSSPGenetic {

	public static void main(String[] args) {
		Long start = System.currentTimeMillis();

		//创造初始化染色体矩阵
		Chromosome2[] chromosomes = initializeChromosomeGroup();
		
		//迭代进化
		for(int i = 0; i < 2000;i++) {
			
			chromosomes = upgradeChromosomeGroup(chromosomes);
			
		}
		
		Chromosome2 last = chromosomes[0];
		
		Long end = System.currentTimeMillis();
		System.out.println("final count " + 2000);
		System.out.println("final time cost : " + (end - start) + " ms");
		Map<String, Object> map = Tools.getPlanFromSolution(last);
		System.out.println("final solution time: " + map.get("totalTime"));

		Tools.drawGaunt((Map<Integer, Machine>)map.get("machineMap"));
	}
	/**
	 * @param timeMatrix
	 * @param chromosomeMatrix
	 * @return 
	 */
	private static Chromosome2[] upgradeChromosomeGroup(Chromosome2[] chromosomes) {
		//计算染色体适应性
		Chromosome2[] chs = calculateChromosomeAdapt(chromosomes);
		//计算染色体选中概率
		calculateSelect(chs);
		
		//复制最好的两个
		List<Chromosome2> l = Lists.newArrayList(chs);
		Collections.sort(l);
		Chromosome2[] newChromosomes = new Chromosome2[chromosomes.length];
		Chromosome2 c1 = (Chromosome2)l.toArray()[l.size() - 1];
		newChromosomes[0] = c1;
		Chromosome2 c2 = (Chromosome2)l.toArray()[l.size() - 2];
		newChromosomes[1] = c2;
		
		//随机选中二个（赌轮盘）后交叉并变异（循环）
		copyAndchange(chs, newChromosomes);
		
		return newChromosomes;
	}

	private static void copyAndchange(Chromosome2[] old, Chromosome2[] newChromosomes) {
		Chromosome2 c1;
		Chromosome2 c2;
		for(int i = 2;i < old.length;) {
			int next1 = nextDiscrete(old);
			int next2 = 0;
			 
			do {
				next2 = nextDiscrete(old);
			} while (next2 == next1);
			
			c1 = old[next1];
			c2 = old[next2];
			
			int[] o1 = c1.os;
			int[] o2 = c2.os;
			
			Random random = new Random();
			int r1 = random.nextInt(o1.length);
			int r2;
			do {
				r2 = random.nextInt(o2.length);
			} while (r2 == r1);
			
			int start = 0;
			int end = 0;
			if(r1 < r2) {
				start = r1; end = r2;
			}
			else {
				start = r2; end = r1;
			}
			
			int[] father1 = o1;
			int[] father2 = o2;
			
			int[] child1 = new int[father1.length];
			int[] child2 = new int[father2.length];
			
			generateNewChild(start, end, father1, father2, child1);
			generateNewChild(start, end, father2, father1, child2);
			
			Chromosome2 nc1 = c1;
			Chromosome2 nc2 = c2;
			
			nc1.os = child1;
			nc2.os = child2;
			
			
			////msmsms
			int[] m1 = c1.ms;
			int[] m2 = c2.ms;
			
			Random random2 = new Random();
			int r3 = random2.nextInt(m1.length);
			int r4;
			do {
				r4 = random.nextInt(m2.length);
			} while (r3 == r4);
			
			if(r3 < r4) {
				start = r3; end = r4;
			}
			else {
				start = r4; end = r3;
			}
			
			int m1Temp[] = m1;
			int m2Temp[] = m2;
			
			System.arraycopy(m1, start, m2Temp, start, end - start);
			System.arraycopy(m2, start, m1Temp, start, end - start);
			
			nc1.ms = m1Temp;
			nc2.ms = m2Temp;
			
			newChromosomes[i++] = nc1;
			newChromosomes[i++] = nc2;
		}
	}

	private static void generateNewChild(int start, int end, int[] father1, int[] father2, int[] child1) {
		System.arraycopy(father1, start, child1, start, (end - start));
		List<Pair> pairs = new ArrayList<Pair>();
		for(int m = start;m <= end;m++) {
			int num = child1[m];
			int count = 0;
			for(int n = 0;n <= m;n++) {
				if(father1[n] == num) {
					count++;
				}
			}
			if(count > 0) {
				Pair<Integer, Integer> pair = new ImmutablePair<Integer, Integer>(count,num);
				pairs.add(pair);
			}
		}
		
		int[] tailf2 = Arrays.copyOfRange(father2, end, father2.length);
		int[] tempFather2 = new int[father2.length];
		System.arraycopy(father2, 0, tempFather2, tailf2.length, father2.length - tailf2.length);
		System.arraycopy(tailf2, 0, tempFather2, 0, tailf2.length);

		int[] tempFather2Cp = new int[father2.length];
		System.arraycopy(tempFather2, 0, tempFather2Cp, 0, tempFather2.length);
		
		for(Pair p : pairs) {
			int index = (Integer)p.getKey();
			int value = (Integer)p.getValue();
			for(int t = 0;t < tempFather2.length;t++) {
				if(tempFather2[t] == value) {
					index--;
				}
				if(index == 0) {
					tempFather2Cp[t] = 0;
					break;
				}
			}
		}
		
		int[] tempChild1 = new int[father1.length - (end - start)];
		
		for(int cur = 0, m = 0; m < tempFather2Cp.length;m++) {
			if(tempFather2Cp[m] != 0) {
				tempChild1[cur++] = tempFather2Cp[m];
			}
		}
		
		System.arraycopy(tempChild1, 0, child1, end, (child1.length - end));
		System.arraycopy(tempChild1, (child1.length - end), child1, 0, (tempChild1.length - (child1.length - end)));
	}
	
	public static int nextDiscrete(Chromosome2[] chs)
    {
        double sum = 0.0;
        for (int i = 0; i < chs.length; i++)
            sum += chs[i].selet;

        double r = Math.random() * sum;
        sum = 0.0;
        for (int i = 0; i < chs.length; i++) {
            sum += chs[i].selet;
            if (sum > r)
                return i;
        }
        return chs.length - 1;
    }

	/**
	 * @param adapt
	 * @return
	 */
	private static Chromosome2[] calculateSelect(Chromosome2[] chs) {
		double sum = 0;
		for(Chromosome2 a : chs) {
			sum += 1.0/a.adapt;
		}
		for(int i = 0;i < chs.length;i++) {
			chs[i].selet = (1.0/chs[i].adapt)/sum;
		}
		return chs;
	}

	/**
	 * @param chromosomeMatrix
	 * @param timeMatrix 
	 * @return
	 */
	private static Chromosome2[] calculateChromosomeAdapt(Chromosome2[] chromosomes) {
		for(Chromosome2 c : chromosomes) {
			c.adapt = (int)Tools.getPlanFromSolution(c).get("totalTime");
		} 
		
		return chromosomes;
	}

	private static Chromosome2[] initializeChromosomeGroup() {
		Chromosome2[] choromosomes = new Chromosome2[50];
		Chromosome2 first = new Chromosome2();
		first.os = new int[]{1,1,2,2,2};
		first.ms = Tools.generateMs();
		choromosomes[0] = first;
		
		for(int i = 1;i < choromosomes.length;i++) {
			Chromosome2 news = generateNewSolution(first);
			choromosomes[i] = news;
		}
		
		return choromosomes;
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

class Chromosome2 implements Comparable{
	public int[] os;
	public int[] ms;
	public int adapt;
	public double selet;
	
	
	@Override
	public int compareTo(Object o) {
		if(o instanceof Chromosome2) {
			Chromosome2 o1 = (Chromosome2) o;
			
			if(this.selet > o1.selet) {
				return 1;
			}
			else if(this.selet < o1.selet) {
				return -1;
			}
			else {
				return 0;
			}
		}
		else {
			return 1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		Chromosome2 solution2;
		if(obj instanceof Chromosome2) {
			solution2 = (Chromosome2)obj;
		}
		else {
			return false;
		}
		if(this.os == null && solution2.os != null) {
			return false;
		}
		if(this.os != null && solution2.os == null) {
			return false;
		}
		if(this.ms == null && solution2.ms != null) {
			return false;
		}
		if(this.ms != null && solution2.ms == null) {
			return false;
		}
		return true;
	}
}

