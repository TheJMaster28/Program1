/**
* Web worker: an object of this class executes in its own new thread
* to receive and respond to a single HTTP request. After the constructor
* the object executes on its "run" method, and leaves when it is done.
*
* One WebWorker object is only responsible for one client connection. 
* This code uses Java threads to parallelize the handling of clients:
* each WebWorker runs in its own thread. This means that you can essentially
* just think about what is happening on one client at a time, ignoring 
* the fact that the entirety of the webserver execution might be handling
* other clients, too. 
*
* This WebWorker class (i.e., an object of this class) is where all the
* client interaction is done. The "run()" method is the beginning -- think
* of it as the "main()" for a client interaction. It does three things in
* a row, invoking three methods in this class: it reads the incoming HTTP
* request; it writes out an HTTP header to begin its response, and then it
* writes out some HTML content for the response content. HTTP requests and
* responses are just lines of text (in a very particular format). 
*
**/

import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
//import java.util.Date;
import java.text.DateFormat;
//import java.util.TimeZone;
import java.util.*;

public class WebWorker implements Runnable
{

private Socket socket;
private int error=0;

/**
* Constructor: must have a valid open socket
**/
public WebWorker(Socket s)
{
   socket = s;
}

/**
* Worker thread starting point. Each worker handles just one HTTP 
* request and then returns, which destroys the thread. This method
* assumes that whoever created the worker created it with a valid
* open socket object.
**/
public void run()
{
   System.err.println("Handling connection...");
   try {
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      String request = readHTTPRequest(is,os);
      writeHTTPHeader(os,"text/html"); 
	  if ( error == 1 )
		ErrorWrite(os);
	  else if ( request.isEmpty() )
		writeContent(os);
	  else
		 writeRequestContent(os, is, request );
      os.flush();
      socket.close();
   } catch (Exception e) {
      System.err.println("Output error: "+e);
   }
   System.err.println("Done handling connection.\n");
   return;
}

/**
* Read the HTTP request header.
**/
private String readHTTPRequest(InputStream is, OutputStream os)
{
   String line;
   BufferedReader r = new BufferedReader(new InputStreamReader(is));
   while (true) {
      try {
         while (!r.ready()) Thread.sleep(1);
         line = r.readLine();
         System.err.println("Request line: ("+line+")");
         if (line.length()==0) break;
		 // my stuuf here
		 
		 if ( line.substring(0,3).compareTo("GET") == 0 ){
			 if ( line.substring(5, line.length() - 9).compareTo("") == 0 ) break;
			 BufferedReader reader = new BufferedReader( new FileReader(line.substring(5, line.length() - 9) ) );
			 StringBuilder s = new StringBuilder();
			 String l = reader.readLine();
			 System.out.println(l);
			 while ( l != null ) {
				 s.append(l + "\n");
				 
				 l = reader.readLine();
			 }
			 l = s.toString();
			 return l;
			 //System.out.println(l);
			 //writeRequestContent(os, is, l );
			 //os.write(l.getBytes());
			 //File requestFile = new File(line.substring(4, line.length() - 9) );
			 //os.write(requestFile.readAllBytes()
		 }
		 
      } catch (Exception e) {
         System.err.println("Request error: "+e);
		 error =1;
         break;
      }
   }
   return "";
}

/**
* Write the HTTP header lines to the client network connection.
* @param os is the OutputStream object to write to
* @param contentType is the string MIME content type (e.g. "text/html")
**/
private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
{
   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("GMT"));
   os.write("HTTP/1.1 200 OK\n".getBytes());
   os.write("Date: ".getBytes());
   os.write((df.format(d)).getBytes());
   os.write("\n".getBytes());
   os.write("Server: Jon's very own server\n".getBytes());
   //os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
   //os.write("Content-Length: 438\n".getBytes()); 
   os.write("Connection: close\n".getBytes());
   os.write("Content-Type: ".getBytes());
   os.write(contentType.getBytes());
   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   return;
}

/**
* Write the data content to the client network connection. This MUST
* be done after the HTTP header has been written out.
* @param os is the OutputStream object to write to
**/
private void writeContent(OutputStream os) throws Exception
{
   os.write("<html><head></head><body>\n".getBytes());
   os.write("<h3>My web server works!\n how does this work?</h3>\n".getBytes());
   os.write("</body></html>\n".getBytes());
}
private void writeRequestContent(OutputStream os, InputStream is, String request ) throws Exception {
	
	os.flush();
	
	//writeHTTPHeader(os,"text/html");
	
	// write somw how to process string to find <cs371date> and <cs371server>
	//Scanner scan = new Scanner(request);
	
	//scan.nextLine().replace(
	/*StringBuilder s = new StringBuilder();
	String l = "";
	while ( scan.hasNext() ) {
		l = 
		s.append(scan.next());
		l = s.toString();
		if ( l.compareTo("<cs371date>") == 0) {
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			os.write("Date: ".getBytes());
			os.write((df.format.(d)).getBytes();
			
		}
		else if ( l.compareTo("<cs371server>") == 0 ) {
			os.write("The Cake is a Lie".getBytes());
			\
		}
		else if ( l.length
	} 
	
	while (scan.hasNext()){
		
		
	}*/
	Date d = new Date();
	DateFormat df = DateFormat.getDateTimeInstance();
	df.setTimeZone(TimeZone.getTimeZone("GMT"));
	os.write(request.replace("<cs371date>", (df.format(d)) ).replace("<cs371server>", "Jeffrey's Java Server" ).getBytes() );
	
}
private void ErrorWrite ( OutputStream os ) throws Exception {
	os.write("<html><head></head><body>\n".getBytes());
   os.write("<h3>404 Not Found</h3>\n".getBytes());
   os.write("</body></html>\n".getBytes());
}

} // end class
