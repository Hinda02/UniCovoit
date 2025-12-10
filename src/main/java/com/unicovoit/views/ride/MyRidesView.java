package com.unicovoit.views.ride;

import com.unicovoit.entity.Ride;
import com.unicovoit.entity.RideStatus;
import com.unicovoit.service.RideService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.unicovoit.views.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "my-rides", layout = MainLayout.class)
@PageTitle("Mes trajets | UniCovoit")
public class MyRidesView extends VerticalLayout {

    private final RideService rideService;
    private final Grid<Ride> grid = new Grid<>(Ride.class, false);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MyRidesView(RideService rideService) {
        this.rideService = rideService;

        if (!SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createGridSection());
        loadRides();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Div titleSection = new Div();
        H2 title = new H2("Mes trajets");
        title.addClassName(LumoUtility.Margin.NONE);
        Paragraph subtitle = new Paragraph("Gérez les trajets que vous proposez");
        subtitle.addClassName(LumoUtility.TextColor.SECONDARY);
        titleSection.add(title, subtitle);

        Button createButton = new Button("Nouveau trajet", VaadinIcon.PLUS.create());
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("create-ride")));

        header.add(titleSection, createButton);
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
        grid.addClassName("rides-grid");
        grid.setSizeFull();

        grid.addColumn(ride -> ride.getDepartureCity() + " → " + ride.getArrivalCity())
                .setHeader("Trajet")
                .setSortable(true)
                .setFlexGrow(2);

        grid.addColumn(ride -> ride.getDepartureDateTime().format(DATE_FORMATTER))
                .setHeader("Date de départ")
                .setSortable(true)
                .setFlexGrow(1);

        grid.addColumn(ride -> ride.getPricePerSeat() + " €")
                .setHeader("Prix")
                .setFlexGrow(0)
                .setWidth("100px");

        grid.addColumn(ride -> ride.getSeatsAvailable() + "/" + ride.getSeatsTotal())
                .setHeader("Places")
                .setFlexGrow(0)
                .setWidth("100px");

        grid.addComponentColumn(this::createStatusBadge)
                .setHeader("Statut")
                .setFlexGrow(0)
                .setWidth("130px");

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Actions")
                .setFlexGrow(0)
                .setWidth("250px");

        grid.setEmptyStateText("Aucun trajet créé");
    }

    private Span createStatusBadge(Ride ride) {
        Span badge = new Span();
        badge.getElement().getThemeList().add("badge");
        badge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "0.875rem")
                .set("font-weight", "500");

        switch (ride.getStatus()) {
            case PUBLISHED:
                badge.setText("Publié");
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

    private HorizontalLayout createActionButtons(Ride ride) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button viewButton = new Button("Voir", VaadinIcon.EYE.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        viewButton.addClickListener(e -> viewRide(ride));

        if (ride.getStatus() == RideStatus.PUBLISHED) {
            Button cancelButton = new Button("Annuler", VaadinIcon.CLOSE_CIRCLE.create());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            cancelButton.addClickListener(e -> confirmCancelRide(ride));
            actions.add(viewButton, cancelButton);
        } else {
            actions.add(viewButton);
        }

        return actions;
    }

    private void loadRides() {
        try {
            List<Ride> rides = rideService.getRidesByDriver(SessionManager.getCurrentUserId());
            grid.setItems(rides);
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors du chargement des trajets: " + ex.getMessage());
        }
    }

    private void viewRide(Ride ride) {
        getUI().ifPresent(ui -> ui.navigate("rides/" + ride.getId()));
    }

    private void confirmCancelRide(Ride ride) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Annuler le trajet");
        dialog.setText(String.format(
                "Êtes-vous sûr de vouloir annuler le trajet %s → %s du %s ? " +
                "Les passagers ayant réservé seront notifiés.",
                ride.getDepartureCity(),
                ride.getArrivalCity(),
                ride.getDepartureDateTime().format(DATE_FORMATTER)
        ));

        dialog.setCancelable(true);
        dialog.setCancelText("Non");

        dialog.setConfirmText("Oui, annuler");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> cancelRide(ride));

        dialog.open();
    }

    private void cancelRide(Ride ride) {
        try {
            rideService.cancelRide(ride.getId(), SessionManager.getCurrentUser());
            NotificationHelper.showSuccess("Trajet annulé avec succès");
            loadRides();
        } catch (Exception ex) {
            NotificationHelper.showError("Erreur lors de l'annulation: " + ex.getMessage());
        }
    }
}
