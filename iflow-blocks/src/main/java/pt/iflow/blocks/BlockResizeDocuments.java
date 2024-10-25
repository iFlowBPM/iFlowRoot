package pt.iflow.blocks;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.io.FilenameUtils;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.StringUtilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BlockResizeDocuments
        extends Block
{
    public Port portIn;
    public Port portSuccess;
    public Port portEmpty;
    public Port portError;
    private static final String LIST_DOCID = "list_docid";
    private static final String MAX_WIDTH = "maxWidth";
    private static final String MAX_HEIGHT = "maxHeight";

    public BlockResizeDocuments(int anFlowId, int id, int subflowblockid, String filename)
    {
        super(anFlowId, id, subflowblockid, filename);
        this.hasInteraction = false;
    }

    public Port getEventPort()
    {
        return null;
    }

    public Port[] getInPorts(UserInfoInterface userInfo)
    {
        Port[] retObj = new Port[1];
        retObj[0] = this.portIn;
        return retObj;
    }

    public Port[] getOutPorts(UserInfoInterface userInfo)
    {
        Port[] retObj = new Port[2];
        retObj[0] = this.portSuccess;
        retObj[1] = this.portEmpty;
        retObj[2] = this.portError;
        return retObj;
    }

    public String before(UserInfoInterface userInfo, ProcessData procData)
    {
        return "";
    }

    public boolean canProceed(UserInfoInterface userInfo, ProcessData procData)
    {
        return true;
    }

    public Port after(UserInfoInterface userInfo, ProcessData procData)
    {
        Port outPort = this.portSuccess;
        String login = userInfo.getUtilizador();
        Documents docBean = BeanFactory.getDocumentsBean();

        String sMaxWidth = null;
        String sMaxHeight = null;
        ProcessListVariable sDocidVar = null;

        try {
            sMaxWidth = procData.transform(userInfo, this.getAttribute(MAX_WIDTH));
            sMaxHeight = procData.transform(userInfo, this.getAttribute(MAX_HEIGHT));
            sDocidVar = procData.getList(this.getAttribute(LIST_DOCID));

        } catch (Exception e) {
            Logger.error(login, this, "after", procData.getSignature() + "error transforming attributes");
            return portError;
        }


        if (sDocidVar.size() == 0 || StringUtilities.isEmpty(sMaxWidth) || StringUtilities.isEmpty(sMaxHeight)) {
            Logger.error(login, this, "after", procData.getSignature() + "empty value for block attributes");
            outPort = portError;
        }else {

            // Iterate list of docids
            for(int i = 0; i < sDocidVar.size(); i++){
                String docId = sDocidVar.getItem(i).getValue().toString();

                Document doc = docBean.getDocument(userInfo, procData, Integer.parseInt(docId));

                if (doc == null){
                    outPort = this.portEmpty;
                    Logger.error(userInfo.getUtilizador(), this, "after",procData.getSignature() + "document doesnt exist for docid: " + sDocidVar);
                    return outPort;
                }

                String extension = FilenameUtils.getExtension(doc.getFileName().toLowerCase());

                // Check if img or pdf
                if (doc.getFileName().contains(".pdf") || doc.getFileName().contains(".PDF")) {
                    // resize pdf
                    byte[] newPdfByteArray = compressPdf(doc.getContent());

                    // Update doc size in Database
                    updateResizedDocInDb(procData, userInfo, doc,  newPdfByteArray);

                }else {
                    // resize image
                    try {
                        BufferedImage resizedImage = reduceImage(doc.getContent(), sMaxWidth, sMaxHeight, extension);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(resizedImage, extension, baos);
                        baos.flush();
                        byte[] newImgByteArray = baos.toByteArray();

                        // Update doc size in Database
                        updateResizedDocInDb(procData, userInfo, doc,  newImgByteArray);

                    } catch (Exception e) {
                        Logger.error(userInfo.getUtilizador(), this, "after",procData.getSignature() + "Error reducing image: " + doc.getFileName());
                    }
                }
            }

            outPort = this.portSuccess;
        }

        return outPort;
    }

    public static byte[] compressPdf(byte[] pdfBytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
            PdfReader reader = new PdfReader(bais);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Use PdfStamper to compress the PDF
            PdfStamper stamper = new PdfStamper(reader, baos);
            stamper.setFullCompression();  // Enable full compression
            stamper.getWriter().setCompressionLevel(9); // Set maximum compression level (0 to 9)
            stamper.setFormFlattening(true); // Flatten the form fields
            reader.removeUnusedObjects(); // Remove unused objects

            stamper.getWriter().setPdfVersion(PdfWriter.VERSION_1_4);
            reader.getCatalog().remove(PdfName.METADATA);

            stamper.close();
            reader.close();

            return baos.toByteArray();

        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateResizedDocInDb(ProcessData procData, UserInfoInterface userInfo, Document doc, byte[] newImgByteArray) {
        doc.setContent(newImgByteArray);
        Logger.info(userInfo.getUtilizador(), this, "after", "Replacing the old image with the resized one.");
        BeanFactory.getDocumentsBean().updateDocument(userInfo, procData, doc);
    }

    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public BufferedImage reduceImage(byte[] imageBytes, String newMaxWidth, String newMaxHeight, String extension) {

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage originalImage = ImageIO.read(bais);

            if (originalImage == null){
                throw new NullPointerException("Unable to get originalBufferedImage");
            }

            //resize image
            int maxWidth = Integer.parseInt(newMaxWidth);
            int maxHeigth = Integer.parseInt(newMaxHeight);

            int imageWidth = originalImage.getWidth();
            int imageHeigth = originalImage.getHeight();

            double ratio = imageWidth/Double.parseDouble(imageHeigth+"");

            double ratioWidth = maxWidth/Double.parseDouble(imageWidth + "");
            double ratioHeigth = maxHeigth/Double.parseDouble(imageHeigth + "");
            boolean ratioWidthSmaller = false;

            if (ratioWidth<ratioHeigth)
                ratioWidthSmaller=true;

            int newWidth;
            int newHeight;

            if(ratioWidthSmaller) {
                Double auxW = Double.parseDouble((imageWidth * ratioWidth) + "");
                newWidth = auxW.intValue();
                Double auxH = Double.parseDouble((newWidth/ratio) + "");
                newHeight = auxH.intValue();
            }
            else {
                Double auxH = Double.parseDouble((imageHeigth * ratioHeigth)+"");
                newHeight = auxH.intValue();
                Double auxW = Double.parseDouble((newHeight * ratio)+"");
                newWidth = auxW.intValue();
            }

            return resizeAndValidateImage(originalImage, newWidth, newHeight, extension);
        } catch (IOException e) {
        }
        return null;
    }

    public BufferedImage resizeAndValidateImage(BufferedImage originalBufferedImage, int newWidth, int newHeight, String extension){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalBufferedImage, extension, baos);
            int originalSize = baos.size();

            for(int i = 0; i < 3; i++) {
                //resize image
                BufferedImage resizedBufferedImage = resize(originalBufferedImage, newWidth, newHeight);

                ByteArrayOutputStream resizedBaos = new ByteArrayOutputStream();
                ImageIO.write(resizedBufferedImage, extension, resizedBaos);
                int resizedSize = resizedBaos.size();

                // If after resize the image is bigger than the original, return the original
                if (resizedSize >= originalSize) {
                    return originalBufferedImage;
                }

                // failed resize is 1935 bytes
                if (resizedSize > 1935) {
                    return resizedBufferedImage;
                }
            }

            // after 3 failed resize tries, return the original
            return originalBufferedImage;

        } catch (IOException e) {
            return originalBufferedImage;
        }
    }


    public String getDescription(UserInfoInterface userInfo, ProcessData procData)
    {
        return null;
    }

    public String getResult(UserInfoInterface userInfo, ProcessData procData)
    {
        return null;
    }

    public String getUrl(UserInfoInterface userInfo, ProcessData procData)
    {
        return null;
    }
}

