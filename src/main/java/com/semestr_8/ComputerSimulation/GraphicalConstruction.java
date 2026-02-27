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
