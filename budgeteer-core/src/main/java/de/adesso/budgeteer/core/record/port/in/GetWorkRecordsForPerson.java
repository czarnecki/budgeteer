package de.adesso.budgeteer.core.record.port.in;

import de.adesso.budgeteer.core.record.domain.WorkRecord;

import java.util.List;

public interface GetWorkRecordsForPerson {
    List<WorkRecord> getWorkRecordsForPerson(long personId);
}
