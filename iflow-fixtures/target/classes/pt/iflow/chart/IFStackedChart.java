package pt.iflow.chart;

import org.jfree.data.general.Dataset;

import pt.iknow.chart.ChartCtx;
import pt.iknow.chart.StackedChart;


/**
 * UniFlow specific StackedChart. This class loads data from a ProcessData instance.
 * @author oscar
 *
 */
public class IFStackedChart extends StackedChart {

  protected Dataset doGetDataSet(ChartCtx ctx, Object dataSource) {
    return super.doGetDataSet(ctx, DataConverter.convertBarDataSource(ctx, dataSource));
  }

}
