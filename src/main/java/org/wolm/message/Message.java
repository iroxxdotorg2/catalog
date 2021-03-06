package org.wolm.message;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.wolm.catalog.AccessLevel;

/**
 * Stores one message from the message log
 * 
 * @author wolm
 */
public class Message {

	private Date date;
	private String title;
	private List<String> series;
	private List<Integer> trackNumbers;
	private String description;
	private String type;
	private AccessLevel visibility;
	private List<String> speakers;
	private URL audioLink;
	private URL videoLink;

	private transient String audioLinkError;
	private transient String videoLinkError;
	private transient String visibilityError;

	private static final List<String> TYPES = Arrays.asList(new String[] { "C.O.R.E.", "Message", "Prayer", "Q&A",
			"Song", "Special Event", "Testimony", "Training", "Word" });
	private static final List<String> SPECIAL_LINKS = Arrays.asList(new String[] { "-", "n/a", "n/e", "abrogated",
			"in progress", "editing", "rendering", "rendered", "flash", "uploading" });

	public Message() {
		super();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getSeries() {
		return series;
	}

	public void setSeries(List<String> series) {
		this.series = series;
	}

	public List<Integer> getTrackNumbers() {
		return trackNumbers;
	}

	/**
	 * @param seriesName Name of a series that this message is in
	 * @return Track number of this message in that series. <code>null</code> if not in the specified series
	 */
	public Integer getTrackNumber(String seriesName) {
		if (series == null) return null;
		if (trackNumbers == null) return null;

		for (int index = 0; index < trackNumbers.size(); index++)
			if (series.get(index).equalsIgnoreCase(seriesName)) return trackNumbers.get(index);
		return null;
	}

	public void setTrackNumbers(List<Integer> trackNumbers) {
		this.trackNumbers = trackNumbers;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AccessLevel getVisibility() {
		return visibility;
	}

	public void setVisibility(AccessLevel visibility) {
		this.visibility = visibility;
	}

	public void setVisibilityAsString(String visibility) {
		if (visibility == null) {
			this.visibility = null;
			return;
		}

		switch (visibility) {
		case "Public":
		case "public":
			this.visibility = AccessLevel.PUBLIC;
			break;
		case "Protected":
		case "protected":
			this.visibility = AccessLevel.PROTECTED;
			break;
		case "Private":
		case "private":
			this.visibility = AccessLevel.PRIVATE;
			break;
		case "Private (Raw)":
		case "private (raw)":
			this.visibility = AccessLevel.RAW;
			break;
		default:
			this.visibility = null;
			visibilityError = "unknown visibility '" + visibility + "'";
			break;
		}
	}

	public List<String> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<String> speakers) {
		this.speakers = speakers;
	}

	public URL getAudioLink() {
		return audioLink;
	}

	public void setAudioLink(URL audioLink) {
		this.audioLink = audioLink;
	}

	public void setAudioLinkAsString(String link) {
		if (link == null || SPECIAL_LINKS.contains(link)) return;
		try {
			audioLink = new URL(link);
		}
		catch (MalformedURLException e) {
			audioLinkError = "unable to parse audio URL '" + link + "': " + e.getMessage();
		}
	}

	public URL getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(URL videoLink) {
		this.videoLink = videoLink;
	};

	public void setVideoLinkAsString(String link) {
		if (link == null || SPECIAL_LINKS.contains(link)) return;
		try {
			videoLink = new URL(link);
		}
		catch (MalformedURLException e) {
			videoLinkError = "unable to parse video URL '" + link + "': " + e.getMessage();
		}
	}

	public boolean isValid(PrintStream s) {
		boolean valid = true;
		boolean needsHeader = true;

		if (getTitle() == null) {
			printValidationError(s, needsHeader, "has no title");
			valid = needsHeader = false;
		}

		if (getDate() == null) {
			printValidationError(s, needsHeader, "has no date");
			valid = needsHeader = false;
		}

		int seriesCount = getSeries() == null ? 0 : getSeries().size();
		int trackCount = getTrackNumbers() == null ? 0 : getTrackNumbers().size();
		if (seriesCount != trackCount) {
			printValidationError(s, needsHeader, "is in " + seriesCount + " series, but has track data for "
					+ trackCount + " series");
			valid = needsHeader = false;
		}

		if (getType() != null && !TYPES.contains(getType())) {
			printValidationError(s, needsHeader, "has an unknown type '" + getType() + "'");
			// this is not a validation problem, just a warning that there might be a typo
			needsHeader = false;
		}

		if (audioLinkError != null) {
			printValidationError(s, needsHeader, audioLinkError);
			valid = needsHeader = false;
		}

		if (videoLinkError != null) {
			printValidationError(s, needsHeader, videoLinkError);
			valid = needsHeader = false;
		}

		if (visibilityError != null) {
			printValidationError(s, needsHeader, visibilityError);
			valid = needsHeader = false;
		}

		return valid;
	}

	private void printValidationError(PrintStream s, boolean needsHeader, String error) {
		if (s == null) return;

		if (needsHeader) s.println("Message '" + getTitle() + "' has the following problems:");
		s.println("    * " + error);
	}

	@Override
	public String toString() {
		DateFormat fmt = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
		return getTitle() + " (" + fmt.format(getDate()) + ")";
	}

	/** Comparator that sorts Message by date, oldest to newest (<code>null</code> at end) */
	public static Comparator<Message> byDate = new Comparator<Message>() {
		public int compare(Message Message1, Message Message2) {
			Date date1 = Message1.getDate();
			Date date2 = Message2.getDate();
			if (date1 == null && date2 == null) return 0;
			if (date1 == null) return 1;
			if (date2 == null) return -1;
			return date1.before(date2) ? -1 : (date1.equals(date2) ? 0 : 1);
		}
	};

	/** Comparator that sorts Message by date, newest to oldest (<code>null</code> at end) */
	public static Comparator<Message> byDateDescending = new Comparator<Message>() {
		public int compare(Message Message1, Message Message2) {
			Date date1 = Message1.getDate();
			Date date2 = Message2.getDate();
			if (date1 == null && date2 == null) return 0;
			if (date1 == null) return 1;
			if (date2 == null) return -1;
			return date1.before(date2) ? 1 : (date1.equals(date2) ? 0 : -1);
		}
	};

	/** Compares two messages by their track number for a given series */
	public static class ByTrackNumber implements Comparator<Message> {
		private final String seriesName;

		public ByTrackNumber(String seriesName) {
			super();
			this.seriesName = seriesName;
		}

		public int compare(Message msg1, Message msg2) {
			Integer trackNumber1 = msg1.getTrackNumber(seriesName);
			Integer trackNumber2 = msg2.getTrackNumber(seriesName);

			if (trackNumber1 == null && trackNumber2 == null) return 0;
			if (trackNumber1 == null) return 1;
			if (trackNumber2 == null) return -1;
			return trackNumber1 - trackNumber2;
		}

	}
}
