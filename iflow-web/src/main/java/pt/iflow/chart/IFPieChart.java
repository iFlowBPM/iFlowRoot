package pt.iflow.chart;

import org.jfree.data.general.Dataset;

import pt.iknow.chart.ChartCtx;
import pt.iknow.chart.PieChart;


/**
 * UniFlow specific PieChart. This class loads data from a ProcessData instance.
 * @author oscar
 *
 */
public class IFPieChart extends PieChart {

  protected Dataset doGetDataSet(ChartCtx ctx, Object dataSource) {
    return super.doGetDataSet(ctx, DataConverter.convertPieDataSource(ctx, dataSource));
  }

}
