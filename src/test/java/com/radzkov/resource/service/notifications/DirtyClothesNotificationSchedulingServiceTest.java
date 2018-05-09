package com.radzkov.resource.service.notifications;

import com.google.common.collect.ArrayListMultimap;
import com.radzkov.resource.repository.SubscriptionRepository;
import com.radzkov.resource.repository.UserRepository;
import de.bytefish.fcmjava.client.FcmClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;

/**
 * @author Radzkov Andrey
 */
@RunWith(MockitoJUnitRunner.class)
public class DirtyClothesNotificationSchedulingServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private DirtyClothesNotificationGroupingService notificationGroupingService;
    @Mock
    private FcmClient fcmClient;

    @InjectMocks
    private DirtyClothesNotificationSchedulingService notificationService;

    @Before
    public void setUpClass() throws NoSuchFieldException {
        Field fcmClientField = DirtyClothesNotificationSchedulingService.class.getDeclaredField("fcmClient");
        fcmClientField.setAccessible(true);
        ReflectionUtils.setField(fcmClientField, notificationService, fcmClient);

        Field dirtyClothesNotificationEnabledField = DirtyClothesNotificationSchedulingService.class.getDeclaredField("dirtyClothesNotificationEnabled");
        dirtyClothesNotificationEnabledField.setAccessible(true);
        ReflectionUtils.setField(dirtyClothesNotificationEnabledField, notificationService, true);

    }

    @Test
    public void invokeIfEnabled() throws NoSuchFieldException {
        Mockito.when(notificationGroupingService.findAllSendersForEachReceiver(any()))
                .thenReturn(ArrayListMultimap.create());
        Mockito.when(notificationGroupingService.groupByDirtyClothesTypes(any()))
                .thenReturn(ArrayListMultimap.create());

        notificationService.notifyAboutDirtyClothes();
        Mockito.verify(userRepository, Mockito.times(1)).findAllByUserOptionsReceiverIsTrue();
    }

    @Test
    public void notInvokedIfDisabled() throws NoSuchFieldException {

        Field dirtyClothesNotificationEnabledField = DirtyClothesNotificationSchedulingService.class.getDeclaredField("dirtyClothesNotificationEnabled");
        dirtyClothesNotificationEnabledField.setAccessible(true);
        ReflectionUtils.setField(dirtyClothesNotificationEnabledField, notificationService, false);
        try {
            notificationService.notifyAboutDirtyClothes();
            Mockito.verify(userRepository, Mockito.times(0)).findAllByUserOptionsReceiverIsTrue();
        } finally {
            ReflectionUtils.setField(dirtyClothesNotificationEnabledField, notificationService, true);
        }
    }

}
