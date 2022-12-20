import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
	public static void main(String[] args) throws Exception {
		// создаём конфиг
		LinksSuggester linksSuggester = new LinksSuggester(new File("data/config"));
		List<Suggest> suggestList = linksSuggester.getSuggestList();

		// перебираем пдфки в data/pdfs
		var dir = new File("data/pdfs");

		for (var fileIn : Objects.requireNonNull(dir.listFiles())) {
			// для каждой пдфки создаём новую в data/converted
			String newName = fileIn.getName().replace(".pdf", " ");
			var fileOut = new File("data/converted/" + newName + " (ред).pdf");
			var doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter(fileOut));

			//создадим массив boolean[] для хранения в нем информации, встречалось ли уже ключевое слово в тексте pdf
			boolean[] boolSuggest = new boolean[suggestList.size()];
			//маркер создания новой страницы
			boolean createNewPage = false;

			// перебираем страницы pdf
			for (int i = 1; i <= doc.getNumberOfPages(); i++) {
				//создаем новую страницу
				PdfPage newPage = null;

				//если на предыдущей итерации уже была создана новая страница, переходим на следующую страницу
				if (createNewPage && i < doc.getNumberOfPages()) {
					i++;
					createNewPage = false;
				}
				var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));

				//Создадим начальный список рекомендаций для вставки в новую страницу
				List<Suggest> startSuggestList = linksSuggester.suggest(text);

				//Создадим финальный список рекомендаций для вставки в новую страницу
				List<Suggest> finalSuggestList = new ArrayList<>();

				if (!startSuggestList.isEmpty()) {
					for (Suggest suggest : startSuggestList) {
						for (int j = 0; j < suggestList.size(); j++) {
							if (suggest.equals(suggestList.get(j)) && !boolSuggest[j]) {
								boolSuggest[j] = true;        //ставим маркер, что такая рекомендация теперь уже есть
								if (!createNewPage) {
									newPage = doc.addNewPage(i + 1);
									createNewPage = true;
								}
								finalSuggestList.add(suggest);
							}
						}
					}
				}
				// вставляем в новую страницу рекомендуемые ссылки из финального списка рекомендаций
				if (newPage != null) {
					var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
					try (Canvas canvas = new Canvas(newPage, rect)) {
						Paragraph paragraph = new Paragraph("Suggestions:\n");
						paragraph.setFontSize(25);
						for (Suggest suggest : finalSuggestList) {
							paragraph.add(suggest.getTitle() + "\n");
							PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
							PdfAction action = PdfAction.createURI(suggest.getUrl());
							annotation.setAction(action);
							Link link = new Link(suggest.getUrl(), annotation);
							paragraph.add(link.setUnderline());
							paragraph.add("\n");
						}
						canvas.add(paragraph);
					}
				}
			}
			doc.close();
		}
	}
}
