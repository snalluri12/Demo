package com.veeva.vault.custom.triggers;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.data.RecordTrigger;
import com.veeva.vault.sdk.api.data.RecordTriggerContext;
import com.veeva.vault.sdk.api.data.RecordChange;
import com.veeva.vault.sdk.api.core.ValueType;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.*;

import java.util.List;
import java.time.ZonedDateTime;


@RecordTriggerInfo(object = "order__c", events = RecordEvent.AFTER_INSERT)
public class OrderShipmentTrigger implements RecordTrigger {

    public void execute(RecordTriggerContext recordTriggerContext) {

        RecordService recordService = ServiceLocator.locate(RecordService.class);
        List<Record> shipments = VaultCollections.asList();

        for (RecordChange newOrder : recordTriggerContext.getRecordChanges()) {
            String orderId = newOrder.getNew().getValue("id", ValueType.STRING);
            String orderName = newOrder.getNew().getValue("name__v", ValueType.STRING);
            String address = "1500 Broadway Ave New York, NY";

            Record shipment = recordService.newRecord("shipment__c");
            shipment.setValue("name__v", "Shipment For " + orderName);
            shipment.setValue("order__c", orderId);
            shipment.setValue("date__c", ZonedDateTime.now().plusDays(1));
            String trackingNumber = "trk" + "-" + orderId;
            shipment.setValue("address__c", address);
            shipment.setValue("shipment_tracking_number__c", trackingNumber);

            shipments.add(shipment);
        }
        if (!shipments.isEmpty()) {
            RecordBatchSaveRequest recordSaveRequest = recordService.newRecordBatchSaveRequestBuilder().withRecords(shipments).build();
            recordService.batchSaveRecords(recordSaveRequest)
                    .onErrors(errors -> {
                        throw new RollbackException("ORDER_CREATE_FAIL", "Failed to create Order.");
                    })
                    .execute();
        }

    }
}
