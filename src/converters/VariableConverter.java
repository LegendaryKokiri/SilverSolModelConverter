package converters;

import java.io.File;
import java.util.List;

public class VariableConverter {
	
	public static int[] toArrayInteger(List<Integer> integers) {
		int[] array = new int[integers.size()];
		for(int i = 0; i < integers.size(); i++) {
			array[i] = integers.get(i);
		}
		return array;
	}
	
	public static double[] toArrayDouble(List<Double> doubles) {
		double[] array = new double[doubles.size()];
		for(int i = 0; i < doubles.size(); i++) {
			array[i] = doubles.get(i);
		}
		return array;
	}
	
	public static File[] toArrayFile(List<File> files) {
		File[] array = new File[files.size()];
		for(int i= 0; i < files.size(); i++) {
			array[i] = files.get(i);
		}
		return array;
	}
	
}
