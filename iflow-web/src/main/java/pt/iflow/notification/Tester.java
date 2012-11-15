package pt.iflow.notification;

import pt.iflow.api.notification.Email;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Tester {

  public static void main(String[] args) {
//    Email email = new Email("sembom04xmh01.rl.mcnet.pt");
    //Email email = new Email("mail.telepac.pt");
    //email.SendMsg("jose.costa@iknow.pt", "jose.costa@iknow.pt", "Teste1", "Testing");
    
//    Email email = new Email("eddie.iknow.pt", "gosma", "mesda");
//    Email email = new Email("eddie.iknow.pt");
    
    Email email = new Email("mail");
    email.setStartTls(false);
    email.setUser(null);
    email.setPass(null);
    email.setAuth(false);
    
//    Email email = new Email("smtp.gmail.com", "oscar.lopes@gmail.com", "rfc1459");
//    email.DisableEmailManager();
//    email.bEmailManager=true;
//  email.SendMsg("oscar@iknow.pt", "oscar.lopes@gmail.com", "Teste1", "Testing");
    email.sendMsg("oscar@iknow.pt", "oscar@iknow.pt", "Teste1", "Testing");

    /*
        SMSFtpImpl sms = new SMSFtpImpl();

        sms.setServer("localhost");
        sms.setUser("weblogic");
        sms.setPassword("wl123");
        sms.setRemoteDir("smstest");
        sms.setRemoteFile(sms.smsFileName());

        sms.setDate (new Date());
        sms.setPhone ("917501486");
        sms.setSubject ("TESTE1");
        sms.setMessage ("OLA PALHACO, O SEU CREDITO FOI APROVADO 3.");
        System.out.println ("FORMAT: " + sms.formatSMS());

        sms.addSms();

        sms.setDate (new Date());
        sms.setPhone ("916018473");
        sms.setSubject ("TESTE 7");
        sms.setMessage ("TESTE DO BANCO NUMERO SETE ola bom dia");
        System.out.println ("FORMAT: " + sms.formatSMS());

        sms.addSms();

        sms.sendBatch();
*/

  }
}