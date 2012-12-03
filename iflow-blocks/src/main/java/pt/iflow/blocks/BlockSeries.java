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
package pt.iflow.blocks;

import java.text.MessageFormat;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.series.SeriesManager;
import pt.iflow.api.utils.series.SeriesProcessor;
import pt.iknow.utils.StringUtilities;

/**
 * <p>Title: BlockValidacoes</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockSeries extends Block {
  public Port portIn, portSuccess, portError;

  private static final String SERIES = "Series";
  private static final String PREFIX = "ExtraPrefix";
  private static final String SUFFIX = "ExtraSuffix";
  private static final String OUTPUTVAR = "OutputVariable";
   
  public BlockSeries(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
    saveFlowState = false;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }
  
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[1];
      retObj[0] = portIn;
      return retObj;
  }

  /**
   * No action in this block
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();
    Port outPort = portSuccess;
    
    try {
      String format = "{0}{1}{2}";
      String seriesname = getAttribute(SERIES);
      try {
        seriesname = procData.transform(userInfo, seriesname);
      } catch (Exception e) {
        if (Logger.isDebugEnabled()) {
          Logger.warning(login, this, "after", "could not transform series '" + seriesname + "', ignoring step.");
        }
      }

      String prefix = getAttribute(PREFIX);
      try {
        prefix = procData.transform(userInfo, prefix);
      } catch (Exception e) {
        if (Logger.isDebugEnabled()) {
          Logger.warning(login, this, "after", "could not transform prefix '" + prefix + "', ignoring step.");
        }
      } finally { 
        if (StringUtilities.isEmpty(prefix)) {
          prefix = "";
        }
      }

      String suffix = getAttribute(SUFFIX);
      try {
        suffix = procData.transform(userInfo, suffix);
      } catch (Exception e) {
        if (Logger.isDebugEnabled()) {
          Logger.warning(login, this, "after", "could not transform suffix '" + suffix + "', ignoring step.");
        }
      } finally {
        if (StringUtilities.isEmpty(suffix)) {
          suffix = "";
        }
      }
      // output var is not string parsed...
      String outvar = getAttribute(OUTPUTVAR);
      // Validations
      if (StringUtilities.isEmpty(seriesname)) {
        Logger.error(login, this, "after", procData.getSignature() + "Series is empty");
        outPort = portError;
      } else {
        if (StringUtilities.isEmpty(outvar)) {
          Logger.error(login,this,"after", procData.getSignature() + "Output var is empty");
          outPort = portError;
        } else {
          SeriesProcessor seriesProcessor = SeriesManager.getSeriesFromName(userInfo, seriesname);
          String series = seriesProcessor.getNext();
          String output = MessageFormat.format(format, prefix, series, suffix);
          procData.parseAndSet(outvar, output);
          
          logMsg.append("Set '" + outvar + "' as '" + output + "';");
          Logger.info(login,this,"after",procData.getSignature() + outvar + "=" + output);
        }
      }
    } catch (Exception ee) {
      Logger.error(login, this, "after", procData.getSignature() + "caught exception", ee);
      outPort = portError;
    }
    
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData process) {
    return this.getDesc(userInfo, process, true, "Series");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Series efectuada");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
