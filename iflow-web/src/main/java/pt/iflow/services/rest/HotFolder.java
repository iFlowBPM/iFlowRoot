package pt.iflow.services.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.hotfolder.HotFolderConfig;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/hotFolder")
public class HotFolder {

	@GET
	@Produces("text/plain")
	public String getClichedMessage() {
		return "Service for uploading files to a flow hotfolder, do a multipart_forma_data post having input with type=file and name=file to /hotFolder/<FLOW_ID>";
	}

	@POST
	@Path("{flowid}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response post(@PathParam("flowid") Integer flowid,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		FlowSettings flowSettingsBean = BeanFactory.getFlowSettingsBean();
		FlowSetting hfOn = flowSettingsBean.getFlowSetting(flowid,
				HotFolderConfig.ONOFF);
		FlowSetting hfFolder = flowSettingsBean.getFlowSetting(flowid,
				HotFolderConfig.SUBS_FOLDERS);
		
		if ("true".equals(hfOn.getValue())) {
			File fileToStream = new File(hfFolder.getValue().split(";")[0] + File.separator
					+ fileDetail.getFileName());
			OutputStream out = null;
			try {
				int read = 0;
				byte[] bytes = new byte[1024];

				out = new FileOutputStream(fileToStream);
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
				out.close();
			} catch (IOException e) {
				return Response.status(500).entity(e.getMessage()).build();
			} finally {
				if( out != null) Utils.safeClose(out);
			}    
		}
		String output = "OK";
		return Response.status(200).entity(output).build();
	}
}
