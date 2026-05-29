package com.semestr_8.ComputerSimulation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicalConstruction {

    public static void plotTask1(ArrayList<Double> events, double lambdaMax, double tZero) {

        XYSeries lambdaSeries = new XYSeries("λ(t)=0,2-0,003t");
        XYSeries eventSeries = new XYSeries("События");
        XYSeries tZeroSeries = new XYSeries("t = " + String.format("%.2f", tZero));

        for (double t = 0; t <= 100; t += 0.5){
            double lambda = Math.max(0, 0.2 - 0.003 * t);
            lambdaSeries.add(t, lambda);
        }

        for (double event : events){
            //eventSeries.add(event, 0);
            double lambda_at_event = Math.max(0, 0.2 - 0.003 * event);
            eventSeries.add(event, lambda_at_event);
        }

        tZeroSeries.add(tZero, 0);
        tZeroSeries.add(tZero, lambdaMax);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(lambdaSeries);
        dataset.addSeries(eventSeries);
        dataset.addSeries(tZeroSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Задание 1: Нестационарный пуассоновский поток",
                "Время t",
                "Интенсивность λ(t)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0, 100);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesPaint(0, Color.BLUE);

        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesPaint(1, Color.RED);

        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, false);
        renderer.setSeriesPaint(2, Color.BLACK);
        renderer.setSeriesStroke(2, new BasicStroke(
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{5.0f, 5.0f}, 0.0f
        ));

        plot.setRenderer(renderer);

        showChart(chart, "Задание 1 - Результаты моделирования");

    }

    public static void plotTask2(ArrayList<Double> timePoints,
                                 ArrayList<Integer> statePoints,
                                 ArrayList<Double> rejectTimes,
                                 ArrayList<Double> serveTimes){

        // ==================== СОСТОЯНИЕ КАНАЛА ====================
        XYSeries stateSeries = new XYSeries("Состояние канала (0-свободен, 1-занят)");
        for (int i = 0; i < timePoints.size(); i++) {
            stateSeries.add(timePoints.get(i), statePoints.get(i));
        }

        // ==================== МОМЕНТЫ ОТКАЗОВ ====================
        XYSeries rejectSeries = new XYSeries("Отказы (заявка не обслужена)");

        for (double t : rejectTimes) {
            rejectSeries.add(t, -0.1); // Чуть ниже оси, чтобы не сливаться
        }

        // ==================== МОМЕНТЫ НАЧАЛА ОБСЛУЖИВАНИЯ ====================
        XYSeries serveSeries = new XYSeries("Начало обслуживания");

        for (double t : serveTimes) {
            serveSeries.add(t, 0.5); // Посередине между 0 и 1
        }

        // ==================== ЛИНИИ-ГРАНИЦЫ ИНТЕРВАЛОВ μ(t) ====================
        XYSeries muBorder1 = new XYSeries("μ=0.2 → μ=0.9 (t=20)");
        muBorder1.add(20.0, -0.2);
        muBorder1.add(20.0, 1.2);

        XYSeries muBorder2 = new XYSeries("μ=0.9 → μ=0.4 (t=80)");
        muBorder2.add(80.0, -0.2);
        muBorder2.add(80.0, 1.2);

        // ==================== ГОРИЗОНТАЛЬНЫЕ ПОДПИСИ ЗОН μ(t) ====================
        // (используем отдельные точки с текстовыми метками в легенде)
        XYSeries zoneSlow = new XYSeries("μ=0.2 [0-20]");
        zoneSlow.add(10.0, 1.05);
        XYSeries zoneFast = new XYSeries("μ=0.9 (20-80]");
        zoneFast.add(50.0, 1.05);
        XYSeries zoneMedium = new XYSeries("μ=0.4 (80-100]");
        zoneMedium.add(90.0, 1.05);

        // ==================== СБОРКА ДАТАСЕТА ====================
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(stateSeries);      // Индекс 0
        dataset.addSeries(rejectSeries);     // Индекс 1
        dataset.addSeries(serveSeries);      // Индекс 2
        dataset.addSeries(muBorder1);        // Индекс 3
        dataset.addSeries(muBorder2);        // Индекс 4
        dataset.addSeries(zoneSlow);         // Индекс 5
        dataset.addSeries(zoneFast);         // Индекс 6
        dataset.addSeries(zoneMedium);       // Индекс 7

        // ==================== СОЗДАНИЕ ГРАФИКИ ====================
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Задание 2: Одноканальная СМО с отказами",
                "Время t",
                "Состояние / События",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // ==================== НАСТРОЙКА ОТОБРАЖЕНИЯ ====================
        XYPlot plot = chart.getXYPlot();

        // Оси
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0, 100);
        domainAxis.setLabel("Время t");

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(-0.3, 1.3);
        rangeAxis.setLabel("Состояние канала (0/1) и события");

        // Рендерер
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // --- Серия 0: Состояние канала (ступеньки) ---
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        // --- Серия 1: Отказы (красные крестики внизу) ---
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesPaint(1, Color.RED);

        // --- Серия 2: Начало обслуживания (зеленые треугольники) ---
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShapesVisible(2, true);
        renderer.setSeriesShape(2, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesPaint(2, new Color(0, 150, 0)); // Темно-зеленый

        // --- Серии 3-4: Вертикальные границы (пунктир) ---
        for (int i = 3; i <= 4; i++) {
            renderer.setSeriesLinesVisible(i, true);
            renderer.setSeriesShapesVisible(i, false);
            renderer.setSeriesPaint(i, Color.DARK_GRAY);
            renderer.setSeriesStroke(i, new BasicStroke(
                    1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1.0f, new float[]{5.0f, 5.0f}, 0.0f
            ));
        }

        // --- Серии 5-7: Метки зон (прозрачные, только для легенды) ---
        for (int i = 5; i <= 7; i++) {
            renderer.setSeriesLinesVisible(i, false);
            renderer.setSeriesShapesVisible(i, false);
            renderer.setSeriesVisibleInLegend(i, true); // Видны только в легенде
        }

        plot.setRenderer(renderer);

        // Цвет фона
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // ============ ПОКАЗ ГРАФИКА ============
        showChart(chart, "Задание 2 - Результаты моделирования");

    }

    public static void plotTask3(List<Double> timePoints,
                                 List<Integer> queueSize,
                                 List<Integer> channelState) {
        // ==== График длины очереди ====
        XYSeries queueSeries = new XYSeries("Длина очереди");
        for (int i = 0; i < timePoints.size(); i++) {
            queueSeries.add(timePoints.get(i), queueSize.get(i));
        }

        // ==== График состояния канала (0/1) для наглядности (можно отдельным dataset) ====
        XYSeries channelSeries = new XYSeries("Состояние канала (0-своб.,1-зан.)");
        for (int i = 0; i < timePoints.size(); i++) {
            channelSeries.add(timePoints.get(i), channelState.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(queueSeries);
        dataset.addSeries(channelSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Задание 3: Одноканальная СМО с ожиданием",
                "Время t",
                "Длина очереди / Состояние канала",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0, 100);
        domainAxis.setLabel("Время t");

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(-0.5, Math.max(10, getMaxQueue(queueSize) + 1));
        rangeAxis.setLabel("Длина очереди / Состояние канала");

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // Серия 0 – очередь (ступеньки)
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        // Серия 1 – состояние канала (ступеньки, пунктир?)
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesStroke(1, new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 4.0f}, 0.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        showChart(chart, "Задание 3 - Результаты моделирования");
    }

    // Вспомогательный метод для поиска максимума в списке
    private static int getMaxQueue(List<Integer> queueSize) {
        int max = 0;
        for (int v : queueSize) if (v > max) max = v;
        return max;
    }

    private static void showChart(JFreeChart chart, String title){

        SwingUtilities.invokeLater(() ->{
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ChartPanel(chart), BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
