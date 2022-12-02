import java.io.*;
import java.util.*;

public class LinksSuggester {
        private final List <Suggest> suggestList;

    public LinksSuggester(File file) throws IOException, WrongLinksFormatException {
 //       Map<String, String> map = new HashMap<>();
        List <Suggest> suggestList =new ArrayList<>();
        //извлекаем из файла file лист с рекомендациями
        BufferedReader reader= new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null) {
            //Проверка на то, что каждая строка состоит из трёх частей
            String[] split = line.split("\t");
            if (split.length != 3) {
                throw new WrongLinksFormatException("строка состоит не из трёх частей!");
            }
            //заполняем SuggestList
            suggestList.add(new Suggest(split[0], split[1], split[2]));
            line = reader.readLine();
        }
       this.suggestList = suggestList;
    }

    public List<Suggest> getSuggestList() {
        return suggestList;
    }

    //Метод suggest анализирует переданный текст и возвращает список всех подошедших рекомендаций
    public List<Suggest> suggest(String text) {
        return null;
    }

    @Override
    public String toString() {
        return "LinksSuggester{" +
                "suggestList=" + suggestList +
                '}';
    }
}
