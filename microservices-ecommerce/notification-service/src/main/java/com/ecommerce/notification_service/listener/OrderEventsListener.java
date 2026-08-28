package com.ecommerce.notification_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.ecommerce.notification_service.event.OrderPlacedEvent;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderEventsListener {

    private final JavaMailSender mailSender;
// ocurre después de band order.confirmed desde rabbitmq
 @RabbitListener(queues = "notification-queue")   
 public void handleOrderConfirmedEvent(OrderPlacedEvent event) {

    log.info("Evento recibido para orden {}", event.orderNumber());

    try {
        StringBuilder body = new StringBuilder();
        body.append("Hola!\n\n");
        body.append("Tu pedido número ").append(event.orderNumber()).append(" ha sido recibido:\n\n");

        event.items().forEach(item ->
            body.append("- ").append(item.sku()).append(" x").append(item.quantity()).append("\n")
        );

        body.append("\nPronto recibirás más noticias sobre el envío.\n\n");
        body.append("Gracias por comprar con nosotros!");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("pedidos@ecommerce.com");
        message.setTo(event.email());
        message.setSubject("Orden confirmada - " + event.orderNumber());
        message.setText(body.toString());

        mailSender.send(message);
        log.info("✅ Correo enviado a {} para la orden {}", event.email(), event.orderNumber());
    } catch (Exception e) {
        log.error("❌ Error al enviar correo: {}", e.getMessage());
    }
 }

}
