package ru.itgroup.intouch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itgroup.intouch.annotation.Loggable;
import ru.itgroup.intouch.dto.request.NotificationSettingsDto;
import ru.itgroup.intouch.dto.response.settings.SettingsItemDto;
import ru.itgroup.intouch.service.NotificationSettingsService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${server.api.prefix}/notifications/settings")
public class NotificationSettingsController {
    private final NotificationSettingsService notificationSettingsService;

    @Loggable
    @GetMapping("")
    public List<SettingsItemDto> getSettings(@RequestParam Long userId)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return notificationSettingsService.getSettings(userId);
    }

    @Loggable
    @PutMapping("")
    public ResponseEntity<?> updateSettings(@RequestBody NotificationSettingsDto notificationSettingsDto,
                                            @RequestParam Long userId) {
        System.out.println(userId);
        notificationSettingsService.updateSettings(notificationSettingsDto, userId);
        return ResponseEntity.ok("");
    }
}
