//package uz.app.bot;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/webhook")
//public class WebhookController {
//
//    private final TelegramBotService telegramBotService;
//
//    @PostMapping
//    public void onUpdateReceived(@RequestBody Update update) {
//        telegramBotService.onWebhookUpdateReceived(update);
//    }
//}
