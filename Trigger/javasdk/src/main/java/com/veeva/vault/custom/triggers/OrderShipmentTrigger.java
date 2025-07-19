package com.veeva.vault.custom.triggers;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.data.RecordTrigger;
import com.veeva.vault.sdk.api.data.RecordTriggerContext;
import com.veeva.vault.sdk.api.data.RecordChange;
import com.veeva.vault.sdk.api.core.ValueType;
import com.veeva.vault.sdk.api.data.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import com.veeva.vault.sdk.api.data.*;


@RecordTriggerInfo(object = "order__c", name = "create_shipment_trigger", events = RecordEvent.AFTER_INSERT)
public class OrderShipmentTrigger implements RecordTrigger {

    public void execute(RecordTriggerContext recordTriggerContext) {

        RecordService recordService = ServiceLocator.locate(RecordService.class);
        List<Record> shipments = new ArrayList<>();

        for (RecordChange change : recordTriggerContext.getRecordChanges()) {
            String orderId = change.getNew().getValue("id", ValueType.STRING);

            Record shipment = recordService.newRecord("shipment__c");
            shipment.setValue("name__v", "Shipment for Order " + orderId);
            shipment.setValue("order__c", orderId);
            shipment.setValue("date__c", LocalDateTime.now().plusDays(1));
            shipment.setValue("shipment_tracking_number__c", UUID.randomUUID().toString());

            shipments.add(shipment);
        }
        recordService.batchSaveRecords(shipments).execute();

    }
}
