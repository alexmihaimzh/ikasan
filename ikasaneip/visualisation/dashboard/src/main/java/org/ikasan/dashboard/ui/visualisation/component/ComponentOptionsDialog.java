package org.ikasan.dashboard.ui.visualisation.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.model.flow.AbstractWiretapNode;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.MetaDataApplicationRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.rest.client.dto.TriggerDto;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.model.FlowMetaDataImpl;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.NodeFoundStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ComponentOptionsDialog extends Dialog {
    Logger logger = LoggerFactory.getLogger(ComponentOptionsDialog.class);

    protected ConfigurationRestServiceImpl configurationRestService;

    protected TriggerRestServiceImpl triggerRestService;

    protected Module module;

    protected String flowName;

    protected String componentName;

    protected NetworkDiagram networkDiagram;

    protected AbstractWiretapNode abstractWiretapNode;

    private MetaDataApplicationRestServiceImpl metaDataApplicationRestService;

    protected boolean configuredResource;

    protected ComponentOptionsDialog(Module module, String flowName, String componentName, boolean configuredResource,
                                     ConfigurationRestServiceImpl configurationRestService,
                                     TriggerRestServiceImpl triggerRestService, NetworkDiagram networkDiagram,
                                     AbstractWiretapNode abstractWiretapNode, MetaDataApplicationRestServiceImpl metaDataApplicationRestService)
    {
        this.module = module;
        this.flowName = flowName;
        this.componentName = componentName;
        this.configurationRestService = configurationRestService;
        this.configuredResource = configuredResource;
        this.triggerRestService = triggerRestService;
        this.networkDiagram = networkDiagram;
        this.abstractWiretapNode = abstractWiretapNode;
        this.metaDataApplicationRestService = metaDataApplicationRestService;

        init();
    }

    private void init()
    {
        VerticalLayout verticalLayout = new VerticalLayout();

        Image mrSquidImage = new Image("/frontend/images/mr-squid-head.png", "");
        mrSquidImage.setHeight("35px");

        H3 componentOptions = new H3(
            String.format(getTranslation("label.component-options", UI.getCurrent().getLocale())));

        HorizontalLayout header = new HorizontalLayout();
        header.add(mrSquidImage, componentOptions);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, mrSquidImage, componentOptions);

        verticalLayout.add(header);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, header);

        if ( this.configuredResource )
        {
            Button componentConfigurationButton = new Button(
                getTranslation("button.component-configuration", UI.getCurrent().getLocale()));
            componentConfigurationButton.setWidthFull();
            componentConfigurationButton.addClickListener((ComponentEventListener<ClickEvent<Button>>)
                buttonClickEvent -> openComponentConfiguration());

            verticalLayout.add(componentConfigurationButton);

            ComponentSecurityVisibility.applySecurity(componentConfigurationButton, SecurityConstants.ALL_AUTHORITY
                , SecurityConstants.PLATORM_CONFIGURATON_ADMIN
                , SecurityConstants.PLATORM_CONFIGURATON_READ
                , SecurityConstants.PLATORM_CONFIGURATON_WRITE);
        }

        Button invokerConfigurationButton = new Button(
            getTranslation("button.invoker-configuration", UI.getCurrent().getLocale()));
        invokerConfigurationButton.setWidthFull();
        invokerConfigurationButton.addClickListener((ComponentEventListener<ClickEvent<Button>>)
            buttonClickEvent -> openInvokerConfiguration());

        verticalLayout.add(invokerConfigurationButton);

        ComponentSecurityVisibility.applySecurity(invokerConfigurationButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.PLATORM_CONFIGURATON_ADMIN
            , SecurityConstants.PLATORM_CONFIGURATON_READ
            , SecurityConstants.PLATORM_CONFIGURATON_WRITE);

        Button createWiretapBeforeComponentWithTTLOneDayButton = new Button(
            getTranslation("button.wiretap-before-component-oneday", UI.getCurrent().getLocale()));
        createWiretapBeforeComponentWithTTLOneDayButton.setWidthFull();
        createWiretapBeforeComponentWithTTLOneDayButton.addClickListener(
            (ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> createWiretapWithTTLOneDay("before"));
        verticalLayout.add(createWiretapBeforeComponentWithTTLOneDayButton);

        ComponentSecurityVisibility.applySecurity(createWiretapBeforeComponentWithTTLOneDayButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.WIRETAP_WRITE
            , SecurityConstants.WIRETAP_ADMIN);

        Button createWiretapAfterComponentWithTTLOneDayButton = new Button(
            getTranslation("button.wiretap-after-component-oneday", UI.getCurrent().getLocale()));
        createWiretapAfterComponentWithTTLOneDayButton.setWidthFull();
        createWiretapAfterComponentWithTTLOneDayButton.addClickListener(
            (ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> createWiretapWithTTLOneDay("after"));
        verticalLayout.add(createWiretapAfterComponentWithTTLOneDayButton);

        ComponentSecurityVisibility.applySecurity(createWiretapAfterComponentWithTTLOneDayButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.WIRETAP_ADMIN
            , SecurityConstants.WIRETAP_WRITE);

        Button createLogBeforeComponentButton = new Button(
            getTranslation("button.log-before-component", UI.getCurrent().getLocale()));
        createLogBeforeComponentButton.setWidthFull();
        createLogBeforeComponentButton
            .addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> createLog("before"));
        verticalLayout.add(createLogBeforeComponentButton);

        ComponentSecurityVisibility.applySecurity(createLogBeforeComponentButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.WIRETAP_ADMIN
            , SecurityConstants.WIRETAP_WRITE);

        Button createLogAfterComponentButton = new Button(
            getTranslation("button.log-after-component", UI.getCurrent().getLocale()));
        createLogAfterComponentButton.setWidthFull();
        createLogAfterComponentButton
            .addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> createLog("after"));
        verticalLayout.add(createLogAfterComponentButton);

        ComponentSecurityVisibility.applySecurity(createLogAfterComponentButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.WIRETAP_ADMIN
            , SecurityConstants.WIRETAP_WRITE);

        this.add(verticalLayout);
    }

    private void openComponentConfiguration()
    {
        ComponentConfigurationDialog componentConfigurationDialog = new ComponentConfigurationDialog(module,
            flowName, componentName, configurationRestService
        );

        this.close();
        componentConfigurationDialog.open();
    }

    private void openInvokerConfiguration()
    {
        InvokerConfigurationDialog componentConfigurationDialog = new InvokerConfigurationDialog(module, flowName,
            componentName, configurationRestService
        );

        this.close();
        componentConfigurationDialog.open();
    }

    private void createWiretapWithTTLOneDay(String relationship)
    {
        createTrigger(relationship,"wiretapJob", "720");
    }

    private void createLog(String relationship)
    {
        createTrigger(relationship,"loggingJob",null);
    }

    private void createTrigger(String relationship, String job, String ttl)
    {
        TriggerDto triggeDto = new TriggerDto(this.module.getName(), this.flowName, this.componentName, relationship,
            job, ttl
        );
        boolean success = this.triggerRestService.create(this.module.getUrl(), triggeDto);
        if ( success )
        {
            this.updateDiagramState(job, relationship);
            NotificationHelper
                .showUserNotification(getTranslation("message.wiretap-save-successful", UI.getCurrent().getLocale()));

            Optional<FlowMetaData> flowMetaDataOptional = this.metaDataApplicationRestService.getFlowMetadata(module.getUrl(), module.getName(), flowName);

            flowMetaDataOptional.ifPresent(flowMetaData -> {
                logger.info(flowMetaData.toString());

                flowMetaData.getFlowElements().stream()
                    .filter(flowElementMetaData -> flowElementMetaData.getComponentName().equals(this.componentName))
                    .findFirst().ifPresent(decorators -> this.abstractWiretapNode.setDecoratorMetaDataList(decorators.getDecorators()));
            });
        }
        else
        {
            NotificationHelper.showErrorNotification(
                getTranslation("message.wiretap-save-unsuccessful", UI.getCurrent().getLocale()));
        }

        this.close();
    }

    private void updateDiagramState(String job, String relationship){
        if(job.equals("wiretapJob")){
            if(relationship.equals("after")) {
                UI.getCurrent().access(() -> this.networkDiagram.addWiretapAfter(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getWiretapAfterImageX(),
                    this.abstractWiretapNode.getY() + this.abstractWiretapNode.getWiretapAfterImageY(),
                    this.abstractWiretapNode.getWiretapAfterImageW(), this.abstractWiretapNode.getWiretapAfterImageH()));
                abstractWiretapNode.setWiretapAfterStatus(NodeFoundStatus.FOUND);
            }
            else if(relationship.equals("before")) {
                UI.getCurrent().access(() -> this.networkDiagram.addWiretapBefore(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getWiretapBeforeImageX(),
                    this.abstractWiretapNode.getY() + this.abstractWiretapNode.getWiretapBeforeImageY(),
                    this.abstractWiretapNode.getWiretapBeforeImageW(), this.abstractWiretapNode.getWiretapBeforeImageH()));
                abstractWiretapNode.setWiretapBeforeStatus(NodeFoundStatus.FOUND);
            }
        }
        else if(job.equals("loggingJob")){
            if(relationship.equals("after")) {
                UI.getCurrent().access(() -> this.networkDiagram.addLogWiretapAfter(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getLogWiretapAfterImageX(),
                    this.abstractWiretapNode.getY() + this.abstractWiretapNode.getLogWiretapAfterImageY(),
                    this.abstractWiretapNode.getLogWiretapAfterImageW(), this.abstractWiretapNode.getLogWiretapAfterImageH()));
                abstractWiretapNode.setLogWiretapAfterStatus(NodeFoundStatus.FOUND);
            }
            else if(relationship.equals("before")) {
                UI.getCurrent().access(() -> this.networkDiagram.addLogWiretapBefore(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getLogWiretapBeforeImageX(),
                    this.abstractWiretapNode.getY() + this.abstractWiretapNode.getLogWiretapBeforeImageY(),
                    this.abstractWiretapNode.getLogWiretapBeforeImageW(), this.abstractWiretapNode.getLogWiretapBeforeImageH()));
                abstractWiretapNode.setLogWiretapBeforeStatus(NodeFoundStatus.FOUND);
            }
        }

        UI.getCurrent().access(() -> this.networkDiagram.diagamRedraw());
    }
}
