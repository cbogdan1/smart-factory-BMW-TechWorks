package com.smartfactory.order.control.order_service;

import java.time.LocalDateTime;

import com.smartfactory.common.dto.order.CreateOrderRequest;
import com.smartfactory.common.dto.order.OrderResponse;
import com.smartfactory.common.dto.order.OrderStatusUpdateResponse;
import com.smartfactory.common.dto.order.OrderViewResponse;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.common.payloads.order_service.OrderStatusUpdatedPayload;
import com.smartfactory.order.boundary.exception_handler.OrderNotFoundException;
import com.smartfactory.order.control.mapper.OrderCreatedPayloadMapper;
import com.smartfactory.order.control.mapper.OrderStatusResponseMapper;
import com.smartfactory.order.control.mapper.OrderStatusUpdatedPayloadMapper;
import com.smartfactory.order.control.mapper.OrderViewMapper;
import com.smartfactory.order.control.repository.OrderRepository;
import com.smartfactory.order.control.validation_service.ValidationService;
import com.smartfactory.order.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderServiceImpl implements OrderService {

    private final Event<OrderCreatedPayload> orderCreatedEvent;
    private final Event<OrderStatusUpdatedPayload> orderStatusUpdatedEvent;

    private final OrderRepository orderRepository;
    private final IdGeneratorService idGeneratorService;
    private final ValidationService validationService;
    private final OrderCreatedPayloadMapper payloadMapper;
    private final OrderStatusResponseMapper statusMapper;
    private final OrderViewMapper viewMapper;

    public OrderServiceImpl(final OrderRepository orderRepository, final IdGeneratorService idGeneratorService,
            final ValidationService validationService, final OrderCreatedPayloadMapper payloadMapper,
            final OrderStatusResponseMapper statusMapper, final OrderViewMapper viewMapper,
            final Event<OrderCreatedPayload> orderCreatedEvent,
            final Event<OrderStatusUpdatedPayload> orderStatusUpdatedEvent) {

        this.orderRepository = orderRepository;
        this.idGeneratorService = idGeneratorService;
        this.validationService = validationService;

        this.payloadMapper = payloadMapper;
        this.statusMapper = statusMapper;
        this.viewMapper = viewMapper;

        this.orderCreatedEvent = orderCreatedEvent;
        this.orderStatusUpdatedEvent = orderStatusUpdatedEvent;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(final CreateOrderRequest request) {
        //throws exceptions and aborts @transactional if any problems occur
        validationService.validate(request.getVehicleModel(), request.getBatteryType());

        final Order order = new Order();
        order.setOrderId(idGeneratorService.generateOrderId());
        order.setVehicleId(idGeneratorService.generateVehicleId());

        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());

        order.setVehicleModel(request.getVehicleModel());
        order.setColor(request.getColor());

        order.setBatteryType(request.getBatteryType());
        order.setPriority(request.getPriority());

        //Every new order starts in CREATED status
        order.setStatus(OrderStatus.CREATED);

        final LocalDateTime now = LocalDateTime.now();

        order.setCreatedDate(now);
        order.setLastModifiedDate(now);

        orderRepository.persist(order);

        final OrderCreatedPayload payload = payloadMapper.toPayload(order);

        orderCreatedEvent.fire(payload);

        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(final Order order) {

        final OrderResponse response = new OrderResponse();

        response.setOrderId(order.getOrderId());
        response.setVehicleId(order.getVehicleId());

        response.setCustomerName(order.getCustomerName());
        response.setCustomerEmail(order.getCustomerEmail());

        response.setVehicleModel(order.getVehicleModel());

        response.setColor(order.getColor());

        response.setBatteryType(order.getBatteryType());
        response.setPriority(order.getPriority());

        response.setStatus(order.getStatus());

        response.setCreatedDate(order.getCreatedDate());
        response.setLastModifiedDate(order.getLastModifiedDate());
        response.setCompletionDate(order.getCompletionDate());

        return response;
    }

    @Override
    public OrderViewResponse getOrderById(final String id) {

        return orderRepository.findByOrderId(id).map(viewMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));

    }

    @Transactional
    @Override
    public OrderStatusUpdateResponse changeStatus(final String orderId, final OrderStatus newStatus) {
        return orderRepository.findByOrderId(orderId).map(order -> {
            OrderStatusTransitionHelper.checkTransition(order.getStatus(), newStatus);
            //dirty checking is active so no need to reference the repository Class
            OrderStatus previousStatus = order.getStatus();

            order.setStatus(newStatus);
            order.setLastModifiedDate(LocalDateTime.now());

            final OrderStatusUpdatedPayload payload = OrderStatusUpdatedPayloadMapper.build(order, previousStatus);

            orderStatusUpdatedEvent.fire(payload);

            return statusMapper.toResponse(newStatus);

        }).orElseThrow(() -> new OrderNotFoundException(orderId));

    }

}