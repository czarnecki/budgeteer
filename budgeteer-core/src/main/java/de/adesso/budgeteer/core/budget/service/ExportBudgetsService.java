package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.domain.BudgetSummary;
import de.adesso.budgeteer.core.budget.port.in.ExportBudgetsUseCase;
import de.adesso.budgeteer.core.budget.port.out.GetBudgetSummariesForProjectPort;
import de.adesso.budgeteer.core.common.DateRange;
import de.adesso.budgeteer.core.template.port.out.GetTemplateByIdPort;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.wickedsource.budgeteer.SheetTemplate.SheetTemplate;
import org.wickedsource.budgeteer.SheetTemplate.TemplateWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class ExportBudgetsService implements ExportBudgetsUseCase {

    private final GetBudgetSummariesForProjectPort getBudgetSummariesForProjectPort;
    private final GetTemplateByIdPort getTemplateByIdPort;

    @Override
    public File exportContract(long projectId, long templateId, DateRange overall, DateRange month) {
        var overallBudgetReportList = getBudgetSummariesForProjectPort.getBudgetSummariesForProject(projectId, overall);
        var monthlyBudgetReportList = getBudgetSummariesForProjectPort.getBudgetSummariesForProject(projectId, month);

        var workbook = getTemplateByIdPort.getTemplateById(templateId).getWorkbook();

        writeBudgetData(workbook.getSheetAt(0), overallBudgetReportList);
        writeBudgetData(workbook.getSheetAt(1), monthlyBudgetReportList);

        writeSummary(workbook.getSheetAt(0), overallBudgetReportList);
        writeSummary(workbook.getSheetAt(1), overallBudgetReportList);

        try {
            BaseFormulaEvaluator.evaluateAllFormulaCells(workbook);
        } catch (NotImplementedException e) {
            workbook.setForceFormulaRecalculation(true);
        }
        return createOutputFile(workbook);
    }

    private void writeSummary(Sheet sheet, List<BudgetSummary> summary) {
        var template = SheetTemplate.<BudgetSummary>of(Map.of("name", budgetSummary -> budgetSummary.getBudget().getName()), sheet);
        var templateWriter = new TemplateWriter<>(template, summary);
        templateWriter.write();
        templateWriter.removeFlagSheet();
    }

    private void writeBudgetData(Sheet sheet, List<BudgetSummary> budgetList) {
        var template = SheetTemplate.withMap(budgetReportFieldMapper(), budgetReportMapMapper(), sheet);
        var tw = new TemplateWriter<>(template, budgetList);
        setWarnings(budgetList, tw);
        tw.write();
    }

    private void setWarnings(List<BudgetSummary> budgetList, TemplateWriter<BudgetSummary> templateWriter) {
        budgetList.forEach(budgetData -> {
            String flag;
            if (budgetData.getProgress() != null && budgetData.getProgress() >= 0.6 && budgetData.getProgress() < 0.8) {
                flag = "warning1";
            } else if (budgetData.getProgress() != null && budgetData.getProgress() >= 0.8 && budgetData.getProgress() < 1) {
                flag = "warning2";
            } else if (budgetData.getProgress() != null && budgetData.getProgress() >= 1) {
                flag = "warning3";
            } else {
                return;
            }
            templateWriter.addFlag(budgetData, "progress", flag);
        });
    }

    private File createOutputFile(Workbook workbook) {
        File outputFile;
        try {
            outputFile = File.createTempFile("report-", ".xlsx");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        outputFile.deleteOnExit();
        try (var outputStream = new FileOutputStream(outputFile)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    private Map<String, Function<BudgetSummary, Object>> budgetReportFieldMapper() {
        return Map.ofEntries(
                entry("id", budgetSummary -> budgetSummary.getBudget().getId()),
                entry("from", budgetSummary -> budgetSummary.getRange().getStartDate()),
                entry("until", budgetSummary -> budgetSummary.getRange().getEndDate()),
                entry("spent_net", budgetSummary -> budgetSummary.getSpent().getAmount().doubleValue()),
                entry("spent_gross", budgetSummary -> budgetSummary.getSpentGross().getAmount().doubleValue()),
                entry("hoursAggregated", BudgetSummary::getHoursAggregated),
                entry("budgetRemaining_net", budgetSummary -> budgetSummary.getBudgetRemaining().getAmount().doubleValue()),
                entry("budgetRemaining_gross", budgetSummary -> budgetSummary.getBudgetRemainingGross().getAmount().doubleValue()),
                entry("progress", BudgetSummary::getProgress)
        );
    }

    private Map<String, Function<BudgetSummary, Map<String, String>>> budgetReportMapMapper() {
        return Map.of("attributes", BudgetSummary::getAttributes);
    }
}
