package au.java.traffic.analysis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {
	/**
	 * Get all lines from the given trafic file
	 * 
	 * @param traficMap
	 * @param bReader
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void readTrafic(Map<String, Long> traficMap, BufferedReader bReader) throws NumberFormatException, IOException, ParseException {
		String line;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        while ((line = bReader.readLine()) != null) {
        	String datavalue[] = line.split(" ", -1);
			if(datavalue.length > 0){
				traficMap.put(datavalue[0].toString(), Long.valueOf(datavalue[1].toString()));
			}
        }
        bReader.close();
	}
	
	/**
	 * Get contiguous half hour records from the given trafic file
	 * 
	 * @param traficMap
	 * @param bReader
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void readHalfHourTrafic(Map<String, Long> traficMap, BufferedReader bReader) throws NumberFormatException, IOException, ParseException {
		String line;
		Date firstDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        while ((line = bReader.readLine()) != null) {
        	String datavalue[] = line.split(" ", -1);
			if(datavalue.length > 0){
				Date secondDate = sdf.parse(datavalue[0].toString());
				if(isHalfHourTimeframe(firstDate, secondDate)) {
					traficMap.put(datavalue[0].toString(), Long.valueOf(datavalue[1].toString()));
				}
				firstDate = secondDate;
			}
        }
        bReader.close();
	}
	
	/**
	 * Compare if two dates have half hour (30 minutes) timeframe
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	private static Boolean isHalfHourTimeframe(Date firstDate, Date secondDate) {
		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		if(diff == 30) return true;
		
		return false;
	}
	
	/**
	 * Compare if two dates are equal 
	 * 
	 * @param first
	 * @param second
	 * @return
	 * @throws ParseException
	 */
	private static Boolean isSameDay(String first, String second) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date firstDate = sdf.parse(first);
	    Date secondDate = sdf.parse(second);
	     
		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		if(diff == 0) return true;
		
		return false;
	}
	
	/**
	 * Get trafic cars from given file
	 * 
	 * @param dataFileName
	 * @param isHalfHourTrafic
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static Map<String, Long> getCarTrafics(String dataFileName, Boolean isHalfHourTrafic) throws IOException, ParseException {
		Map<String, Long> traficMap = new TreeMap<>();
		
		File dataFile = new File(dataFileName);
		boolean isExist = dataFile.exists();
		if(isExist) {
			log.debug("Data file is found");
			
	        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));
	        if(isHalfHourTrafic) readHalfHourTrafic(traficMap, bReader);
	        else readTrafic(traficMap, bReader);
		}
		
		return traficMap;
	}
	
	/**
	 * The number of cars seen in total
	 * 
	 * @param traficMap
	 */
	public static void showTotalTraficCars(Map<String, Long> traficMap) {
		long totalCars = 0;
		if(traficMap != null && traficMap.size() > 0) {
			for(Map.Entry<String, Long> entry : traficMap.entrySet()) {
				totalCars += entry.getValue().longValue();
			}
		}
		
		log.info("==============================================");
		log.info("Total Cars: " + totalCars);
	}
	
	/**
	 * A sequence of lines where each line contains a date (in yyyy-mm-dd format) 
	 * and the number of cars seen on that day (eg. 2016-11-23 289) 
	 * for all days listed in the input file.
	 * 
	 * @param traficMap
	 * @throws ParseException
	 */
	public static void showDailyCars(Map<String, Long> traficMap) throws ParseException {
		log.info("==============================================");
		log.info("Daily Cars:");
		if(traficMap != null && traficMap.size() > 0) {
			long dailyTotal = 0;
			int counter = 0;
			String first = "";
			String second = "";
			
			for(Map.Entry<String, Long> entry : traficMap.entrySet()) {
				String strTraficTime = entry.getKey().substring(0, 10);
				counter++;
				if(counter == 1) {
					first = strTraficTime;
					dailyTotal = entry.getValue();
					continue;
				}
				second = strTraficTime;
				
				if(isSameDay(first, second)) dailyTotal += entry.getValue();
				else {
					log.info(first + " " + dailyTotal);
					first = strTraficTime;
					dailyTotal = entry.getValue();
				}
			}
			log.info(second + " " + dailyTotal);
		}
	}

	/**
	 * The top 3 half hours with most cars, in the same format as the input file
	 * 
	 * @param traficMap
	 */
	public static void showTop3HalfHourCars(Map<String, Long> traficMap) {
		log.info("==============================================");
		log.info("The top 3 half hours with most cars:");
		if(traficMap != null && traficMap.size() > 0) {
			Map<String, Long> sortedMap = new LinkedHashMap<>();
			traficMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
			sortedMap.entrySet().stream().limit(3).forEach(x -> log.info(x.getKey() + " " + x.getValue()));
		}
	}
	
	/**
	 * The 1.5 hour period with least cars (i.e. 3 contiguous half hour records)
	 * 
	 * @param traficMap
	 * @throws ParseException 
	 */
	public static void showLeastCarsIn3ContiguousHalfHours(Map<String, Long> traficMap) throws ParseException {
		log.info("==============================================");
		log.info("The 1.5 hour period with least cars:");
		
		long maxValue = Long.MAX_VALUE;
		long subTotal = 0;
		if(traficMap != null && traficMap.size() > 0) {
			String first = "", second = "";
			String firstKey = "", secondKey = "";
			
			int counter = 0;
			Map<String, Long> resultdMap = new LinkedHashMap<>();
			while(traficMap.size() >= 3) {
				Map<String, Long> savedMap = new LinkedHashMap<>();
				for(Map.Entry<String, Long> entry : traficMap.entrySet()) {
					String strTraficTime = entry.getKey().substring(0, 10);
					counter++;
					savedMap.put(entry.getKey(), entry.getValue());
					subTotal += entry.getValue().longValue();
					if(counter == 1) {
						first = strTraficTime;
						firstKey = entry.getKey();
					} else {
						second = strTraficTime;
						if(counter == 2) {
							secondKey = entry.getKey();
						} else if(counter == 3) {
							/* if same day, remove first, otherwise remove all the days */
							traficMap.remove(firstKey);
							if(isSameDay(first, second)) {
								if(subTotal < maxValue) {
									maxValue = subTotal;
									resultdMap = savedMap;
								}
							} else traficMap.remove(secondKey);
							
							counter = 0;
							subTotal = 0;
							break;
						}
					}
				}
			}
			resultdMap.entrySet().stream().forEach(x -> log.info(x.getKey() + " " + x.getValue()));
		}
	}
}
