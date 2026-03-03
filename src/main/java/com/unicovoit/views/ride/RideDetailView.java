package com.unicovoit.views.ride;

import com.unicovoit.dto.CreateBookingDto;
import com.unicovoit.entity.Ride;
import com.unicovoit.service.BookingService;
import com.unicovoit.service.MessageService;
import com.unicovoit.service.RideService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;

@Route(value = "rides/:id", layout = MainLayout.class)
@PageTitle("Détails du trajet | UniCovoit")
public class RideDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final RideService rideService;
    private final BookingService bookingService;
    private final MessageService messageService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Ride ride;
    private Long rideId;

    private final VerticalLayout contentContainer = new VerticalLayout();

    public RideDetailView(RideService rideService, BookingService bookingService, MessageService messageService) {
        this.rideService = rideService;
        this.bookingService = bookingService;
        this.messageService = messageService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(contentContainer);
    }

    private void loadAndDisplayRide() {
        try {
            ride = rideService.getRideById(rideId);
            displayRideDetails();
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement du trajet: " + ex.getMessage());
            getUI().ifPresent(ui -> ui.navigate("search"));
        }
    }

    private void displayRideDetails() {
        contentContainer.removeAll();

        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        card.setWidth("900px");
        card.getStyle().set("padding", "var(--lumo-space-xl)");

        // Header with back button
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Button backButton = new Button("Retour", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.getPage().getHistory().back()));

        header.add(backButton);

        // Route title
        H2 routeTitle = new H2(ride.getDepartureCity() + " → " + ride.getArrivalCity());
        routeTitle.addClassName(LumoUtility.Margin.Top.SMALL);

        // Date and status
        HorizontalLayout dateStatus = new HorizontalLayout();
        Span date = new Span(ride.getDepartureDateTime().format(DATE_FORMATTER));
        date.addClassName(LumoUtility.TextColor.SECONDARY);

        Span statusBadge = createStatusBadge();
        dateStatus.add(date, statusBadge);
        dateStatus.setAlignItems(Alignment.CENTER);

        // Driver info section
        Div driverSection = createDriverSection();

        // Ride details section
        Div detailsSection = createDetailsSection();

        // Vehicle info section
        Div vehicleSection = createVehicleSection();

        // Description
        if (ride.getDescription() != null && !ride.getDescription().isBlank()) {
            H4 descTitle = new H4("Description");
            Paragraph descText = new Paragraph(ride.getDescription());
            descText.addClassName(LumoUtility.TextColor.SECONDARY);
            card.add(descTitle, descText);
        }

        // Preferences
        Div preferencesSection = createPreferencesSection();

        // Action buttons
        HorizontalLayout actions = createActionButtons();

        card.add(header, routeTitle, dateStatus, new Hr(), driverSection, new Hr(),
                detailsSection, vehicleSection, preferencesSection, new Hr(), actions);

        contentContainer.add(card);
        contentContainer.setAlignItems(Alignment.CENTER);
    }

    private Span createStatusBadge() {
        Span badge = new Span();
        badge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "0.875rem")
                .set("font-weight", "500");

        switch (ride.getStatus()) {
            case PUBLISHED:
                badge.setText("Disponible");
                badge.getStyle()
                        .set("background-color", "var(--lumo-success-color-10pct)")
                        .set("color", "var(--lumo-success-text-color)");
                break;
            case CANCELLED:
                badge.setText("Annulé");
                badge.getStyle()
                        .set("background-color", "var(--lumo-error-color-10pct)")
                        .set("color", "var(--lumo-error-text-color)");
                break;
            case COMPLETED:
                badge.setText("Terminé");
                badge.getStyle()
                        .set("background-color", "var(--lumo-contrast-10pct)")
                        .set("color", "var(--lumo-contrast-70pct)");
                break;
        }

        return badge;
    }

    private Div createDriverSection() {
        Div section = new Div();

        H4 title = new H4("Conducteur");
        title.addClassName(LumoUtility.Margin.Bottom.SMALL);

        HorizontalLayout driverInfo = new HorizontalLayout();
        driverInfo.setAlignItems(Alignment.CENTER);

        Icon userIcon = VaadinIcon.USER.create();
        userIcon.setSize("32px");
        userIcon.addClassName(LumoUtility.TextColor.PRIMARY);

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        Span name = new Span(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        name.getStyle().set("font-weight", "500");

        Span university = new Span(ride.getDriver().getUniversity());
        university.addClassName(LumoUtility.TextColor.SECONDARY);

        info.add(name, university);
        driverInfo.add(userIcon, info);

        section.add(title, driverInfo);
        return section;
    }

    private Div createDetailsSection() {
        Div section = new Div();

        H4 title = new H4("Détails du trajet");

        HorizontalLayout details = new HorizontalLayout();
        details.setWidthFull();
        details.addClassName(LumoUtility.Gap.XLARGE);

        details.add(
            createDetailItem(VaadinIcon.EURO, "Prix par place", ride.getPricePerSeat() + " €"),
            createDetailItem(VaadinIcon.USERS, "Places disponibles", ride.getSeatsAvailable() + "/" + ride.getSeatsTotal()),
            createDetailItem(VaadinIcon.CLOCK, "Durée", ride.getDurationMinutes() != null ? ride.getDurationMinutes() + " min" : "Non spécifié")
        );

        section.add(title, details);
        return section;
    }

    private Div createVehicleSection() {
        Div section = new Div();

        H4 title = new H4("Véhicule");

        HorizontalLayout vehicleInfo = new HorizontalLayout();
        vehicleInfo.setAlignItems(Alignment.CENTER);

        Icon carIcon = VaadinIcon.CAR.create();
        carIcon.setSize("32px");
        carIcon.addClassName(LumoUtility.TextColor.PRIMARY);

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        Span model = new Span(ride.getVehicle().getBrand() + " " + ride.getVehicle().getModel());
        model.getStyle().set("font-weight", "500");

        Span details = new Span(
            (ride.getVehicle().getColor() != null ? ride.getVehicle().getColor() + " - " : "") +
            (ride.getVehicle().getPlateNumber() != null ? ride.getVehicle().getPlateNumber() : "")
        );
        details.addClassName(LumoUtility.TextColor.SECONDARY);

        info.add(model, details);
        vehicleInfo.add(carIcon, info);

        section.add(title, vehicleInfo);
        return section;
    }

    private Div createDetailItem(VaadinIcon icon, String label, String value) {
        Div item = new Div();
        item.getStyle().set("flex", "1");

        Icon iconComponent = icon.create();
        iconComponent.addClassName(LumoUtility.TextColor.PRIMARY);
        iconComponent.getStyle().set("margin-right", "8px");

        Span labelSpan = new Span(label);
        labelSpan.addClassName(LumoUtility.TextColor.SECONDARY);
        labelSpan.getStyle().set("font-size", "0.875rem");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-weight", "500");

        HorizontalLayout iconLabel = new HorizontalLayout(iconComponent, labelSpan);
        iconLabel.setAlignItems(Alignment.CENTER);
        iconLabel.setSpacing(false);

        item.add(iconLabel, valueSpan);
        return item;
    }

    private Div createPreferencesSection() {
        Div section = new Div();

        H4 title = new H4("Préférences");

        HorizontalLayout preferences = new HorizontalLayout();
        preferences.addClassName(LumoUtility.Gap.LARGE);

        preferences.add(
            createPreferenceItem(VaadinIcon.MUSIC, "Musique", ride.isMusicEnabled()),
            createPreferenceItem(VaadinIcon.DOG, "Animaux", ride.isPetsAllowed()),
            createPreferenceItem(VaadinIcon.BAN, "Fumeur", ride.isSmokingAllowed())
        );

        section.add(title, preferences);
        return section;
    }

    private Div createPreferenceItem(VaadinIcon icon, String label, boolean allowed) {
        Div item = new Div();

        Icon iconComponent = icon.create();
        iconComponent.getStyle().set("margin-right", "8px");

        if (allowed) {
            iconComponent.addClassName(LumoUtility.TextColor.SUCCESS);
        } else {
            iconComponent.addClassName(LumoUtility.TextColor.ERROR);
        }

        Span text = new Span(label + ": " + (allowed ? "Oui" : "Non"));

        HorizontalLayout layout = new HorizontalLayout(iconComponent, text);
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(false);

        item.add(layout);
        return item;
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.END);
        actions.addClassName(LumoUtility.Gap.MEDIUM);

        // Check if user is the driver
        boolean isDriver = ride.getDriver().getId().equals(SessionManager.getCurrentUserId());

        if (!isDriver && ride.getSeatsAvailable() > 0 && ride.getStatus().toString().equals("PUBLISHED")) {
            // Show booking button for passengers
            Button bookButton = new Button("Réserver", VaadinIcon.TICKET.create());
            bookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
            bookButton.addClickListener(e -> showBookingDialog());
            actions.add(bookButton);
        }

        // Message driver button (if not driver)
        if (!isDriver) {
            Button messageButton = new Button("Contacter", VaadinIcon.ENVELOPE.create());
            messageButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
            messageButton.addClickListener(e -> contactDriver());
            actions.add(messageButton);
        }

        return actions;
    }

    private void showBookingDialog() {
        // Create booking dialog
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Réserver des places");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);

        Paragraph info = new Paragraph("Combien de places souhaitez-vous réserver ?");

        IntegerField seatsField = new IntegerField("Nombre de places");
        seatsField.setValue(1);
        seatsField.setMin(1);
        seatsField.setMax(ride.getSeatsAvailable());
        seatsField.setStepButtonsVisible(true);
        seatsField.setWidthFull();

        content.add(info, seatsField);
        dialog.add(content);

        Button cancelButton = new Button("Annuler", e -> dialog.close());
        Button confirmButton = new Button("Confirmer la réservation", e -> {
            createBooking(seatsField.getValue());
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    private void createBooking(Integer seats) {
        try {
            CreateBookingDto dto = new CreateBookingDto();
            dto.setRideId(ride.getId());
            dto.setSeatsBooked(seats);

            bookingService.createBooking(dto, SessionManager.getCurrentUser());
            NotificationHelper.showSuccess("Réservation effectuée ! En attente de confirmation du conducteur.");

            // Reload ride to update available seats
            loadAndDisplayRide();

        } catch (Exception ex) {
            NotificationHelper.showError(ex.getMessage());
        }
    }

    private void contactDriver() {
        // Navigate to conversation with driver
        getUI().ifPresent(ui -> ui.navigate("messages/" + ride.getDriver().getId()));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessionManager.isLoggedIn()) {
            event.forwardTo("login");
            return;
        }

        event.getRouteParameters().get("id").ifPresent(id -> {
            try {
                rideId = Long.parseLong(id);
                loadAndDisplayRide();
            } catch (NumberFormatException ex) {
                NotificationHelper.showError("ID de trajet invalide");
                event.forwardTo("search");
            }
        });
    }
}
