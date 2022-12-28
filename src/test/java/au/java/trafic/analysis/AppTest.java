package au.java.trafic.analysis;

import org.junit.jupiter.api.Test;

import au.java.traffic.analysis.App;
import au.java.traffic.analysis.constant.Constants;
import au.java.traffic.analysis.util.Util;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AppTest {

	@Test
	public void testMain() throws IOException, ParseException {
		String fileName = App.class.getClassLoader().getResource(Constants.TRAFFIC_FILE_NAME).getFile();
		log.info(fileName);

		Map<String, Long> traficMap = Util.getCarTrafics(fileName, false);
		Util.showTotalTraficCars(traficMap);
		Util.showDailyCars(traficMap);

		Map<String, Long> halfHourTraficMap = Util.getCarTrafics(fileName, true);
		Util.showTop3HalfHourCars(halfHourTraficMap);
		Util.showLeastCarsIn3ContiguousHalfHours(halfHourTraficMap);
		
		assertTrue(true);
	}

	@Test
	public void givenTwoDatesHalfHourDifferentiating() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		Date firstDate = sdf.parse("2021-12-01T05:00:00");
		Date secondDate = sdf.parse("2021-12-01T05:30:00");

		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

		assertEquals(30, diff);
	}

	@Test
	public void givenTwoDatesOneDayDifferentiating() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date firstDate = sdf.parse("2021-12-08");
		Date secondDate = sdf.parse("2021-12-09");

		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		assertEquals(1, diff);
	}
}
