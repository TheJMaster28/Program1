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
import java.text.DateFormat;
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
      String request = readHTTPRequest(is);
	  File file = new File(request);
	  // checks if file exixts and is a file it can write
	  if ( !file.exists() || !file.isFile() ) {
		  if (request.compareTo("") != 0) {
			  error = 1;
		  }
	  }
	  writeHTTPHeader(os,"text/html"); 
	  // writes home page if URL is localhost:8080/
	  if ( request.compareTo("") == 0 )
		  writeHome(os);
	  
	  // otherwise write the file 
      else 
		 if ( error == 0 ) 
			writeContent(os, file);
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
* @return String of request to be checked
**/
private String readHTTPRequest(InputStream is )
{
   String line;
   String lineSubString = "";
   BufferedReader r = new BufferedReader(new InputStreamReader(is));
   while (true) {
      try {
         while (!r.ready()) Thread.sleep(1);
         line = r.readLine();
         System.err.println("Request line: ("+line+")");
         if (line.length()==0) break;
		 // reads GET request and returns string of file path 
		 if ( line.substring(0,3).compareTo("GET") == 0 ){
			lineSubString = line.substring(5, line.length() - 9);
		 }
      } catch (Exception e) {
         System.err.println("Request error: "+e);
		 break;
      }
   }
   return lineSubString;
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
   // writes 404 error if flag is set
   if ( error == 0 )
	os.write("HTTP/1.1 200 OK\n".getBytes());
   else
	os.write("HTTP/1.1 404 Not Found\n".getBytes());
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
   // writes out eror page
   if ( error == 1 ) {
	   os.write( "<html><head>404 Not Found </head> </html>".getBytes() );
   }
   return;
}

/**
* Write the data content to the client network connection. This MUST
* be done after the HTTP header has been written out.
* @param os is the OutputStream object to write to
**/
private void writeContent(OutputStream os, File request) throws Exception
{
	// sets date format
	Date d = new Date();
	DateFormat df = DateFormat.getDateTimeInstance();
	df.setTimeZone(TimeZone.getTimeZone("GMT"));
	
	// writes file if it exists and replaces tags for <cs371date> and <cs371server>
	if ( request.exists() ) {
		Scanner scan = new Scanner(request);
		while ( scan.hasNext() ) {
			os.write(scan.nextLine().replace("<cs371date>", (df.format(d)) ).replace("<cs371server>", "Jeffrey's Java Server" ).getBytes() );
		}
	}
   
}
/**
* Writes a home page if you are at localhost:8080/
* @param os is the Outputstream object to write to
*/
private void writeHome( OutputStream os ) throws Exception {
	os.write("<html><head></head><body>\n<h3>Home Page\n</h3>\n</body></html>\n".getBytes());
}
} // end class
