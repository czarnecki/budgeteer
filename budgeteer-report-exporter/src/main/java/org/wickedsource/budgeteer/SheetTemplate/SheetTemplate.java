package org.wickedsource.budgeteer.SheetTemplate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class SheetTemplate<T> {

    public static final Pattern TEMPLATE_TAG_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_\\-]+)(?:\\.(?<attribute>[a-zA-Z0-9\\-_.]+))?}");

    private final Sheet sheet;
    private final Multimap<String, Integer> fieldPositionInRow;
    private final Map<String, Function<T, Object>> fieldMappers;
    private final Map<String, Function<T, Collection<SheetTemplateSerializable>>> collectionMappers;
    private final Map<String, Function<T, Map<String, String>>> mapMappers;
    private final FlagTemplate flagTemplate;

    public static <T> SheetTemplate<T> of(Map<String, Function<T, Object>> fieldMappers, Sheet sheet) {
        return new SheetTemplate<>(fieldMappers, Collections.emptyMap(), Collections.emptyMap(), sheet);
    }

    public static <T> SheetTemplate<T> withCollection(Map<String, Function<T, Object>> fieldMappers, Map<String, Function<T, Collection<SheetTemplateSerializable>>> collectionMappers, Sheet sheet) {
        return new SheetTemplate<>(fieldMappers, collectionMappers, Collections.emptyMap(), sheet);
    }

    public static <T> SheetTemplate<T> withMap(Map<String, Function<T, Object>> fieldMappers, Map<String, Function<T, Map<String, String>>> mapMappers, Sheet sheet) {
        return new SheetTemplate<>(fieldMappers, Collections.emptyMap(), mapMappers, sheet);
    }

    private SheetTemplate(Map<String, Function<T, Object>> fieldsMappers,
                          Map<String, Function<T, Collection<SheetTemplateSerializable>>> collectionMappers,
                          Map<String, Function<T, Map<String, String>>> mapMappers,
                          Sheet sheet) {
        this.sheet = sheet;
        this.fieldMappers = fieldsMappers;
        this.collectionMappers = collectionMappers;
        this.mapMappers = mapMappers;
        this.fieldPositionInRow = createFieldMapping();
        this.flagTemplate = checkForFlagTemplate();
    }

    private FlagTemplate checkForFlagTemplate() {
        var flagSheet = sheet.getWorkbook().getSheet("Flags");
        return flagSheet != null ? new FlagTemplate(flagSheet) : null;
    }

    private Multimap<String, Integer> createFieldMapping() {
        var fieldMappings = ArrayListMultimap.<String, Integer>create();
        for (Cell cell : sheet.getRow(findTemplateRow())) {
            List<String> fields = mapCellValueToFieldNames(cell);
            if (!fields.isEmpty()) {
                fields.forEach(name -> fieldMappings.put(name, cell.getColumnIndex()));
            }
        }
        return fieldMappings;
    }

    public int findTemplateRow() {
        for (Row currentRow : sheet) {
            if (rowContainsTemplate(currentRow)) {
                return currentRow.getRowNum();
            }
        }
        return 0;
    }

    private boolean rowContainsTemplate(Row currentRow) {
        for (Cell cell : currentRow) {
            if (cellContainsTemplateTag(cell)) {
                return true;
            }
        }
        return false;
    }

    private String getCellValue(Cell cell) {
        String cellValue = null;
        if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {
            cellValue = cell.getCellFormula();
        } else if (cell.getCellTypeEnum().equals(CellType.STRING)) {
            cellValue = cell.getStringCellValue();
        }
        return cellValue;
    }


    private List<String> mapCellValueToFieldNames(Cell cell) {
        String cellValue = getCellValue(cell);
        if (cellValue == null) {
            return Collections.emptyList();
        }
        return getFieldFromCellValue(cellValue);
    }

    private List<String> getFieldFromCellValue(String cellValue) {
        var matcher = TEMPLATE_TAG_PATTERN.matcher(cellValue);
        var fields = new ArrayList<String>(matcher.groupCount());
        while (matcher.find()) {
            String field = matcher.group(1);
            if (hasMapping(field)) {
                fields.add(field);
            }
        }
        return fields;
    }

    boolean hasMapping(String group) {
        if (group.charAt(0) == '.') {
            return false;
        }
        String[] tokens = group.split("\\.");
        return fieldMappers.containsKey(tokens[0]) || collectionMappers.containsKey(tokens[0]) || mapMappers.containsKey(tokens[0]);
    }

    boolean cellContainsTemplateTag(Cell cell) {
        return !mapCellValueToFieldNames(cell).isEmpty();
    }

    public Multimap<String, Integer> getFieldPositionInRow() {
        return fieldPositionInRow;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public Map<String, Function<T, Object>> getFieldMappers() {
        return fieldMappers;
    }

    public Map<String, Function<T, Collection<SheetTemplateSerializable>>> getCollectionMappers() {
        return collectionMappers;
    }

    public Map<String, Function<T, Map<String, String>>> getMapMappers() {
        return mapMappers;
    }

    FlagTemplate getFlagTemplate() {
        return flagTemplate;
    }

}
