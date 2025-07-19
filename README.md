
# Veeva Vault POC – Order & Shipment Automation for a Customer

## 📌 Project Overview

This proof of concept demonstrates an end-to-end solution in Veeva Vault where:
- Creating a new `Order__c` record linked to `Customer__c` automatically triggers creation of a linked `Shipment__c` record via Vault Java SDK.
- `Shipment__c` records include logic for generating its required fields, including a tracking number and an address. 
- A custom report details the relationship between `Customer__c`, `Order__c`, and `Shipment__c`, enabling business users to filter and view order fulfillment data.

---

## 🛠️ Object Relationships

![Entity Relationship Diagram](veeva_vault_entity_relationship_diagram.png)

```
Customer__c
   ↑
Order__c (Reference: customer__c)
   ↓
Shipment__c (Reference: order__c)
```

---

## ⚙️ Trigger Configuration

**Trigger Class:** `OrderShipmentTrigger.java`  
**Trigger Event:** `AFTER_INSERT` on `Order__c`

### Logic Summary:
- When an order is inserted, a corresponding `Shipment__c` record is created.
- `shipment_tracking_number__c` is generated using the format: `trk-[orderId]`.
- If the order's name is `"Quick Track"`, the shipment address is defaulted to: `1500 Broadway Ave, New York, NY`.

### Error Handling:
- If shipment creation fails, the transaction is rolled back using a `RollbackException`.

---

## 🧪 API Usage (Postman)

To simulate external system integration, Orders are created via the Vault REST API using Postman.

### Sample Request:
```
POST /api/v20.3/objects/order__c
Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body:
{
  "name__v": "Quick Track",
  "customer__c": "CUSTOMER_RECORD_ID"
}
```

> See `postman/` folder for sample collection and environment.

---

## 📊 Report Configuration

**Report Type:** Custom  
**Primary Object:** `Order__c`  
**Related Objects:**
- `Customer__c` (via lookup)
- `Shipment__c` (child relationship)

### Fields Included:
- `Customer__c.name__v`
- `Order__c.id`
- `Order__c.order_date__c`
- `Shipment__c.status__c`
- `Shipment__c.shipment_tracking_number__c`

### Optional Filters:
- Shipment Status = e.g., “In Transit”
- Order Date Range

---

## 📂 File Structure

```
/src/
  OrderShipmentTrigger.java
/postman/
  Order_Create.json
/report/
  Orders_with_Shipments_and_Customers_Report_Screenshot.png
README.md
```

---

## 🔍 Known Limitations / Open Points

- `address__c` on `Shipment__c` must always be set; logic currently defaults only for `"Quick Track"` orders.
- Future iterations could support multiple shipments per order.
- `Shipment Status` could be converted to a picklist with controlled lifecycle values.

---

## ✅ How to Run / Test

1. Use Postman to create a new `Order__c` record.
2. Confirm that a `Shipment__c` is created via the SDK trigger.
3. Navigate to the custom Vault report to verify visibility of linked records.

---

## 👥 Handoff Notes

This README, Java trigger code, Postman collection, and screenshots can be used by any Vault developer or administrator to reproduce, extend, or maintain the current setup.

For questions or suggested improvements, please contact [Your Name].
