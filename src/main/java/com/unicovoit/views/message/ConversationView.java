package com.unicovoit.views.message;

import com.unicovoit.dto.SendMessageDto;
import com.unicovoit.entity.Message;
import com.unicovoit.entity.UserAccount;
import com.unicovoit.service.MessageService;
import com.unicovoit.service.UserService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "messages/:partnerId", layout = MainLayout.class)
@PageTitle("Conversation | UniCovoit")
public class ConversationView extends VerticalLayout implements BeforeEnterObserver {

    private final MessageService messageService;
    private final UserService userService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private Long partnerId;
    private UserAccount partner;

    private final VerticalLayout messagesContainer = new VerticalLayout();
    private final TextArea messageInput = new TextArea();
    private final Button sendButton = new Button("Envoyer");

    public ConversationView(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    private void initializeView() {
        removeAll();

        add(createHeader(), createMessagesSection(), createInputSection());
        loadConversation();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.setAlignItems(Alignment.CENTER);
        header.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.SMALL);

        Button backButton = new Button(VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("messages")));

        VerticalLayout partnerInfo = new VerticalLayout();
        partnerInfo.setPadding(false);
        partnerInfo.setSpacing(false);

        H3 name = new H3(partner.getFirstName() + " " + partner.getLastName());
        name.addClassName(LumoUtility.Margin.NONE);

        Span university = new Span(partner.getUniversity());
        university.addClassName(LumoUtility.TextColor.SECONDARY);
        university.getStyle().set("font-size", "0.875rem");

        partnerInfo.add(name, university);

        header.add(backButton, partnerInfo);
        return header;
    }

    private VerticalLayout createMessagesSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSizeFull();
        section.setPadding(true);
        section.setSpacing(true);
        section.addClassName(LumoUtility.Background.CONTRAST_5);
        section.getStyle()
                .set("overflow-y", "auto")
                .set("flex", "1");

        messagesContainer.setPadding(false);
        messagesContainer.setSpacing(true);
        messagesContainer.setWidthFull();

        section.add(messagesContainer);
        return section;
    }

    private HorizontalLayout createInputSection() {
        HorizontalLayout section = new HorizontalLayout();
        section.setWidthFull();
        section.setPadding(true);
        section.setSpacing(true);
        section.setAlignItems(Alignment.END);
        section.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.SMALL);

        messageInput.setPlaceholder("Écrivez votre message...");
        messageInput.setWidthFull();
        messageInput.setMaxLength(5000);
        messageInput.getStyle()
                .set("min-height", "60px")
                .set("max-height", "120px");

        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendButton.setIcon(VaadinIcon.PAPERPLANE.create());
        sendButton.addClickShortcut(Key.ENTER);
        sendButton.addClickListener(e -> sendMessage());

        section.add(messageInput, sendButton);
        section.expand(messageInput);

        return section;
    }

    private void loadConversation() {
        try {
            List<Message> messages = messageService.getConversation(
                    SessionManager.getCurrentUserId(),
                    partnerId
            );

            // Mark messages as read
            messageService.markConversationAsRead(partnerId, SessionManager.getCurrentUser());

            messagesContainer.removeAll();

            if (messages.isEmpty()) {
                Div emptyState = new Div();
                emptyState.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Padding.LARGE);

                Paragraph text = new Paragraph("Aucun message pour le moment. Envoyez le premier message !");
                text.addClassName(LumoUtility.TextColor.SECONDARY);

                emptyState.add(text);
                messagesContainer.add(emptyState);
            } else {
                for (Message message : messages) {
                    messagesContainer.add(createMessageBubble(message));
                }

                // Scroll to bottom
                getElement().executeJs("setTimeout(() => { " +
                    "const container = $0.querySelector('[style*=\"overflow-y\"]'); " +
                    "if (container) container.scrollTop = container.scrollHeight; " +
                    "}, 100);", getElement());
            }

        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement de la conversation: " + ex.getMessage());
        }
    }

    private Div createMessageBubble(Message message) {
        boolean isSentByMe = message.getSender().getId().equals(SessionManager.getCurrentUserId());

        Div bubbleContainer = new Div();
        bubbleContainer.setWidthFull();
        bubbleContainer.getStyle().set("display", "flex");
        bubbleContainer.getStyle().set("justify-content", isSentByMe ? "flex-end" : "flex-start");

        Div bubble = new Div();
        bubble.getStyle()
                .set("max-width", "70%")
                .set("padding", "12px 16px")
                .set("border-radius", "16px")
                .set("margin-bottom", "4px");

        if (isSentByMe) {
            bubble.addClassNames(LumoUtility.Background.PRIMARY, LumoUtility.TextColor.PRIMARY_CONTRAST);
        } else {
            bubble.addClassNames(LumoUtility.Background.BASE);
            bubble.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        }

        Paragraph content = new Paragraph(message.getContent());
        content.addClassName(LumoUtility.Margin.NONE);
        content.getStyle().set("word-wrap", "break-word");

        Span time = new Span(message.getSentAt().format(TIME_FORMATTER));
        time.getStyle()
                .set("font-size", "0.75rem")
                .set("opacity", "0.7")
                .set("display", "block")
                .set("text-align", "right")
                .set("margin-top", "4px");

        bubble.add(content, time);
        bubbleContainer.add(bubble);

        return bubbleContainer;
    }

    private void sendMessage() {
        String content = messageInput.getValue();

        if (content == null || content.trim().isEmpty()) {
            NotificationHelper.showWarning("Le message ne peut pas être vide");
            return;
        }

        sendButton.setEnabled(false);
        sendButton.setText("Envoi...");

        try {
            SendMessageDto dto = new SendMessageDto();
            dto.setReceiverId(partnerId);
            dto.setContent(content.trim());

            messageService.sendMessage(dto, SessionManager.getCurrentUser());

            messageInput.clear();
            loadConversation();

            sendButton.setEnabled(true);
            sendButton.setText("Envoyer");

        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors de l'envoi: " + ex.getMessage());
            sendButton.setEnabled(true);
            sendButton.setText("Envoyer");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessionManager.isLoggedIn()) {
            event.forwardTo("login");
            return;
        }

        event.getRouteParameters().get("partnerId").ifPresent(id -> {
            try {
                partnerId = Long.parseLong(id);
                partner = userService.getUserById(partnerId);
                initializeView();
            } catch (Exception ex) {
                NotificationHelper.showError("Utilisateur introuvable");
                event.forwardTo("messages");
            }
        });
    }
}
