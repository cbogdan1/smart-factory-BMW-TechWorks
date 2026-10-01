package com.smartfactory.inventory.control;

import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_MINIMUM_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_STOCK_AVAILABLE_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_STOCK_AVAILABLE_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_MESSAGE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_STOCK_MINIMUM_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_MESSAGE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_STOCK_NEGATIVE_QUANTITY_ERROR_MESSAGE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_STOCK_QUANTITY_NEGATIVE_ERROR_CODE;
import java.util.List;
import com.smartfactory.common.dto.inventory.CreateUpdateInventoryStockRequest;
import com.smartfactory.common.dto.inventory.InventoryStockResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;
import com.smartfactory.inventory.boundary.LowStockPublisher;
import com.smartfactory.inventory.control.exception.InventoryStockInvalidQuantitiesException;
import com.smartfactory.inventory.control.exception.InventoryStockNotFoundException;
import com.smartfactory.inventory.control.exception.PartNotFoundException;
import com.smartfactory.inventory.control.mapper.InventoryStockMapper;
import com.smartfactory.inventory.entity.InventoryStock;
import com.smartfactory.inventory.entity.Part;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class InventoryStockService {
    @Inject
    InventoryStockRepository stockRepository;

    @Inject
    PartRepository partRepository;

    @Inject
    InventoryStockRepository inventoryStockRepository;

    @Inject
    LowStockPublisher lowStockPublisher;


    /**
     * Update an existing record/Insert a new record by its partId
     * If the record exists:
     *
     * @param partId  id of the part requested
     * @param request json of new values for updating/adding a record
     * @return InventoryResponse of inserted/updated record
     */
    @Transactional
    public InventoryStockResponse updateStock(final String partId,
            final CreateUpdateInventoryStockRequest request) {
        log.info("Updating stock for partId: {}", partId);
        validateQuantityRelations(request);

        InventoryStock stock = stockRepository.findByPartId(partId).orElseGet(() -> {
            final InventoryStock newStock = new InventoryStock();

            final Part part = partRepository.findByPartId(partId)
                    .orElseThrow(() -> new PartNotFoundException(partId));

            newStock.setPart(part);

            return newStock;
        });


        stock = InventoryStockMapper.toEntity(stock, request);


        if (!stockRepository.isPersistent(stock)) {
            stockRepository.persist(stock);
        }


        return InventoryStockMapper.fromEntity(stock);
    }


    /**
     * Get the full stock record for requested part
     *
     * @param partId id of the part requested
     * @return InventoryResponse of the stock record with said partId
     */
    public InventoryStockResponse getStockForPart(final String partId) {
        final InventoryStock stock = stockRepository.findByPartId(partId)
                .orElseThrow(() -> new InventoryStockNotFoundException(partId));


        return InventoryStockMapper.fromEntity(stock);
    }


    /**
     * Get all stock records
     *
     * @return list of all stock records
     */
    public List<InventoryStockResponse> getAllStock() {
        return stockRepository.listAll().stream().map(InventoryStockMapper::fromEntity)
                .toList();
    }


    /**
     * Validate request json values by checking if min<=max and available<=max
     *
     * @param request json to be checked
     */
    private void validateQuantityRelations(final CreateUpdateInventoryStockRequest request) {
        if (request.availableQuantity < 0 || request.reservedQuantity < 0 || request.minimumQuantity < 0
                || request.maximumQuantity < 0) {

            throw new InventoryStockInvalidQuantitiesException(
                    INVENTORY_STOCK_NEGATIVE_QUANTITY_ERROR_MESSAGE,
                    INVENTORY_STOCK_QUANTITY_NEGATIVE_ERROR_CODE);
        }

        if (request.minimumQuantity > request.maximumQuantity) {
            throw new InventoryStockInvalidQuantitiesException(
                    INVENTORY_STOCK_MINIMUM_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_MESSAGE,
                    INVENTORY_MINIMUM_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_CODE);
        }

        if (request.availableQuantity > request.maximumQuantity) {
            throw new InventoryStockInvalidQuantitiesException(
                    INVENTORY_STOCK_AVAILABLE_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_MESSAGE,
                    INVENTORY_STOCK_AVAILABLE_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_CODE
            );
        }
    }

    @Transactional
    public InventoryStock processPartsDelivered(final String eventId, final PartsDeliveredPayload payload) {
        final InventoryStock stock = stockRepository.findByPartId(payload.partId()).orElseThrow(
                () -> new BusinessException(
                        "Inventory stock not found for part ID: " + payload.partId()));

        final long newAvailable = stock.getAvailableQuantity() + payload.quantity();
        stock.setAvailableQuantity(newAvailable);
        inventoryStockRepository.persist(stock);
        log.info("Successfully updated stock for partId={}: added {} units. New availableQuantity={}", payload.partId(),
                payload.quantity(), newAvailable);
        return stock;
    }

    public void checkAndPublishLowStock(final InventoryStock stock) {
        if (stock.getAvailableQuantity() < stock.getMinimumQuantity()) {
            lowStockPublisher.publishLowStock(stock.getPart().getPartId(), stock.getPart().getPartCode(),
                    stock.getAvailableQuantity(), stock.getMinimumQuantity());
        }
    }


}
