package app.rasendriya.cintamasjid.prays;

import app.rasendriya.cintamasjid.prays.MethodNotif.HighLatMethod;
import app.rasendriya.cintamasjid.prays.MethodNotif.MidnightMethod;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * <p>
 * Pray times calculator based on <a href="http://praytimes.org">PrayNotifTimes.js</a>: Prayer NotifTimes Calculator (ver 2.3).
 * </p>
 * <p>
 * <strong>Usage:</strong><br/>
 * <code>
 * {@link NotifNotifTimes} pt = new {@link NotifNotifTimes}({@link MethodNotif#ISNA});<br/>
 * pt.adjustMinutes({@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#MAGHRIB}, 1);<br/>
 * pt.adjustAngle({@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#ISHA}, 18);<br/>
 * pt.tuneOffset({@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#FAJR}, 2);<br/>
 * <br/>
 * Map<NotifTime, Double> times = pt.getNotifTimes(new GregorianCalendar(2009, 2, 27), new Location(-6.1744444, 106.8294444, 10));<br/>
 * for ({@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime} t : new {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime}[] { {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#FAJR}, {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#SUNRISE}, {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#DHUHR}, {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#ASR}, {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#MAGHRIB}, {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#ISHA} }) { <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;System.out.println(t + " : " + {@link Util}.toNotifTime12(times.get(t), false)); <br/>
 * }
 * </code>
 * </p>
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class NotifTimes {

	public enum NotifTime {
		IMSAK, FAJR, SUNRISE, DHUHR, ASR, SUNSET, MAGHRIB, ISHA, MIDNIGHT
	}

	private final MethodNotif method;
	private final int numIterations = 1;
	private final Map<NotifTime, Integer> offsets;
	private double lat, lng, elv, jDate;
	private int timeZone;

	public NotifTimes(MethodNotif calculationMethodNotif) {
		method = calculationMethodNotif.clone();

		method.setMinutes(NotifTime.IMSAK, 10);
		method.setMinutes(NotifTime.DHUHR, 0);
		method.setAsrFactor(MethodNotif.ASR_FACTOR_STANDARD);
		method.setHighLatMethod(HighLatMethod.NIGHT_MIDDLE);

		// TODO: This is to fix "eval to assume minutes as angle". I don't know
		// if this is correct.
		method.setAngle(NotifTime.IMSAK, 10);
		method.setAngle(NotifTime.MAGHRIB, 1);

		// Default offsets
		offsets = new HashMap<NotifTime, Integer>();
		for (NotifTime t : NotifTime.values()) {
			offsets.put(t, 0);
		}
	}

	public MethodNotif getMethodNotif() {
		return method;
	}

	/**
	 * Adjust minute difference of specific time.
	 * <ul>
	 * <li>As for {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#IMSAK}: Minutes before fajr</li>
	 * <li>As for {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#DHUHR}: Minutes after mid-day</li>
	 * <li>As for {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#MAGHRIB}: Minutes after sunset</li>
	 * <li>As for {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#ISHA}: Minutes after maghrib</li>
	 * </ul>
	 *
	 * @param time
	 *            NotifTime to adjust the minutes. The valid times are
	 *            {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#IMSAK}, {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#DHUHR}, {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#MAGHRIB}
	 *            and {@link app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime#ISHA}.
	 * @param minutes
	 *            Minute difference
	 */
	public void adjustMinutes(NotifTime time, int minutes) {
		method.setMinutes(time, minutes);
	}

	/**
	 * Set twilight angle of specific time
	 *
	 * @param time
	 *            NotifTime to adjust the angle
	 * @param angle
	 *            angle in degree
	 */
	public void adjustAngle(NotifTime time, double angle) {
		method.setAngle(time, angle);
	}

	/**
	 * Tune offset of final calculation
	 *
	 * @param time
	 *            NotifTime of the offset
	 * @param minutes
	 *            Minutes of the offset
	 */
	public void tuneOffset(NotifTime time, int minutes) {
		offsets.put(time, minutes);
	}

	/**
	 * Set Asr factor for shadow.
	 *
	 * @param factor
	 *            The factor could be {@link MethodNotif#ASR_FACTOR_STANDARD}, {@link MethodNotif#ASR_FACTOR_HANAFI}.
	 */
	public void setAsrFactor(double factor) {
		method.setAsrFactor(factor);
	}

	/**
	 * Set method for midnight calculation
	 *
	 * @param midnightMethodNotif
	 *            Midnight method
	 */
	public void setMidnightMethodNotif(MidnightMethod midnightMethodNotif) {
		method.setMidnightMethod(midnightMethodNotif);
	}

	/**
	 * Set higher latitudes methods
	 *
	 * @param highLatMethodNotif
	 *            The method
	 */
	public void setMidnightMethod(MidnightMethod highLatMethodNotif) {
		method.setMidnightMethod(highLatMethodNotif);
	}

	/**
	 * Get wall clock times of pray times for a location in specific date.
	 *
	 * @param date
	 *            Date of pray times
	 * @param location
	 *            Location of calculation
	 * @return Wall clock times
	 */
	public Map<NotifTime, Double> getNotifTimes(Calendar date, Location location) {
		lat = location.getLat();
		lng = location.getLng();
		elv = location.getElv();
		timeZone = date.getTimeZone().getRawOffset() / 3600000;

		jDate = julian(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DAY_OF_MONTH)) - lng / (15 * 24);

		return computeNotifTimes();
	}

	//---------------------- Calculation Functions -----------------------

	// compute mid-day time
	private double midDay(double time) {
		double eqt = sunPosition(jDate + time).equation;
		double noon = DMath.fixHour(12 - eqt);
		return noon;
	}

	// compute the time at which sun reaches a specific angle below horizon
	private double sunAngleNotifTime(double angle, double time, boolean isCcw) {
		double decl = sunPosition(jDate + time).declination;
		double noon = midDay(time);
		double t = 1 / 15.0 * DMath.arccos((-DMath.sin(angle) - DMath.sin(decl)
				* DMath.sin(lat))
				/ (DMath.cos(decl) * DMath.cos(lat)));
		return noon + (isCcw ? -t : t);
	}

	// compute asr time
	private double asrNotifTime(double factor, double time) { // BENAR
		double decl = sunPosition(jDate + time).declination;
		double angle = -DMath.arccot(factor + DMath.tan(Math.abs(lat - decl)));
		return sunAngleNotifTime(angle, time, false);
	}

	// compute declination angle of sun and equation of time
	// Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
	private SunPosition sunPosition(double julianDate) {
		double d = julianDate - 2451545.0;

		double g = DMath.fixAngle(357.529 + 0.98560028 * d);
		double q = DMath.fixAngle(280.459 + 0.98564736 * d);
		double L = DMath.fixAngle(q + 1.915 * DMath.sin(g) + 0.020
				* DMath.sin(2 * g));

		double e = 23.439 - 0.00000036 * d;
		double RA = DMath.arctan2(DMath.cos(e) * DMath.sin(L), DMath.cos(L)) / 15;

		double decl = DMath.arcsin(DMath.sin(e) * DMath.sin(L)); // declination of the Sun
		double eqt = q / 15 - DMath.fixHour(RA); // equation of time

		return new SunPosition(decl, eqt);
	}

	private static class SunPosition {
		private final double declination;
		private final double equation;

		private SunPosition(double declination, double equation) {
			this.declination = declination;
			this.equation = equation;
		}
	}

	// convert Gregorian date to Julian day
	// Ref: Astronomical Algorithms by Jean Meeus
	private double julian(int year, int month, int day) {
		if (month <= 2) {
			year -= 1;
			month += 12;
		}

		double A = Math.floor(year / 100);
		double B = 2 - A + Math.floor(A / 4);

		return Math.floor(365.25 * (year + 4716))
				+ Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;
	}


	//---------------------- Compute Prayer NotifTimes -----------------------

	// compute prayer times at given julian date
	private Map<NotifTime, Double> computePrayerNotifTimes(Map<NotifTime, Double> times) {
		times = dayPortion(times);

		Map<NotifTime, Double> compResult = new HashMap<NotifTime, Double>();

		compResult.put(NotifTime.IMSAK, sunAngleNotifTime(method.getAngle(NotifTime.IMSAK), times.get(NotifTime.IMSAK), true));
		compResult.put(NotifTime.FAJR, sunAngleNotifTime(method.getAngle(NotifTime.FAJR), times.get(NotifTime.FAJR), true));
		compResult.put(NotifTime.SUNRISE, sunAngleNotifTime(riseSetAngle(), times.get(NotifTime.SUNRISE), true));
		compResult.put(NotifTime.DHUHR, midDay(times.get(NotifTime.DHUHR)));
		compResult.put(NotifTime.ASR, asrNotifTime(method.getAsrFactor(), times.get(NotifTime.ASR)));
		compResult.put(NotifTime.SUNSET, sunAngleNotifTime(riseSetAngle(), times.get(NotifTime.SUNSET), false));
		compResult.put(NotifTime.MAGHRIB, sunAngleNotifTime(method.getAngle(NotifTime.MAGHRIB), times.get(NotifTime.MAGHRIB), false));
		compResult.put(NotifTime.ISHA, sunAngleNotifTime(method.getAngle(NotifTime.ISHA), times.get(NotifTime.ISHA), false));

		return compResult;
	}

	// compute prayer times
	private Map<NotifTime, Double> computeNotifTimes() {
		// default times
		Map<NotifTime, Double> times = new HashMap<NotifTime, Double>();
		times.put(NotifTime.IMSAK, 5d);
		times.put(NotifTime.FAJR, 5d);
		times.put(NotifTime.SUNRISE, 6d);
		times.put(NotifTime.DHUHR, 12d);
		times.put(NotifTime.ASR, 13d);
		times.put(NotifTime.SUNSET, 18d);
		times.put(NotifTime.MAGHRIB, 18d);
		times.put(NotifTime.ISHA, 18d);

		// main iterations
		for (int i = 1; i <= numIterations; i++)
			times = computePrayerNotifTimes(times);

		adjustNotifTimes(times);

		// add midnight time
		times.put(
				NotifTime.MIDNIGHT,
				method.getMidnightMethod() == MidnightMethod.JAFARI ? times
						.get(NotifTime.SUNSET)
						+ timeDiff(times.get(NotifTime.SUNSET), times.get(NotifTime.FAJR))
						/ 2
						: times.get(NotifTime.SUNSET)
								+ timeDiff(times.get(NotifTime.SUNSET),
										times.get(NotifTime.SUNRISE)) / 2);

		return tuneNotifTimes(times);
	}

	// adjust times
	private void adjustNotifTimes(Map<NotifTime, Double> times) {

		for (Entry<NotifTime, Double> time : times.entrySet()) {
			times.put(time.getKey(), time.getValue() + timeZone - lng / 15);
		}

		if (method.getHighLatMethod() != HighLatMethod.NONE)
			adjustHighLats(times);

		if (method.getMinute(NotifTime.IMSAK) != null)
			times.put(NotifTime.IMSAK,
					times.get(NotifTime.FAJR) - method.getMinute(NotifTime.IMSAK) / 60d);

		if (method.getMinute(NotifTime.MAGHRIB) != null)
			times.put(NotifTime.MAGHRIB,
					times.get(NotifTime.SUNSET) + method.getMinute(NotifTime.MAGHRIB)
							/ 60d);

		if (method.getMinute(NotifTime.ISHA) != null)
			times.put(NotifTime.ISHA,
					times.get(NotifTime.MAGHRIB) + method.getMinute(NotifTime.ISHA) / 60d);

		if (method.getMinute(NotifTime.DHUHR) != null)
			times.put(NotifTime.DHUHR,
					times.get(NotifTime.DHUHR) + method.getMinute(NotifTime.DHUHR) / 60d);

		// TODO: If you look at the original Javascript version, the creator
		// uses eval to assume minutes as angle. I don't know what's his base.
	}

	private Map<NotifTime, Double> clone(Map<NotifTime, Double> times) {
		Map<NotifTime, Double> clone = new HashMap<NotifTime, Double>();
		clone.putAll(times);

		return clone;
	}

	// return sun angle for sunset/sunrise
	private double riseSetAngle() {
		// var earthRad = 6371009; // in meters
		// var angle = DMath.arccos(earthRad/(earthRad+ elv));
		double angle = 0.0347 * Math.sqrt(elv); // an approximation
		return 0.833 + angle;
	}

	// apply offsets to the times
	private Map<NotifTime, Double> tuneNotifTimes(Map<NotifTime, Double> times) {
		times = clone(times);

		for (Entry<NotifTime, Double> time : times.entrySet()) {
			times.put(time.getKey(),
					time.getValue() + offsets.get(time.getKey()) / 60d);
		}

		return times;
	}

	// adjust times for locations in higher latitudes
	private void adjustHighLats(Map<NotifTime, Double> times) {
		double nightNotifTime = timeDiff(times.get(NotifTime.SUNSET),
				times.get(NotifTime.SUNRISE));

		times.put(
				NotifTime.IMSAK,
				adjustHLNotifTime(times.get(NotifTime.IMSAK), times.get(NotifTime.SUNRISE),
						method.getAngle(NotifTime.IMSAK), nightNotifTime, true));
		times.put(
				NotifTime.FAJR,
				adjustHLNotifTime(times.get(NotifTime.FAJR), times.get(NotifTime.SUNRISE),
						method.getAngle(NotifTime.FAJR), nightNotifTime, true));
		times.put(
				NotifTime.ISHA,
				adjustHLNotifTime(times.get(NotifTime.ISHA), times.get(NotifTime.SUNSET),
						method.getAngle(NotifTime.ISHA), nightNotifTime, false));
		times.put(
				NotifTime.MAGHRIB,
				adjustHLNotifTime(times.get(NotifTime.MAGHRIB), times.get(NotifTime.SUNSET),
						method.getAngle(NotifTime.MAGHRIB), nightNotifTime, false));
	}

	// adjust a time for higher latitudes
	private double adjustHLNotifTime(Double time, double base, double angle,
			double night, boolean isCcw) {
		double portion = nightPortion(angle, night);
		double timeDiff = isCcw ? timeDiff(time, base) : timeDiff(base, time);
		if (time == null || timeDiff > portion)
			time = base + (isCcw ? -portion : portion);
		return time;
	}

	// the night portion used for adjusting times in higher latitudes
	private double nightPortion(double angle, double night) {
		HighLatMethod method = this.method.getHighLatMethod();
		double portion = 1 / 2d; // MidNight
		if (method == HighLatMethod.ANGLE_BASED)
			portion = 1 / 60d * angle;
		if (method == HighLatMethod.ONE_SEVENTH)
			portion = 1 / 7d;
		return portion * night;
	}

	// convert hours to day portions
	private Map<NotifTime, Double> dayPortion(Map<NotifTime, Double> times) {
		Map<NotifTime, Double> dayPortion = new HashMap<NotifTime, Double>();

		for (Entry<NotifTime, Double> time : times.entrySet()) {
			dayPortion.put(time.getKey(), time.getValue() / 24d);
		}

		return dayPortion;
	}

	// compute the difference between two times
	private double timeDiff(double time1, double time2) {
		return DMath.fixHour(time2- time1);
	}


	/* Test drive ----------------------------------------------------------- */

	public static void main(String[] args) {
		NotifTimes pt = new NotifTimes(MethodNotif.ISNA);
		pt.adjustAngle(NotifTime.FAJR, 20);
		pt.adjustMinutes(NotifTime.DHUHR, 2);
		pt.adjustMinutes(NotifTime.MAGHRIB, 1);
		pt.adjustAngle(NotifTime.ISHA, 18);

		pt.tuneOffset(NotifTime.FAJR, 2);
		pt.tuneOffset(NotifTime.SUNRISE, -2);
		pt.tuneOffset(NotifTime.ASR, 2);
		pt.tuneOffset(NotifTime.MAGHRIB, 2);
		pt.tuneOffset(NotifTime.ISHA, 2);

		today(pt);

		if (args.length > 0) {
			aYear(pt);
		}
	}

	private static void aYear(NotifTimes pt) {
		GregorianCalendar cal = new GregorianCalendar(2011, 0, 1);
		for (int i = 0; i < 365; i++) {
			/*System.out.print("@"
					+ cal.get(Calendar.DAY_OF_MONTH)
					+ "-"
					+ cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + " = ");*/

			Map<NotifTime, Double> times = pt.getNotifTimes(cal, new Location(-6.1744444,
					106.8294444, 10));
			for (NotifTime t : new NotifTime[] { NotifTime.FAJR, NotifTime.SUNRISE, NotifTime.DHUHR,
					NotifTime.ASR, NotifTime.MAGHRIB, NotifTime.ISHA, NotifTime.MIDNIGHT }) {
				System.out.print(t + " : "
						+ Util.toTime12(times.get(t), false));
				System.out.print(",");
			}
			System.out.println();

			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	private static void today(NotifTimes pt) {
		Map<NotifTime, Double> times = pt.getNotifTimes(new GregorianCalendar(),
				new Location(-6.1744444, 106.8294444, 10));

		for (NotifTime t : new NotifTime[] { NotifTime.FAJR, NotifTime.SUNRISE, NotifTime.DHUHR,
				NotifTime.ASR, NotifTime.MAGHRIB, NotifTime.ISHA, NotifTime.MIDNIGHT }) {
			System.out.println(t + " : "
					+ Util.toTime12(times.get(t), false));
		}
	}
}
