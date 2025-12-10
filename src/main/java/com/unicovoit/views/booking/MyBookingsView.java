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

@Route(value = "my-bookings", layout = MainLayout.class)
@PageTitle("Mes réservations | UniCovoit")
public class MyBookingsView extends VerticalLayout {

    private final BookingService bookingService;
    private final Grid<Booking> grid = new Grid<>(Booking.class, false);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MyBookingsView(BookingService bookingService) {
        this.bookingService = bookingService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createGridSection());
        loadBookings();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Div titleSection = new Div();
        H2 title = new H2("Mes réservations");
        title.addClassName(LumoUtility.Margin.NONE);
        Paragraph subtitle = new Paragraph("Consultez vos réservations de trajets");
        subtitle.addClassName(LumoUtility.TextColor.SECONDARY);
        titleSection.add(title, subtitle);

        Button searchButton = new Button("Rechercher un trajet", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("search")));

        header.add(titleSection, searchButton);
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
        grid.addClassName("bookings-grid");
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
            booking.getRide().getDriver().getFirstName() + " " +
            booking.getRide().getDriver().getLastName())
                .setHeader("Conducteur")
                .setFlexGrow(1);

        grid.addColumn(Booking::getSeatsBooked)
                .setHeader("Places")
                .setFlexGrow(0)
                .setWidth("100px");

        grid.addColumn(booking -> booking.getRide().getPricePerSeat() + " €")
                .setHeader("Prix")
                .setFlexGrow(0)
                .setWidth("100px");

        grid.addComponentColumn(this::createStatusBadge)
                .setHeader("Statut")
                .setFlexGrow(0)
                .setWidth("150px");

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Actions")
                .setFlexGrow(0)
                .setWidth("250px");

        grid.setEmptyStateText("Aucune réservation");
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
                badge.setText("Annulée (conducteur)");
                badge.getStyle()
                        .set("background-color", "var(--lumo-error-color-10pct)")
                        .set("color", "var(--lumo-error-text-color)");
                break;
            case CANCELLED_BY_PASSENGER:
                badge.setText("Annulée");
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

        Button viewButton = new Button("Voir le trajet", VaadinIcon.EYE.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        viewButton.addClickListener(e -> viewRide(booking));

        actions.add(viewButton);

        if (booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.CONFIRMED) {
            Button cancelButton = new Button("Annuler", VaadinIcon.CLOSE_CIRCLE.create());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            cancelButton.addClickListener(e -> confirmCancelBooking(booking));
            actions.add(cancelButton);
        }

        return actions;
    }

    private void loadBookings() {
        try {
            List<Booking> bookings = bookingService.getPassengerBookings(SessionManager.getCurrentUserId());
            grid.setItems(bookings);
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement des réservations: " + ex.getMessage());
        }
    }

    private void viewRide(Booking booking) {
        getUI().ifPresent(ui -> ui.navigate("rides/" + booking.getRide().getId()));
    }

    private void confirmCancelBooking(Booking booking) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Annuler la réservation");
        dialog.setText(String.format(
                "Êtes-vous sûr de vouloir annuler votre réservation pour le trajet %s → %s du %s ?",
                booking.getRide().getDepartureCity(),
                booking.getRide().getArrivalCity(),
                booking.getRide().getDepartureDateTime().format(DATE_FORMATTER)
        ));

        dialog.setCancelable(true);
        dialog.setCancelText("Non");

        dialog.setConfirmText("Oui, annuler");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> cancelBooking(booking));

        dialog.open();
    }

    private void cancelBooking(Booking booking) {
        try {
            bookingService.cancelBookingByPassenger(booking.getId(), SessionManager.getCurrentUser());
            NotificationHelper.showSuccess("Réservation annulée avec succès");
            loadBookings();
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors de l'annulation: " + ex.getMessage());
        }
    }
}
