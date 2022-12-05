import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

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


			// перебираем страницы pdf
			boolean createNewPage = false;   //маркер создания новой страницы

			for (int i = 1; i <= doc.getNumberOfPages(); i++) {
				//список рекомендаций для вставки
				List<Suggest> newListSuggest = new ArrayList<>();

				if (createNewPage && i < doc.getNumberOfPages()) {
					i++;                      //если была создана новая страница, переходим на следующую страницу
					createNewPage = false;
				}
				var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
				List<Suggest> suggestListPage = linksSuggester.suggest(text);
				System.out.println("str " + i + ", " + suggestListPage);

				if (!suggestListPage.isEmpty() && !createNewPage) {
					for (Suggest suggest : suggestListPage) {
						for (int j = 0; j < suggestList.size(); j++) {
							if (suggest.equals(suggestList.get(j)) && !boolSuggest[j]) {
								boolSuggest[j] = true;        //маркер, что такая рекомендация теперь уже есть
								if (!createNewPage) {
									var newPage = doc.addNewPage(i + 1);
									System.out.println("NEW PAGE!!!!!!!");
									createNewPage = true;
								}
								newListSuggest.add(suggest);
							}
						}
					}
				}
				// вставляем туда рекомендуемые ссылки из конфига
				System.out.println("newListSuggest :" + newListSuggest);
			}
			doc.close();
		}
	}
}
