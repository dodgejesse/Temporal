package edu.uw.cs.utils.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PageContentGetter {
	public static String getPageContent(URL url) throws IOException {
		HttpURLConnection uc;
		uc = (HttpURLConnection) url.openConnection();
		final int responseCode = uc.getResponseCode();
		if (responseCode == 200) {
			final BufferedReader rd = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			final StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			return sb.toString();
		} else {
			throw new IOException("Got response code " + responseCode + " for "
					+ url.toString());
		}
	}
}
