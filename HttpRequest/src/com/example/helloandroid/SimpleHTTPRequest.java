package com.example.helloandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SimpleHTTPRequest {
	private String output;
	private int timeout = 10000;

    SimpleHTTPRequest(String url) {
      HttpURLConnection connection = null;
      // OutputStreamWriter wr = null;
      BufferedReader rd  = null;
      StringBuilder sb = null;
      String line = null;
    
      URL serverAddress = null;
    
      try {
          serverAddress = new URL(url);
          //set up out communications stuff
          connection = null;
        
          //Set up the initial connection
          connection = (HttpURLConnection)serverAddress.openConnection();
          connection.setRequestMethod("GET");
          connection.setDoOutput(true);
          connection.setReadTimeout(this.timeout);
                    
          connection.connect();
        
          //get the output stream writer and write the output to the server
          //not needed in this example
          //wr = new OutputStreamWriter(connection.getOutputStream());
          //wr.write("");
          //wr.flush();
        
          //read the result from the server
          rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
          sb = new StringBuilder();
        
          /* INSERT READ!! */////////////////////////////////
          
          while ((line = rd.readLine()) != null)
          {
              sb.append(line + '\n');
        	  //sb.append("asdf\n");
          }
          
          this.output = sb.toString();
                    
      } catch (MalformedURLException e) {
          this.output = "Malformed Url " + e.getMessage();
      } catch (ProtocolException e) {
    	  this.output = "Protocol Error " + e.getMessage();
      } catch (IOException e) {
          this.output = "IO Error " + e.getMessage();
      }
      finally
      {
          //close the connection, set all objects to null
          connection.disconnect();
          rd = null;
          sb = null;
          // wr = null;
          connection = null;
      }
    }
    
    public String getOutput() {
    	return this.output;
    }
}
