package org.wolm.catalog.catalog;

import java.io.File;
import java.util.List;

import org.wolm.catalog.AccessLevel;
import org.wolm.catalog.PageRender;
import org.wolm.series.Series;
import org.wolm.series.SeriesPageRender;
import org.wolm.series.SeriesUrlRender;

public class SeriesIndexPageRender extends PageRender {
	private final List<Series> seriesList;
	private String indexTitle = null;

	public SeriesIndexPageRender(List<Series> seriesList) {
		super("series-index");
		this.seriesList = seriesList;
	}

	public String getIndexTitle() {
		return indexTitle;
	}

	public void setIndexTitle(String indexTitle) {
		this.indexTitle = indexTitle;
	}

	@Override
	public void render(File pageFile) throws Exception {
		System.out.println("Writing series index to file " + pageFile.getName() + "…");

		addDataToModel("title", indexTitle);
		addDataToModel("seriesList", seriesList);

		super.render(pageFile);

		// write out supporting files (i.e. all the series pages)
		File pageDirectory = pageFile.getParentFile();
		for (Series series : seriesList) {
			if (series.getVisibility() != AccessLevel.PUBLIC) {
				System.out.println("Skpping non-Public series " + series.getTitle());
				continue;
			}
			PageRender seriesRender = new SeriesPageRender(series);
			File seriesFile = new File(pageDirectory, new SeriesUrlRender(series).getFileName());
			seriesRender.render(seriesFile);
		}

	}

}
