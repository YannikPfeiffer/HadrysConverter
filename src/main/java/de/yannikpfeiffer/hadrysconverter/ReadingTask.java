package de.yannikpfeiffer.hadrysconverter;

import javafx.concurrent.Task;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadingTask extends Task<ArrayList<String>> {
	private String sourcePath;

	public ReadingTask(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	@Override
	protected ArrayList<String> call() throws Exception {
		updateTitle("Lese Dokument");
		ArrayList<String> finalTextArray = new ArrayList<>();

		File myFile = new File(sourcePath);

		PDDocument doc = PDDocument.load(myFile);
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(doc);

		doc.close();

		System.out.println("Text size: " + text.length());

		BufferedReader buffReader = new BufferedReader(new StringReader(text));
		String line = null;
		ArrayList<String> textColumns = new ArrayList<>();

		//final int flags = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE

		String regexp = "^(Übung|Seite|Aufgaben) (.*)$";
		String regexp2 = "^[0-9]+\\.(.*)$";

		//remove tags: Seite|Übung|Aufgaben
		Pattern p = Pattern.compile(regexp);
		Matcher m;
		while ((line = buffReader.readLine()) != null) {
			m = p.matcher(line);
			if (!m.matches()) {
				textColumns.add(line);
			}
		}

		Pattern p2 = Pattern.compile(regexp2);

		//remove unnecessary breaks
		for (String line2 : textColumns) {
			System.out.println(line2);
			if (p2.matcher(line2).matches()) {
				finalTextArray.add(line2);
			} else {
				int pos = finalTextArray.size() - 1;

				if (pos > 0) {
					String getString = finalTextArray.get(pos);
					String newString = getString.concat(line2);

					finalTextArray.set(pos, newString);
				}
			}
		}

		//remove numbering
		for (int i = 0; i < finalTextArray.size(); i++) {

			String line3 = finalTextArray.get(i).replaceAll("[0-9]+\\. ", "");

			finalTextArray.set(i, line3);
			this.updateProgress(i + 1, finalTextArray.size());
		}
		System.out.println("ArrayList generated.");
		return finalTextArray;
	}
}
