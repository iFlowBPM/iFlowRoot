package pt.iflow.chart;

import org.jfree.data.general.Dataset;

import pt.iknow.chart.ChartCtx;
import pt.iknow.chart.Pie3DChart;


/**
 * UniFlow specific Pie3DChart. This class loads data from a ProcessData instance.
 * @author oscar
 *
 */
public class IFPie3DChart extends Pie3DChart {

  protected Dataset doGetDataSet(ChartCtx ctx, Object dataSource) {
    return super.doGetDataSet(ctx, DataConverter.convertPieDataSource(ctx, dataSource));
  }

}
