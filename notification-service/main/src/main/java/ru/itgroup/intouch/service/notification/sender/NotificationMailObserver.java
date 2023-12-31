package ru.itgroup.intouch.service.notification.sender;

import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import model.Notification;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.itgroup.intouch.annotation.Loggable;
import ru.itgroup.intouch.contracts.service.notification.sender.NotificationObserver;
import ru.itgroup.intouch.repository.jooq.NotificationSettingRepository;
import ru.itgroup.intouch.service.EmailSender;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static ru.itgroup.intouch.tables.NotificationSettings.NOTIFICATION_SETTINGS;

@Service
@RequiredArgsConstructor
public class NotificationMailObserver implements NotificationObserver {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final NotificationSettingRepository notificationSettingRepository;
    private final MessageSource messageSource;
    private final EmailSender emailSender;

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }

    @Override
    public void send(@NotNull Notification notification) {
        boolean isEnableMailing = notificationSettingRepository
                .isEnable(notification.getReceiver().getId(), NOTIFICATION_SETTINGS.SEND_EMAIL_MESSAGE);

        if (isEnableMailing) {
            sendMail(notification);
        }
    }

    @Override
    public void send(List<Notification> notifications) {
        Map<Long, Boolean> mailReceiverIdList = getMailReceiverIdList(notifications);
        boolean isMailing;
        for (Notification notification : notifications) {
            isMailing = mailReceiverIdList.get(notification.getReceiver().getId()) == null ||
                    mailReceiverIdList.get(notification.getReceiver().getId());

            if (isMailing) {
                sendMail(notification);
            }
        }
    }

    private Map<Long, Boolean> getMailReceiverIdList(@NotNull List<Notification> notifications) {
        Set<Long> receiverIdList = notifications.stream()
                                                .map(notification -> notification.getReceiver().getId())
                                                .collect(Collectors.toSet());

        return notificationSettingRepository.getMailingSettingsMap(receiverIdList);
    }

    @Loggable
    private void sendMail(@NotNull Notification notification) {
        String to = notification.getReceiver().getEmail();
        String subject = messageSource.getMessage("notification.new", null, Locale.getDefault());

        executorService.execute(() -> {
            try {
                emailSender.send(to, subject, notification.getContent());
            } catch (MessagingException ignored) {
            }
        });
    }
}
