package app.rasendriya.cintamasjid.prays;

import app.rasendriya.cintamasjid.prays.NotifTimes.NotifTime;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculation method for notification
 * edited by <a href="mailto:rohimatdeni@gmail.com">Deni Rohimat</a>
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class MethodNotif {

	/**
	 * Muslim World League
	 */
	public static final MethodNotif MWL;

	/**
	 * Islamic Society of North America (ISNA)
	 */
	public static final MethodNotif ISNA;

	/**
	 * Egyptian General Authority of Survey
	 */
	public static final MethodNotif EGYPT;

	/**
	 * Umm Al-Qura University, Makkah
	 */
	public static final MethodNotif MAKKAH;

	/**
	 * University of Islamic Sciences, Karachi
	 */
	public static final MethodNotif KARACHI;

	/**
	 * Institute of Geophysics, University of Tehran
	 */
	public static final MethodNotif TEHRAN;

	/**
	 * Shia Ithna-Ashari, Leva Institute, Qum
	 */
	public static final MethodNotif JAFARI;

	static {
		MWL = new MethodNotif("Muslim World League");
		MWL.setAngle(NotifTime.FAJR, 18);
		MWL.setAngle(NotifTime.ISHA, 17);

		ISNA = new MethodNotif("Islamic Society of North America (ISNA)");
		ISNA.setAngle(NotifTime.FAJR, 15);
		ISNA.setAngle(NotifTime.ISHA, 15);

		EGYPT = new MethodNotif("Egyptian General Authority of Survey");
		EGYPT.setAngle(NotifTime.FAJR, 19.5);
		EGYPT.setAngle(NotifTime.ISHA, 17.5);

		MAKKAH = new MethodNotif("Umm Al-Qura University, Makkah");
		MAKKAH.setAngle(NotifTime.FAJR, 18.5);
		MAKKAH.setMinutes(NotifTime.ISHA, 90);

		KARACHI = new MethodNotif("University of Islamic Sciences, Karachi");
		KARACHI.setAngle(NotifTime.FAJR, 18);
		KARACHI.setAngle(NotifTime.ISHA, 18);

		TEHRAN = new MethodNotif("Institute of Geophysics, University of Tehran");
		TEHRAN.setAngle(NotifTime.FAJR, 17.7);
		TEHRAN.setAngle(NotifTime.ISHA, 14);
		TEHRAN.setAngle(NotifTime.MAGHRIB, 4.5);
		TEHRAN.setMidnightMethod(MidnightMethod.JAFARI);

		JAFARI = new MethodNotif("Shia Ithna-Ashari, Leva Institute, Qum");
		JAFARI.setAngle(NotifTime.FAJR, 16);
		JAFARI.setAngle(NotifTime.ISHA, 14);
		JAFARI.setAngle(NotifTime.MAGHRIB, 4);
		JAFARI.setMidnightMethod(MidnightMethod.JAFARI);
	}

	public static final int ASR_FACTOR_STANDARD = 1;
	public static final int ASR_FACTOR_HANAFI = 2;

	/**
	 * Midnight methods
	 *
	 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
	 *
	 */
	public enum MidnightMethod {
		/**
		 * The mean NotifTime from Sunset to Sunrise
		 */
		STANDARD,

		/**
		 * The mean NotifTime from Maghrib to Fajr
		 */
		JAFARI
	};

	/**
	 * Higher latitudes methods
	 *
	 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
	 *
	 */
	public enum HighLatMethod {
		/**
		 * The middle of the night method
		 */
		NIGHT_MIDDLE,

		/**
		 * The angle-based method (recommended)
		 */
		ANGLE_BASED,

		/**
		 * The 1/7th of the night method
		 */
		ONE_SEVENTH,

		/**
		 * No adjustments
		 */
		NONE
	};

	private final String name;

	private final Map<NotifTime, Double> angles;
	private final Map<NotifTime, Integer> minutes; // added into final angles
	private double asrFactor;
	private MidnightMethod midnightMethod;
	private HighLatMethod highLatMethod;

	public MethodNotif(String name) {
		this.name = name;
		angles = new HashMap<NotifTimes.NotifTime, Double>();
		minutes = new HashMap<NotifTimes.NotifTime, Integer>();

		// Base values for all methods
		setMinutes(NotifTime.MAGHRIB, 0);
		setMidnightMethod(MidnightMethod.STANDARD);
	}

	public String getName() {
		return name;
	}

	public double getAsrFactor() {
		return asrFactor;
	}

	/**
	 * Set Asr factor for shadow.
	 *
	 * @param factor
	 *            The factor could be {@link #ASR_FACTOR_STANDARD}, {@link #ASR_FACTOR_HANAFI}.
	 */
	public void setAsrFactor(double factor) {
		this.asrFactor = factor;
	}

	public MidnightMethod getMidnightMethod() {
		return midnightMethod;
	}

	public void setMidnightMethod(MidnightMethod jafari2) {
		this.midnightMethod = jafari2;
	}

	public HighLatMethod getHighLatMethod() {
		return highLatMethod;
	}

	public void setHighLatMethod(HighLatMethod highLatMethod) {
		this.highLatMethod = highLatMethod;
	}

	public void copyFrom(MethodNotif method) {
		angles.putAll(method.angles);

		if (method.midnightMethod != null) {
			setMidnightMethod(method.midnightMethod);
		}
	}

	/**
	 * Get twilight angle of specific NotifTime
	 *
	 * @param NotifTime
	 *            NotifTime to adjust the angle
	 * @return Angle in degree
	 */
	public Double getAngle(NotifTime NotifTime) {
		return angles.get(NotifTime);
	}

	/**
	 * Set twilight angle of specific NotifTime
	 *
	 * @param NotifTime
	 *            NotifTime to adjust the angle
	 * @param angle
	 *            angle in degree
	 */
	@SuppressWarnings("static-access")
	public void setAngle(NotifTime NotifTime, double angle) {
		if (NotifTime != NotifTime.IMSAK && NotifTime != NotifTime.FAJR && NotifTime != NotifTime.MAGHRIB
				&& NotifTime != NotifTime.ISHA) {
			throw new IllegalArgumentException("Can not set angle for " + NotifTime);
		}

		angles.put(NotifTime, angle);
	}

	/**
	 * Get minute difference.
	 *
	 * @param NotifTime
	 *            NotifTime to adjust the minutes.
	 * @return Minute difference
	 */
	public Integer getMinute(NotifTime NotifTime) {
		return minutes.get(NotifTime);
	}

	@SuppressWarnings("static-access")
	public void setMinutes(NotifTime NotifTime, int minutes) {
		if (NotifTime != NotifTime.IMSAK && NotifTime != NotifTime.DHUHR && NotifTime != NotifTime.MAGHRIB
				&& NotifTime != NotifTime.ISHA) {
			throw new IllegalArgumentException("Can not set minutes for "
					+ NotifTime);
		}

		this.minutes.put(NotifTime, minutes);
		// setAngle(NotifTime, minute / 60d);
	}

	@Override
	public MethodNotif clone() {
		MethodNotif m = new MethodNotif(name);
		m.angles.clear();
		m.angles.putAll(angles);

		m.asrFactor = asrFactor;
		m.midnightMethod = midnightMethod;
		m.highLatMethod = highLatMethod;
		return m;
	}
}
