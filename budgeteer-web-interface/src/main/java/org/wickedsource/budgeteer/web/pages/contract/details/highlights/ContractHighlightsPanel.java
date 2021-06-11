package org.wickedsource.budgeteer.web.pages.contract.details.highlights;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.EnumLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.wickedsource.budgeteer.web.components.datelabel.DateLabel;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModel;

import java.io.IOException;
import java.io.OutputStream;

public class ContractHighlightsPanel extends Panel {

    public ContractHighlightsPanel(String id, final IModel<ContractModel> model) {
        super(id, model);
        add(new Label("name", model.map(ContractModel::getName)));
        add(new Label("internalNumber", model.map(ContractModel::getInternalNumber)));
        add(new DateLabel("startDate", model.map(ContractModel::getStartDate)));
        add(new EnumLabel<>("type", model.map(ContractModel::getType)));
        add(new Label("budget", model.map(ContractModel::getBudget)));


        WebMarkupContainer linkContainer = new WebMarkupContainer("linkContainer"){
            @Override
            public boolean isVisible() {
                return model.getObject().getFileUploadModel().getLink() != null && !model.getObject().getFileUploadModel().getLink().isEmpty();
            }
        };
        linkContainer.add(new ExternalLink("link", Model.of(model.getObject().getFileUploadModel().getLink()), Model.of(model.getObject().getFileUploadModel().getLink())));
        add(linkContainer);

        WebMarkupContainer fileContainer = new WebMarkupContainer("fileContainer"){
            @Override
            public boolean isVisible() {
                return model.getObject().getFileUploadModel().getFileName() != null && !model.getObject().getFileUploadModel().getFileName().isEmpty();
            }
        };
        final byte[] file = model.getObject().getFileUploadModel().getFile();
        final String fileName = model.getObject().getFileUploadModel().getFileName();
        Link<Void> fileDownloadLink = new Link<Void>("file") {

            @Override
            public void onClick() {
                AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
                    @Override
                    public void write(OutputStream output) throws IOException {
                        output.write(file);
                    }
                };
                ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, fileName);
                getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
            }
        };
        fileDownloadLink.setBody(Model.of(fileName));
        fileContainer.add(fileDownloadLink);
        add(fileContainer);

        add(new ListView<>("additionalInformation", model.map(ContractModel::getAttributes)) {
            @Override
            protected void populateItem(ListItem<ContractModel.Attribute> item) {
                item.add(new Label("value", item.getModel().map(ContractModel.Attribute::getValue)));
                item.add(new Label("key", item.getModel().map(ContractModel.Attribute::getKey)));
            }
        });
    }

}
