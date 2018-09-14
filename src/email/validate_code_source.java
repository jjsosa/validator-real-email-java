package email;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.naming.*; 
import javax.naming.directory.*;
public class validate_code_source {
	
	private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	 public static void main( String args[] ) {
	      String testData[] = {
	    		  "jjjwqeufrhwue@clever-global.com",
	    		  "sosaosuna@gmail.com",
	    		  "uwhkegoeirngoihwoei@gmail.com",
	    		  "meloinvento@noexiste.com",
	    		  "tvf@tvf.cz",
	    		  "info@ermaelan.com",
	    		  "drp@drp.cz",
	    		  "begeddov@jfinity.com",
	    		  "vdv@dyomedea.com",
	    		  "me@aaronsw.com",
	    		  "aaron@theinfo.org",
	    		  "rss-dev@yahoogroups.com",
	    		  "tvf@tvf.cz",
	          };
	      
	      for (int i = 0; i<testData.length; i++) {
	         System.out.println(testData[i] + " is valid? " + 
	               isAddressValid(testData[i]));
	      }
	 }
	 
    private static int hear( BufferedReader in ) throws IOException {
      String line = null;
      int res = 0;
      while ( (line = in.readLine()) != null ) {
          String pfx = line.substring( 0, 3 );
          try {
             res = Integer.parseInt( pfx );
          } 
          catch (Exception ex) {
             res = -1;
          }
          if ( line.charAt( 3 ) != '-' ) break;
      }
      return res;
      }
    
    private static void say( BufferedWriter wr, String text ) 
       throws IOException {
      wr.write( text + "\r\n" );
      wr.flush();
      return;
      }
    
    private static ArrayList getMX( String hostName )
          throws NamingException {
      // Perform a DNS lookup for MX records in the domain
      Hashtable env = new Hashtable();
      env.put("java.naming.factory.initial",
              "com.sun.jndi.dns.DnsContextFactory");
      DirContext ictx = new InitialDirContext(env);
      Attributes attrs = ictx.getAttributes
                            (hostName, new String[] {"MX"});
      Attribute attr = attrs.get("MX");
      // If we don't have an MX record, try the machine itself
      if ((attr == null) || (attr.size() == 0)) {
        attrs = ictx.getAttributes( hostName, new String[] { "A" });
        attr = attrs.get( "A" );
        if( attr == null ) 
             throw new NamingException
                      ( "No match for name '" + hostName + "'" );
      }
      // Huzzah! we have machines to try. Return them as an array list
      // NOTE: We SHOULD take the preference into account to be absolutely
      //   correct. This is left as an exercise for anyone who cares.
      ArrayList mailExchangerDomainList = new ArrayList();
      NamingEnumeration mailExchangerCompleteList = attr.getAll();
      while ( mailExchangerCompleteList.hasMore() ) {
         String mx = (String) mailExchangerCompleteList.next();
         // If the Mail Exchanger is an IP, then may be false
         if(Pattern.matches(IPADDRESS_PATTERN, mx)) {
        	 
         }else {
        	 String f[] = mx.split( " " );
             if ( f[1].endsWith( "." ) ) 
                 f[1] = f[1].substring( 0, (f[1].length() - 1));
             mailExchangerDomainList.add( f[1] );
         }
      }
      return mailExchangerDomainList;
      }
    
    public static boolean isAddressValid( String address ) {
      // Find the separator for the domain name
      int pos = address.indexOf('@');
      // If the address does not contain an '@', it's not valid
      if (pos == -1) return false;
      // Isolate the domain/machine name and get a list of mail exchangers
      String domain = address.substring(++pos);
      ArrayList mxList = null;
      try {
         mxList = getMX(domain);
      } catch (NamingException ex) {
         return false;
      }
      // Just because we can send mail to the domain, doesn't mean that the
      // address is valid, but if we can't, it's a sure sign that it isn't
      if (mxList.size() == 0) return false;
      // Now, do the SMTP validation, try each mail exchanger until we get
      // a positive acceptance. It *MAY* be possible for one MX to allow
      // a message [store and forwarder for example] and another [like
      // the actual mail server] to reject it. This is why we REALLY ought
      // to take the preference into account.
      for (int mx = 0; mx < mxList.size(); mx++) {
          boolean valid = false;
          try {
              int res;
              Socket skt = new Socket((String) mxList.get(mx), 25);
              BufferedReader rdr = new BufferedReader
                 (new InputStreamReader(skt.getInputStream()));
              BufferedWriter wtr = new BufferedWriter
                 (new OutputStreamWriter(skt.getOutputStream()));
              res = hear(rdr);
              if (res != 220) throw new Exception("Invalid header");
              say(wtr, "EHLO orbaker.com");
              res = hear(rdr);
              if (res != 250) throw new Exception("Not ESMTP");
              // Validate the sender address  
              say(wtr, "MAIL FROM: <tim@orbaker.com>");
              res = hear(rdr);
              if (res != 250) throw new Exception("Sender rejected");
              say(wtr, "RCPT TO: <" + address + ">");
              res = hear(rdr);
              // Be polite
              say(wtr, "RSET"); hear(rdr);
              say(wtr, "QUIT"); hear(rdr);
              if (res != 250) 
                 throw new Exception("Address is not valid!");
              valid = true;
              rdr.close();
              wtr.close();
              skt.close();
          } 
          catch (Exception ex) {
            // Do nothing but try next host
          } 
          finally {
            if (valid) return true;
          }
      }
      return false;
      }
    public String call_this_to_validate(String email) {
        String testData[] = {email};
        String return_string="";
        for (int j = 0 ; j < testData.length ; j++ ) {
        	return_string=( testData[j] + " is valid? " + 
                 isAddressValid( testData[j] ) );
        }
        return return_string;
        }
}