package de.adesso.budgeteer.core.contract.service;

import de.adesso.budgeteer.core.contract.domain.ContractSummary;
import de.adesso.budgeteer.core.contract.port.in.ExportContractUseCase;
import de.adesso.budgeteer.core.contract.port.out.GetContractSummariesInRangePort;
import de.adesso.budgeteer.core.template.port.out.GetTemplateByIdPort;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.wickedsource.budgeteer.SheetTemplate.SheetTemplate;
import org.wickedsource.budgeteer.SheetTemplate.TemplateWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class ExportContractService implements ExportContractUseCase {

    private final GetTemplateByIdPort getTemplateByIdPort;
    private final GetContractSummariesInRangePort getContractSummariesInRangePort;

    @Override
    public File exportContract(long projectId, long templateId, LocalDate until) {
        var workbook = getTemplateByIdPort.getTemplateById(templateId).getWorkbook();

        // Overall summary
        var contractReportList = getContractSummariesInRangePort.getContractSummariesInRange(projectId, null, until);
        writeContractData(workbook.getSheetAt(0), contractReportList);
        writeSummary(workbook.getSheetAt(0), contractReportList, false);

        // Monthly summary
        var monthlyContractReportList = getContractSummariesInRangePort.getContractSummariesInRange(projectId, until, until);
        writeContractData(workbook.getSheetAt(1), monthlyContractReportList);
        writeSummary(workbook.getSheetAt(1), monthlyContractReportList, true);

        BaseFormulaEvaluator.evaluateAllFormulaCells(workbook);
        return outputfile(workbook);
    }

    private void writeSummary(Sheet sheet, List<ContractSummary> summaries, boolean removeFlagSheet) {
        Set<String> recipients = summaries.stream()
                .map(contract -> contract.getContract().getAttributes().get("rechnungsempfaenger"))
                .collect(Collectors.toSet());
        var template = SheetTemplate.<String>of(Map.of("name", recipient -> recipient), sheet);
        var templateWriter = new TemplateWriter<>(template, recipients);
        templateWriter.write();
        if (removeFlagSheet) {
            templateWriter.removeFlagSheet();
        }
    }

    private File outputfile(Workbook wb) {
        File outputFile = null;
        FileOutputStream out;
        try {
            outputFile = File.createTempFile("contract-report-", ".xlsx");
            outputFile.deleteOnExit();
            out = new FileOutputStream(outputFile);
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    private void writeContractData(Sheet sheet, List<ContractSummary> reportList) {
        var template = SheetTemplate.withMap(contractSummaryFieldMappers(),
                contractSummaryMapMappers(),
                sheet);
        var templateWriter = new TemplateWriter<>(template, reportList);
        setWarnings(reportList, templateWriter);
        templateWriter.write();
    }

    private void setWarnings(List<ContractSummary> summaries, TemplateWriter<ContractSummary> templateWriter) {
        summaries.forEach(contractSummary -> {
            var ratioBurned = contractSummary.ratioBurned().doubleValue();
            if (ratioBurned >= 0.6 && ratioBurned < 0.8) {
                templateWriter.addFlag(contractSummary, "progress", "warning1");
            } else if (ratioBurned >= 0.8 && ratioBurned < 1) {
                templateWriter.addFlag(contractSummary, "progress", "warning2");
            } else if (ratioBurned >= 1) {
                templateWriter.addFlag(contractSummary, "progress", "warning3");
            }
        });
    }

    private Map<String, Function<ContractSummary, Object>> contractSummaryFieldMappers() {
        return Map.ofEntries(
                entry("id", contract -> contract.getContract().getId()),
                entry("contract", contract -> contract.getContract().getName()),
                entry("contractId", contract -> contract.getContract().getInternalNumber()),
                entry("from", ContractSummary::getFrom),
                entry("until", ContractSummary::getUntil),
                entry("budgetSpent_net", contract -> contract.getBudgetSpent().getAmount()),
                entry("budgetLeft_net", contract -> contract.getBudgetLeft().getAmount()),
                entry("budgetTotal_net", contract -> contract.totalBudget().getAmount()),
                entry("budgetSpent_gross", contract -> contract.budgetSpentGross().getAmount()),
                entry("budgetLeft_gross", contract -> contract.budgetLeftGross().getAmount()),
                entry("budgetTotal_gross", contract -> contract.totalBudgetGross().getAmount()),
                entry("taxRate", contract -> contract.getContract().getTaxRate()),
                entry("progress", ContractSummary::ratioBurned)
        );
    }

    private Map<String, Function<ContractSummary, Map<String, String>>> contractSummaryMapMappers() {
        return Map.ofEntries(entry("attributes", contract -> contract.getContract().getAttributes()));
    }

}
