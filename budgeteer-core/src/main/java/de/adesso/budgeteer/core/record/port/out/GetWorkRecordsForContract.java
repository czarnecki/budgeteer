package de.adesso.budgeteer.core.record.port.out;

import de.adesso.budgeteer.core.record.domain.WorkRecord;

import java.util.List;

public interface GetWorkRecordsForContract {
    List<WorkRecord> getWorkRecordsForContract(long contractId);
}
