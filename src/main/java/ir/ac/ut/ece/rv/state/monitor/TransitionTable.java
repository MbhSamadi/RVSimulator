package ir.ac.ut.ece.rv.state.monitor;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransitionTable {

    private List<TransitionItem> items;

    private static final String CSV_GENERAL_PATH = "src/main/resources/input/table-%s.csv";

    public TransitionTable(String actorName) {
        String csvPath = String.format(CSV_GENERAL_PATH, actorName);
        List<String[]> rows = readCsv(csvPath);
        items = rows.stream().map(row ->
                new TransitionItem(
                        Transition.of(row[0]),
                        Transition.of(row[1]),
                        Arrays.stream(row[2].replaceAll("[\\[\\]]", "").split("\\)"))
                                .map(Transition::of)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()),
                        Boolean.parseBoolean(row[3])
                )
        ).collect(Collectors.toList());
    }

    private List<String[]> readCsv(String csvPath) {
        try {
            CSVReader csvReader = new CSVReader(new FileReader(csvPath), CSVParser.DEFAULT_SEPARATOR,
                    CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
            List<String[]> list = csvReader.readAll();
            csvReader.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<TransitionItem> findItemByMethodName(String methodName) {
        return items
                .stream()
                .filter(it -> it.isForMethod(methodName))
                .collect(Collectors.toList());
    }

    public List<TransitionItem> getItems() {
        return items;
    }
}

