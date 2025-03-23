//package uz.app.bot;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.bots.TelegramWebhookBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import uz.app.entity.User;
//import uz.app.repository.UserRepository;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class TelegramBotService extends TelegramWebhookBot {
//
//    private final UserRepository userRepository;
//
//    @Value("${telegram.bot.username}")
//    private String botUsername;
//
//    @Value("${telegram.bot.token}")
//    private String botToken;
//
//    @Value("${telegram.bot.webhook}")
//    private String botWebhookUrl;
//
//    public void registerWebhook() {
//        try {
//            SetWebhook setWebhook = SetWebhook.builder().url(botWebhookUrl).build();
//            this.setWebhook(setWebhook);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @Override
//    public String getBotPath() {
//        return "/webhook";
//    }
//
//    @Override
//    public Update onWebhookUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            handleUpdate(update);
//        }
//        return update;
//    }
//
//    private void handleUpdate(Update update) {
//        Long chatId = update.getMessage().getChatId();
//        String text = update.getMessage().getText();
//        Integer telegramUserId = Math.toIntExact(update.getMessage().getFrom().getId());
//
//        if (text.equalsIgnoreCase("/start")) {
//            handleStartCommand(chatId, telegramUserId);
//        }
//    }
//
//    private void handleStartCommand(Long chatId, Integer telegramUserId) {
//        Optional<User> userOptional = userRepository.findByPhoneNumber(String.valueOf(telegramUserId));
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            String response = "👋 Hello " + user.getFirstName() + "!\nYour phone number: " + user.getPhoneNumber();
//            sendMessage(chatId, response);
//        } else {
//            sendMessage(chatId, "User not found. Please register first.");
//        }
//    }
//
//    private void sendMessage(Long chatId, String text) {
//        SendMessage message = SendMessage.builder()
//                .chatId(chatId.toString())
//                .text(text)
//                .build();
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//}
