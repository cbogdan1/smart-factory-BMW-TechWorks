package com.smartfactory.inventory.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import com.smartfactory.common.dto.inventory.CreateUpdateInventoryStockRequest;
import com.smartfactory.common.dto.inventory.InventoryStockResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.inventory.entity.InventoryStock;
import com.smartfactory.inventory.entity.Part;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InventoryStockServiceTest {

    @Mock
    InventoryStockRepository stockRepository;

    @Mock
    PartRepository partRepository;

    @InjectMocks
    InventoryStockService stockService;

    @Test
    void shouldCreateNewStock() {
        final String partId = "PART-001";
        final UUID internalId = UUID.randomUUID();

        final Part part = new Part();
        part.setId(internalId);
        part.setPartId(partId);

        final CreateUpdateInventoryStockRequest request =
                new CreateUpdateInventoryStockRequest();

        request.availableQuantity = 100L;
        request.reservedQuantity = 20L;
        request.minimumQuantity = 10L;
        request.maximumQuantity = 200L;

        when(stockRepository.findByPartId(partId))
                .thenReturn(Optional.empty());

        when(partRepository.findByPartId(partId))
                .thenReturn(Optional.of(part));

        when(stockRepository.isPersistent(any(InventoryStock.class)))
                .thenReturn(false);

        final InventoryStockResponse response =
                stockService.updateStock(partId, request);

        assertEquals(partId, response.partId);
        assertEquals(100L, response.availableQuantity);
        assertEquals(20L, response.reservedQuantity);

        verify(stockRepository).persist(any(InventoryStock.class));
    }

    @Test
    void shouldUpdateExistingStock() {
        final String partId = "PART-001";

        final Part part = new Part();
        part.setPartId(partId);

        final InventoryStock existingStock = new InventoryStock();
        existingStock.setPart(part);

        final CreateUpdateInventoryStockRequest request =
                new CreateUpdateInventoryStockRequest();

        request.availableQuantity = 100L;
        request.reservedQuantity = 20L;
        request.minimumQuantity = 10L;
        request.maximumQuantity = 200L;

        when(stockRepository.findByPartId(partId))
                .thenReturn(Optional.of(existingStock));

        when(stockRepository.isPersistent(existingStock))
                .thenReturn(true);

        final InventoryStockResponse response =
                stockService.updateStock(partId, request);

        assertEquals(100L, response.availableQuantity);
        assertEquals(20L, response.reservedQuantity);

        verify(stockRepository).findByPartId(partId);
    }

    @Test
    void shouldGetStockForPart() {
        final String partId = "PART-001";
        final UUID internalId = UUID.randomUUID();

        final Part part = new Part();
        part.setId(internalId);
        part.setPartId(partId);

        final InventoryStock stock = new InventoryStock();
        stock.setPart(part);
        stock.setAvailableQuantity(100L);
        stock.setReservedQuantity(20L);

        when(stockRepository.findByPartId(partId))
                .thenReturn(Optional.of(stock));

        final InventoryStockResponse response =
                stockService.getStockForPart(partId);

        assertEquals(partId, response.partId);
        assertEquals(100L, response.availableQuantity);
        assertEquals(20L, response.reservedQuantity);
    }

    @Test
    void shouldThrowBusinessExceptionWhenStockDoesNotExist() {
        final String partId = "PART-001";

        when(stockRepository.findByPartId(partId))
                .thenReturn(Optional.empty());

        final BusinessException exception = assertThrows(
                BusinessException.class,
                () -> stockService.getStockForPart(partId)
        );

        assertEquals("INVENTORY_STOCK_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void shouldRejectNegativeQuantity() {
        final String partId = "PART-001";

        final CreateUpdateInventoryStockRequest request =
                new CreateUpdateInventoryStockRequest();

        request.availableQuantity = -1L;
        request.reservedQuantity = 20L;
        request.minimumQuantity = 10L;
        request.maximumQuantity = 200L;

        final BusinessException exception = assertThrows(
                BusinessException.class,
                () -> stockService.updateStock(partId, request)
        );

        assertEquals(
                "INVENTORY_STOCK_QUANTITY_NEGATIVE_ERROR",
                exception.getErrorCode()
        );
    }

    @Test
    void shouldThrowBusinessExceptionWhenPartDoesNotExist() {
        final String partId = "PART-001";

        final CreateUpdateInventoryStockRequest request =
                new CreateUpdateInventoryStockRequest();

        request.availableQuantity = 100L;
        request.reservedQuantity = 20L;
        request.minimumQuantity = 10L;
        request.maximumQuantity = 200L;

        when(stockRepository.findByPartId(partId))
                .thenReturn(Optional.empty());

        when(partRepository.findByPartId(partId))
                .thenReturn(Optional.empty());

        final BusinessException exception = assertThrows(
                BusinessException.class,
                () -> stockService.updateStock(partId, request)
        );

        assertEquals("PART_NOT_FOUND", exception.getErrorCode());
    }
}