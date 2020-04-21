import org.jfree.ui.RefineryUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Lab4 {

    private static int n = 12;
    private static int N = 4096;
    private static int Wgr = 2400;
    private static double rangeMin = 0;
    private static double rangeMax = 100;
    private static long time = 0;
    public static List<Double> signal = new ArrayList<>();
    private static Random random = new Random();

    private static List<List<Double>> w_coeff = new ArrayList<>(new ArrayList<>());
    private static List<Double> w_coeff_new = new ArrayList<>();
    ;
    private static List<Double> fI = new ArrayList<>();
    private static List<Double> fII = new ArrayList<>();
    private static List<Double> f = new ArrayList<>();

    public static void main(String[] args) {
        createPlot("signal", createSignal());
        createWCoeff();
        createPlot("F", createF());
        Lab3.signal = Lab4.signal;
        Lab3.createFI();
        Lab3.createTable();
        createPlot("F", Lab3.createFp());
        System.out.printf("%d ms", (Lab4.time - Lab3.time) / 1_000_000);
    }

    static {
        for (int i = 0; i < N; i++) {
            w_coeff.add(new ArrayList<>(Collections.nCopies(N / 2, 0.0)));
        }
        IntStream.range(0, N)
                .forEach(i ->
                {
                    w_coeff_new.add(i, 0.0);
                    f.add(i, 0.0);
                });
        IntStream.range(0, N / 2)
                .forEach(i ->
                {
                    fI.add(i, 0.0);
                    fII.add(i, 0.0);
                });
    }


    public static void createWCoeff() {
        long startTime = System.nanoTime();
        for (int i = 0; i < w_coeff.size(); i++) {
            for (int j = 0; j < w_coeff.get(i).size(); j++) {
                w_coeff.get(i).set(j, Math.cos((4 * Math.PI / N) * i * j) + Math.sin((4 * Math.PI / N) * i * j));
            }
        }

        for (int i = 0; i < w_coeff_new.size(); i++) {
            w_coeff_new.set(i, Math.cos((2 * Math.PI / N) * i) + Math.sin((2 * Math.PI / 4) * i));
        }
        long endTime = System.nanoTime();
        time+= endTime - startTime;
    }

    public static List<Double> createF() {
        long startTime = System.nanoTime();
        for (int i = 0; i < N/2; i++) {
            for (int j = 0; j < N/2; j++) {
                fII.set(i, fII.get(i) + signal.get(2*j) * w_coeff.get(i).get(j));
                fI.set(i, fII.get(i) + signal.get(2*j+1) * w_coeff.get(i).get(j));
            }
        }

        IntStream.range(0, N).forEach( i -> {
            if (i < N/2) {
                f.set(i, f.get(i) + fII.get(i) + w_coeff_new.get(i) * fI.get(i));
            } else {
                f.set(i, f.get(i) + fII.get(i-(N/2)) + w_coeff_new.get(i) * fI.get(i-(N/2)));
            }
        });
        long endTime = System.nanoTime();
        time+= endTime - startTime;
        return f;
    }

    private static List<Double> createSignal() {
        IntStream.range(0, N)
                .forEach(i -> signal.add(i, 0.0));

        for (int i = 1; i < n + 1; i++) {
            double ai = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
            double fi = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
            int finalI = i;
            IntStream.range(0, N).forEach(j -> signal.set(j,
                    signal.get(j) + ai * Math.sin((((double) Wgr / finalI * (j + 1))) + fi)));
        }
        return signal;
    }

    public static void createPlot(String title, List<Double> list) {
        double[] arr = list.stream().mapToDouble(d -> d).toArray();
        final XYSeriesDemo demo = new XYSeriesDemo(title, arr);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
