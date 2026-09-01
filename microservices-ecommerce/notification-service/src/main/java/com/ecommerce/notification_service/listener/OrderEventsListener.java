package com.ecommerce.notification_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
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
public void handleOrderConfirmedEvent(OrderConfirmedEvent event) {

    log.info("Evento recibido para orden {}", event.orderNumber());

    try {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("pedidos@ecommerce.com");
        message.setTo(event.email());
        message.setSubject("Orden Confirmada - " + event.orderNumber());
        message.setText("Hola!\n\n" +
                "Tu pedido con número " + event.orderNumber() + " ha sido recibido exitosamente.\n" +
                "Pronto recibirás más noticias sobre el envío.\n\n" +
                "Gracias por comprar con nosotros!");
        mailSender.send(message);

        // log.info("✅ Correo enviado exitosamente a: {}", event.email());
        log.info("✅ Correo enviado a {} para la orden {}", event.email(), event.orderNumber());
    } catch (Exception e) {
        log.error("❌ Error al enviar correo: {}", e.getMessage());
    }
}


  @RabbitListener(queues = "notification-queue")   
 public void handleOrderCancelledEvent(OrderCancelledEvent event) {

    log.info("Evento recibido para orden {}", event.orderNumber());

    try {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("pedidos@ecommerce.com");
        message.setTo(event.email());
        message.setSubject("Orden Cancelada - " + event.orderNumber());
        message.setText("Hola!\n\n" +
                "Tu pedido con número " + event.orderNumber() + " ha sido recibido exitosamente.\n" +
                "Pronto recibirás más noticias sobre el envío.\n\n" +
                "Gracias por comprar con nosotros!");
        mailSender.send(message);

        // log.info("✅ Correo enviado exitosamente a: {}", event.email());
        log.info("✅ Correo enviado a {} para la orden {}", event.email(), event.orderNumber());
    } catch (Exception e) {
        log.error("❌ Error al enviar correo: {}", e.getMessage());
    }
 }

 /**                                                                     
                                                                                
     .:::.     .:::              :::.             :       :.       :.           
    .=====    .=====            .=   :=.          =       =:       :.   -       
    .=====    .=====            .=    --   :::    = :::   =: ::.   :. :-=::.    
    .=====    .=====            .=---=:  .=   =.  ==  :=. ==.  --  =:  .=       
    .=====    .=====            .=   --    :--=.  =    =. =:   --  =:  .=       
    .=====    .=====            .=    =: .=   =.  =    =. =:   --  =:  .=       
    .=========================  .=    -- .=====.  :====.   -====   =:   -==:    
    .=========================                                                  
    .=========================  .::      .:.    ::::-                           
    .===============     =====  .:.:     ::.   :    .:.                         
    .===============     =====  .: :.   :.:.  :.     -:                         
    .===============-....=====  .:  :  .: :.  :.     :-                         
    .=========================  .:  .: :  :.  ::     :.                         
    .=========================  .:   ::.  :.   ::   ::                          
     .:::::::::::::::::::::::.   .    .   ..     .......                                                                       
  */

}
