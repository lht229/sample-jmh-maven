package com.izeye.samples;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * JMHSample_38_PerInvokeSetup.
 *
 * @author Johnny Lim
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class JMHSample_38_PerInvokeSetup {

	private void bubbleSort(byte[] b) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int c = 0; c < b.length - 1; c++) {
				if (b[c] > b[c + 1]) {
					byte t = b[c];
					b[c] = b[c + 1];
					b[c + 1] = t;
					changed = true;
				}
			}
		}
	}

	@State(Scope.Benchmark)
	public static class Data {

		@Param({"1", "16", "256"})
		int count;

		byte[] arr;

		@Setup
		public void setup() {
			this.arr = new byte[this.count];
			Random random = new Random(1234);
			random.nextBytes(this.arr);
		}
	}

	@Benchmark
	public byte[] measureWrong(Data d) {
		bubbleSort(d.arr);
		return d.arr;
	}

	@State(Scope.Thread)
	public static class DataCopy {
		byte[] copy;

		@Setup(Level.Invocation)
		public void setup(Data d) {
			this.copy = Arrays.copyOf(d.arr, d.arr.length);
		}
	}

	@Benchmark
	public byte[] measureNeutral(DataCopy d) {
		bubbleSort(d.copy);
		return d.copy;
	}

	@Benchmark
	public byte[] measureRight(Data d) {
		byte[] c = Arrays.copyOf(d.arr, d.arr.length);
		bubbleSort(c);
		return c;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_38_PerInvokeSetup.class.getSimpleName())
				.build();

		new Runner(opt).run();
	}

}
