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

public class TestJSSPGenetic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Long start = System.currentTimeMillis();

		//创造初始化染色体矩阵
		int[][] chromosomeMatrix = generateChromosomeMatrix();
		
		//迭代进化
		for(int i = 0; i < 500;i++) {
			
			chromosomeMatrix = upgradeChromosome(chromosomeMatrix);

		}
		
		int[] last = chromosomeMatrix[0];
		
		Long end = System.currentTimeMillis();
		System.out.println("final count " + 200);
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
	private static int[][] upgradeChromosome(int[][] chromosomeMatrix) {
		//计算染色体适应性
		Chromosome[] chs = calculateChromosomeAdapt(chromosomeMatrix);
		//计算染色体选中概率
		calculateSelect(chs);
		
		//复制最好的两个
		List<Chromosome> l = Lists.newArrayList(chs);
		Collections.sort(l);
		int[][] newChromosomeMatrix = new int[50][36];
		Chromosome c1 = (Chromosome)l.toArray()[l.size() - 1];
		newChromosomeMatrix[0] = c1.value;
		Chromosome c2 = (Chromosome)l.toArray()[l.size() - 2];
		newChromosomeMatrix[1] = c2.value;
		
		//随机选中二个（赌轮盘）后交叉并变异（循环）
		copyAndchange(chs, newChromosomeMatrix);
		
		return newChromosomeMatrix;
	}

	private static void copyAndchange(Chromosome[] chs, int[][] newChromosomeMatrix) {
		Chromosome c1;
		Chromosome c2;
		for(int i = 2;i < chs.length;) {
			int next1 = nextDiscrete(chs);
			int next2 = 0;
			 
			do {
				next2 = nextDiscrete(chs);
			} while (next2 == next1);
			
			c1 = chs[next1];
			c2 = chs[next2];
			
			int[] v1 = c1.value;
			int[] v2 = c2.value;
			
			Random random = new Random();
			int r1 = random.nextInt(v1.length);
			int r2;
			do {
				r2 = random.nextInt(v1.length);
			} while (r2 == r1);
			
			int start = 0;
			int end = 0;
			if(r1 < r2) {
				start = r1; end = r2;
			}
			else {
				start = r2; end = r1;
			}
			
			int[] father1 = v1;
			int[] father2 = v2;
			
			int[] child1 = new int[father1.length];
			int[] child2 = new int[father2.length];
			
			generateNewChild(start, end, father1, father2, child1);
			generateNewChild(start, end, father2, father1, child2);

			newChromosomeMatrix[i++] = child1;
			newChromosomeMatrix[i++] = child2;
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
	
	public static int nextDiscrete(Chromosome[] chs)
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
	private static Chromosome[] calculateSelect(Chromosome[] chs) {
		double sum = 0;
		for(Chromosome a : chs) {
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
	private static Chromosome[] calculateChromosomeAdapt(int[][] chromosomeMatrix) {
		Chromosome[] adapt = new Chromosome[chromosomeMatrix.length];
		for(int i = 0;i < chromosomeMatrix.length;i++) {
			int[] chromosome = chromosomeMatrix[i];
			Chromosome c = new Chromosome();
			c.value = chromosome;
			c.adapt = (int) Tools.getPlanFromSolution(chromosome).get("totalTime");
			adapt[i] = c;
		}
		
		return adapt;
	}

	/**
	 * 
	 */
	private static int[][] generateChromosomeMatrix() {
		int[][] choromosomeMatrix = new int[50][36];
		int[] solution = initializeSolution();
		choromosomeMatrix[0] = solution;
		for(int i = 1;i < choromosomeMatrix.length;i++) {
			int[] news = generateNewSolution(solution);
			choromosomeMatrix[i] = news;
		}
		
		return choromosomeMatrix;
	}
	
	private static int[] initializeSolution() {
		int[] initialize = {1,2,3,4,5,6,1,2,3,4,5,6,1,2,3,4,5,6,
								1,2,3,4,5,6,1,2,3,4,5,6,1,2,3,4,5,6};
		
		return initialize;
	}
	
	
	private static int[] generateNewSolution(int[] old) {
		int[] newSolution = new int[old.length];
		System.arraycopy(old, 0, newSolution, 0, old.length);
		
		Random random = new Random();
		int first = random.nextInt(36);
		int second = random.nextInt(36);
		
		int temp = newSolution[first];
		newSolution[first] = newSolution[second];
		newSolution[second] = temp;
		
		return newSolution;
	}

	/**
	 * @param left
	 * @param right
	 * @param array
	 */
	private static void quickSort(int left, int right, double[] array) {
		if(left < right) {
			double pivot = array[left];
			int low = left;
			int high = right;
			while(low < high) {
				while(low < high && array[high] >= pivot) {
					high--;
				}
				if(array[high] < pivot) {
					array[low] = array[high];
				}
				while(low < high && array[low] <= pivot) {
					low++;
				}
				if(array[low] > pivot) {
					array[high] = array[low];
				}
			}
			array[low] = pivot;
			
			quickSort(left, low - 1, array);
			quickSort(low + 1, right, array);
		}
		
	}
}

class Chromosome implements Comparable{
	public int[] value;
	public int adapt;
	public double selet;
	
	
	@Override
	public int compareTo(Object o) {
		if(o instanceof Chromosome) {
			Chromosome o1 = (Chromosome) o;
			
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
}
