package pt.iflow.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.pdfcrowd.Pdfcrowd;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.mail.parsers.MessageParseException;
import pt.iknow.utils.StringUtilities;

/**
 * 
 * @apiNote Utility method to convert the html email content to a PDF file. It
 *          resorts to an external payed service to do so
 *          (https://pdfcrowd.com/)
 *
 */
public class BuildPdf {

	private static final String PDF_CROWD_USERNAME = Setup.getProperty("PDF_CROWD_USERNAME");
	private static final String PDF_CROWD_APIKEY = Setup.getProperty("PDF_CROWD_APIKEY");

	private BuildPdf() {
		throw new IllegalStateException("BuildPdf is an utility class");
	}

	public static InputStream convertEmailToPdf(String text, Part part, List<File> files) throws MessageParseException {
		if (text == null) {
			return null;

		} else if (files == null || files.isEmpty()) {
			return callApiPdfCrowd(text);
		}

		Pattern pattern = Pattern.compile("src=\"(.*?)\"");
		Matcher matcher = pattern.matcher(text);
		Map<String, List<String>> imageContentMap = null;

		while (matcher.find()) {
			if (StringUtilities.isEmpty(matcher.group(1))) {
				continue;
			}

			if (matcher.group(1).toLowerCase().contains("cid:")) {
				String cidInHtml = matcher.group(1).substring(matcher.group(1).indexOf("cid:") + "cid:".length());

				if (imageContentMap == null) {
					imageContentMap = new HashMap<String, List<String>>();
					getImagesInMail(part, text, files, imageContentMap);
				}

				if (imageContentMap.containsKey(cidInHtml)) {
					// list: [0] imagem; [1] mimetype
					List<String> imageContentAndMimeTypeList = imageContentMap.get(cidInHtml);   
					
					String mimeType = imageContentAndMimeTypeList.get(1).toLowerCase();
					String replacement = "data:" + mimeType + ";base64," + imageContentAndMimeTypeList.get(0);
					text = text.replace(matcher.group(1), replacement);

				}
			}
		}
		return callApiPdfCrowd(text);

	}

	public static void getImagesInMail(Part part, String text, List<File> files, Map<String, List<String>> imageContentMap) throws MessageParseException {
		try {
			if (part.isMimeType("multipart/*")) {
				Multipart mp = (Multipart) part.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					getImagesInMail(mp.getBodyPart(i), text, files, imageContentMap);
				}
			} else if (part.isMimeType("message/rfc822")) {
				getImagesInMail((Part) part.getContent(), text, files, imageContentMap);

			} else if (part.isMimeType("image/*")) {
				String contentId = ((MimeBodyPart) part).getContentID();

				if (StringUtilities.isNotEmpty(contentId)) {
					contentId = contentId.substring(1, contentId.length() - 1);

					if (text.contains(contentId)) {
						boolean isInFileList = false;
						String encodedImage = "";
						String partName = FilenameUtils.getBaseName(part.getFileName());
						String fileName = "";

						for (File file : files) {
							fileName = FilenameUtils.getBaseName(file.getName());
							if (partName.contains(fileName)) {
								byte[] fileContent = FileUtils.readFileToByteArray(file);
								Logger.adminInfo("BuildPdf", "getImagesInMail",
										"Image from file list started Base64 Encoding");
								encodedImage = Base64.getEncoder().encodeToString(fileContent);
								isInFileList = true;
								break;
							}
						}

						if (!isInFileList) {
							byte[] inlineImageContent = IOUtils.toByteArray(part.getInputStream());
							Logger.adminInfo("BuildPdf", "getImagesInMail", "Inline image started Base64 Encoding");
							encodedImage = Base64.getEncoder().encodeToString(inlineImageContent);

						}
						String mimeType = part.getContentType().split(";")[0];

						// arraylist: [0] imagem [1] mimetype
						List<String> imageContentAndMimeTypeList = new ArrayList<String>();
						imageContentAndMimeTypeList.add(encodedImage);
						imageContentAndMimeTypeList.add(mimeType);

						imageContentMap.put(contentId, imageContentAndMimeTypeList);

					}
				}
			}
		} catch (Exception e) {
			throw new MessageParseException(e);
		}
	}

	private static InputStream callApiPdfCrowd(String text) {

		try {
			Pdfcrowd.HtmlToPdfClient client = new Pdfcrowd.HtmlToPdfClient(PDF_CROWD_USERNAME, PDF_CROWD_APIKEY);
			byte[] pdf = client.convertString("<html><body>" + StringEscapeUtils.unescapeHtml(text) + "</body></html>");

			return new ByteArrayInputStream(pdf);

		} catch (Pdfcrowd.Error why) {
			Logger.adminWarning("BuildPdf", "callApiPdfCrowd", "Pdfcrowd Error: ", why);
			throw why;
		}

	}
}
