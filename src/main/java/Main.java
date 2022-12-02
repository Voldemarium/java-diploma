import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // создаём конфиг
        LinksSuggester linksSuggester = new LinksSuggester(new File("data/config"));
        List<Suggest> suggestList = linksSuggester.getSuggestList();
        System.out.println(suggestList);


        // перебираем пдфки в data/pdfs

        // для каждой пдфки создаём новую в data/converted

        // перебираем страницы pdf

        // если в странице есть неиспользованные ключевые слова, создаём новую страницу за ней

        // вставляем туда рекомендуемые ссылки из конфига
    }
}
