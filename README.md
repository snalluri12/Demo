
# Order and Shipment Automation for QuickTrack 

## Project Overview

This proof of concept demonstrates an end-to-end solution in Veeva Vault where:
- Creating a new `Order__c` record linked to `Customer__c` automatically triggers creation of a linked `Shipment__c` record via Vault Java SDK.
- `Shipment__c` records include logic for generating its required fields, including a tracking number and an address. 
- A custom report details the relationship between `Customer__c`, `Order__c`, and `Shipment__c`, enabling business users to filter and view order fulfillment data.

---

## Object Relationships
<br>

![Entity Relationship Diagram](./Object-Entity%20Diagram.png)

<br>

---

## Trigger Configuration

**Trigger Class:** `OrderShipmentTrigger.java`  
**Trigger Event:** `AFTER_INSERT` on `Order__c`

### Logic Summary:
- When an order is inserted, a corresponding `Shipment__c` record is created.
- `shipment_tracking_number__c` is generated using the format: `trk-[orderId]`.
- If the order's name is `"Quick Track"`, the shipment address is defaulted to: `1500 Broadway Ave, New York, NY`.

### Error Handling:
- If shipment creation fails, the transaction is rolled back using a `RollbackException`.

---

## API Usage (Postman and curl)

Customers and Orders are created via the Vault REST API using Postman.

### Sample Request (one insert):
```
POST /api/v25.1/vobjects/order__c
Headers:
Authorization: {session ID}
Content-Type: application/x-www-form-urlencoded
X-VaultAPI-MigrationMode: true
Accept: application/json

Body (x-www-form-urlencoded):
name__v: Order 1
customer__c: VEM000000003001
status__v: active__v
order_name__c: Quick Track Supplies

```

### Sample Request (bulk insert):
```
curl -X POST -H "Authorization: {sessionID}" \
-H "Content-Type: text/csv" \
-H "Accept: text/csv" \
--data-binary @"../order_import_sample.csv" \
https://vaultsystemintegration-candidate-exercise-srinija.veevavault.com/api/v25.1/vobjects/order__c

```


Go to this [link](https://srinija-s-team.postman.co/workspace/adeba121-c4d0-4820-8813-443cc794c9f7/request/46722228-857bfea0-104f-43ea-be07-88ae2ed12bf2?tab=body) for sample collection and environment.

---

## Report Configuration

**Report Type:** 
- Order with Customer and Shipment
**Primary Object:** `Order__c`  
**Related Objects:**
- `Customer__c` (via lookup)
- `Shipment__c` (child relationship)

### Fields Included:
- `Order__c.name__v`
- `Order__c.order_name__c`
- `Order__c.status__v`
- `Customer__c.name__v`
- `Shipment__c.name__v`
- `Shipment__c.date__c`
- `Shipment__c.shipment_tracking_number__c`
- `Shipment__c.address`

[See Report](https://vaultsystemintegration-candidate-exercise-srinija.veevavault.com/ui/#reporting/viewer/0RP00000000A001)

### Filter:
- Order Date Range

---

## File Structure

```
README.md
/src/
  orders.py
  order_import_sample.csv
/Trigger/
  Trigger/javasdk/src/main/java/com/veeva/vault/custom/triggers/OrderShipmentrigger.java

```

---

## 🔍 Known Limitations / Open Points

- `address__c` on `Shipment__c` must always be set; logic currently defaults only for `"Quick Track"` orders and is not conditional based on customer 
- Future iterations could support having the shipment tracking number as a lookup field on the order. Once the shipment is created, the order field should be set. This makes   the shipments more accessible through the order. 
- `Shipment Status` could be configured as a custom picklist so that there is more values than "Active" or "Inactive" (eg. "Pending", "Shipping")

---

## How to Run / Test

1. Use Postman to create a new `Order__c` record.
2. Confirm that a `Shipment__c` is created via the SDK trigger.
3. Navigate to the custom Vault report to verify visibility of linked records.

---

## Handoff Notes

This README, Java trigger code, Postman collection, and screenshots can be used by any Vault developer or administrator to reproduce, extend, or maintain the current setup.
