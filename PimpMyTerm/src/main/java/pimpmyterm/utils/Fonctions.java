package pimpmyterm.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pimpmyterm.beans.Log;
import pimpmyterm.core.Core;

public class Fonctions {

	public static List<String> currentBuffer = new ArrayList<String>();

	public static void trace(String type, String message, String from) {
		String date = Fonctions.getDateFormat(new Date(), "HH:mm:ss.SSS");
		String threadId=String.format("%02d", Thread.currentThread().getId());
		String line = date + " [Thread-" + threadId + "] " + type + " " + from + " - " + message;
		Log aLog=new Log(new Date(),date,threadId,type,from,message);
		if ("ERR".equals(type)) {
			System.err.println(line);
			Core.getInstance().getLogs().add(aLog);
		} else if ("DEAD".equals(type)) {
			System.err.println(line);
			System.err.println(
					date + " " + Thread.currentThread().getId() + " " + type + " " + from + " - Fatal error, exit");
			Core.getInstance().getLogs().add(aLog);
			System.exit(1);

		} else if ("DBG".equals(type)) {
			if (Core.getInstance().getDebug()) {
				System.out.println(line);
				Core.getInstance().getLogs().add(aLog);
			}
		} else {
			System.out.println(line);
			Core.getInstance().getLogs().add(aLog);
		}


		if ( Core.getInstance().getLogs().size() > Core.getInstance().getMaxLogEntries()) 
			{
			Core.getInstance().getLogs().remove(0);
			}

	}
	
	

	public static String getDateFormat(Date date, String format_Ex_YYYY_MM_DD) {
		if (format_Ex_YYYY_MM_DD == null) {
			format_Ex_YYYY_MM_DD = "yyyyMMddHHmmss";
		}
		DateFormat dateFormat = new SimpleDateFormat(format_Ex_YYYY_MM_DD);
		return dateFormat.format(date);

	}

	public static String getFieldFromString(String chaine, String delimiteur, Integer Field) {
		String[] liste = chaine.split(delimiteur);
		String retour = "";
		try {
			retour = liste[Field];
		} catch (Exception e) {

		}
		return retour;
	}

	public static Date getDateFormat(String date, String format_Ex_YYYY_MM_DD) {
		Date dt = null;
		try {
			if (format_Ex_YYYY_MM_DD == null) {
				format_Ex_YYYY_MM_DD = "yyyyMMddHHmmss";
			}
			SimpleDateFormat df = new SimpleDateFormat(format_Ex_YYYY_MM_DD);

			dt = df.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dt;

	}
	
	public static Double dmsToDd(String coordinate,String direction)
	{
		// 4743.4849
		// 00645.1911
		String leftPart=Fonctions.getFieldFromString(coordinate, "\\.", 0);
		Integer degree=Integer.parseInt(leftPart) / 100;
		Integer minute=Integer.valueOf((Integer.parseInt(leftPart) - (degree * 100)));
		String rightPart=Fonctions.getFieldFromString(coordinate, "\\.", 1);
		Double minuteDecimale=Double.valueOf(minute +"." + rightPart) / 60d;
		if ( direction.equals("S") || direction.equals("W"))
		{
			return Double.valueOf(degree + minuteDecimale) * -1;
		}

		return Double.valueOf(degree + minuteDecimale);
	}

	public static void attendre(Integer timeInMs) {
		try {
			Thread.sleep(timeInMs);
		} catch (InterruptedException e) {
			System.out.println("Something wrong while waiting");
		}
	}


	
	public static void writeArrayInAFile(String file, List<String> liste, Boolean append) {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(file, append));
			for (String chaine : liste) {
				out.write(chaine);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeDataInAFile(String file, String ligne) {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(file, true));
			out.write(ligne);
			out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void flushBufferIntoFile(String fileName) {
		writeArrayInAFile(fileName, currentBuffer, true);
		currentBuffer.clear();
	}
	
	public static Boolean toBoolean(boolean value)
	{
		if ( value )
		{
			return Boolean.TRUE;
		}else
		{
			return Boolean.FALSE;
		}
	}

	public static List<String> shellCommand(String commande) {
		List<String> liste = new ArrayList<String>();
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(commande);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {

				liste.add(line);

			}
			input.close();
		} catch (IOException e) {
			liste.add("Error in command " + commande);
		}

		return liste;
	}

	/**
	 * Charge dans son int�gralit� un ou +sieurs fichiers et place les lignes de
	 * ceux-ci dans une ArrayList
	 * 
	 * @param file Nom du fichier relatif au chemin des datas séparés par des
	 *             virgules optionnellement
	 * @return ArrayList contenant les lignes du fichier � lire
	 * @throws IOException En cas d'erreur IO
	 */
	public static ArrayList<String> getAllLinesFromFiles(String path, String file) throws IOException {
		ArrayList<String> lignes = new ArrayList<String>();
		for (String ssFile : file.split(",")) {
			BufferedReader lecteurAvecBuffer = null;
			String ligne;
			lecteurAvecBuffer = new BufferedReader(new FileReader(path + "/" + ssFile));
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				// Fonctions.outPrintln(ligne);
				lignes.add(ligne);
			}
			lecteurAvecBuffer.close();
		}
		return lignes;
	}

	/**
	 * Charge dans son int�gralit� un fichier et place les lignes de celui-ci dans
	 * une ArrayList
	 * 
	 * @param file Nom du fichier relatif au chemin des datas
	 * @return ArrayList contenant les lignes du fichier � lire
	 * @throws IOException En cas d'erreur IO
	 */
	public static ArrayList<String> getAllLinesFromFile(String file) throws IOException {
		ArrayList<String> lignes = new ArrayList<String>();
		BufferedReader lecteurAvecBuffer = null;
		String ligne;
		lecteurAvecBuffer = new BufferedReader(new FileReader(file));
		while ((ligne = lecteurAvecBuffer.readLine()) != null) {
			// Fonctions.outPrintln(ligne);
			lignes.add(ligne);
		}
		lecteurAvecBuffer.close();
		return lignes;
	}

	public static String getFreeMemory() {
		Float memoire = Float.valueOf(Long.valueOf(Runtime.getRuntime().freeMemory()));
		return String.format("%.2f", Float.valueOf(memoire / 1024 / 1024  / 1024));
	}

	public static String getTotalMemory() {
		Float memoire = Float.valueOf(Long.valueOf(Runtime.getRuntime().totalMemory()));
		return String.format("%.2f", Float.valueOf(memoire / 1024 / 1024  / 1024));
	}

	public static String getUsedMemory() {
		Float free = Float.valueOf(Long.valueOf(Runtime.getRuntime().freeMemory()));
		Float total = Float.valueOf(Long.valueOf(Runtime.getRuntime().totalMemory()));
		return String.format("%.2f", Float.valueOf((total - free) / 1024 / 1024  / 1024));
	}

	public static String egrep(String pattern, String chaine) {
		Boolean present = Boolean.TRUE;

		for (String subPattern : pattern.split("\\|")) {
			if (chaine.contains(subPattern)) {
				present = present && true;

			} else {
				present = present && false;
			}
		}
		if (present) {
			return chaine;
		} else
			return "";
	}

	@SuppressWarnings("deprecation")
	public static List<String> getReponseFromUrl(String url) {
		List<String> retour = new ArrayList<String>();
		URL u;
		InputStream is = null;
		DataInputStream dis;
		String s;

		try {
			u = new URL(url);
			is = u.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));
			while ((s = dis.readLine()) != null) {
				retour.add(s);
			}
		} catch (MalformedURLException mue) {
			System.err.println("Ouch - a MalformedURLException happened.");
			mue.printStackTrace();

		} catch (IOException ioe) {
			System.err.println("Oops- an IOException happened.");
			ioe.printStackTrace();

		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}
		return retour;
	}

	public static String serializeList(List<String> aList) {
		StringBuilder builder = new StringBuilder();
		if (aList != null) {
			for (String aLine : aList) {
				builder.append(aLine + ",");
			}
		}
		return builder.toString();
	}

	public static String getComputerName() {
		String computerName = "";
		try {
			computerName = System.getenv().get("COMPUTERNAME");
			if (computerName == null || "".equals(computerName)) {

				computerName = shellCommandOneLine("hostname");
			}
		} catch (IOException e) {
			computerName = "Unknown";
		}
		return computerName;
	}

	public static String shellCommandOneLine(String commande) throws IOException {
		String retour = "";

		Process p = null;
		p = Runtime.getRuntime().exec(commande);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = input.readLine()) != null) {
			retour = line;
		}
		input.close();
		return retour;
	}

	public static void traceBanner() {
		
        
		Fonctions.trace("INF", " _____ _           _____     _____               ","CORE");
		Fonctions.trace("INF", "|  _  |_|_____ ___|     |_ _|_   _|___ ___ _____","CORE"); 
		Fonctions.trace("INF", "|   __| |     | . | | | | | | | | | -_|  _|     |","CORE"); 
		Fonctions.trace("INF", "|__|  |_|_|_|_|  _|_|_|_|_  | |_| |___|_| |_|_|_|","CORE"); 
		 
				  
	}
}
