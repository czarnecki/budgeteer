package de.adesso.budgeteer.core.record.port.in;

import de.adesso.budgeteer.core.record.domain.WorkRecord;

import java.util.List;

public interface GetWorkRecordsForProject {
    List<WorkRecord> getWorkRecordsForProject(long projectId);
}
