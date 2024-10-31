package com.github.mattiolileo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class StorageHandler implements OnCreateNodePolicy {
    private static final Logger logger = LoggerFactory.getLogger(StorageHandler.class);

    // Dependencies
    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private ServiceRegistry serviceRegistry;

    // Behaviours
    private Behaviour onCreateNode;

    @Value("${setContentStore.siteName}")
    private String siteName;
    @Value("${setContentStore.aspectName}")
    private String aspectName;
    @Value("${setContentStore.aspectPropertyName}")
    private String aspectPropertyName;
    @Value("${setContentStore.aspectPropertyValue}")
    private String aspectPropertyValue;

    public void init() {
        logger.debug(String.format(
                "Start init Custom StorageHandler. siteName: %s, aspectName: %s, aspectPropertyName: %s, aspectPropertyValue: %s",
                this.siteName, this.aspectName, this.aspectPropertyName, this.aspectPropertyValue));
        // Create behaviours
        this.onCreateNode = new JavaBehaviour(this, "onCreateNode", NotificationFrequency.FIRST_EVENT);

        // Bind behaviours to node policies
        this.policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT,
                this.onCreateNode);
   
        logger.debug("Done Init Custom StorageHandler");
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) throws RuntimeException {
        logger.trace("Inside onCreateNode");

        // get the parent node
        NodeRef childRef = childAssocRef.getChildRef();

        String actualSite = serviceRegistry.getSiteService().getSiteShortName(childRef);
        logger.debug("[onCreateNode] Actual site: " + actualSite);
        if (this.siteName.equalsIgnoreCase(actualSite)) {
            QName qname = QName.createQName(this.aspectName);
            QName property = QName.createQName(this.aspectPropertyName);
            logger.trace("[onCreateNode] Checking/Applying aspect");
            if (!nodeService.hasAspect(childRef, qname)) {
                Map<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();

                aspectProps.put(property, this.aspectPropertyValue);
                nodeService.addAspect(childRef, qname, aspectProps);
                logger.debug("[onCreateNode] Aspect added to: " + childRef.toString());
            } else {
                logger.trace("[onCreateNode] Nothing");
            }
        } else {
            logger.trace("[onCreateNode] Not the correct site");
        }
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}