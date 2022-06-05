import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> wordList = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // здесь надо создать список файлов в папке pdfs

        if (!pdfsDir.isDirectory()) { // проверим, является ли объект каталогом
            return;
        }
        var file = pdfsDir.listFiles(); // получаем все вложенные объекты в каталоге
        if (file == null) {
            System.out.println("Нет файлов");
        }

        for (File pdf : file) { // здесь необходимо перебрать все файлы в папке pdfs
            var doc = new PdfDocument(new PdfReader(pdf)); // создать объект пдф-документа
            int number = doc.getNumberOfPages(); // количество страниц в документе

            for (int i = 1; i <= number; i++) {
                PdfPage page = doc.getPage(i);// получить объект одной страницы документа
                var text = PdfTextExtractor.getTextFromPage(page); // получить текст со страницы
                var words = text.split("\\P{IsAlphabetic}+"); // разбить текст на слова (а они в этих документах разделены могут быть не только пробелами)

                Map<String, Integer> freqs = new HashMap<>(); // ведем подсчет частоты слов
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word.toLowerCase(), 0) + 1);
                }

                for (Map.Entry<String, Integer> a : freqs.entrySet()) {
                    List<PageEntry> pageEntryList = new ArrayList<>();
                    if (wordList.containsKey(a.getKey())) { // вернет true, если такой ключ (a.getKey() - ключ от "a") используется в map
                        pageEntryList = wordList.get(a.getKey()); // значение по ключу

                    }

                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, a.getValue());
                    pageEntryList.add(pageEntry);
                    Collections.sort(pageEntryList);
                    wordList.put(a.getKey(), pageEntryList);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        String wordFind = word.toLowerCase();
        if (wordList.containsKey(wordFind)) {
            return wordList.get(wordFind);
        }
        return Collections.emptyList();
    }
}

