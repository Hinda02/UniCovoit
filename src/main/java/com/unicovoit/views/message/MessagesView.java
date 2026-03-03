package com.unicovoit.views.message;

import com.unicovoit.entity.Message;
import com.unicovoit.service.MessageService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "messages", layout = MainLayout.class)
@PageTitle("Messages | UniCovoit")
public class MessagesView extends VerticalLayout {

    private final MessageService messageService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VerticalLayout conversationsList = new VerticalLayout();

    public MessagesView(MessageService messageService) {
        this.messageService = messageService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createMessagesCard());
        loadConversations();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();

        Div titleSection = new Div();
        H2 title = new H2("Messages");
        title.addClassName(LumoUtility.Margin.NONE);
        Paragraph subtitle = new Paragraph("Vos conversations");
        subtitle.addClassName(LumoUtility.TextColor.SECONDARY);
        titleSection.add(title, subtitle);

        header.add(titleSection);
        return header;
    }

    private Div createMessagesCard() {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.MEDIUM, LumoUtility.BorderRadius.LARGE);
        card.setSizeFull();
        card.getStyle().set("padding", "var(--lumo-space-l)");

        conversationsList.setPadding(false);
        conversationsList.setSpacing(false);

        card.add(conversationsList);
        return card;
    }

    private void loadConversations() {
        try {
            Long currentUserId = SessionManager.getCurrentUserId();

            // Get all sent and received messages
            List<Message> sentMessages = messageService.getSentMessages(currentUserId);
            List<Message> receivedMessages = messageService.getReceivedMessages(currentUserId);

            // Combine and group by conversation partner
            Map<Long, List<Message>> conversations = new HashMap<>();

            for (Message msg : sentMessages) {
                Long partnerId = msg.getReceiver().getId();
                conversations.computeIfAbsent(partnerId, k -> new ArrayList<>()).add(msg);
            }

            for (Message msg : receivedMessages) {
                Long partnerId = msg.getSender().getId();
                conversations.computeIfAbsent(partnerId, k -> new ArrayList<>()).add(msg);
            }

            // Sort conversations by most recent message
            List<Map.Entry<Long, List<Message>>> sortedConversations = conversations.entrySet().stream()
                    .sorted((e1, e2) -> {
                        Message latest1 = e1.getValue().stream()
                                .max(Comparator.comparing(Message::getSentAt))
                                .orElse(null);
                        Message latest2 = e2.getValue().stream()
                                .max(Comparator.comparing(Message::getSentAt))
                                .orElse(null);

                        if (latest1 == null || latest2 == null) return 0;
                        return latest2.getSentAt().compareTo(latest1.getSentAt());
                    })
                    .collect(Collectors.toList());

            conversationsList.removeAll();

            if (sortedConversations.isEmpty()) {
                conversationsList.add(createEmptyState());
            } else {
                for (Map.Entry<Long, List<Message>> entry : sortedConversations) {
                    Long partnerId = entry.getKey();
                    List<Message> messages = entry.getValue();
                    conversationsList.add(createConversationCard(partnerId, messages));
                }
            }

        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement des messages: " + ex.getMessage());
        }
    }

    private Div createEmptyState() {
        Div emptyState = new Div();
        emptyState.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Padding.XLARGE);

        Icon icon = VaadinIcon.ENVELOPE_O.create();
        icon.setSize("64px");
        icon.addClassName(LumoUtility.TextColor.SECONDARY);

        H3 title = new H3("Aucun message");
        Paragraph text = new Paragraph("Commencez une conversation en contactant un conducteur");
        text.addClassName(LumoUtility.TextColor.SECONDARY);

        emptyState.add(icon, title, text);
        return emptyState;
    }

    private Div createConversationCard(Long partnerId, List<Message> messages) {
        Div card = new Div();
        card.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BorderRadius.MEDIUM);
        card.getStyle()
                .set("cursor", "pointer")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("transition", "background-color 0.2s");

        card.getElement().addEventListener("mouseenter", e ->
            card.getStyle().set("background-color", "var(--lumo-contrast-5pct)")
        );
        card.getElement().addEventListener("mouseleave", e ->
            card.getStyle().remove("background-color")
        );

        card.addClickListener(e -> openConversation(partnerId));

        // Get the latest message
        Message latestMessage = messages.stream()
                .max(Comparator.comparing(Message::getSentAt))
                .orElse(null);

        if (latestMessage == null) return card;

        // Determine partner name
        String partnerName;
        if (latestMessage.getSender().getId().equals(SessionManager.getCurrentUserId())) {
            partnerName = latestMessage.getReceiver().getFirstName() + " " +
                         latestMessage.getReceiver().getLastName();
        } else {
            partnerName = latestMessage.getSender().getFirstName() + " " +
                         latestMessage.getSender().getLastName();
        }

        // Count unread messages from this partner
        long unreadCount = messages.stream()
                .filter(m -> m.getReceiver().getId().equals(SessionManager.getCurrentUserId()))
                .filter(m -> !m.isRead())
                .count();

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);

        // Avatar icon
        Icon avatar = VaadinIcon.USER.create();
        avatar.setSize("40px");
        avatar.addClassName(LumoUtility.TextColor.PRIMARY);

        // Content
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle().set("flex", "1");

        HorizontalLayout nameRow = new HorizontalLayout();
        nameRow.setWidthFull();
        nameRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

        H4 name = new H4(partnerName);
        name.addClassName(LumoUtility.Margin.NONE);

        Span time = new Span(latestMessage.getSentAt().format(DATE_FORMATTER));
        time.addClassName(LumoUtility.TextColor.SECONDARY);
        time.getStyle().set("font-size", "0.875rem");

        nameRow.add(name, time);

        Paragraph preview = new Paragraph(
            (latestMessage.getSender().getId().equals(SessionManager.getCurrentUserId()) ? "Vous: " : "") +
            (latestMessage.getContent().length() > 50 ?
                latestMessage.getContent().substring(0, 50) + "..." :
                latestMessage.getContent())
        );
        preview.addClassName(LumoUtility.TextColor.SECONDARY);
        preview.getStyle().set("font-size", "0.875rem");
        preview.addClassName(LumoUtility.Margin.NONE);

        content.add(nameRow, preview);

        layout.add(avatar, content);

        // Unread badge
        if (unreadCount > 0) {
            Span badge = new Span(String.valueOf(unreadCount));
            badge.getStyle()
                    .set("background-color", "var(--lumo-primary-color)")
                    .set("color", "var(--lumo-primary-contrast-color)")
                    .set("border-radius", "50%")
                    .set("padding", "4px 8px")
                    .set("font-size", "0.75rem")
                    .set("font-weight", "bold")
                    .set("min-width", "24px")
                    .set("text-align", "center");
            layout.add(badge);
        }

        card.add(layout);
        return card;
    }

    private void openConversation(Long partnerId) {
        getUI().ifPresent(ui -> ui.navigate("messages/" + partnerId));
    }
}
