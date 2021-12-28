package pt.iflow.api.documents;

import java.io.InputStream;
import java.util.Date;

public class DocumentDataStream   extends DocumentData
{
	  private InputStream contentStream;
	  
	  public InputStream getContentStream()
	  {
	    return this.contentStream;
	  }
	  
	  public void setContentStream(InputStream contentStream)
	  {
	    this.contentStream = contentStream;
	  }
	  
	  public DocumentDataStream(int aDocId, String asFileName, byte[] aContent, Date aUpdated, int flowid, int pid, int subpid)
	  {
	    super(aDocId, asFileName, aContent, aUpdated, flowid, pid, subpid);
	  }

	public DocumentDataStream() {
		// TODO Auto-generated constructor stub
	}
	  
	 
	}