package com.defiance.defiance.util;

import com.defiance.defiance.model.Order;
import com.defiance.defiance.model.OrderItem;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.model.User;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResponseUtil {
    private ResponseUtil() {}

    public static Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("first_name", user.getFirstName());
        map.put("middle_name", user.getMiddleName());
        map.put("last_name", user.getLastName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("address_line", user.getAddressLine());
        map.put("barangay", user.getBarangay());
        map.put("city", user.getCity());
        map.put("province", user.getProvince());
        map.put("postal_code", user.getPostalCode());
        map.put("is_admin", Boolean.TRUE.equals(user.getIsAdmin()));
        return map;
    }

    public static Map<String, Object> productToMap(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("price", product.getPrice());
        map.put("image", product.getImage());
        map.put("category", product.getCategory());
        map.put("is_active", Boolean.TRUE.equals(product.getIsActive()));
        map.put("stock", product.getStock());
        return map;
    }

    public static Map<String, Object> orderToMap(Order order, List<OrderItem> items, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("user_id", order.getUserId());
        map.put("full_name", order.getFullName());
        map.put("email", order.getEmail());
        map.put("phone", order.getPhone());
        map.put("address", buildAddress(order));
        map.put("address_line", order.getAddressLine());
        map.put("barangay", order.getBarangay());
        map.put("city", order.getCity());
        map.put("province", order.getProvince());
        map.put("postal_code", order.getPostalCode());
        map.put("payment_method", order.getPaymentMethod());
        map.put("total", order.getTotal());
        map.put("status", order.getStatus());
        map.put("created_at", formatDate(order));
        if (order.getReceiptPath() != null && !order.getReceiptPath().isBlank()) {
            map.put("receipt_url", buildUrl(baseUrl, order.getReceiptPath()));
        }
        if (items != null) {
            List<Map<String, Object>> itemMaps = new ArrayList<>();
            for (OrderItem item : items) {
                Map<String, Object> im = new HashMap<>();
                im.put("id", item.getId());
                im.put("order_id", item.getOrderId());
                im.put("product_id", item.getProductId());
                im.put("qty", item.getQty());
                im.put("price", item.getPrice());
                im.put("subtotal", item.getSubtotal());
                itemMaps.add(im);
            }
            map.put("items", itemMaps);
        }
        return map;
    }

    private static String buildAddress(Order order) {
        StringBuilder sb = new StringBuilder();
        if (order.getAddressLine() != null) sb.append(order.getAddressLine());
        if (order.getBarangay() != null && !order.getBarangay().isBlank()) sb.append(", ").append(order.getBarangay());
        if (order.getCity() != null && !order.getCity().isBlank()) sb.append(", ").append(order.getCity());
        if (order.getProvince() != null && !order.getProvince().isBlank()) sb.append(", ").append(order.getProvince());
        if (order.getPostalCode() != null && !order.getPostalCode().isBlank()) sb.append(" ").append(order.getPostalCode());
        return sb.toString().trim();
    }

    private static String formatDate(Order order) {
        if (order.getCreatedAt() == null) return "";
        return order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String buildUrl(String baseUrl, String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) return path;
        String cleaned = path.startsWith("/") ? path.substring(1) : path;
        return baseUrl + "/" + cleaned;
    }
}
