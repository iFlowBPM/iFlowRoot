package pt.iflow.api.utils.series;

import java.util.EnumSet;


public enum SeriesFilter {
	ALL,
	ENABLED,
	DISABLED,
	NEW,
	USED,
	BURNED;

	public static EnumSet<SeriesFilter> getEnumSet() {
		return EnumSet.noneOf(SeriesFilter.class);
	}
}
