public class protype {
	public static void main ( String args[] ) {
		String s = smartSubString("localhost:8080/here/this/gif");
		System.out.println(s);
	}
	public static String smartSubString( String s ) {
		if ( s.charAt(s.length()-1) == '/' )
			return "";
		String str = smartSubString(s.substring(0, s.length() - 1 ) )+ s.charAt(s.length()-1);
		return str;
	
	}
}
	