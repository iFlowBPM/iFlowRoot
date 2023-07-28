package pt.iflow.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockSendSMS extends Block {
    public Port portIn, portSuccess, portError;

    private final static String SMS_TEXT = "smsText";
    private final static String SMS_PHONE_NUMBERS = "smsPhoneNumbers";
    private final static String SMS_PROVIDER_PASSWORD = "passwordVar";
    private final static String SMS_PROVIDER_USERNAME = "usernameVar";
    private final static String SMS_PROVIDER_URL = "urlVar";

    private static final Map<Integer, String> statusCodeMap = new HashMap<Integer, String>() {{
        put(200, "Success");
        put(1400, "Method not allowed");
        put(1401, "Invalid request");
        put(1402, "Invalid content");
        put(1403, "No data found");
        put(1404, "Invalid XML");
        put(1405, "Invalid JSON");
        put(1500, "Unknown error");
        put(1504, "Temporary error. Try again");
        put(1506, "Not implemented yet");
        put(1600, "Invalid user");
        put(1601, "Insufficient credit");
        put(1605, "Invalid email");
        put(1902, "Invalid recipient");
        put(1903, "Invalid message");
        put(1904, "MT ID not valid");
    }};


    public BlockSendSMS(int anFlowId, int id, int subflowblockid, String subflow) {
        super(anFlowId, id, subflowblockid, subflow);
        hasInteraction = false;
    }

    public Port getEventPort() {
        return null;
    }

    public Port[] getInPorts(UserInfoInterface userInfo) {
        Port[] retObj = new Port[1];
        retObj[0] = portIn;
        return retObj;
    }

    public Port[] getOutPorts(UserInfoInterface userInfo) {
        Port[] retObj = new Port[2];
        retObj[0] = portSuccess;
        retObj[1] = portError;
        return retObj;
    }

    /**
     * No action in this block
     * <p>
     * <p>
     * a value of type 'DataSet'
     *
     * @return always 'true'
     */
    public String before(UserInfoInterface userInfo, ProcessData procData) {
        return "";
    }

    /**
     * No action in this block
     * <p>
     * <p>
     * a value of type 'DataSet'
     *
     * @return always 'true'
     */
    public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
        return true;
    }


    private static String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        byte[] credentialsBytes = credentials.getBytes();
        byte[] base64CredentialsBytes = Base64.getUrlEncoder().encode(credentialsBytes);
        return "Basic " + new String(base64CredentialsBytes);
    }

    private static ClientResponse sendSms(List<String> phoneNumbers, String text, String smsUsername, String smsPassword, String smsUrl) throws JSONException {
        Client client = Client.create();

        JSONObject smsRequestJson = new JSONObject();
        smsRequestJson.put("user", smsUsername);
        smsRequestJson.put("password", smsPassword);
        smsRequestJson.put("txt", text);

        JSONObject dstJson = new JSONObject();
        dstJson.put("num", phoneNumbers);

        smsRequestJson.put("dst", dstJson);

        WebResource webResource = client.resource(smsUrl);

        return webResource
                    .type(MediaType.APPLICATION_JSON)
                    .post(ClientResponse.class, smsRequestJson.toString());
    }

    private String getStatusCodeDescription(int statusCode) {
            return statusCodeMap.get(statusCode);
    }

    /**
     * Executes the block main action
     * <p>
     * <p>
     * a value of type 'DataSet'
     *
     * @return the port to go to the next block
     */
    public Port after(UserInfoInterface userInfo, ProcessData procData) {
        try {

            Logger.info(userInfo.getUtilizador(),this,"after","BlockSendSMS - Entered");

            String smsTextVariableEncoded = procData.transform(userInfo, this.getAttribute(SMS_TEXT),true);
            String textEncoded = StringEscapeUtils.unescapeHtml(smsTextVariableEncoded);
            String text = StringEscapeUtils.unescapeHtml(textEncoded);

            String smsProviderVariablePasswordEncoded = procData.transform(userInfo, this.getAttribute(SMS_PROVIDER_PASSWORD),true);
            String smsProviderPasswordEncoded = StringEscapeUtils.unescapeHtml(smsProviderVariablePasswordEncoded);
            String smsProviderPassword = StringEscapeUtils.unescapeHtml(smsProviderPasswordEncoded);

            String smsProviderVariableUrlEncoded = procData.transform(userInfo, this.getAttribute(SMS_PROVIDER_URL),true);
            String smsProviderUrlEncoded = StringEscapeUtils.unescapeHtml(smsProviderVariableUrlEncoded);
            String smsProviderUrl = StringEscapeUtils.unescapeHtml(smsProviderUrlEncoded);

            String smsProviderVariableUsernameEncoded = procData.transform(userInfo, this.getAttribute(SMS_PROVIDER_USERNAME),true);
            String smsProviderUsernameEncoded = StringEscapeUtils.unescapeHtml(smsProviderVariableUsernameEncoded);
            String smsProviderUsername = StringEscapeUtils.unescapeHtml(smsProviderUsernameEncoded);

            String smsPhoneNumbersVariableEncoded = procData.transform(userInfo, this.getAttribute(SMS_PHONE_NUMBERS),true);
            String smsPhoneNumbersEncoded = StringEscapeUtils.unescapeHtml(smsPhoneNumbersVariableEncoded);
            String phoneNumbers = StringEscapeUtils.unescapeHtml(smsPhoneNumbersEncoded);

            Logger.info(userInfo.getUtilizador(),this,"after","BlockSendSMS - Variables retrieved as input on block: text-"
                    + text + " | url:" + smsProviderUrl + " | username: " + smsProviderUsername + " | password: " + smsProviderPassword + " | "
                    + " Phone Numbers: " + phoneNumbers);

            List<String> phoneNumbersList = new ArrayList<>(Arrays.asList(phoneNumbers.split(",")));

            ClientResponse response = sendSms(phoneNumbersList, text, smsProviderUsername, smsProviderPassword, smsProviderUrl);

            Logger.info(userInfo.getUtilizador(),this,"after","BlockSendSMS - Response Status Code: "+response.getStatus() + " with description: " + getStatusCodeDescription(response.getStatus()));

            if(response.getStatus() == HttpStatus.SC_OK || response.getStatus() == HttpStatus.SC_ACCEPTED) {
                return portSuccess;
            }
            else {
                return portError;
            }
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            Logger.error(userInfo.getUtilizador(),this,"after","BlockSendSMS - Error sending SMS: " + e.getMessage());
            return portError;
        }
    }

    @Override
    public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getResult(UserInfoInterface userInfo, ProcessData procData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
        // TODO Auto-generated method stub
        return null;
    }

}