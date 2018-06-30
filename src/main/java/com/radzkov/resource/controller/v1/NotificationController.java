package com.radzkov.resource.controller.v1;

import com.radzkov.resource.dto.TypeQuery;
import com.radzkov.resource.entity.NotificationItem;
import com.radzkov.resource.entity.NotificationType;
import com.radzkov.resource.repository.NotificationItemRepository;
import com.radzkov.resource.repository.NotificationTypeRepository;
import com.radzkov.resource.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Radzkov Andrey
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class NotificationController {

    private final NotificationItemRepository notificationItemRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserRepository userRepository;

    @PutMapping("/add-notification")
    public void addNotification(@RequestBody @Valid TypeQuery type, @AuthenticationPrincipal String username) {
        NotificationType notificationType = notificationTypeRepository.findByName(type.getType());
        NotificationItem notification = new NotificationItem();
        notification.setType(notificationType);
        notification.setOwner(userRepository.findUserByUsername(username));
        notification.setReceiver(userRepository.findUserByUsername(type.getReceiver()));
        notificationItemRepository.save(notification);
    }

    @GetMapping("/all-types")
    public Iterable<NotificationType> getAllTypesWithCount() {
        return notificationTypeRepository.findAll();
    }

}
