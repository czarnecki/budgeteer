package org.wickedsource.budgeteer.service.contract;


import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.MoneyUtil;
import org.wickedsource.budgeteer.persistence.contract.ContractEntity;
import org.wickedsource.budgeteer.persistence.contract.ContractFieldEntity;
import org.wickedsource.budgeteer.persistence.contract.ContractRepository;
import org.wickedsource.budgeteer.persistence.project.ProjectContractField;
import org.wickedsource.budgeteer.service.AbstractMapper;
import org.wickedsource.budgeteer.web.components.fileUpload.FileUploadModel;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractDataMapper extends AbstractMapper<ContractEntity, ContractModel>{

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public ContractModel map(ContractEntity entity) {
        if(entity == null)
            return null;
        var contractModel = new ContractModel();
        contractModel.setName(entity.getName());
        contractModel.setId(entity.getId());
        contractModel.setBudget(entity.getBudget());
        contractModel.setBudgetLeft(toMoneyNullsafe(contractRepository.getBudgetLeftByContractId(entity.getId())));
        contractModel.setBudgetSpent(toMoneyNullsafe(contractRepository.getSpentBudgetByContractId(entity.getId())));
        contractModel.setInternalNumber(entity.getInternalNumber());
        contractModel.setProjectId(entity.getProject().getId());
        contractModel.setType(entity.getType() == ContractEntity.ContractType.T_UND_M ? ContractModel.Type.TIME_AND_MATERIAL : ContractModel.Type.FIXED_PRICE);
        contractModel.setStartDate(entity.getStartDate());
        contractModel.setFileUploadModel(new FileUploadModel(entity.getFileName(), entity.getFile(), entity.getLink()));
        contractModel.setTaxRate(entity.getTaxRate());

        List<ContractModel.Attribute> contractAttributes = new ArrayList<>();
        for(ProjectContractField projectContractField:  entity.getProject().getContractFields()){
            contractAttributes.add(new ContractModel.Attribute(projectContractField.getFieldName()));
        }
        for(ContractFieldEntity fieldEntity : entity.getContractFields()){
            contractAttributes.add(new ContractModel.Attribute(fieldEntity.getField().getFieldName(), fieldEntity.getValue()));
        }
        contractModel.setAttributes(new ArrayList<>(contractAttributes));

        return contractModel;
    }

    @Override
    public List<ContractModel> map(List<ContractEntity> entityList){
        return entityList.stream().map(this::map).collect(Collectors.toList());
    }
    
    private Money toMoneyNullsafe(Double cents) {
        if (cents == null) {
            return MoneyUtil.createMoneyFromCents(0L);
        } else {
            return MoneyUtil.createMoneyFromCents(Math.round(cents));
        }
    }
}
