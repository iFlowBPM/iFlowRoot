package pt.iflow.api.processdata;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.iflow.api.utils.CharSequenceIterator;
import pt.iflow.api.utils.Logger;

public class ProcessXmlWriter extends Reader {

  static enum State { START, HEADER, ERROR, SIMPLEVARS, LISTVARS, APPDATAVARS, FOOTER, END };

  private static final int initialStringBuilderCapacity = 4096;

  private static final int EOF = -1;

  private State _state = State.START;
  private StringBuilder _inProcess = null;
  private Iterator<Character> _inIterator = null;
  private List<String> _varNames = null;
  private int _varIndex = -1;
  private int _counter = -1;

  private ProcessData _pd;
  private ProcessXml _pxml;

  public ProcessXmlWriter(ProcessData pd) {
    _pd = pd;
    _pxml = new ProcessXml(pd);
    init();
  }

  void init() {
    _state = State.START;
    _inProcess = new StringBuilder(initialStringBuilderCapacity);
    _inIterator = null;
    _varNames = null;
    _varIndex = -1;
    _counter = 0;
  }

  @Override
  public void close() throws IOException {
    init();
    _state = State.END;
  }

  @Override
  public synchronized void reset() throws IOException {
    close();
    _state = State.START;
  }

  int nextState() throws IOException {
    _inIterator = null;
    switch (_state) {
    case HEADER:
      _state = State.ERROR;
      break;
    case ERROR:
      _state = State.SIMPLEVARS;
      break;
    case SIMPLEVARS:
      if (_varNames != null && _varIndex >= _varNames.size()) {
        _state = State.LISTVARS;
        _varNames = null;
      }
      break;
    case LISTVARS:
      if (_varNames != null && _varIndex >= _varNames.size()) {
        _state = State.APPDATAVARS;
        _varNames = null;
      }
      break;
    case APPDATAVARS:
      if (_varNames != null && _varIndex >= _varNames.size()) {
        _state = State.FOOTER;
        _varNames = null;
      }
      break;
    case FOOTER:
      _state = State.END;
      break;
    }			
    return read();
  }

  @Override
  public int read() throws IOException {
    try {
      if (_state == State.START) {
        _state = State.HEADER;
        return read();
      }
      else if (_state == State.END)
        return EOF;


      if (_inIterator != null && !_inIterator.hasNext()) {
        return nextState();
      }

      if (_inIterator == null) {
        _inProcess.setLength(0);
        switch (_state) {
        case HEADER:
          _pxml.generateXmlHeader(_inProcess);
          break;
        case ERROR:
          if(!_pd.hasError()) 
            return nextState();
          _pxml.generateXmlError(_pd.getError(),_inProcess);
          break;
        case SIMPLEVARS:
          if (_varNames == null) {
            _varNames = new ArrayList<String>(_pd.getSimpleVariableNames());
            _varIndex = 0;

            if (_varNames.size() == 0)
              return nextState();
          }
          // Introduced by "readonly" and "bindable" variables
          while(_inProcess.length() == 0 && _varIndex < _varNames.size())
            _pxml.generateXmlSimpleVar(_pd.get(_varNames.get(_varIndex++)),_inProcess);

          if(_varIndex >= _varNames.size()) return nextState();

          break;
        case LISTVARS:
          if (_varNames == null) {
            _varNames = new ArrayList<String>(_pd.getListVariableNames());
            _varIndex = 0;

            if (_varNames.size() == 0)
              return nextState();
          }
          // Introduced by "readonly" and "bindable" variables
          while(_inProcess.length() == 0 && _varIndex < _varNames.size())
            _pxml.generateXmlListVar(_pd.getList(_varNames.get(_varIndex++)),_inProcess);

          if(_varIndex >= _varNames.size()) return nextState();

          break;
        case APPDATAVARS:
          if (_varNames == null) {
            _varNames = new ArrayList<String>(_pd._appData.keySet());
            _varIndex = 0;

            if (_varNames.size() == 0)
              return nextState();
          }
          while(_inProcess.length() == 0 && _varIndex < _varNames.size()) {
            String var = _varNames.get(_varIndex++);
            _pxml.generateXmlAppData(var, _pd.getAppData(var),_inProcess);
          }
          if(_varIndex >= _varNames.size()) return nextState();

          break;
        case FOOTER:
          _pxml.generateXmlFooter(_inProcess);
          break;
        }

        _inIterator = new CharSequenceIterator(_inProcess);
      }

      _counter++;
      return _inIterator.next();
    }
    catch (Exception e) {
      Logger.error("", this, "read", "EXCEPTION: " + 
          "\nSTATE: " + _state + 
          "\nINPROCESS: " + new String(_inProcess) + 
          "\n" + _inIterator + 
          "\n Generated " + _counter+" characters",
          e);
      throw new IOException();
    }
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int r = 0, c;
    if(_state==State.END) return -1;
    for(r = 0; r < len && (c=read())!=-1; r++) {
      if(c == EOF) break;
      cbuf[off+r] = (char) c;
    }

    return r;
  }


}
