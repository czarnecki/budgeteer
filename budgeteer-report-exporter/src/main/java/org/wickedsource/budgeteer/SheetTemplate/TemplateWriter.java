package org.wickedsource.budgeteer.SheetTemplate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class TemplateWriter<T> {

    private static final String TEMPLATE_TAG_FORMAT = "\\{%s\\}";

    private final SheetTemplate<T> template;
    private final Sheet sheet;
    private final Collection<T> entries;
    private final Multimap<T, FieldFlag> flagMapping;

    private int currentRowIndex;
    private Row currentRow;

    public TemplateWriter(SheetTemplate<T> sheetTemplate, Collection<T> entries) {
        this.template = sheetTemplate;
        this.sheet = sheetTemplate.getSheet();
        this.entries = entries;
        this.flagMapping = ArrayListMultimap.create();
    }

    public void write() {
        insertRows(); // insert Rows with template tags
        if (null != entries && !entries.isEmpty()) {
            currentRowIndex = template.findTemplateRow();
            entries.forEach(this::insert);
        }
    }

    void insert(T dto) {
        currentRow = sheet.getRow(currentRowIndex);
        replaceTemplateTags(dto);
        setCellStyle(dto);
        currentRowIndex++;
    }

    private void replaceTemplateTags(T data) {
        var fieldMapping = template.getFieldPositionInRow();

        for (var entry : fieldMapping.entries()) {
            var currentCell = currentRow.getCell(entry.getValue());
            var tagName = entry.getKey();
            var fieldValue = isDynamicField(tagName) ? readDynamicAttributeValue(tagName, data) : template.getFieldMappers().get(tagName).apply(data);
            replaceTemplateTagByFieldValueIntoCell(tagName, fieldValue, currentCell);
        }
    }

    private Object readDynamicAttributeValue(String tagName, T data) {
        var collection = template.getCollectionMappers().get(tagName);
        var map = template.getMapMappers().get(tagName);
        if (collection != null && map != null) {
            throw new IllegalStateException();
        }
        if (collection != null) {
            return collection.apply(data).stream()
                    .filter(attribute -> attribute.getName().equals(subKeyOf(tagName)))
                    .findFirst()
                    .orElse(null);
        }
        if (map != null) {
            return map.apply(data).get(subKeyOf(tagName));
        }
        return null;
    }

    private void replaceTemplateTagByFieldValueIntoCell(String fieldName, Object fieldValue, Cell currentCell) {
        if (containsOnlyOneTemplateTag(currentCell)) {
            mapFieldValueToCell(fieldValue, currentCell);
        } else {
            replaceTemplateTagInCell(fieldName, fieldValue, currentCell);
        }
    }

    private void replaceTemplateTagInCell(String fieldname, Object fieldValue, Cell currentCell) {
        String fieldValueString = (fieldValue != null) ? fieldValue.toString() : "";
        if (currentCell.getCellTypeEnum().equals(CellType.FORMULA)) {
            var formula = currentCell.getCellFormula();
            var templateTag = String.format(TEMPLATE_TAG_FORMAT, fieldname);
            var newFormula = formula.replaceAll(templateTag, fieldValueString);
            currentCell.setCellFormula(newFormula);
        } else {
            var cellValue = currentCell.getStringCellValue();
            var templateTag = String.format(TEMPLATE_TAG_FORMAT, fieldname);
            var newCellValue = cellValue.replaceAll(templateTag, fieldValueString);
            currentCell.setCellValue(newCellValue);
        }

    }

    private void mapFieldValueToCell(Object fieldValue, Cell cell) {
        if (fieldValue == null) {
            cell.setCellType(CellType.BLANK);
            return;
        }
        if (fieldValue instanceof Double) {
            cell.setCellValue((Double) fieldValue);
        } else if (fieldValue instanceof String) {
            cell.setCellValue((String) fieldValue);
        } else if (fieldValue instanceof Boolean) {
            cell.setCellValue((Boolean) fieldValue);
        } else if (fieldValue instanceof Date) {
            cell.setCellValue((Date) fieldValue);
        } else if (fieldValue instanceof RichTextString) {
            cell.setCellValue((RichTextString) fieldValue);
        } else if (fieldValue instanceof Calendar) {
            cell.setCellValue((Calendar) fieldValue);
        } else if (fieldValue instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) fieldValue).doubleValue());
        } else if (fieldValue instanceof LocalDate) {
            cell.setCellValue(Date.from(((LocalDate) fieldValue).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            throw new IllegalArgumentException();
        }
    }

    boolean containsOnlyOneTemplateTag(Cell currentCell) {
        if (currentCell.getCellTypeEnum() != CellType.STRING) {
            return false;
        }
        var matcher = SheetTemplate.TEMPLATE_TAG_PATTERN.matcher(currentCell.getStringCellValue());
        return matcher.matches();
    }

    boolean isDynamicField(String fieldName) {
        return template.getMapMappers().containsKey(fieldName) || template.getCollectionMappers().containsKey(fieldName);
    }

    String subKeyOf(String tagName) {
        int dotIndex = tagName.indexOf(".");
        if (dotIndex == -1) {
            return null;
        }
        return tagName.substring(dotIndex + 1);
    }

    void insertRows() {
        // determine number of rows to be inserted
        int numberOfRows = entries != null ? entries.size() : 0;

        boolean templateRowIsLastRow = (template.findTemplateRow() == sheet.getLastRowNum());

        if (!templateRowIsLastRow && numberOfRows == 0) { // if we do not have data, we have to remove the template row
            sheet.removeRow(sheet.getRow(template.findTemplateRow()));
            sheet.shiftRows(template.findTemplateRow(), sheet.getLastRowNum(), -1);
        }

        // copy template row numberOfRows-1 times
        for (var i = 0; i < numberOfRows - 1; i++) {
            copyRow(sheet, template.findTemplateRow() + i);
        }
    }

    void copyRow(Sheet sheet, int from) {
        if (from < sheet.getLastRowNum()) {
            sheet.shiftRows(from + 1, sheet.getLastRowNum(), 1);
        }
        var copyRow = sheet.getRow(from);
        var insertRow = sheet.createRow(from + 1);
        for (Cell copyCell : copyRow) {
            var insertCell = insertRow.createCell(copyCell.getColumnIndex());
            copyCellValues(copyCell, insertCell);
            insertCell.setCellStyle(copyCell.getCellStyle());
        }
    }

    void copyCellValues(Cell copyCell, Cell insertCell) {
        switch (copyCell.getCellTypeEnum()) {
            case STRING:
                insertCell.setCellValue(copyCell.getStringCellValue());
                break;
            case BOOLEAN:
                insertCell.setCellValue(copyCell.getBooleanCellValue());
                break;
            case BLANK:
                insertCell.setCellType(CellType.BLANK);
                break;
            case FORMULA:
                insertCell.setCellFormula(copyCell.getCellFormula());
                break;
            case NUMERIC:
                insertCell.setCellValue(copyCell.getNumericCellValue());
                break;
            default:
                throw new IllegalArgumentException("Unknown Type"); // should not occur
        }
    }

    void setCellStyle(T dto) {
        if (!flagMapping.containsKey(dto)) {
            return;
        }
        for (FieldFlag flag : flagMapping.get(dto)) {
            String fieldName = flag.getField();
            String flagName = flag.getFlag();
            for (var columnIndex : template.getFieldPositionInRow().get(fieldName)) {
                var cell = currentRow.getCell(columnIndex);
                cell.setCellStyle(sheet.getWorkbook().createCellStyle());
                cell.getCellStyle().cloneStyleFrom(template.getFlagTemplate().getCellStyleFor(flagName));
            }
        }
    }

    public void addFlag(T dto, String fieldname, String flag) {
        if (template.getFieldPositionInRow().containsKey(fieldname) && template.getFlagTemplate() != null && template.getFlagTemplate().contains(flag)) {
            flagMapping.put(dto, new FieldFlag(fieldname, flag));
        }
    }

    public void removeFlagSheet() {
        var flagSheet = sheet.getWorkbook().getSheet("Flags");
        if (null != flagSheet) {
            int sheetIndex = sheet.getWorkbook().getSheetIndex(flagSheet);
            sheet.getWorkbook().removeSheetAt(sheetIndex);
        }
    }
}
