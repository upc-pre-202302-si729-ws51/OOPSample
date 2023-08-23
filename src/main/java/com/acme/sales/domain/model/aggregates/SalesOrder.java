package com.acme.sales.domain.model.aggregates;

import com.acme.shared.domain.model.valueobjects.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SalesOrder {

    private UUID internalId;

    private Address shippingAddress;
    private SalesOrderStatus status;

    private List<SalesOrderItem> items;


    public SalesOrder(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
        this.internalId = UUID.randomUUID();
        this.status = SalesOrderStatus.CREATED;
        this.items = new ArrayList<>();
    }

    public double calculateTotalPrice() {
        return items
                .stream()
                .mapToDouble(SalesOrderItem::calculatePrice)
                .sum();
    }

    public void addItem(int quantity, Long productId, double unitPrice) {
        items.add(new SalesOrderItem(quantity, productId, unitPrice));
    }

    public void confirm() {
        this.status = SalesOrderStatus.APPROVED;
    }

    private void clearItems() {
        this.items.clear();
    }

    public void cancel() {
        this.status = SalesOrderStatus.CANCELLED;
        clearItems();
    }

    public void dispatch() {
        this.status = SalesOrderStatus.SHIPPED;
        items.forEach(SalesOrderItem::dispatch);
    }

    public boolean isDispatched() {
        return items.stream().allMatch(SalesOrderItem::isDispatched);
    }

    public boolean isInProgress() {
        return this.status == SalesOrderStatus.IN_PROGRESS;
    }

    public void verifyItemsDispatched() {
        if(!isDispatched()) {
            status = items.stream()
                    .anyMatch(SalesOrderItem::isDispatched) ?
                    SalesOrderStatus.IN_PROGRESS :
                    SalesOrderStatus.APPROVED;
        }
    }

    public UUID getInternalId() {
        return internalId;
    }
}
