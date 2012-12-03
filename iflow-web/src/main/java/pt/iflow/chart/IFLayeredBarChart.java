/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
