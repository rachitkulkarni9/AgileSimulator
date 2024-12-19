package com.groupesan.project.java.scrumsimulator.mainpackage.ui.dialogs.simulation;

import com.groupesan.project.java.scrumsimulator.mainpackage.ui.widgets.BaseComponent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.PlotOrientation;

import javax.swing.*;
import java.awt.*;

/**
 * This is an attempt to use the JFree library to implement a dynamically altering burndown chart.
 * Credits to the library belong to https://github.com/jfree/jfreechart
 * Tutorials used to learn how to utilize it effectively:
 * https://www.tutorialspoint.com/jfreechart/jfreechart_line_chart.htm
 * https://stackoverflow.com/questions/7328326/dynamically-change-y-axis-range-in-jfreechart
 * https://github.com/jfree/jfreechart
 * https://steemit.com/visualization/@datatreemap/visualize-a-multiple-lines-graph-with-jfreechart-in-java
 * http://www.java2s.com/Code/Java/Chart/JFreeChartCombinedXYPlotDemo1.htm
 */
public class BurndownChart extends JPanel implements BaseComponent {


    private XYSeries linear;
    private XYSeriesCollection defaultDataset;
    private JFreeChart lineChart;
    private XYSeries burndown;

    public BurndownChart() {
        burndown = new XYSeries("Burndown Chart");
        linear = new XYSeries("Linear Chart");
        defaultDataset = new XYSeriesCollection();
        lineChart = ChartFactory.createXYLineChart("Burndown Chart", "Time", "Velocity", defaultDataset, PlotOrientation.VERTICAL, false, false, false);
        this.init();
    }

    public XYSeriesCollection getDefaultDataset() {
        return defaultDataset;
    }

    public void resetData() {
        if(!burndown.isEmpty() && !linear.isEmpty()) {
            burndown.clear();
            linear.clear();
        }
    }

    public XYSeries getLinear(){
        return linear;
    }


    public XYSeries getBurndown(){
        return burndown;
    }
    public void setLinearLine(Integer day, Double totalPoints){

        linear.add(day, totalPoints);

    }

    public void setBurndown(Integer day, Double points) {
        burndown.add(day, points);
    }

    public void updateChart(){
        defaultDataset.removeAllSeries();
        defaultDataset.addSeries(linear);
        defaultDataset.addSeries(burndown);
    }


    @Override
    public void init() {

        XYPlot plot = lineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLUE);

        plot.setRenderer(renderer);
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(800,600));

        this.setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);

    }
}
