package au.java.traffic.analysis;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import au.java.traffic.analysis.constant.Constants;
import au.java.traffic.analysis.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
	public static void main(String[] args) throws IOException, ParseException {
		String fileName = App.class.getClassLoader().getResource(Constants.TRAFFIC_FILE_NAME).getFile();
		log.info(fileName);

		Map<String, Long> traficMap = Util.getCarTrafics(fileName, false);
		Util.showTotalTraficCars(traficMap);
		Util.showDailyCars(traficMap);
		
		Map<String, Long> halfHourTraficMap = Util.getCarTrafics(fileName, true);
		Util.showTop3HalfHourCars(halfHourTraficMap);
		Util.showLeastCarsIn3ContiguousHalfHours(halfHourTraficMap);
	}
}
