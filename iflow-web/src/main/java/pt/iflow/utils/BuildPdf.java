package pt.iflow.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.pdfcrowd.Pdfcrowd;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;

/**
 * 
 * @apiNote Utility method to convert the html email content to a PDF file.
 *          It resorts to an external payed service to do so (https://pdfcrowd.com/)
 *
 */
public class BuildPdf {
	
	private static final String PDF_CROWD_USERNAME = Setup.getProperty("PDF_CROWD_USERNAME");
	private static final String PDF_CROWD_APIKEY = Setup.getProperty("PDF_CROWD_APIKEY");

	private BuildPdf() {
		throw new IllegalStateException("BuildPdf is an utility class");
	}

	public static InputStream convertEmailToPdf(String text, List<File> files) {

		if (text == null) {
			return null;

		} else if (files == null || files.isEmpty()) {
			return callApiPdfCrowd(text);
		}

		try {
			List<File> imagesList = new ArrayList<File>();
			for (File entry : files) {
				String type = Files.probeContentType(FileSystems.getDefault().getPath(entry.getPath()));
				if (type == null) {
					if (entry.getPath().toLowerCase().endsWith(".webp")) {
						type = "image/webp";

					} else {
						break;
					}
				}
				if (type.split("/")[0].equals("image")) {
					imagesList.add(entry);
				}
			}
			if (imagesList.isEmpty()) {
				return null;
			}

			Pattern pattern = Pattern.compile("src=\"(.*?)\"");
			Matcher matcher = pattern.matcher(text);
			text = insertImagesToHtml(text, imagesList, matcher);
			
			return callApiPdfCrowd(text);

		} catch (IOException ioe) {
			Logger.adminWarning("BuildPdf", "convertEmailToPdf", "error getting ", ioe);
		}
		return null;

	}

	private static String insertImagesToHtml(String text, List<File> imagesList, Matcher matcher) throws IOException {
		while (matcher.find()) {
			for (int i = 0; i < imagesList.size(); i++) {
				byte[] fileContent = FileUtils.readFileToByteArray(imagesList.get(0));
				String encodedImage = Base64.getEncoder().encodeToString(fileContent);

				String mimeType = Files.probeContentType(FileSystems.getDefault().getPath(imagesList.get(0).getPath()));

				if (mimeType == null) {
					if (imagesList.get(0).getPath().toLowerCase().endsWith(".webp")) {
						mimeType = "image/webp";

					}
				}
				String replacement = "data:" + mimeType + ";base64," + encodedImage + "\"";
				text = text.replace(matcher.group(1), replacement);
				imagesList.remove(0);
				break;
			}
		}
		return text;
	}
	
	private static InputStream callApiPdfCrowd(String text) {

		try {
			Pdfcrowd.HtmlToPdfClient client = new Pdfcrowd.HtmlToPdfClient(PDF_CROWD_USERNAME, PDF_CROWD_APIKEY);
			byte[] pdf = client.convertString("<html><body>" + text + "</body></html>");

			return new ByteArrayInputStream(pdf);

		} catch (Pdfcrowd.Error why) {
			Logger.adminWarning("BuildPdf", "callApiPdfCrowd", "Pdfcrowd Error: ", why);
		}
		return null;

	}
}
