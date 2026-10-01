package com.smartfactory.common.dto.order;

import com.smartfactory.common.enums.OrderStatus;
import lombok.NonNull;

public record OrderStatusUpdateRequest(@NonNull OrderStatus newStatus) {
}
