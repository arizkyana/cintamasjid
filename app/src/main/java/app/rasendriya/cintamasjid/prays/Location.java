package app.rasendriya.cintamasjid.prays;

/**
 * To define location
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class Location {
	private final double lat;
	private final double lng;
	private final double elv;

	public Location(double latitude, double longitude, double elevation) {
		lat = latitude;
		lng = longitude;
		elv = elevation;
	}

	public Location(double lat, double lng) {
		this(lat, lng, 0);
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public double getElv() {
		return elv;
	}
}
