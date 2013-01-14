package pt.iflow.chart;

import java.awt.Color;
import java.awt.GradientPaint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.util.SortOrder;

import pt.iknow.chart.BarChart;
import pt.iknow.chart.ChartCtx;


/**
 * UniFlow specific BarChart. This class loads data from a ProcessData instance.
 * @author oscar
 *
 */
public class IFLayeredBarChart extends BarChart {
  
  protected Dataset doGetDataSet(ChartCtx ctx, Object dataSource) {
    return super.doGetDataSet(ctx, DataConverter.convertBarDataSource(ctx, dataSource));
  }

  protected JFreeChart doGetChart(ChartCtx ctx, Dataset dataset) {
    JFreeChart chart = ChartFactory.createBarChart(getChartTitle(), // chart title (can be null)
        getXxAxis(), // domain axis label (can be null)
        getYyAxis(), // range axis label (can be null)
        (CategoryDataset) dataset, // data
        PlotOrientation.VERTICAL, // orientation
        isGenerateLegend(), // include legend
        isGenerateTooltips(), // tooltips?
        false // URLs?
        );

    CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
    categoryplot.setBackgroundPaint(Color.lightGray);
    categoryplot.setDomainGridlinePaint(Color.white);
    categoryplot.setDomainGridlinesVisible(true);
    categoryplot.setRangeGridlinePaint(Color.white);

    NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
    numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LayeredBarRenderer layeredbarrenderer = new LayeredBarRenderer();
    layeredbarrenderer.setDrawBarOutline(false);

    categoryplot.setRenderer(layeredbarrenderer);
    categoryplot.setRowRenderingOrder(SortOrder.DESCENDING);
    GradientPaint gradientpaint = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
    GradientPaint gradientpaint1 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    layeredbarrenderer.setSeriesPaint(0, gradientpaint);
    layeredbarrenderer.setSeriesPaint(1, gradientpaint1);

    return chart;
  }


}
