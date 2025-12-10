package com.unicovoit.views.booking;

import com.unicovoit.entity.Booking;
import com.unicovoit.entity.BookingStatus;
import com.unicovoit.service.BookingService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "booking-requests", layout = MainLayout.class)
@PageTitle("Demandes de réservation | UniCovoit")
public class BookingRequestsView extends VerticalLayout {

    private final BookingService bookingService;
    private final Grid<Booking> grid = new Grid<>(Booking.class, false);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public BookingRequestsView(BookingService bookingService) {
        this.bookingService = bookingService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createGridSection());
        loadBookingRequests();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Div titleSection = new Div();
        H2 title = new H2("Demandes de réservation");
        title.addClassName(LumoUtility.Margin.NONE);
        Paragraph subtitle = new Paragraph("Gérez les réservations pour vos trajets");
        subtitle.addClassName(LumoUtility.TextColor.SECONDARY);
        titleSection.add(title, subtitle);

        Button myRidesButton = new Button("Mes trajets", VaadinIcon.CAR.create());
        myRidesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        myRidesButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("my-rides")));

        header.add(titleSection, myRidesButton);
        return header;
    }

    private VerticalLayout createGridSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSizeFull();
        section.setPadding(false);

        configureGrid();
        section.add(grid);

        return section;
    }

    private void configureGrid() {
        grid.addClassName("booking-requests-grid");
        grid.setSizeFull();

        grid.addColumn(booking ->
            booking.getRide().getDepartureCity() + " → " + booking.getRide().getArrivalCity())
                .setHeader("Trajet")
                .setSortable(true)
                .setFlexGrow(2);

        grid.addColumn(booking ->
            booking.getRide().getDepartureDateTime().format(DATE_FORMATTER))
                .setHeader("Date de départ")
                .setSortable(true)
                .setFlexGrow(1);

        grid.addColumn(booking ->
            booking.getPassenger().getFirstName() + " " +
            booking.getPassenger().getLastName())
                .setHeader("Passager")
                .setFlexGrow(1);

        grid.addColumn(booking -> booking.getPassenger().getUniversity())
                .setHeader("Université")
                .setFlexGrow(1);

        grid.addColumn(Booking::getSeatsBooked)
                .setHeader("Places")
                .setFlexGrow(0)
                .setWidth("100px");

        grid.addComponentColumn(this::createStatusBadge)
                .setHeader("Statut")
                .setFlexGrow(0)
                .setWidth("150px");

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Actions")
                .setFlexGrow(0)
                .setWidth("300px");

        grid.setEmptyStateText("Aucune demande de réservation");
    }

    private Span createStatusBadge(Booking booking) {
        Span badge = new Span();
        badge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "0.875rem")
                .set("font-weight", "500");

        switch (booking.getStatus()) {
            case PENDING:
                badge.setText("En attente");
                badge.getStyle()
                        .set("background-color", "var(--lumo-contrast-10pct)")
                        .set("color", "var(--lumo-contrast-90pct)");
                break;
            case CONFIRMED:
                badge.setText("Confirmée");
                badge.getStyle()
                        .set("background-color", "var(--lumo-success-color-10pct)")
                        .set("color", "var(--lumo-success-text-color)");
                break;
            case CANCELLED_BY_DRIVER:
                badge.setText("Refusée");
                badge.getStyle()
                        .set("background-color", "var(--lumo-error-color-10pct)")
                        .set("color", "var(--lumo-error-text-color)");
                break;
            case CANCELLED_BY_PASSENGER:
                badge.setText("Annulée (passager)");
                badge.getStyle()
                        .set("background-color", "var(--lumo-error-color-10pct)")
                        .set("color", "var(--lumo-error-text-color)");
                break;
        }

        return badge;
    }

    private HorizontalLayout createActionButtons(Booking booking) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button viewRideButton = new Button("Voir trajet", VaadinIcon.EYE.create());
        viewRideButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        viewRideButton.addClickListener(e -> viewRide(booking));

        Button contactButton = new Button("Contacter", VaadinIcon.ENVELOPE.create());
        contactButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        contactButton.addClickListener(e -> contactPassenger(booking));

        actions.add(viewRideButton, contactButton);

        if (booking.getStatus() == BookingStatus.PENDING) {
            Button confirmButton = new Button("Confirmer", VaadinIcon.CHECK.create());
            confirmButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
            confirmButton.addClickListener(e -> confirmBooking(booking));

            Button rejectButton = new Button("Refuser", VaadinIcon.CLOSE.create());
            rejectButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            rejectButton.addClickListener(e -> confirmRejectBooking(booking));

            actions.add(confirmButton, rejectButton);
        }

        return actions;
    }

    private void loadBookingRequests() {
        try {
            List<Booking> bookings = bookingService.getDriverBookings(SessionManager.getCurrentUserId());
            grid.setItems(bookings);
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement des demandes: " + ex.getMessage());
        }
    }

    private void viewRide(Booking booking) {
        getUI().ifPresent(ui -> ui.navigate("rides/" + booking.getRide().getId()));
    }

    private void contactPassenger(Booking booking) {
        getUI().ifPresent(ui -> ui.navigate("messages/" + booking.getPassenger().getId()));
    }

    private void confirmBooking(Booking booking) {
        try {
            bookingService.confirmBooking(booking.getId(), SessionManager.getCurrentUser());
            NotificationHelper.showSuccess("Réservation confirmée !");
            loadBookingRequests();
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur: " + ex.getMessage());
        }
    }

    private void confirmRejectBooking(Booking booking) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Refuser la réservation");
        dialog.setText(String.format(
                "Êtes-vous sûr de vouloir refuser la réservation de %s %s ?",
                booking.getPassenger().getFirstName(),
                booking.getPassenger().getLastName()
        ));

        dialog.setCancelable(true);
        dialog.setCancelText("Annuler");

        dialog.setConfirmText("Refuser");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> rejectBooking(booking));

        dialog.open();
    }

    private void rejectBooking(Booking booking) {
        try {
            bookingService.cancelBookingByDriver(booking.getId(), SessionManager.getCurrentUser());
            NotificationHelper.showSuccess("Réservation refusée");
            loadBookingRequests();
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur: " + ex.getMessage());
        }
    }
}
