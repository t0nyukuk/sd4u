package sd4u.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sd4u.html.HTMLMerger;
import sd4u.html.HTMLParser;
import sd4u.html.UrlConverter;
import sd4u.image.ImageDownloader;
import sd4u.image.WrongFormatException;

/**
 * This class provides save singular pages with changings
 * @author Halil Cetiner & Y. Hakan Kalayci
 */

public class HTMLSaver {
	
	public static int FILE = 0;
	public static int PROJECT = 1;
	
	//type will be used for saving images and other data fixed areas such as /images
	/**
	 * This method finishes saving
	 * @param html Html code as string
	 * @param path File path project will be saved in
	 * @param type type will be used for saving images and other data fixed areas such as images
	 */
	public static void finish(String html,String path,int type){
		//System.out.println(html);
		//System.out.println(html);
		path=UrlConverter.toJava(path);
		System.out.println("path: " + path);
		html=showHideAdjust(html);
		String title = HTMLParser.getTitle(html);
		String page = HTMLParser.getPage(html);
		HTMLMerger merger = new HTMLMerger(title,page);
		String htmlText=merger.getFullHTML();
		if( type==PROJECT ){
			Pattern replace = Pattern.compile("../../sd4uBundle");
			Matcher regexMatcher = replace.matcher( htmlText.trim() );
			htmlText = regexMatcher.replaceAll( "../../../sd4uBundle" );
		}
		saveHtmlFile( htmlText,path );
	}
	
	//this part for images we must copy image to our directory or specified director than we should give that
	//a relative path
	//this part takes two path and than changes path in html text to relative path. 
	
	/**
	 * This method provides using images by saving them in a folder
	 * @param sb
	 * @param initialIndex 
	 * @param path
	 */
	public static void editPath(StringBuffer sb, int initialIndex,String path){
		
		StringBuffer prefix = new StringBuffer("");
		int finalIndex = initialIndex;
		for( int i=0;i<path.length();i++ ){
			//System.out.println(path.charAt(i));
			if( path.charAt(i)==sb.charAt( finalIndex ) )
				finalIndex++;
			else
			{
				for(int j=i;j<path.length();j++)
					if( path.charAt(i)=='/' )
						prefix.append("../");
				break;
			}
		}
		sb.replace(initialIndex, finalIndex,prefix.toString());
		
	}
	
	public static String ON_THE_EDITOR = "<!-- On the editor --></div>";
	public static String ON_THE_FILE = "<!-- On the file --></pre>";
	public static String showHideAdjust(String html){
		
		StringBuffer sb = new StringBuffer(html);
		
		while(true){
			
			int initIndex = sb.indexOf(ON_THE_EDITOR);
			if(initIndex==-1)
				break;
			initIndex += "<!-- On the editor -->".length();
			int lastIndex = initIndex + "</div>".length();
			sb.replace(initIndex,lastIndex,"");
			int newPos = sb.indexOf(ON_THE_FILE,initIndex)+ON_THE_FILE.length();
			sb.insert(newPos, "</div>");
		}
		
		return sb.toString();
		
	}
	
	/**
	 * This method checks that is there an image at Html code
	 * @param html Html code which will be checked
	 * @return
	 */
	public static boolean hasImage(String html){
		return html.indexOf("<img src=")!=-1;
	}
	
	/**
	 * This method gets image to save in a new folder
	 * @param html Html code
	 * @param path image's path
	 * @param name image's name
	 * @return
	 */
	public static String downloadImages(String html,String path,String name){
		StringBuffer sb = new StringBuffer(html);
		int initIndex=0;
		int endIndex=0;
		ImageDownloader downloader = new ImageDownloader();
		int counter = 1;
		while(true){
			initIndex = sb.indexOf( "<img src=\"",initIndex );
			if(initIndex==-1)
				break;
			initIndex+="<img src=\"".length();
			//System.out.println("initIndex="+initIndex+" "+name);
			endIndex = sb.indexOf( "\"",initIndex );
			String currentPath = path+"/img"+counter;
			
			try {
				String asd=downloader.downloadImage(sb.substring(initIndex, endIndex),currentPath );
				sb.replace(initIndex, endIndex, name+"/"+getName(asd) );
				//System.out.println(asd);
				//System.out.println(sb.substring(initIndex, endIndex));
				//System.out.println("name/img"+counter);
			} catch (WrongFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return sb.toString();
			}
			
			counter++;
		}
		return sb.toString();
	}
	
	/**
	 * This method detects the name of image by its path
	 * @param path path of image
	 * @return name of image
	 */
	public static String getName(String path){
		String name="";
		for(int i=path.length()-1;i>=0;i--)
			if( path.charAt(i)!='/' )
				name=path.charAt(i)+name;
			else
				break;
		return name;
	}
	
	/**
	 * Provides saving edited Html code
	 * @param html Html code
	 * @param path path to save
	 */
	public static void saveHtmlFile(String html, String path){
		
		if( hasImage(html) )
		{
			new File(path).mkdir();
			html=downloadImages(html,path,getName(path));
		}
		path=path+".html";
		File file = new File(path);
		if( !file.exists() )
		{
			try {
				System.out.println( file.createNewFile() );
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(html);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
