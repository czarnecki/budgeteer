package org.wickedsource.budgeteer.service.budget.report;

import de.adesso.budgeteer.core.common.DateRange;
import de.adesso.budgeteer.core.contract.domain.Contract;
import de.adesso.budgeteer.core.contract.port.in.GetContractByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.money.Money;
import org.springframework.stereotype.Service;
import org.wickedsource.budgeteer.MoneyUtil;
import org.wickedsource.budgeteer.SheetTemplate.SheetTemplate;
import org.wickedsource.budgeteer.SheetTemplate.TemplateWriter;
import org.wickedsource.budgeteer.persistence.record.WorkRecordRepository;
import org.wickedsource.budgeteer.service.budget.BudgetDetailData;
import org.wickedsource.budgeteer.service.budget.BudgetService;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetTagFilterModel;
import org.wickedsource.budgeteer.service.template.TemplateService;
import org.wickedsource.budgeteer.web.BudgeteerSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class BudgetReportService {

    private final BudgetService budgetService;
    private final GetContractByIdUseCase getContractByIdUseCase;
    private final WorkRecordRepository workRecordRepository;
    private final TemplateService templateService;

    /**
     * Creates an excel spreadsheet containing the budgets informations
     *
     * @param projectId         ProjectId
     * @param filter            TagFilter of the selected Budgets
     * @param metaInformationen Necessary informations about the report
     * @return Excel spreadsheet file
     */
    public File createReportFile(long templateId, long projectId, BudgetTagFilterModel filter, ReportMetaInformation metaInformationen) {
        List<BudgetReportData> overallBudgetReportList = loadOverallBudgetReportData(projectId, filter,
                metaInformationen);
        List<BudgetReportData> monthlyBudgetReportList = loadMonthlyBudgetReportData(projectId, filter,
                metaInformationen);

        XSSFWorkbook wb = getSheetWorkbook(templateId);

        writeBudgetData(wb.getSheetAt(0), overallBudgetReportList);
        writeBudgetData(wb.getSheetAt(1), monthlyBudgetReportList);

        List<BudgetSummary> overallSummary = createBudgetSummary(overallBudgetReportList);
        List<BudgetSummary> monthlySummary = createBudgetSummary(monthlyBudgetReportList);

        writeSummary(wb.getSheetAt(0), overallSummary);
        writeSummary(wb.getSheetAt(1), monthlySummary);

        try {
            XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
        } catch (NotImplementedException e) {
            wb.setForceFormulaRecalculation(true);
        }
        return createOutputFile(wb);
    }

    private void writeSummary(XSSFSheet sheet, List<BudgetSummary> summary) {
        var template = SheetTemplate.<BudgetSummary>of(Map.of("name", BudgetSummary::getName), sheet);
        var tw = new TemplateWriter<>(template, summary);
        tw.write();
        tw.removeFlagSheet();
    }

    private void writeBudgetData(Sheet sheet, List<BudgetReportData> budgetList) {
        var template = SheetTemplate.withMap(budgetReportFieldMapper(), budgetReportMapMapper(), sheet);
        TemplateWriter<BudgetReportData> tw = new TemplateWriter<>(template, budgetList);
        setWarnings(budgetList, tw);
        tw.write();
    }

    private void setWarnings(List<BudgetReportData> budgetList, TemplateWriter<BudgetReportData> tw) {
        budgetList.forEach(budgetData -> {
            if (budgetData.getProgress() != null && budgetData.getProgress() >= 0.6 && budgetData.getProgress() < 0.8) {
                tw.addFlag(budgetData, "progress", "warning1");
            } else if (budgetData.getProgress() != null && budgetData.getProgress() >= 0.8 && budgetData.getProgress() < 1) {
                tw.addFlag(budgetData, "progress", "warning2");
            } else if (budgetData.getProgress() != null && budgetData.getProgress() >= 1) {
                tw.addFlag(budgetData, "progress", "warning3");
            }
        });
    }

    private List<BudgetSummary> createBudgetSummary(List<BudgetReportData> budgetList) {
        var recipients = budgetList.stream()
                .map(budget -> budget.getAttributes().getOrDefault("rechnungsempfaenger", ""))
                .collect(Collectors.toSet());
        return recipients.stream().map(BudgetSummary::new).collect(Collectors.toList());
    }

    private XSSFWorkbook getSheetWorkbook(long id) {
        return templateService.getById(id).getWb();
    }

    private File createOutputFile(XSSFWorkbook wb) {
        File outputFile = null;
        FileOutputStream out;
        try {
            outputFile = File.createTempFile("report-", ".xlsx");
            outputFile.deleteOnExit();
            out = new FileOutputStream(outputFile);
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    public List<BudgetReportData> loadOverallBudgetReportData(long projectId, BudgetTagFilterModel filter,
                                                              ReportMetaInformation metaInformation) {
        List<BudgetDetailData> budgets = budgetService.loadBudgetsDetailData(projectId, filter);
        return budgets.stream()
                .map(budget -> enrichReportData(budget, metaInformation.getOverallTimeRange()))
                .collect(Collectors.toList());
    }

    public List<BudgetReportData> loadMonthlyBudgetReportData(long projectId, BudgetTagFilterModel filter,
                                                              ReportMetaInformation metaInformation) {
        List<BudgetDetailData> budgets = budgetService.loadBudgetsDetailData(projectId, filter);
        return budgets.stream()
                .map(budget -> enrichReportData(budget, metaInformation.getMonthlyTimeRange()))
                .collect(Collectors.toList());
    }

    private BudgetReportData enrichReportData(BudgetDetailData budget, DateRange dateRange) {
        Contract contract;
        Map<String, String> attributes = null;
        BigDecimal taxRate = BigDecimal.ZERO;
        if (budget.getContractId() != 0L) {
            contract = getContractByIdUseCase.getContractById(budget.getContractId());
            taxRate = contract.getTaxRate();
            attributes = contract.getAttributes();
        }

        Double spentMoneyInPeriodInCents = workRecordRepository.getSpentBudgetInTimeRange(budget.getId(),
                dateRange.getStartDate(), dateRange.getEndDate());
        double spentMoneyInPeriod = toMoneyNullsafe(spentMoneyInPeriodInCents).getAmount().doubleValue();
        Double spentMoneyInCents = workRecordRepository.getSpentBudgetUntilDate(budget.getId(), dateRange.getEndDate());
        double spentMoney = toMoneyNullsafe(spentMoneyInCents).getAmount().doubleValue();
        BigDecimal taxCoefficient = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN));
        double totalMoney = budget.getTotal().getAmount().doubleValue();
        Double progress = (Math.abs(totalMoney) < Math.ulp(1.0) && Math.abs(spentMoney) < Math.ulp(1.0)) ? null : spentMoney / totalMoney;
        double totalHours = workRecordRepository.getTotalHoursInTimeRange(budget.getId(), dateRange.getStartDate(),
                dateRange.getEndDate());

        BudgetReportData data = new BudgetReportData();
        data.setName(budget.getName());
        data.setFrom(dateRange.getStartDate());
        data.setUntil(dateRange.getEndDate());
        data.setAttributes(attributes);
        data.setSpent_net(spentMoneyInPeriod);
        data.setSpent_gross(spentMoneyInPeriod * taxCoefficient.doubleValue());
        data.setBudgetRemaining_net(totalMoney - spentMoney);
        data.setBudgetRemaining_gross((totalMoney - spentMoney) * taxCoefficient.doubleValue());
        data.setHoursAggregated(totalHours);
        data.setProgress(progress);
        return data;
    }

    public Date getLastDayOfLastMonth() {
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        return Date.from(firstOfMonth.minus(1, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getStartDateOfBudget(long budgetId) {
        return workRecordRepository.getFirstWorkRecordDate(budgetId);
    }

    public Date getStartDateOfBudgets() {
        BudgetTagFilterModel filter = BudgeteerSession.get().getBudgetFilter();
        List<BudgetDetailData> budgets = budgetService.loadBudgetsDetailData(BudgeteerSession.get().getProjectId(), filter);
        List<Long> budgetIds = budgets.stream().map(BudgetDetailData::getId).collect(Collectors.toList());
        return workRecordRepository.getFirstWorkRecordDateByBudgetIds(budgetIds);
    }

    public Date getFirstDayOfLastMonth() {
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        return Date.from(firstOfMonth.minus(1, ChronoUnit.MONTHS).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Money toMoneyNullsafe(Double cents) {
        if (cents == null) {
            return MoneyUtil.createMoneyFromCents(0L);
        } else {
            return MoneyUtil.createMoneyFromCents(Math.round(cents));
        }
    }


    private Map<String, Function<BudgetReportData, Object>> budgetReportFieldMapper() {
        return Map.ofEntries(
                entry("id", BudgetReportData::getId),
                entry("from", BudgetReportData::getFrom),
                entry("until", BudgetReportData::getUntil),
                entry("spent_net", BudgetReportData::getSpent_net),
                entry("spent_gross", BudgetReportData::getSpent_gross),
                entry("hoursAggregated", BudgetReportData::getHoursAggregated),
                entry("budgetRemaining_net", BudgetReportData::getBudgetRemaining_net),
                entry("budgetRemaining_gross", BudgetReportData::getBudgetRemaining_gross),
                entry("progress", BudgetReportData::getProgress)
        );
    }

    private Map<String, Function<BudgetReportData, Map<String, String>>> budgetReportMapMapper() {
        return Map.of("attributes", BudgetReportData::getAttributes);
    }
}
