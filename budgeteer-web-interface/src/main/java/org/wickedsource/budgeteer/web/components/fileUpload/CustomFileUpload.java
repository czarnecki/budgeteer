package org.wickedsource.budgeteer.web.components.fileUpload;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CustomFileUpload extends FormComponentPanel<FileUploadModel> {
    private final TextField<String> linkField;
    private final FileUploadField uploadField;

    public CustomFileUpload(String id, IModel<FileUploadModel> model) {
        super(id, model);

        linkField = new TextField<>("link", model.flatMap(fileUploadModel -> Model.of(fileUploadModel.getLink())));
        add(linkField);

        var fileName = new Label("fileName", model.map(FileUploadModel::getFileName))
                .setOutputMarkupId(true);
        add(fileName);

        uploadField = new FileUploadField("fileUpload");
        uploadField.add(new AjaxEventBehavior("change") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                getModelObject().setChanged(true);
            }
        });
        add(uploadField);

        var deleteButton = new WebMarkupContainer("delete");
        deleteButton.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                getModelObject().setChanged(false);
                getModelObject().setFileName(null);
                getModelObject().setFile(null);
                target.add(uploadField);
                target.add(fileName);
            }
        });
        add(deleteButton);
    }

    @Override
    public void convertInput() {
        var link = linkField.getConvertedInput();
        var fileName = uploadField.getFileUpload() == null ? getModelObject().getFileName() : uploadField.getFileUpload().getClientFileName();
        var file = uploadField.getFileUpload() == null ? getModelObject().getFile() : uploadField.getFileUpload().getBytes();
        setConvertedInput(new FileUploadModel(fileName, file, link));
    }

    public byte[] getFile() {
        byte[] result;
        if(getModelObject().isChanged()){
            result = uploadField.getFileUpload().getBytes();
        } else {
            result = getModelObject().getFile();
        }
        return result;
    }

    public String getFileName() {
        String result;
        if(getModelObject().isChanged()){
            result = uploadField.getFileUpload().getClientFileName();
        } else {
            result = getModelObject().getFileName();
        }
        return result;
    }
}
