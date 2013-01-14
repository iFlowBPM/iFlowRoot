package pt.iflow.chart;

import org.jfree.data.general.Dataset;

import pt.iknow.chart.BarChart;
import pt.iknow.chart.ChartCtx;


/**
 * UniFlow specific BarChart. This class loads data from a ProcessData instance.
 * @author oscar
 *
 */
public class IFBarChart extends BarChart {
  
  protected Dataset doGetDataSet(ChartCtx ctx, Object dataSource) {
    return super.doGetDataSet(ctx, DataConverter.convertBarDataSource(ctx, dataSource));
  }


}
