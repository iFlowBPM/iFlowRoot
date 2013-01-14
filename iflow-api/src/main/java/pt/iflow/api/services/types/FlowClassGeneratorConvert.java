package pt.iflow.api.services.types;

public class FlowClassGeneratorConvert {
	private String header;
	private String footer;
	private String content;
	private String linesep;

	public FlowClassGeneratorConvert() {
		super();
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLinesep() {
		return linesep;
	}

	public void setLinesep(String linesep) {
		this.linesep = linesep;
	}
}
