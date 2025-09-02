package com.food.ordering.system.consulta.adapter.in.rest;

import com.food.ordering.system.consulta.adapter.out.dataaccess.entity.OrderOutboxEntity;
import com.food.ordering.system.consulta.adapter.out.dataaccess.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
@Slf4j
public class ConsultaController {

    private final OrderOutboxRepository orderOutboxRepository;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderOutboxEntity> getOrderById(@PathVariable UUID orderId) {
        log.info("Consulting order with ID: {}", orderId);
        
        return orderOutboxRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderOutboxEntity>> getAllOrders() {
        log.info("Consulting all orders");
        
        List<OrderOutboxEntity> orders = orderOutboxRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/aggregate/{aggregateId}")
    public ResponseEntity<List<OrderOutboxEntity>> getOrdersByAggregateId(@PathVariable UUID aggregateId) {
        log.info("Consulting orders by aggregate ID: {}", aggregateId);
        
        List<OrderOutboxEntity> orders = orderOutboxRepository.findByAggregateId(aggregateId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/trackId/{tackingId}")
    public ResponseEntity<OrderOutboxEntity> getOrderByTracking(@PathVariable UUID tackingId){
        return orderOutboxRepository.findByTrackingId(tackingId)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        var healthResponse = new java.util.HashMap<String, Object>();
        healthResponse.put("status", "UP");
        healthResponse.put("service", "consulta-service");
        healthResponse.put("timestamp", java.time.LocalDateTime.now());
        healthResponse.put("version", "1.0.0");
        
        return ResponseEntity.ok(healthResponse);
    }

    @GetMapping("/swagger-ui/index.html")
    public ResponseEntity<String> swaggerUI() {
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body("""
                    <!DOCTYPE html>
                    <html lang="en">
                      <head>
                        <meta charset="UTF-8">
                        <title>Swagger UI - Consulta Service</title>
                        <style>
                          body { margin: 0; padding: 20px; font-family: Arial, sans-serif; }
                          .header { background: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px; }
                          .endpoint { background: #fff; border: 1px solid #ddd; border-radius: 5px; padding: 15px; margin-bottom: 15px; }
                          .method { display: inline-block; padding: 5px 10px; border-radius: 3px; color: white; font-weight: bold; margin-right: 10px; }
                          .get { background: #61affe; }
                          .post { background: #49cc90; }
                          .put { background: #fca130; }
                          .delete { background: #f93e3e; }
                          .url { font-family: monospace; background: #f8f9fa; padding: 5px; border-radius: 3px; }
                          .description { margin-top: 10px; color: #666; }
                        </style>
                      </head>
                      <body>
                        <div class="header">
                          <h1>🔄 Consulta Service API</h1>
                          <p>Documentación de la API del servicio de consulta</p>
                        </div>
                        
                        <div class="endpoint">
                          <span class="method get">GET</span>
                          <span class="url">/api/consultas/health</span>
                          <div class="description">Endpoint de salud del servicio</div>
                        </div>
                        
                        <div class="endpoint">
                          <span class="method get">GET</span>
                          <span class="url">/api/consultas/orders</span>
                          <div class="description">Obtener todas las órdenes</div>
                        </div>
                        
                        <div class="endpoint">
                          <span class="method get">GET</span>
                          <span class="url">/api/consultas/order/{orderId}</span>
                          <div class="description">Obtener una orden por ID</div>
                        </div>
                        
                        <div class="endpoint">
                          <span class="method get">GET</span>
                          <span class="url">/api/consultas/orders/aggregate/{aggregateId}</span>
                          <div class="description">Obtener órdenes por ID de agregado</div>
                        </div>
                        
                        <div class="endpoint">
                          <span class="method get">GET</span>
                          <span class="url">/api/consultas/trackId/{trackingId}</span>
                          <div class="description">Obtener una orden por ID de seguimiento</div>
                        </div>
                        
                        <div class="endpoint">
                          <span class="method get">GET</span>
                          <span class="url">/api/consultas/swagger-ui.html</span>
                          <div class="description">Acceder a Swagger UI completo (redirección)</div>
                        </div>
                      </body>
                    </html>
                    """);
    }
}
