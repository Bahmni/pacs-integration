package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7;

import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.model.OrderDetails;

import java.util.Date;

public interface ZDSMapper {
    void mapStudyInstanceUID(ORM_O01 message, String orderNumber, Date dateCreated);
}
